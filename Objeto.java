import java.io.Serializable;

public class Objeto implements Serializable {
    HashMapPessoas hmp;
    HashMapEleicao hme;

    public Objeto(HashMapPessoas hmp, HashMapEleicao hme) {
        this.hmp = hmp;
        this.hme = hme;
    }

    public HashMapPessoas getEleicao() {
        return hmp;
    }

    public void setEleicao() {
        this.hmp = hmp;
    }

    public HashMapEleicao getLista_candidato() {
        return hme;
    }

    public void setLista_candidato() {
        this.hme = hme;
    }

    public String toString() {
        return "{hmp='" + this.hmp +
                "', hme='" + this.hme +
                "}";
    }
}
