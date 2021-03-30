import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;
import java.util.UUID;

public class MulticastClient extends Thread {
    private String TERMINALS = "224.3.2.1";
    private String VOTE = "224.3.2.2";
    private int PORT = 4321;
    private boolean busy = false;
    private boolean blocked = true;
    private String id = UUID.randomUUID().toString();

    public static void main(String[] args) {
        MulticastClient client = new MulticastClient();
        client.start();
    }

    public void run() {
        MulticastSocket terminal_socket = null;
        MulticastSocket voting_socket = null;

        System.out.println("Client " + id + " running.");
        try {
            terminal_socket = new MulticastSocket(PORT);
            voting_socket = new MulticastSocket(PORT);
            InetAddress terminals_group = InetAddress.getByName(TERMINALS);
            InetAddress voting_group = InetAddress.getByName(VOTE);
            terminal_socket.joinGroup(terminals_group);

            Scanner keyboardScanner = new Scanner(System.in);
            Message msg = new Message();
            ThreadOps op = new ThreadOps();
            DatagramPacket packet;
            // Thread counterThread = new Thread();
            // counterThread.start();

            while (true) {
                if (blocked) {
                    System.out.println("Terminal Bloqueado. Dirija-se a mesa de voto para votar.");
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
                    }
                    if (type.equals("status")) {
                        System.out.println("\n" + msg.packetToString(packet));

                        // Boletim - vai buscar ao rmi server
                        System.out.println("Boletim:");
                        // apresenta boletim
                        // recebe input
                        System.out.print("Insert your vote: ");
                        String vote = keyboardScanner.nextLine();
                        op.sendPacket(msg.make(id, "vote", "Voted for: " + vote), voting_socket, voting_group, PORT);
                        System.out.println("Vote sent!");
                        busy = false;
                        voting_socket.leaveGroup(voting_group);
                    }
                    if (type.equals("error")) {
                        System.out.println("Wrong credentials!");
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