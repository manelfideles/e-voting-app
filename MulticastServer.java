import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastServer extends Thread {
    private String TERMINALS = "224.3.2.1";
    private String VOTE = "224.3.2.2";
    private int PORT = 4321;

    public static void main(String[] args) {
        MulticastServer server = new MulticastServer();
        server.start();
    }

    public MulticastServer() {
        super("Server " + (long) (Math.random() * 1000));
    }

    public void run() {
        MulticastSocket vote_socket = null;
        MulticastSocket terminal_socket = null;

        System.out.println(this.getName() + " running.");
        try {
            /**
             * Server dá join a dois grupos - um para enviar e receber respostas de todos os
             * terminais; outro para enviar e receber os votos de cada terminal. As
             * operações feitas em cada grupo são controladas por 2 threads:
             * 'terminal_thread', que está responsável por comunicar com os terminais, e
             * 'voting_thread' que recebe os votos.
             */

            vote_socket = new MulticastSocket(PORT);
            InetAddress vote_group = InetAddress.getByName(VOTE);
            VotingThread voting_thread = new VotingThread(vote_group, vote_socket);

            terminal_socket = new MulticastSocket(PORT);
            InetAddress terminals_group = InetAddress.getByName(TERMINALS);
            TerminalThread terminal_thread = new TerminalThread(terminals_group, terminal_socket);

            // while (true) {
            // recebeu um eleitor na mesa:
            // 1 - membro da mesa faz uma query à bd de eleitores
            // através do rmi (i.e pesquisa o eleitor)
            // - não implementado -

            // 2 - recebe resposta - existe ou não existe
            // (se não existir, espera pelo próximo eleitor)
            // - não implementado -

            // 3 - Se existir, requisita um terminal
            // i.e manda req ao grupo mc dos terminais - protocolos de comunicação entre
            // dispositivos

            // 4 - Se tiver resposta positiva
            // manda o eleitor para o terminal que respondeu - cada terminal tem um id
            // }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            vote_socket.close();
            terminal_socket.close();
        }
    }
}

class TerminalThread implements Runnable {
    private int PORT = 4321;
    InetAddress group;
    MulticastSocket s;
    Thread t;

    public TerminalThread(InetAddress group, MulticastSocket s) {
        this.group = group;
        this.s = s;
        t = new Thread(this);
        t.start();
    }

    public DatagramPacket receivePacket() {
        byte[] received = new byte[256];
        DatagramPacket receivedPacket = new DatagramPacket(received, received.length);
        try {
            this.s.receive(receivedPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return receivedPacket;
    }

    public void sendPacket(String msg, int PORT) {
        // só envia Strings -> necessário mudar para enviar objetos serializados
        byte[] buffer = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, this.group, PORT);
        try {
            this.s.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            s.joinGroup(group);
            while (true) {
                this.sendPacket("req", PORT); // substituir por um protocolo de comunicação
                DatagramPacket reply = this.receivePacket();

                if ("req".equals(new String(reply.getData(), 0, reply.getLength())))
                    ;

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class VotingThread implements Runnable {
    InetAddress group;
    MulticastSocket s;
    Thread t;

    public VotingThread(InetAddress group, MulticastSocket s) {
        this.group = group;
        this.s = s;
        t = new Thread(this);
        t.start();
    }

    public DatagramPacket receivePacket() {
        byte[] received = new byte[256];
        DatagramPacket receivedPacket = new DatagramPacket(received, received.length);
        try {
            this.s.receive(receivedPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return receivedPacket;
    }

    public void run() {
        try {
            s.joinGroup(group);
            while (true) {
                synchronized (this) {
                    DatagramPacket login_data = this.receivePacket();
                    String login_string = new String(login_data.getData(), 0, login_data.getLength());

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}