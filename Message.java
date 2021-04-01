import java.net.DatagramPacket;
import java.util.HashMap;
import java.util.Map;

public class Message {

    public Message() {
        ;
    }

    public String make(String sender, String type, String content) {
        return "[" + sender + "] " + "type | " + type + "; " + content;
    }

    public String makeList(HashMap<Integer, String> in) {
        String lista = "item_count | " + in.size();
        for (Map.Entry elem : in.entrySet()) {
            int k = (Integer) elem.getKey();
            String v = (String) elem.getValue();
            lista += "; item_" + (k - 1) + "_name | " + v;
        }
        return lista;
    }

    public String splitMakeList(String str) {
        int i;
        String[] data = str.split("item_count | ");
        char first = data[0].charAt(0); // num de listas
        int n = (int)(first);
        data[0].substring(0, 0); // eliminar o n da data[0]

        for (i=0; i<n; i++) {
            String regex = "; item_" + i + "_name | ";
            data = str.split(regex);
        }

        String s = String.join("\n", data);

        return s;
    }

    public String packetToString(DatagramPacket p) {
        return new String(p.getData(), 0, p.getLength());
    }

    public String getTypeFromPacket(DatagramPacket packet) {
        String str = packetToString(packet);
        String type_string = str.substring(str.indexOf("type"), str.indexOf(";"));
        return type_string.split(" | ")[2];
    }

    public String getSenderFromPacket(DatagramPacket packet) {
        String str = packetToString(packet);
        String[] data = str.split(" ");
        return data[0].substring(1, data[0].length() - 1);
    }

    public String getUserFromPacket(DatagramPacket packet) {
        String[] data = packetToString(packet).split("; ");
        return data[1].split(" | ")[2];
    }

    public String getPasswordFromPacket(DatagramPacket packet) {
        String[] data = packetToString(packet).split("; ");
        return data[2].split(" | ")[2];
    }

    public String getContentFromPacket(DatagramPacket packet, String regex) {
        String[] data = packetToString(packet).split(regex);
        /*for (String s : data) {
            System.out.println(s);
        }*/
        //System.out.println("getContentFromPacket: " + data[1]);
        return data[1];
    }

    public String getOpcaoEleicao(DatagramPacket packet, String regex) {
        String[] data = packetToString(packet).split(regex);
        /*for (String s : data) {
            System.out.println(s);
        }*/
        //System.out.println("getOpcaoEleicao: " + data[2]);
        return data[2];
    }
}
