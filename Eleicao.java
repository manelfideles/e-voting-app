import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;

public class Eleicao implements Serializable {
    private static final long serialVersionUID = 1L;
    public int ano_i;
    public int mes_i;
    public int dia_i;
    public int hora_i;
    public int minuto_i;
    public int ano_f;
    public int mes_f;
    public int dia_f;
    public int hora_f;
    public int minuto_f;
    public Date date_i;
    public Date date_f;
    public String titulo;
    public String descricao;
    public String restricao;
    public String old_titulo;
    public ArrayList<HashMap<String, ListaCandidato>> lista_lista_candidato;
    public int num_votos_branco;
    public int num_votos_nulo;
    public int num_total_votos;

    public Eleicao(int ano_i, int mes_i, int dia_i, int hora_i, int minuto_i, int ano_f, int mes_f, int dia_f,
                   int hora_f, int minuto_f, String titulo, String descricao, String restricao, String old_titulo,
                   ArrayList<HashMap<String, ListaCandidato>> lista_lista_candidato, Date date_i, Date date_f) {
        this.ano_i = ano_i;
        this.mes_i = mes_i;
        this.dia_i = dia_i;
        this.hora_i = hora_i;
        this.minuto_i = minuto_i;
        this.ano_f = ano_f;
        this.mes_f = mes_f;
        this.dia_f = dia_f;
        this.hora_f = hora_f;
        this.minuto_f = minuto_f;
        this.titulo = titulo;
        this.descricao = descricao;
        this.restricao = restricao;
        this.old_titulo = old_titulo;
        this.lista_lista_candidato = lista_lista_candidato;
        this.date_i = date_i;
        this.date_f = date_f;
    }

    public String toString() {
        return "{" + "ano_i='" + ano_i + '\'' + ", mes_i='" + mes_i + '\'' + ", dia_i='" + dia_i + '\'' + ", hora_i='"
                + hora_i + '\'' + ", minuto_i='" + minuto_i + '\'' + ", ano_f='" + ano_f + '\'' + ", mes_f='" + mes_f
                + '\'' + ", dia_f='" + dia_f + '\'' + ", hora_f='" + hora_f + '\'' + ", minuto_f='" + minuto_f + '\''
                + ", titulo='" + titulo + '\'' + ", descricao='" + descricao + '\'' + ", restricao='" + restricao + '\''
                + ", lista_lista_candidato='" + lista_lista_candidato + '\'' + ", num_votos_branco='" + num_votos_branco
                + '\'' + ", num_votos_nulo='" + num_votos_nulo + '\'' + ", num_total_votos='" + num_total_votos + '\''
                + ", date_i='" + date_i + '\'' + ", date_f='" + date_f + '\'' + '}';
    }

    public ArrayList<HashMap<String, ListaCandidato>> getListaListaCandidato() {
        return this.lista_lista_candidato;
    }

    public int getAno_i() {
        return ano_i;
    }

    public int getMes_i() {
        return mes_i;
    }

    public int getDia_i() {
        return dia_i;
    }

    public int getHora_i() {
        return hora_i;
    }

    public int getMinuto_i() {
        return minuto_i;
    }

    public int getAno_f() {
        return ano_f;
    }

    public int getMes_f() {
        return mes_f;
    }

    public int getDia_f() {
        return dia_f;
    }

    public int getHora_f() {
        return hora_f;
    }

    public int getMinuto_f() {
        return minuto_f;
    }

    public Date getDate_i() {
        return date_i;
    }

    public Date getDate_f() {
        return date_f;
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

    public Integer getNum_votos_branco() {
        return num_votos_branco;
    }

    public Integer getNum_votos_nulo() {
        return num_votos_nulo;
    }

    public Integer getNum_total_votos() {
        return num_total_votos;
    }
}
