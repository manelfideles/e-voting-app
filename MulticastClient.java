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
            socket = new MulticastSocket(PORT); // create socket and bind it
            InetAddress terminals_group = InetAddress.getByName(TERMINALS);
            socket.joinGroup(terminals_group);

            while (true) {
                byte[] received = new byte[256];
                DatagramPacket receivedPacket = new DatagramPacket(received, received.length);
                socket.receive(receivedPacket);
                String msg = new String(receivedPacket.getData(), 0, receivedPacket.getLength());

                if (busy == false) {
                    if ("# req".equals(msg)) {
                        byte[] buf = id.getBytes();
                        DatagramPacket id_msg = new DatagramPacket(buf, buf.length, terminals_group, PORT);
                        socket.send(id_msg);
                        busy = true;
                    }
                } else if (busy == true && ("# " + id).equals(msg)) {
                    MulticastUser io_vote = new MulticastUser(id); // input thread
                    io_vote.start();

                    synchronized (this) {
                        try {
                            wait(); // para esta thread parar responder sem querer
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }

                    try {
                        io_vote.join(120 * 1000);
                        notify();
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

    public MulticastUser(String id) {
        super("I/O " + (long) (Math.random() * 1000));
        this.id = id;
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
            byte[] login_data = ("[" + this.id + "]; " + "type | login; username | " + read_user + "; password | "
                    + read_password).getBytes();
            DatagramPacket packet = new DatagramPacket(login_data, login_data.length, voting_group, PORT);
            socket.send(packet);

            // espera por autenticação
            // valido ou nao?
            byte[] buf = new byte[256];
            DatagramPacket auth_packet = new DatagramPacket(buf, buf.length);

            // garante que recebe só a confirmação do login
            socket.joinGroup(voting_group);

            socket.receive(auth_packet);
            String auth_string = new String(auth_packet.getData(), 0, auth_packet.getLength());
            if (auth_string.charAt(0) == '#') {
                System.out.println(auth_string.substring(auth_string.indexOf(';') + 2));
            }
            // Boletim - vai buscar ao rmi server
            // apresenta boletim
            // recebe input
            // envia voto secreto
            System.out.println("Vote sent!");

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}