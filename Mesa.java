import java.io.Serializable;
import java.util.HashMap;

public class Mesa implements Serializable {
    private static final long serialVersionUID = 1L;
    public String dep;
    public HashMap<String, Integer> num_eleitores = new HashMap<>(); // {Nome do departamento : Num de pessoas que ja votaram nesse dep }

    public Mesa(String dep) {
        this.dep = dep;
    }

    public String toString() {
        return dep + "";
    }

    public String getDep() {
        return dep;
    }

    public void setDep(String dep) {
        this.dep = dep;
    }

    public HashMap<String, Integer> getNum_eleitores() {
        return num_eleitores;
    }

    public void setNum_eleitores(HashMap<String, Integer> num_eleitores) {
        this.num_eleitores = num_eleitores;
    }

}