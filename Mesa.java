import java.io.Serializable;
import java.util.HashMap;

public class Mesa implements Serializable {
    public String dep;
    public HashMap<String,Integer> num_eleitores;

    public Mesa(String dep) { this.dep = dep; }

    public String toString() {
        return dep + "";
    }

    public String getDep() {
        return dep;
    }

    public void setDep() {
        this.dep = dep;
    }

    public HashMap<String,Integer> getNum_eleitores() {
        return num_eleitores;
    }

    public void setNum_eleitores() {
        this.num_eleitores = num_eleitores;
    }

}