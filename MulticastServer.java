import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.rmi.NotBoundException;
import java.util.*;
import java.rmi.RemoteException;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.nio.file.*;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class MulticastServer extends Thread {
    private static final long serialVersionUID = 1L;
    private String TERMINALS;
    private String VOTE;
    private int PORT;
    private String DEP;

    public static void main(String[] args) {
        MulticastServer server = new MulticastServer();
        server.start();
    }

    public MulticastServer() {
        super("Server " + (long) (Math.random() * 1000));
    }

    public void readConfigFile(String file) {
        try {
            List<String> strings = Files.readAllLines(Paths.get(file));
            this.TERMINALS = strings.get(0);
            this.VOTE = strings.get(1);
            this.PORT = Integer.parseInt(strings.get(2));
            this.DEP = strings.get(3);
        } catch (Exception ioe) {
            System.out.println("IO Exception @ readConfigFile");
            ioe.printStackTrace();
        }
    }

    public void run() {
        MulticastSocket vote_socket = null;
        MulticastSocket terminal_socket = null;

        System.out.println(this.getName() + " running.");
        try {
            /**
             * Server dá join a dois grupos - um para enviar e receber respostas de todos os
             * terminais; outro para enviar e receber os votos de cada terminal. As
             * operações feitas em cada grupo são controladas por 2 threads:
             * 'terminal_thread', que está responsável por comunicar com os terminais, e
             * 'voting_thread' que recebe os votos.
             */

            RMIServer_I rmis = (RMIServer_I) LocateRegistry.getRegistry(6969).lookup("RMI_Server");
            ThreadOps op = new ThreadOps();

            // le config
            readConfigFile("MulticastConfig.txt");

            // configura
            // ligacao rmi
            RemoteMulticastServerObj_Impl remoteServerObj = new RemoteMulticastServerObj(rmis);

            /*try {
                rmis.subscribeMesa(this.DEP, (RemoteMulticastServerObj_Impl) remoteServerObj);
            } catch (RemoteException e) {
                rmis.print_on_rmi_server("Mesa " + this.DEP + " ligou-se ao RMIServer.");
            }*/

            while (true) {

                try {
                    rmis.subscribeMesa(this.DEP, (RemoteMulticastServerObj_Impl) remoteServerObj);
                    break;

                } catch (RemoteException e) {
                    int contador=0;
                    while(contador<30) {
                        try {
                            Thread.sleep(1000);
                            rmis = (RMIServer_I) LocateRegistry.getRegistry(6969).lookup("RMI_Server");
                            rmis.sayHello();
                            break;

                        }catch(NotBoundException | InterruptedException | RemoteException m){
                            contador++;
                            if(contador==30)
                                System.exit(-1);
                        }
                    }
                }
            }

            // multicast
            terminal_socket = new MulticastSocket(PORT);
            InetAddress terminals_group = InetAddress.getByName(TERMINALS);
            TerminalThread terminal_thread = new TerminalThread(terminals_group, terminal_socket, op, rmis, DEP, PORT);

            vote_socket = new MulticastSocket(PORT);
            InetAddress vote_group = InetAddress.getByName(VOTE);
            VotingThread voting_thread = new VotingThread(vote_group, vote_socket, op, rmis, DEP, PORT);

            while (true) {
                //
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            terminal_socket.close();
            vote_socket.close();
        }
    }
}

class TerminalThread extends Thread {
    private static final long serialVersionUID = 1L;
    InetAddress group;
    MulticastSocket s;
    Thread t;
    ThreadOps op;
    RMIServer_I rmis;
    String DEP;
    int PORT;

    public TerminalThread(InetAddress group, MulticastSocket s, ThreadOps op, RMIServer_I rmis, String DEP, int PORT) {
        this.group = group;
        this.s = s;
        this.op = op;
        this.rmis = rmis;
        this.DEP = DEP;
        this.PORT = PORT;
        t = new Thread(this);
        t.start();
    }

    public void printBulletin(HashMap<Integer, Eleicao> hme) {
        for (Map.Entry mapElement : hme.entrySet()) {
            Eleicao e = (Eleicao) mapElement.getValue();
            System.out.println(mapElement.getKey() + " - " + e.getTitulo());
        }
    }

    public void run() {
        try {
            s.joinGroup(group);
            Scanner keyboardScanner = new Scanner(System.in);
            Message msg = new Message();
            Pessoa p = null;
            Eleicao eleicao = null;

            while (true) {
                System.out.println("1 - Identificar um novo utilizador");
                System.out.println("2 - Desligar mesa ");
                System.out.print("Insira a sua escolha > ");
                String input = keyboardScanner.nextLine();
                if ("1".equals(input)) {
                    System.out.print("Insira o CC do eleitor para o identificar: ");
                    String cc = keyboardScanner.nextLine();

                    while (true) {

                        try {
                            p = rmis.getVoter(cc);
                            break;

                        } catch (RemoteException e) {
                            int contador=0;
                            while(contador<30) {
                                try {
                                    Thread.sleep(1000);
                                    rmis = (RMIServer_I) LocateRegistry.getRegistry(6969).lookup("RMI_Server");
                                    rmis.sayHello();
                                    break;

                                }catch(NotBoundException | InterruptedException | RemoteException m){
                                    contador++;
                                    if(contador==30)
                                        System.exit(-1);
                                }
                            }
                        }
                    }

                    if (p != null) {
                        HashMap<Integer, Eleicao> user_bulletin;
                        while (true) {

                            try {
                                user_bulletin = rmis.getBulletin(p);
                                break;

                            } catch (RemoteException e) {
                                int contador=0;
                                while(contador<30) {
                                    try {
                                        Thread.sleep(1000);
                                        rmis = (RMIServer_I) LocateRegistry.getRegistry(6969).lookup("RMI_Server");
                                        rmis.sayHello();
                                        break;

                                    }catch(NotBoundException | InterruptedException | RemoteException m){
                                        contador++;
                                        if(contador==30)
                                            System.exit(-1);
                                    }
                                }
                            }
                        }

                        if (!user_bulletin.isEmpty()) {
                            System.out.println("Selecione a eleicao na qual pretende exercer o seu voto:");
                            printBulletin(user_bulletin);
                            System.out.print("Escolha: ");
                            int opcao_eleicao = Integer.parseInt(keyboardScanner.nextLine());
                            eleicao = user_bulletin.get(opcao_eleicao); // eleição escolhida pelo eleitor

                            // Handshake
                            s.leaveGroup(group);
                            op.sendPacket(msg.make("#", "request", cc), s, group, PORT);
                            s.joinGroup(group);

                            DatagramPacket id_packet = op.receivePacket(s);

                            String id = msg.getSenderFromPacket(id_packet);
                            String type = msg.getTypeFromPacket(id_packet);
                            String sender = msg.getSenderFromPacket(id_packet);

                            // System.out.println("SENDER: " + sender + " TYPE: " + type);

                            if (!sender.startsWith("#") && type.equals("acknowledge")) {
                                s.leaveGroup(group);

                                while (true) {

                                    try {
                                        op.sendPacket(msg.make("#" + id, "reqreply",
                                                msg.makeList(rmis.getListasFromEleicaoEscolhida(eleicao)) + "reqreply; "
                                                        + opcao_eleicao),
                                                s, group, PORT);
                                        break;

                                    } catch (RemoteException e) {
                                        int contador=0;
                                        while(contador<30) {
                                            try {
                                                Thread.sleep(1000);
                                                rmis = (RMIServer_I) LocateRegistry.getRegistry(6969).lookup("RMI_Server");
                                                rmis.sayHello();
                                                break;

                                            }catch(NotBoundException | InterruptedException | RemoteException m){
                                                contador++;
                                                if(contador==30)
                                                    System.exit(-1);
                                            }
                                        }
                                    }
                                }

                                s.joinGroup(group);
                            }
                        } else {
                            System.out.println("Nao pode votar em nenhuma eleicao.");
                        }
                    } else {
                        System.out.println("Nao existe nenhum eleitor com o CC inserido.");
                    }
                } else if ("2".equals(input)) {
                    // clean exit

                    while (true) {

                        try {
                            rmis.unsubscribeMesa(this.DEP);
                            break;

                        } catch (RemoteException e) {
                            int contador=0;
                            while(contador<30) {
                                try {
                                    Thread.sleep(1000);
                                    rmis = (RMIServer_I) LocateRegistry.getRegistry(6969).lookup("RMI_Server");
                                    rmis.sayHello();
                                    break;

                                }catch(NotBoundException | InterruptedException | RemoteException m){
                                    contador++;
                                    if(contador==30)
                                        System.exit(-1);
                                }
                            }
                        }
                    }

                    System.out.println("Mesa " + this.DEP + " desligada.");
                    try {
                        join();
                        break;
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class VotingThread extends Thread {
    private static final long serialVersionUID = 1L;
    InetAddress group;
    MulticastSocket s;
    Thread t;
    ThreadOps op;
    RMIServer_I rmis;
    String DEP;
    int PORT;
    boolean rmiIsDown;

    public VotingThread(InetAddress group, MulticastSocket s, ThreadOps op, RMIServer_I rmis, String DEP, int PORT) {
        this.group = group;
        this.s = s;
        this.op = op;
        this.rmis = rmis;
        this.DEP = DEP;
        this.PORT = PORT;
        t = new Thread(this);
        t.start();
    }

    public void run() {
        try {
            s.joinGroup(group);
            Message msg = new Message();
            Pessoa p = null;
            Eleicao eleicao;
            while (true) {
                DatagramPacket packet = op.receivePacket(s);
                String type = msg.getTypeFromPacket(packet);
                String sender = msg.getSenderFromPacket(packet);

                System.out.println("SENDER: " + sender + " TYPE: " + type);

                try {
                    if (!sender.startsWith("#")) {
                        if (type.equals("login")) {
                            try {
                                String cc = msg.getUserFromPacket(packet);
                                String password = msg.getPasswordFromPacket(packet);

                                // Login verification

                                while (true) {

                                    try {
                                        p = rmis.getVoter(cc);
                                        break;

                                    } catch (RemoteException e) {
                                        int contador=0;
                                        while(contador<30) {
                                            try {
                                                Thread.sleep(1000);
                                                rmis = (RMIServer_I) LocateRegistry.getRegistry(6969).lookup("RMI_Server");
                                                rmis.sayHello();
                                                break;

                                            }catch(NotBoundException | InterruptedException | RemoteException m){
                                                contador++;
                                                if(contador==30)
                                                    System.exit(-1);
                                            }
                                        }
                                    }
                                }

                                if (p != null) {
                                    if (p.getPassword().equals(password)) {
                                        s.leaveGroup(group);
                                        op.sendPacket(
                                                msg.make("#" + sender, "bulletin",
                                                        "logged | on: Bem-vindo ao eVoting, " + p.getNome() + " !"),
                                                s, group, PORT);
                                        s.joinGroup(group);
                                    } else {
                                        op.sendPacket(msg.make("#" + sender, "error", "Wrong credentials"), s, group,
                                                PORT);
                                    }
                                } else {
                                    System.out.println("User nao existe.");
                                }
                            } catch (ArrayIndexOutOfBoundsException e) {
                                e.printStackTrace();
                            }
                        } else if (type.equals("vote")) {
                            String nome_lista = null;

                            int escolha = Integer.parseInt(msg.getContentFromPacket(packet, "; "));
                            int opcao_eleicao = Integer.parseInt(msg.getOpcaoEleicao(packet, "; "));
                            HashMap<Integer, Eleicao> user_bulletin;

                            while (true) {

                                try {
                                    user_bulletin = rmis.getBulletin(p);
                                    break;

                                } catch (RemoteException e) {
                                    int contador = 0;
                                    while (contador < 30) {
                                        try {
                                            Thread.sleep(1000);
                                            rmis = (RMIServer_I) LocateRegistry.getRegistry(6969).lookup("RMI_Server");
                                            rmis.sayHello();
                                            break;

                                        } catch (NotBoundException | InterruptedException | RemoteException m) {
                                            contador++;
                                            if (contador == 30)
                                                System.exit(-1);
                                        }
                                    }
                                }
                            }

                            eleicao = user_bulletin.get(opcao_eleicao);
                            HashMap<Integer, String> hm;

                            while (true) {

                                try {
                                    hm = rmis.getListasFromEleicaoEscolhida(eleicao);
                                    break;

                                } catch (RemoteException e) {
                                    int contador = 0;
                                    while (contador < 30) {
                                        try {
                                            Thread.sleep(1000);
                                            rmis = (RMIServer_I) LocateRegistry.getRegistry(6969).lookup("RMI_Server");
                                            rmis.sayHello();
                                            break;

                                        } catch (NotBoundException | InterruptedException | RemoteException m) {
                                            contador++;
                                            if (contador == 30)
                                                System.exit(-1);
                                        }
                                    }
                                }
                            }

                            nome_lista = hm.get(escolha + 1);
                            // rmi escreve na bd
                            Date d = new Date();


                            while (true) {

                                try {
                                    rmis.atualiza(p.getNum_CC(), nome_lista, eleicao.getTitulo(), DEP, d);
                                    break;

                                } catch (RemoteException e) {
                                    int contador = 0;
                                    while (contador < 30) {
                                        try {
                                            Thread.sleep(1000);
                                            rmis = (RMIServer_I) LocateRegistry.getRegistry(6969).lookup("RMI_Server");
                                            rmis.sayHello();
                                            break;

                                        } catch (NotBoundException | InterruptedException | RemoteException m) {
                                            contador++;
                                            if (contador == 30)
                                                System.exit(-1);
                                        }
                                    }
                                }
                            }

                            op.sendPacket(msg.make("#" + sender, "success", "Voto submetido com sucesso!"), s,
                                    group, PORT);

                             /*catch (RemoteException re) {
                                rmiIsDown = true;
                                while (rmiIsDown  || counterThread.isAlive() ) {
                                    try {
                                        rmis.print_on_rmi_server("Ping from " + DEP);
                                        // rmis.atualiza(p.getNum_CC(), nome_lista, eleicao.getTitulo(), DEP);
                                        sleep(2 * 1000);
                                    } catch (RemoteException ex) {
                                        System.out.println("Reconectando...");
                                        // RMIServer rmis = new RMIServer();
                                        // Registry r = LocateRegistry.createRegistry(6969);
                                        // r.rebind("RMI_Server", rmis);
                                        continue;
                                    } catch (InterruptedException ie) {
                                        ie.printStackTrace();
                                    }
                                    rmiIsDown = false;
                                    break;
                                }
                            }*/
                        }
                    }
                } catch (RemoteException re) {
                    System.out.println("ola estou aqui");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class RemoteMulticastServerObj extends UnicastRemoteObject implements RemoteMulticastServerObj_Impl {

    private static final long serialVersionUID = 1L;
    RMIServer_I rmis;

    RemoteMulticastServerObj(RMIServer_I rmis) throws RemoteException {
        super();
        this.rmis = rmis;
    }

    public void ping() throws RemoteException {
        // resposta
        rmis.print_on_rmi_server("Successful.\n");
    }
}