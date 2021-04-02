import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;
import java.util.UUID;
import java.util.List;
import java.nio.file.*;

public class MulticastClient extends Thread {
    private String TERMINALS;
    private String VOTE;
    private int PORT;
    private String DEP;
    private boolean busy = false;
    private boolean blocked = true;
    private String id = UUID.randomUUID().toString();

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

        readConfigFile("MulticastConfig.txt");

        System.out.println("Client " + id + " running.");
        try {
            // le config
            readConfigFile("MulticastConfig.txt");

            // configura
            terminal_socket = new MulticastSocket(PORT);
            voting_socket = new MulticastSocket(PORT);
            InetAddress terminals_group = InetAddress.getByName(TERMINALS);
            InetAddress voting_group = InetAddress.getByName(VOTE);
            terminal_socket.joinGroup(terminals_group);

            // helpers
            Scanner keyboardScanner = new Scanner(System.in);
            Message msg = new Message();
            ThreadOps op = new ThreadOps();
            DatagramPacket packet;
            // Thread counterThread = new Thread();
            // counterThread.start();

            while (true) {
                if (blocked) {
                    System.out.println("\nTerminal Bloqueado. Dirija-se a mesa de voto para desbloquear um terminal.");
                }
                if (!busy) {
                    packet = op.receivePacket(terminal_socket);
                } else {
                    packet = op.receivePacket(voting_socket);
                }
                String type = msg.getTypeFromPacket(packet);
                String sender = msg.getSenderFromPacket(packet);

                if (sender.equals("#")) {
                    if (type.equals("request") && busy == false) {
                        op.sendPacket(msg.make(id, "acknowledge", null), terminal_socket, terminals_group, PORT);
                        blocked = false;
                    }
                    if (type.equals("reqreply")) {
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
                        msg.getContentFromPacket(packet, "reqreply; ");
                    }
                    if (type.equals("bulletin")) {
                        // apresenta boletim
                        System.out.println(
                                msg.packetToString(packet).substring(msg.packetToString(packet).indexOf(": ")));

                        // recebe input
                        System.out.print("\nInsert your vote: ");
                        String vote = keyboardScanner.nextLine(); // uma opcao do hashmap
                        op.sendPacket(msg.make(id, "vote", vote), voting_socket, voting_group, PORT);
                        System.out.println("Vote sent!");
                        busy = false;
                        blocked = true;
                        voting_socket.leaveGroup(voting_group);
                    }
                    if (type.equals("error")) {
                        System.out.println("Wrong credentials!");
                        blocked = true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            voting_socket.close();
            terminal_socket.close();
        }
    }
}

/*
 * class CounterThread extends Thread { private boolean isRunning = false;
 * 
 * public CounterThread() { this.start(); }
 * 
 * public void run() {
 * 
 * } }
 */