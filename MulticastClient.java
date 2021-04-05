import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
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
    String id = UUID.randomUUID().toString();

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

    public void run() {
        MulticastSocket terminal_socket = null;
        MulticastSocket voting_socket = null;
        System.out.println("Client " + id + " running.");

        try {
            readConfigFile("MulticastConfig.txt");
            String cc = null;
            Session session = null;

            // configura

            terminal_socket = new MulticastSocket(PORT);
            terminal_socket.setSoTimeout(120 * 1000);
            voting_socket = new MulticastSocket(PORT);
            voting_socket.setSoTimeout(120 * 1000);
            InetAddress terminals_group = InetAddress.getByName(TERMINALS);
            InetAddress voting_group = InetAddress.getByName(VOTE);
            terminal_socket.joinGroup(terminals_group);

            // helpers
            DatagramPacket packet;
            Message msg = new Message();
            ThreadOps op = new ThreadOps();
            Scanner keyboardScanner = new Scanner(System.in);
            String opcao_eleicao = null;

            while (true) {
                try {
                    if (blocked) {
                        System.out.println(
                                "\nTerminal Bloqueado. Dirija-se a mesa de voto para desbloquear um terminal.");
                    }
                    if (!busy) {
                        packet = op.receivePacket(terminal_socket);
                    } else {
                        packet = op.receivePacket(voting_socket);
                    }

                    String type = msg.getTypeFromPacket(packet);
                    String sender = msg.getSenderFromPacket(packet);

                    System.out.println("SENDER: " + sender + " TYPE: " + type);

                    if (sender.equals("#") || sender.equals("#" + id)) {
                        if (type.equals("request") && busy == false) {
                            op.sendPacket(msg.make(id, "acknowledge", null), terminal_socket, terminals_group, PORT);
                            cc = msg.getContentFromPacket(packet, "; ");
                            // session = new Session(cc);
                            blocked = false;
                        } else if (type.equals("reqreply")) {
                            // tenta recuperar sessao se houver session file
                            // if(!session.restoreSession()) {
                            // session.saveSession([cc, password]);
                            // }

                            // Login data
                            System.out.print("CC: ");
                            String read_user = keyboardScanner.nextLine();
                            System.out.print("Password: ");
                            String read_password = keyboardScanner.nextLine();

                            // Envia login data com id
                            busy = true;
                            op.sendPacket(
                                    msg.make(id, "login", "username | " + read_user + "; password | " + read_password),
                                    voting_socket, voting_group, PORT);
                            voting_socket.joinGroup(voting_group);

                            System.out.println("Boletim:");
                            msg.splitMakeList(msg.getContentFromPacket(packet, "reqreply; "));
                            opcao_eleicao = msg.getOpcaoEleicao(packet, "reqreply; ");
                        } else if (type.equals("bulletin")) {
                            // apresenta em-vindas
                            System.out.println(
                                    msg.packetToString(packet).substring(msg.packetToString(packet).indexOf(": ")));

                            // recebe input
                            System.out.print("\nInsert your vote: ");
                            String vote = keyboardScanner.nextLine();

                            // Spams packets to server until confirmation is received
                            System.out.println("Aguarde...");
                            op.sendPacket(msg.make(id, "vote", vote + "; " + opcao_eleicao), voting_socket,
                                    voting_group, PORT);
                        } else if (type.equals("success")) {
                            System.out.println("Vote sent!");
                            busy = false;
                            blocked = true;
                            voting_socket.leaveGroup(voting_group);
                        } else if (type.equals("error")) {
                            System.out.println("Wrong credentials!");
                            busy = false;
                            blocked = true;
                            voting_socket.leaveGroup(voting_group);
                        }
                    }
                } catch (SocketException se) {
                    blocked = true;
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            voting_socket.close();
            terminal_socket.close();
        }
    }
}
