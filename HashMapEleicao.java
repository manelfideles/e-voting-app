import java.io.Serializable;
import java.util.HashMap;

public class HashMapEleicao implements Serializable {
    HashMap<String,Eleicao> mape;
    HashMap<String,Mesa> mapm;

    public HashMapEleicao(HashMap<String,Eleicao> mape, HashMap<String,Mesa> mapm) {
        this.mape = mape;
        this.mapm = mapm;
    }

    public String toString() {
        return "{eleicao=" + this.mape +
                ",\n mesa=" + this.mapm +
                "}";
    }

    /*public HashMap<String,Eleicao> getMape() {
        return mape;
    }

    public void setMape() {
        this.mape = mape;
    }

    public HashMap<String,ListaCandidato> getMapc() {
        return mapc;
    }

    public void setMapc() {
        this.mapc = mapc;
    }

    public HashMap<String,Mesa> getMapm() {
        return mapm;
    }

    public void setMapm() {
        this.mapm = mapm;
    }*/

}
