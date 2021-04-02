import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;
import java.rmi.RemoteException;
import java.rmi.registry.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.List;
import java.nio.file.*;

public class MulticastServer extends Thread {
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
            rmis.subscribeMesa(this.DEP, (RemoteMulticastServerObj_Impl) remoteServerObj);

            // multicast
            terminal_socket = new MulticastSocket(PORT);
            InetAddress terminals_group = InetAddress.getByName(TERMINALS);
            TerminalThread terminal_thread = new TerminalThread(terminals_group, terminal_socket, op, rmis);

            vote_socket = new MulticastSocket(PORT);
            InetAddress vote_group = InetAddress.getByName(VOTE);
            VotingThread voting_thread = new VotingThread(vote_group, vote_socket, op, rmis);

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
    private int PORT = 4321;
    InetAddress group;
    MulticastSocket s;
    Thread t;
    ThreadOps op;
    RMIServer_I rmis;

    public TerminalThread(InetAddress group, MulticastSocket s, ThreadOps op, RMIServer_I rmis) {
        this.group = group;
        this.s = s;
        this.op = op;
        this.rmis = rmis;
        t = new Thread(this);
        t.start();
    }

    public void printBulletin(HashMap<Integer, Eleicao> hme) {
        for (Map.Entry mapElement : hme.entrySet()) {
            Eleicao e = (Eleicao) mapElement.getValue();
            System.out.println(mapElement.getKey() + " - " + e.getTitulo());
        }
    }

    public HashMap<Integer, String> getListasFromEleicaoEscolhida(Eleicao e) {
        int j = 1;
        HashMap<Integer, String> out = new HashMap<Integer, String>();
        for (HashMap<String, ListaCandidato> llc : e.lista_lista_candidato) {
            for (Entry<String, ListaCandidato> entry : llc.entrySet()) {
                out.put(j, entry.getValue().nome_lista);
                j++;
            }
        }
        return out;
    }

    public void run() {
        try {
            s.joinGroup(group);
            Scanner keyboardScanner = new Scanner(System.in);
            Message msg = new Message();
            Pessoa p = null;
            Eleicao eleicao = null;

            while (true) {
                System.out.print("Pressione '1' para identificar um novo utilizador: ");
                if ("1".equals(keyboardScanner.nextLine())) {
                    System.out.print("Insira o CC do eleitor para o identificar: ");
                    String cc = keyboardScanner.nextLine();
                    p = rmis.getVoter(cc);
                    if (p != null) {
                        HashMap<Integer, Eleicao> user_bulletin = rmis.getBulletin(p);
                        if (!user_bulletin.isEmpty()) {
                            System.out.println("Selecione a eleicao na qual pretende exercer o seu voto:");
                            printBulletin(user_bulletin);
                            System.out.print("Escolha: ");
                            int opcao_eleicao = Integer.parseInt(keyboardScanner.nextLine());
                            eleicao = user_bulletin.get(opcao_eleicao);

                            // Handshake
                            s.leaveGroup(group);
                            op.sendPacket(msg.make("#", "request", null), s, group, PORT);
                            s.joinGroup(group);
                            DatagramPacket id_packet = op.receivePacket(s);
                            String id_string = msg.packetToString(id_packet);
                            if (id_string.charAt(0) != '#') {
                                op.sendPacket(
                                        msg.make("#", "reqreply", msg.makeList(getListasFromEleicaoEscolhida(eleicao))),
                                        s, group, PORT);
                            }
                        } else {
                            System.out.println("Nao pode votar em nenhuma eleicao.");
                        }
                    } else {
                        System.out.println("Nao existe nenhum eleitor com o CC inserido.");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class VotingThread extends Thread {
    private int PORT = 4321;
    InetAddress group;
    MulticastSocket s;
    Thread t;
    ThreadOps op;
    RMIServer_I rmis;

    public VotingThread(InetAddress group, MulticastSocket s, ThreadOps op, RMIServer_I rmis) {
        this.group = group;
        this.s = s;
        this.op = op;
        this.rmis = rmis;
        t = new Thread(this);
        t.start();
    }

    public void run() {
        try {
            s.joinGroup(group);
            Message msg = new Message();
            Pessoa p = null;
            while (true) {
                DatagramPacket packet = op.receivePacket(s);
                String type = msg.getTypeFromPacket(packet);
                String sender = msg.getSenderFromPacket(packet);

                if (!sender.equals("#")) {
                    if (type.equals("login")) {
                        try {
                            String cc = msg.getUserFromPacket(packet);
                            String password = msg.getPasswordFromPacket(packet);

                            // Login verification
                            p = rmis.getVoter(cc);
                            if (p != null) {
                                if (p.getPassword().equals(password)) {
                                    op.sendPacket(
                                            msg.make("#", "bulletin",
                                                    "logged | on: Bem-vindo ao eVoting, " + p.getNome() + " !"),
                                            s, group, PORT);
                                } else {
                                    op.sendPacket(msg.make("#", "error", "Wrong credentials"), s, group, PORT);
                                }
                            } else {
                                System.out.println("User não existe.");
                            }
                        } catch (ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    }
                    if (type.equals("bulletin")) {
                        op.sendPacket(msg.make("#", "bulletin", "\n" + rmis.getBulletin(p)), s, group, PORT);
                    }
                    if (type.equals("vote")) {
                        // rmi - atualizar o n_votos (brancos/nulos/validos) - acesso
                        // sincronizado
                        // atualizar todas as estruturas de dados
                        // associar pessoa ao local de voto
                        // envia informaçao para a admin console
                    }
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

    public void ping(AdminConsole_I ac) throws RemoteException {
        // resposta
        ac.print_on_admin_console("Successful.");
    }
}