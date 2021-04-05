import java.io.Serializable;
import java.util.HashMap;

public class Mesa implements Serializable {
    private static final long serialVersionUID = 1L;
    public String dep;
    public String mcastaddr;
    // falta referencia para o objeto remoto RemoteMulticastServerObj
    RemoteMulticastServerObj_Impl remoteServerObj;
    private boolean isOn = false;

    public HashMap<String, Integer> num_eleitores = new HashMap<>(); // {Nome da eleicao : Num de pessoas que ja votaram
                                                                     // nessa eleicao }

    public Mesa(String dep, RemoteMulticastServerObj_Impl remoteServerObj) {
        this.dep = dep;
        this.num_eleitores = num_eleitores;
        this.remoteServerObj = remoteServerObj;
    }

    public String toString() {
        return "Mesa:" + dep + "; State: " + isOn;
    }

    public String getDep() {
        return dep;
    }

    public void setDep(String dep) {
        this.dep = dep;
    }

    public void setState(boolean isOn) {
        this.isOn = isOn;
    }

    public HashMap<String, Integer> getNum_eleitores() {
        return num_eleitores;
    }

    public void setNum_eleitores(HashMap<String, Integer> num_eleitores) {
        this.num_eleitores = num_eleitores;
    }
}
