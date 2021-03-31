import java.net.DatagramPacket;

public class Message {

    public Message() {
        ;
    }

    public String make(String sender, String type, String content) {
        return "[" + sender + "] " + "type | " + type + "; " + content;
    }

    public String packetToString(DatagramPacket p) {
        return new String(p.getData(), 0, p.getLength());
    }

    public String getTypeFromPacket(DatagramPacket packet) {
        String packet_string = packetToString(packet);
        String type_string = packet_string.substring(packet_string.indexOf("type"), packet_string.indexOf(";"));
        return type_string.split(" | ")[2];
    }

    public String getSenderFromPacket(DatagramPacket packet) {
        String login_string = packetToString(packet);
        String[] login_data = login_string.split(" ");
        return login_data[0].substring(1, login_data[0].length() - 1);
    }

    public String getUserFromPacket(DatagramPacket packet) {
        String[] login_data = packetToString(packet).split("; ");
        return login_data[1].split(" | ")[2];
    }

    public String getPasswordFromPacket(DatagramPacket packet) {
        String[] login_data = packetToString(packet).split("; ");
        return login_data[2].split(" | ")[2];
    }

    public String getContentFromPacket(DatagramPacket packet, String regex) {
        String[] login_data = packetToString(packet).split(regex);
        System.out.println(login_data[2].split(" | ")[2]);
        return login_data[2].split(" | ")[2];
    }
}
