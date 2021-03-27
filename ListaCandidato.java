import java.io.Serializable;
import java.util.ArrayList;

public class ListaCandidato implements Serializable {
    public String nome_lista;
    public String tipo_lista;
    public String nome_pessoa;
    public int num_pessoas_lista;
    public ArrayList<String> lista = new ArrayList<String>();

    public ListaCandidato(String nome_lista,String tipo_lista, int num_pessoas_lista, ArrayList<String> lista) {
        this.nome_lista = nome_lista;
        this.tipo_lista = tipo_lista;
        this.num_pessoas_lista = num_pessoas_lista;
        this.lista = lista;
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

    public void setNome_lista() {
        this.nome_lista = this.nome_lista;
    }

    public void setTipo_lista() {
        this.tipo_lista = this.tipo_lista;
    }

    public void setNum_pessoas_lista() {
        this.num_pessoas_lista = this.num_pessoas_lista;
    }

    public void setLista() {
        this.lista = this.lista;
    }

    public String toString() {
        return "{nome_lista='" + this.nome_lista +
                "', tipo_lista='" + this.tipo_lista +
                "', num_pessoas_lista='" + this.num_pessoas_lista +
                "', lista=" + this.lista +
                "}";
    }
}