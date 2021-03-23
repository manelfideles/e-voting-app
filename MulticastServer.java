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
            terminal_socket = new MulticastSocket(PORT);
            InetAddress terminals_group = InetAddress.getByName(TERMINALS);
            TerminalThread terminal_thread = new TerminalThread(terminals_group, terminal_socket);

            vote_socket = new MulticastSocket(PORT);
            InetAddress vote_group = InetAddress.getByName(VOTE);
            VotingThread voting_thread = new VotingThread(vote_group, vote_socket);
            while (true) {
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
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            terminal_socket.close();
            vote_socket.close();
        }
    }
}

class TerminalThread extends Thread {
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
            s.joinGroup(this.group);
            while (true) {
                // envia req para terminals_group
                this.sendPacket("# req", PORT); // substituir por um protocolo de comunicação

                DatagramPacket reply_packet = this.receivePacket();
                String reply_string = new String(reply_packet.getData(), 0, reply_packet.getLength());

                if (reply_string.charAt(0) != '#') {
                    System.out.println("Terminal " + reply_string + " replied.");

                    // o cliente receber o seu proprio uuid é o mesmo que mandar ir votar
                    this.sendPacket("# " + reply_string, PORT);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class VotingThread extends Thread {
    private int PORT = 4321;
    InetAddress group;
    MulticastSocket s;
    Thread t;

    public VotingThread(InetAddress group, MulticastSocket s) {
        this.group = group;
        this.s = s;
        t = new Thread(this);
        t.start();
    }

    public boolean fetchVoter(String username, String password) {
        // metodo remoto, pede ao rmi para ir buscar user
        return true;
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
                DatagramPacket login_packet = this.receivePacket();
                String login_string = new String(login_packet.getData(), 0, login_packet.getLength());

                String[] login_data = login_string.split("; ");
                String terminal_id = login_data[0].substring(1, login_data[0].length() - 1);
                String username = login_data[2].split(" | ")[2];
                String password = login_data[3].split(" | ")[2];

                // verifica user, pede ao rmi para autenticar - fetchVoter()
                if (fetchVoter(username, password) == true) {
                    System.out.println("[" + terminal_id + "]" + " User '" + username + "' logged in.");
                    this.sendPacket("# [" + terminal_id + "]; type | status; logged | on", PORT);
                    DatagramPacket vote_packet = this.receivePacket();
                    String vote_string = new String(vote_packet.getData(), 0, vote_packet.getLength());
                    if (vote_string.charAt(0) != '#') {
                        System.out.println("Vote: TINO DE RANS");
                    }
                } else {
                    // recusa user
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}