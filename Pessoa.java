import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Pessoa implements Serializable {
    private static final long serialVersionUID = 1L;

    public String nome;
    public String funcao;
    public String password;
    public String dep;
    public String contacto;
    public String morada;
    public String num_cc;
    public String val_cc;
    public HashMap<String,String> local_momento_voto; // {"nome da eleição" = "DEI 27/05/2022 18:56"}

    public Pessoa(String nome, String funcao, String password, String dep, String contacto, String morada,
            String num_cc, String val_cc) {
        this.nome = nome;
        this.funcao = funcao;
        this.password = password;
        this.dep = dep;
        this.contacto = contacto;
        this.morada = morada;
        this.num_cc = num_cc;
        this.val_cc = val_cc;
    }

    public String toString() {
        return "{" + "nome='" + nome + '\'' + ", funcao='" + funcao + '\'' + ", password='" + password + '\''
                + ", dep='" + dep + '\'' + ", contacto='" + contacto + '\'' + ", morada='" + morada + '\''
                + ", num_cc='" + num_cc + '\'' + ", val_cc='" + val_cc + '\'' + '}';
    }

    public String getNome() {
        return nome;
    }

    public String getFuncao() {
        return funcao;
    }

    public String getPassword() {
        return password;
    }

    public String getDep() {
        return dep;
    }

    public String getContacto() {
        return contacto;
    }

    public String getMorada() {
        return morada;
    }

    public String getNum_CC() {
        return num_cc;
    }

    public String getVal_CC() {
        return val_cc;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDep() {
        this.dep = dep;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public void setMorada(String morada) {
        this.morada = morada;
    }

    public void setNum_CC(String num_cc) {
        this.num_cc = num_cc;
    }

    public void setVal_CC(String val_cc) {
        this.val_cc = val_cc;
    }

    public HashMap<String,String> getLocal_momento_voto() {
        return local_momento_voto;
    }

    public void setLocal_momento_voto() {
        this.local_momento_voto = local_momento_voto;
    }

}
