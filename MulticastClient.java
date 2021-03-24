import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;
import java.util.UUID;

public class MulticastClient extends Thread {
    private String TERMINALS = "224.3.2.1";
    private int PORT = 4321;
    private boolean busy = false;
    private String id = UUID.randomUUID().toString();

    public static void main(String[] args) {
        MulticastClient client = new MulticastClient();
        client.start();
    }

    public void run() {
        MulticastSocket socket = null;
        try {
            ThreadOps op = new ThreadOps();
            socket = new MulticastSocket(PORT); // create socket and bind it
            InetAddress terminals_group = InetAddress.getByName(TERMINALS);
            socket.joinGroup(terminals_group);

            while (true) {
                DatagramPacket receivedPacket = op.receivePacket(socket);
                String msg = op.packetToString(receivedPacket);

                if (busy == false) {
                    if ("# req".equals(msg)) {
                        op.sendPacket(id, socket, terminals_group, PORT);
                        busy = true;
                    }
                } else if (busy == true && ("# " + id).equals(msg)) {
                    MulticastUser io_vote = new MulticastUser(id, op); // input thread
                    io_vote.start();

                    synchronized (this) {
                        try {
                            wait(); // para esta thread não responder sem querer
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }

                    try {
                        io_vote.join(120 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    busy = false;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}

class MulticastUser extends Thread {
    private String VOTE = "224.3.2.2";
    private int PORT = 4321;
    private String id;
    ThreadOps op;

    public MulticastUser(String id, ThreadOps op) {
        super("I/O " + (long) (Math.random() * 1000));
        this.id = id;
        this.op = op;
    }

    public void run() {
        MulticastSocket socket = null;
        try {
            socket = new MulticastSocket(PORT);
            InetAddress voting_group = InetAddress.getByName(VOTE);

            Scanner keyboardScanner = new Scanner(System.in);

            // Recebe login data
            System.out.print("User: ");
            String read_user = keyboardScanner.nextLine();
            System.out.print("Password: ");
            String read_password = keyboardScanner.nextLine();

            // Envia login data com id
            op.sendPacket(
                    "[" + this.id + "]; " + "type | login; username | " + read_user + "; password | " + read_password,
                    socket, voting_group, PORT);

            // espera por autenticação - user valido ou nao?

            // garante que recebe só a confirmação do login
            socket.joinGroup(voting_group);
            DatagramPacket auth_packet = op.receivePacket(socket);
            String auth_string = op.packetToString(auth_packet);

            if (auth_string.charAt(0) == '#') {
                System.out.println(auth_string.substring(auth_string.indexOf(';') + 2));
            }

            // Boletim - vai buscar ao rmi server
            // apresenta boletim
            // recebe input
            System.out.print("Insert your vote: ");
            String vote = keyboardScanner.nextLine();

            // envia voto secreto
            op.sendPacket("[" + this.id + "] " + "vote | " + vote, socket, voting_group, PORT);
            System.out.println("Vote sent!");
            keyboardScanner.close();

            // como eq eu digo à outra thread para acordar?
            // o notify não funfa aqui

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}