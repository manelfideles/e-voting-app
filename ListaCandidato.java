import java.io.Serializable;
import java.util.ArrayList;

public class ListaCandidato implements Serializable {
    public String nome_lista;
    public String tipo_lista;
    public int num_pessoas_lista;
    public ArrayList<String> lista;
    public String nome_eleicao;
    public int num_votos;

    public ListaCandidato(String nome_lista,String tipo_lista, int num_pessoas_lista, ArrayList<String> lista, String nome_eleicao) {
        this.nome_lista = nome_lista;
        this.tipo_lista = tipo_lista;
        this.num_pessoas_lista = num_pessoas_lista;
        this.lista = lista;
        this.nome_eleicao = nome_eleicao;
    }

    public String toString() {
        return "{nome_lista='" + this.nome_lista +
                "', tipo_lista='" + this.tipo_lista +
                "', num_pessoas_lista='" + this.num_pessoas_lista +
                "', lista=" + this.lista +
                "', num_votos=" + this.num_votos +
                "}";
    }

    public String getNome_lista() {
        return this.nome_lista;
    }

    public String getTipo_lista() {
        return this.tipo_lista;
    }

    public Integer getNum_pessoas_lista() {
        return this.num_pessoas_lista;
    }

    public ArrayList<String> getLista() {
        return this.lista;
    }

    public String getNome_eleicao() {
        return this.nome_eleicao;
    }

    public void setNome_lista() {
        this.nome_lista = nome_lista;
    }

    public void setTipo_lista() {
        this.tipo_lista = tipo_lista;
    }

    public void setNum_pessoas_lista() {
        this.num_pessoas_lista = num_pessoas_lista;
    }

    public void setLista() {
        this.lista = lista;
    }

    public void setNome_eleicao() {
        this.nome_eleicao = nome_eleicao;
    }

    public Integer getNum_votos() {
        return this.num_votos;
    }

    public void setNum_votos() {
        this.num_votos = num_votos;
    }

}