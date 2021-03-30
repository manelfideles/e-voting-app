import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;
import java.rmi.registry.*;

public class MulticastServer extends Thread {
    private String TERMINALS = "224.3.2.1";
    private String VOTE = "224.3.2.2";
    private int PORT = 4321;

    public static void main(String[] args) {
        MulticastServer server = new MulticastServer();
        server.start();
    }

    public MulticastServer() {
        super("Server " + (long) (Math.random() * 1000));
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

    public void run() {
        try {
            s.joinGroup(group);
            Scanner keyboardScanner = new Scanner(System.in);
            Message msg = new Message();
            while (true) {
                System.out.print("Pressione '1' para identificar um novo utilizador: ");
                if ("1".equals(keyboardScanner.nextLine())) {
                    System.out.print("Insira o CC do eleitor para o identificar: ");
                    String cc = keyboardScanner.nextLine();
                    if (rmis.getVoter(cc) != null) {
                        s.leaveGroup(group);
                        op.sendPacket(msg.make("#", "request", null), s, group, PORT);

                        // Handshake
                        s.joinGroup(group);
                        DatagramPacket id_packet = op.receivePacket(s);
                        String id_string = msg.packetToString(id_packet);
                        if (id_string.charAt(0) != '#') {
                            op.sendPacket(msg.make("#", "reqreply", "Request reply"), s, group, PORT);
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
                            Pessoa p = rmis.getVoter(cc);
                            if (p != null) {
                                if (p.getPassword().equals(password)) {
                                    op.sendPacket(
                                            msg.make("#", "status",
                                                    "logged | on: Bem-vindo ao eVoting, " + p.getNome()),
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
                        // pede boletim ao rmi;
                    }
                    if (type.equals("vote")) {
                        //
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}