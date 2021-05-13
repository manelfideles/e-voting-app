import java.io.Serializable;
import java.util.HashMap;

public class HashMapPessoas implements Serializable {
    private static final long serialVersionUID = 1L;
    HashMap<String, HashMap<String, Pessoa>> hmp;

    public HashMapPessoas(HashMap<String, HashMap<String, Pessoa>> hmp) {
        this.hmp = hmp;
    }

    public String toString() {
        return hmp + "\n";
    }

    public HashMap<String, HashMap<String, Pessoa>> getHashMapPessoas() {
        return hmp;
    }
}
