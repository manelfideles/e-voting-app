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
}