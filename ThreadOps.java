import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class ThreadOps {
    /**
     * Classe com métodos comuns a todas as classes do programa
     */

    public ThreadOps() {
        ;
    }

    public DatagramPacket receivePacket(MulticastSocket s) {
        byte[] received = new byte[256];
        DatagramPacket receivedPacket = new DatagramPacket(received, received.length);
        try {
            s.receive(receivedPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return receivedPacket;
    }

    public void sendPacket(String msg, MulticastSocket s, InetAddress group, int PORT) {
        // É necessário mudar para enviar objetos serializados?
        byte[] buffer = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, PORT);
        try {
            s.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String packetToString(DatagramPacket p) {
        return new String(p.getData(), 0, p.getLength());
    }

    public String getIdFromPacket(DatagramPacket p) {
        String login_string = this.packetToString(p);
        String[] login_data = login_string.split(" ");
        return login_data[0].substring(1, login_data[0].length() - 1);
    }
}
