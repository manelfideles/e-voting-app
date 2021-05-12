package MULTICAST;

import RMI.Message;
import RMI.Session;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;
import java.util.UUID;
import java.util.List;
import java.nio.file.*;

public class MulticastClient extends Thread {
    private static final long serialVersionUID = 1L;
    String TERMINALS;
    String VOTE;
    int PORT;
    String DEP;
    private boolean busy = false;
    private boolean blocked = true;
    boolean hasRecovered = false;
    String id = UUID.randomUUID().toString();
    String read_user, read_password;
    int sendTries = 1;

    public static void main(String[] args) {
        MulticastClient client = new MulticastClient();
        client.start();
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

    public void cleanup(Session session, MulticastSocket voting_socket, InetAddress voting_group) throws IOException {
        session.destroy();
        read_user = null;
        read_password = null;
        busy = false;
        blocked = true;
        voting_socket.leaveGroup(voting_group);
    }

    public void run() {
        MulticastSocket terminal_socket = null;
        MulticastSocket voting_socket = null;
        System.out.println("Client " + id + " running.");

        try {
            readConfigFile("MulticastConfig.txt");
            Session session = new Session("session.txt");
            boolean sessionWasCreated = session.create();
            read_user = null;
            read_password = null;
            if (sessionWasCreated == false) {
                List<String> userInfo = session.restore();
                if (userInfo != null) {
                    read_user = userInfo.get(0);
                    read_password = userInfo.get(1);
                }
            }

//            System.out.println(read_user);
//            System.out.println(read_password);

            String cc = null;
            String bulletin = null;

            // configura
            terminal_socket = new MulticastSocket(PORT);
            voting_socket = new MulticastSocket(PORT);
            InetAddress terminals_group = InetAddress.getByName(TERMINALS);
            InetAddress voting_group = InetAddress.getByName(VOTE);
            terminal_socket.joinGroup(terminals_group);

            // helpers
            DatagramPacket packet = null;
            Message msg = new Message();
            ThreadOps op = new ThreadOps();
            Scanner keyboardScanner = new Scanner(System.in);
            String opcao_eleicao = null;
            int socketTimeout = 5;

            while (true) {
                try {
                    if (blocked) {
                        socketTimeout = 0;
                        terminal_socket.setSoTimeout(socketTimeout);
                        voting_socket.setSoTimeout(socketTimeout);
                        System.out.println(
                                "\nTerminal Bloqueado. Dirija-se a mesa de voto para desbloquear um terminal.");
                    } else {
                        if (busy) {
                            socketTimeout = 3;
                            terminal_socket.setSoTimeout(socketTimeout * 1000);
                            voting_socket.setSoTimeout(socketTimeout * 1000);
                        } else {
                            socketTimeout = 120;
                            terminal_socket.setSoTimeout(socketTimeout * 1000);
                            voting_socket.setSoTimeout(socketTimeout * 1000);
                        }
                    }

                    if (busy) {
                        packet = op.receivePacket(voting_socket, socketTimeout);
                    } else {
                        packet = op.receivePacket(terminal_socket, socketTimeout);
                    }

                    String type = msg.getTypeFromPacket(packet);
                    String sender = msg.getSenderFromPacket(packet);

                    // System.out.println("SENDER: " + sender + " TYPE: " + type);

                    if (sender.equals("#") || sender.equals("#" + id)) {
                        if (type.equals("request") && busy == false) {
                            op.sendPacket(msg.make(id, "acknowledge", null), terminal_socket, terminals_group, PORT);
                            cc = msg.getContentFromPacket(packet, "; ");
                            blocked = false;
                        } else if (type.equals("reqreply")) {
                            if (read_user == null && read_password == null) {
                                System.out.print("CC: ");
                                read_user = keyboardScanner.nextLine();
                                System.out.print("Password: ");
                                read_password = keyboardScanner.nextLine();
                                session.save(read_user, read_password);
                            }

                            // Envia login data com id
                            busy = true;
                            // System.out.println("SENDER: " + sender + " TYPE: " + type);
                            // System.out.println(read_user + " " + read_password);

                            if (cc.equals(read_user)) {
                                op.sendPacket(
                                        msg.make(id, "login",
                                                "username | " + read_user + "; password | " + read_password),
                                        voting_socket, voting_group, PORT);
                                System.out.println("User autenticado!");
                                voting_socket.joinGroup(voting_group);
                                bulletin = msg.getContentFromPacket(packet, "reqreply; ");
                                opcao_eleicao = msg.getOpcaoEleicao(packet, "reqreply; "); // eleicao escolhida
                            } else {
                                System.out
                                        .println("O CC inserido e o user registado nao sao compativeis! A bloquear...");
                                blocked = true;
                                busy = false;
                                cleanup(session, voting_socket, voting_group);
                            }
                        } else if (type.equals("bulletin")) {
                            System.out.println("\nBem vindo ao eVoting!\n");
                            System.out.println("Boletim:");
                            if (bulletin != null) {
                                msg.splitMakeList(bulletin);
                            } else {
                                System.out.println("Boletim esta null (???)");
                            }

                            // recebe input
                            System.out.print("\nInsira o seu voto: ");
                            String vote = keyboardScanner.nextLine();

                            // Spams packets to server until confirmation is received
                            System.out.println("Aguarde...");
                            op.sendPacket(msg.make(id, "vote", vote + "; " + opcao_eleicao), voting_socket,
                                    voting_group, PORT);

                        } else if (type.equals("error")) {
                            System.out.println("Wrong credentials!");
                            cleanup(session, voting_socket, voting_group);
                        } else if (type.equals("success")) {
                            System.out.println("Vote sent!");
                            cleanup(session, voting_socket, voting_group);
                        }
                    }
                } catch (IOException se) {
                    // // if (!hasRecovered) {
                    // System.out.println("Ping " + sendTries + " : Failed");
                    // sendTries++;
                    // // hasRecovered = false;
                    // blocked = false;
                    // busy = false;
                    // continue;
                    // // }
                }
            }
        } catch (IOException ioe) {
            System.out.println("AAAAAAAAAAA");
            ioe.printStackTrace();
        } finally {
            try {
                voting_socket.close();
                terminal_socket.close();
            } catch (NullPointerException ex) {
                System.out.println("Sockets a null");
                ex.printStackTrace();
            }
        }
    }
}
