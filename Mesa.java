import java.io.Serializable;
import java.util.ArrayList;

public class Mesa implements Serializable {
    public String dep;

    public Mesa(String dep) { this.dep = dep; }

    public String getDep() {
        return dep;
    }

    public void setDep() {
        this.dep = dep;
    }

    public String toString() {
        return "{" +
                "dep='" + dep + '\'' +
                '}';
    }
}