import java.io.Serializable;
import java.util.ArrayList;

public class Pessoa implements Serializable {
    public String nome;
    public String funcao;
    public String password;
    public String dep_fac;
    public String contacto;
    public String morada;
    public String num_cc;
    public String val_cc;

    public Pessoa(String nome, String funcao, String password, String dep_fac, String contacto, String morada, String num_cc, String val_cc) {
        this.nome = nome;
        this.funcao = funcao;
        this.password = password;
        this.dep_fac = dep_fac;
        this.contacto = contacto;
        this.morada = morada;
        this.num_cc = num_cc;
        this.val_cc = val_cc;
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

    public String getDep_Fac() {
        return dep_fac;
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

    public void setNome() {
        this.nome = nome;
    }

    public void setFuncao() {
        this.funcao = funcao;
    }

    public void setPassword() {
        this.password = password;
    }

    public void setDep_Fac() {
        this.dep_fac = dep_fac;
    }

    public void setContacto() {
        this.contacto = contacto;
    }

    public void setMorada() {
        this.morada = morada;
    }

    public void setNum_CC() {
        this.num_cc = num_cc;
    }

    public void setVal_CC() {
        this.val_cc = val_cc;
    }

    public String toString() {
        return "{" +
               "nome='" + nome + '\'' +
               ", funcao='" + funcao + '\'' +
               ", password='" + password + '\'' +
               ", dep_fac='" + dep_fac + '\'' +
               ", contacto='" + contacto + '\'' +
               ", morada='" + morada + '\'' +
               ", num_cc='" + num_cc + '\'' +
               ", val_cc='" + val_cc + '\'' +
               '}';
    }
}
