import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Eleicao implements Serializable {
    public String data_i;
    public String hora_i;
    public String minuto_i;
    public String data_f;
    public String hora_f;
    public String minuto_f;
    public String titulo;
    public String descricao;
    public String restricao;
    public String old_titulo;
    public ArrayList<HashMap<String,ListaCandidato>> lista_lista_candidato;

    public Eleicao(String data_i, String hora_i, String minuto_i, String data_f, String hora_f, String minuto_f, String titulo, String descricao, String restricao, String old_titulo, ArrayList<HashMap<String,ListaCandidato>> lista_lista_candidato) {
        this.data_i = data_i;
        this.hora_i = hora_i;
        this.minuto_i = minuto_i;
        this.data_f = data_f;
        this.hora_f = hora_f;
        this.minuto_f = minuto_f;
        this.titulo = titulo;
        this.descricao = descricao;
        this.restricao = restricao;
        this.old_titulo = old_titulo;
        this.lista_lista_candidato = lista_lista_candidato;
    }

    public String toString() {
        return "{" +
                "data_i='" + data_i + '\'' +
                ", hora_i='" + hora_i + '\'' +
                ", minuto_i='" + minuto_i + '\'' +
                ", data_f='" + data_f + '\'' +
                ", hora_f='" + hora_f + '\'' +
                ", minuto_f='" + minuto_f + '\'' +
                ", titulo='" + titulo + '\'' +
                ", descricao='" + descricao + '\'' +
                ", restricao='" + restricao + '\'' +
                ", lista_lista_candidato='" + lista_lista_candidato + '\'' +
                '}';
    }

    /*public String getData_i() {
        return data_i;
    }

    public String getHora_i() {
        return hora_i;
    }

    public String getMinuto_i() {
        return minuto_i;
    }

    public String getData_f() {
        return data_f;
    }

    public String getHora_f() {
        return hora_f;
    }

    public String getMinuto_f() {
        return minuto_f;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getRestricao() {
        return restricao;
    }

    public void setData_i() {
        this.data_i = data_i;
    }

    public void setHora_i() {
        this.hora_i = hora_i;
    }

    public void setMinuto_i() {
        this.minuto_i = minuto_i;
    }

    public void setData_f() {
        this.data_f = data_f;
    }

    public void setHora_f() {
        this.hora_f = hora_f;
    }

    public void setMinuto_f() {
        this.minuto_f = minuto_f;
    }

    public void setTitulo() {
        this.titulo = titulo;
    }

    public void setDescricao() {
        this.descricao = descricao;
    }

    public void setRestricao() {
        this.restricao = restricao;
    }*/

}