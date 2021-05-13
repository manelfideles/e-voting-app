import java.io.Serializable;
import java.util.HashMap;

public class Objeto implements Serializable {
    private static final long serialVersionUID = 1L;
    HashMapPessoas hmp;
    HashMapEleicao hme;

    public Objeto() {

        // Pessoas
        HashMap<String, Pessoa> mapp = new HashMap<>();
        HashMap<String, HashMap<String, Pessoa>> hmp = new HashMap<>();
        hmp.put("HashMapPessoas", mapp);

        // Eleicoes
        HashMap<String, Eleicao> mape = new HashMap<>();

        // Mesas
        HashMap<String, Mesa> mapm = new HashMap<>();
        {
            mapm.put("DARQ", new Mesa("DARQ", null));
            mapm.put("DCT", new Mesa("DCT", null));
            mapm.put("DEC", new Mesa("DEC", null));
            mapm.put("DEEC", new Mesa("DEEC", null));
            mapm.put("DEI", new Mesa("DEI", null));
            mapm.put("DEM", new Mesa("DEM", null));
            mapm.put("DEQ", new Mesa("DEQ", null));
            mapm.put("DF", new Mesa("DF", null));
            mapm.put("DM", new Mesa("DM", null));
            mapm.put("DQ", new Mesa("DQ", null));
            mapm.put("FLUC", new Mesa("FLUC", null));
            mapm.put("FDUC", new Mesa("FDUC", null));
            mapm.put("FMUC", new Mesa("FMUC", null));
            mapm.put("FFUC", new Mesa("FFUC", null));
            mapm.put("FEUC", new Mesa("FEUC", null));
            mapm.put("FPCEUC", new Mesa("FPCEUC", null));
            mapm.put("FCDEFUC", new Mesa("FCDEFUC", null));
            mapm.put("CdA", new Mesa("CdA", null));
        }

        // Objeto
        HashMapPessoas hashmappessoas = new HashMapPessoas(hmp);
        HashMapEleicao hashmapeleicao = new HashMapEleicao(mape, mapm);

        this.hmp = hashmappessoas;
        this.hme = hashmapeleicao;

    }

    public String toString() {
        return this.hmp + "\n" + this.hme;
    }

    public HashMapPessoas getHMP() {
        return hmp;
    }

    public HashMapEleicao getHME() {
        return hme;
    }
}
