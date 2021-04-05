import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.*;

public class Session {
    private String path;
    private String[] sessionData = new String[2]; // {cc, password}
    private String cc;

    public Session(String cc) {
        this.cc = cc;
        this.path = cc + ".txt";
    }

    // public boolean restoreSession() {
    // // existe session file 'cc'.txt ?
    // File dir = new File("user.dir");
    // File[] matches = dir.listFiles(new FilenameFilter() {
    // public boolean accept(File dir, String filename) {
    // return filename.startsWith(cc) && filename.endsWith("txt");
    // }
    // });
    // if (matches.length == 1) {
    // // lê cenas que tem no file e mete no objeto Cliente
    // try {
    // Files.readAllLines(Paths.get(path));
    // //
    // } catch (Exception ex) {
    // ex.printStackTrace();
    // }
    // }
    // }

    // public void saveSession(String[] in) {
    // try {
    // FileWriter fw = new FileWriter(this.path);
    // PrintWriter pw = new PrintWriter(fw);
    // pw.println(in);
    // pw.close();
    // } catch (IOException ioe) {
    // ioe.printStackTrace();
    // }
    // }

    // public void destroySession() {
    // // apaga o ficheiro session dps de enviar o voto
    // }
}
