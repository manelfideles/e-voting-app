import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.*;
import java.util.List;

public class Session {

    private String path;
    FileWriter fw;

    /**
     * Session constructor. Creates new file if 'session.txt' is not present in
     * user.dir, otherwise this function will restore a previous session file.
     * 
     * @param path to session file
     * @throws IOException
     */
    public Session(String path) throws IOException {
        this.path = path;
        try {
            this.fw = new FileWriter(path, true);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public boolean create() throws IOException {
        File f = new File(path);
        if (f.createNewFile()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 
     * @return List with user info - cc + password
     * @throws IOException
     */
    public List<String> restore() throws IOException {
        if (new File(path).length() != 0)
            return Files.readAllLines(Paths.get(path));
        else
            return null;
    }

    /**
     * Appends data in 's' to session.txt file in user directory
     * 
     * @param s
     */
    public void save(String cc, String password) {
        PrintWriter pw = new PrintWriter(fw);
        pw.println(cc);
        pw.println(password);
        pw.close();
    }

    /**
     * 
     * @throws IOException
     */
    public void destroy() throws IOException {
        new PrintWriter(path).close();
    }
}
