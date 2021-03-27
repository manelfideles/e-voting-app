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
            ThreadOps op = new ThreadOps();
            terminal_socket = new MulticastSocket(PORT); // create socket and bind it
            voting_socket = new MulticastSocket(PORT); // create socket and bind it
            InetAddress terminals_group = InetAddress.getByName(TERMINALS);
            InetAddress voting_group = InetAddress.getByName(VOTE);

            terminal_socket.joinGroup(terminals_group);

            while (true) {
                DatagramPacket receivedPacket = op.receivePacket(terminal_socket);
                String msg = op.packetToString(receivedPacket);

                if (msg.charAt(0) == '#') {
                    if (busy == false && "# req".equals(msg)) {
                        op.sendPacket(id, terminal_socket, terminals_group, PORT);
                        busy = true;
                    } else if (busy == true && ("# " + id).equals(msg)) {

                        terminal_socket.leaveGroup(terminals_group);
                        Scanner keyboardScanner = new Scanner(System.in);

                        // Recebe login data
                        System.out.print("User: ");
                        String read_user = keyboardScanner.nextLine();
                        System.out.print("Password: ");
                        String read_password = keyboardScanner.nextLine();

                        // Envia login data com id
                        op.sendPacket("[" + this.id + "] " + "type | login; username | " + read_user + "; password | "
                                + read_password, voting_socket, voting_group, PORT);

                        // espera por autenticação - user valido ou nao?
                        voting_socket.joinGroup(voting_group);

                        // garante que recebe só a confirmação do login
                        DatagramPacket auth_packet = op.receivePacket(voting_socket);
                        String auth_string = op.packetToString(auth_packet);
                        if (auth_string.charAt(0) == '#') {
                            System.out.println(auth_string.substring(auth_string.indexOf(' ') + 1));
                        }
                        voting_socket.leaveGroup(voting_group);

                        // Boletim - vai buscar ao rmi server
                        // apresenta boletim
                        // recebe input
                        System.out.print("Insert your vote: ");
                        String vote = keyboardScanner.nextLine();

                        // envia voto secreto
                        op.sendPacket("[" + this.id + "] " + "type | vote; Voted for: " + vote, voting_socket,
                                voting_group, PORT);
                        System.out.println("Vote sent!");

                        terminal_socket.joinGroup(terminals_group);
                        busy = false;
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