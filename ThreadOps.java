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
}
