import java.io.Serializable;

public class Objeto implements Serializable {
    HashMapPessoas hmp;
    HashMapEleicao hme;

    public Objeto(HashMapPessoas hmp, HashMapEleicao hme) {
        this.hmp = hmp;
        this.hme = hme;
    }

    public String toString() {
        return this.hmp + "\n" + this.hme;
    }

    public HashMapPessoas getHMP() {
        return hmp;
    }

    public void setHMP() {
        this.hmp = hmp;
    }

    public HashMapEleicao getHME() {
        return hme;
    }

    public void setHME() {
        this.hme = hme;
    }

}
