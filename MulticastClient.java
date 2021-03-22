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

                if (busy == false && "req".equals(msg)) {
                    byte[] buf = id.getBytes();
                    DatagramPacket id_msg = new DatagramPacket(buf, buf.length, terminals_group, PORT);
                    socket.send(id_msg);

                    busy = true;
                    MulticastUser io_vote = new MulticastUser(); // input thread
                    io_vote.start();
                    try {
                        io_vote.join(120 * 1000);
                        System.out.println("Terminal bloqueado. Requisite nova identificação junto da mesa de voto.");
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

    public MulticastUser() {
        super("I/O " + (long) (Math.random() * 1000));
    }

    public void run() {
        MulticastSocket socket = null;
        try {
            socket = new MulticastSocket(); // create socket without binding it (only for sending)
            InetAddress voting_group = InetAddress.getByName(VOTE);
            Scanner keyboardScanner = new Scanner(System.in);

            // Auth - nao faz nada ainda
            System.out.println("User: ");
            String read_user = keyboardScanner.nextLine();
            System.out.println("Password: ");
            String read_password = keyboardScanner.nextLine();

            byte[] login_data = ("type | login; username | " + read_user + "; password | " + read_password).getBytes();

            // Envia
            DatagramPacket packet = new DatagramPacket(login_data, login_data.length, voting_group, PORT);
            socket.send(packet);

            System.out.println("Vote sent!");

            // Boletim - vai buscar ao rmi server

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            socket.close();
        }
    }
}