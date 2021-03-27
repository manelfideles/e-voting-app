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

            ThreadOps op = new ThreadOps();

            terminal_socket = new MulticastSocket(PORT);
            InetAddress terminals_group = InetAddress.getByName(TERMINALS);
            TerminalThread terminal_thread = new TerminalThread(terminals_group, terminal_socket, op);

            vote_socket = new MulticastSocket(PORT);
            InetAddress vote_group = InetAddress.getByName(VOTE);
            VotingThread voting_thread = new VotingThread(vote_group, vote_socket, op);
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
    ThreadOps op;

    public TerminalThread(InetAddress group, MulticastSocket s, ThreadOps op) {
        this.group = group;
        this.s = s;
        this.op = op;
        t = new Thread(this);
        t.start();
    }

    public void run() {
        try {
            s.joinGroup(group);
            while (true) {
                // envia req para terminals_group
                op.sendPacket("# req", s, group, PORT); // substituir por um protocolo de comunicação

                // Handshake
                DatagramPacket id_packet = op.receivePacket(s);
                String id_string = op.packetToString(id_packet);
                if (id_string.charAt(0) != '#') {
                    System.out.println("Terminal " + id_string + " replied.");
                    op.sendPacket("# " + id_string, s, group, PORT); // "Vai votar!"
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
    ThreadOps op;

    public VotingThread(InetAddress group, MulticastSocket s, ThreadOps op) {
        this.group = group;
        this.s = s;
        this.op = op;
        t = new Thread(this);
        t.start();
    }

    public boolean fetchVoter(String username, String password) {
        // metodo remoto, pede ao rmi para ir buscar user
        return true;
    }

    public void run() {
        try {
            s.joinGroup(group);
            String username = null, password = null, terminal_id = null;
            while (true) {
                DatagramPacket packet = op.receivePacket(s);

                String packet_string = op.packetToString(packet);
                String type_string = packet_string.substring(packet_string.indexOf("type"), packet_string.indexOf(";"));
                String type = type_string.split(" | ")[2];

                // Treat user login input
                if (packet_string.charAt(0) != '#') {
                    if (type.equals("login")) {
                        try {
                            String[] login_data = packet_string.split("; ");
                            terminal_id = login_data[0].substring(1, login_data[0].indexOf(" ") - 1);
                            username = login_data[1].split(" | ")[2];
                            password = login_data[2].split(" | ")[2];

                            // Logged in
                            if (fetchVoter(username, password) == true) {
                                System.out.println("[" + terminal_id + "]" + " User '" + username + "' logged in.");
                                s.leaveGroup(group);
                                op.sendPacket("# [" + terminal_id + "] type | status; logged | on", s, group, PORT);
                                s.joinGroup(group);
                            } else {
                                // recusa user
                            }
                        } catch (ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    } else if (type.equals("vote")) {
                        if (fetchVoter(username, password) == true) {
                            // Recebe Voto
                            String id = op.getIdFromPacket(packet);
                            if (id.equals(terminal_id)) {
                                System.out.println("Terminal " + id + "just voted.");
                            }

                            // Enviar para o rmi, que escreve na base de dados

                        }
                    }
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}