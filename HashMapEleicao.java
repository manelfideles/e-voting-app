import java.io.Serializable;
import java.util.HashMap;

public class HashMapEleicao implements Serializable {
    Eleicao eleicao;
    ListaCandidato lista_candidato;
    Mesa mesa;

    public HashMapEleicao(Eleicao eleicao, ListaCandidato lista_candidato, Mesa mesa) {
        this.eleicao = eleicao;
        this.lista_candidato = lista_candidato;
        this.mesa = mesa;
    }

    public Eleicao getEleicao() {
        return eleicao;
    }

    public void setEleicao() {
        this.eleicao = eleicao;
    }

    public ListaCandidato getLista_candidato() {
        return lista_candidato;
    }

    public void setLista_candidato() {
        this.lista_candidato = lista_candidato;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa() {
        this.mesa = mesa;
    }

    public String toString() {
        return "{eleicao='" + this.eleicao +
                "', lista_candidato='" + this.lista_candidato +
                "', mesa='" + this.mesa +
                "}";
    }
}
