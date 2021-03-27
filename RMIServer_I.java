import java.rmi.*;
import java.util.ArrayList;

public interface RMIServer_I extends Remote {
    public void print_on_rmi_server(String s) throws RemoteException;
    public void subscribe(String name, AdminConsole_I ac) throws RemoteException;
    public void regista_pessoa(String nome, String funcao, String password, String dep_fac, String contacto, String morada, String num_cc, String val_cc) throws RemoteException;
    public void cria_eleicao(String data_i, String hora_i, String minuto_i, String data_f, String hora_f, String minuto_f, String titulo, String descricao, String restricao) throws RemoteException;
    public void altera_eleicao(String data_i, String hora_i, String minuto_i, String data_f, String hora_f, String minuto_f, String titulo, String descricao, String restricao) throws RemoteException;
    public void cria_lista_candidatos(String nome_lista, String tipo_lista, int num_pessoas_lista, ArrayList<String> lista) throws RemoteException;
    public void remove_lista_candidatos(String nome_lista, String tipo_lista) throws RemoteException;
    public void altera_lista_candidatos(String nome_lista, String tipo_lista, String novo_nome_lista, int num_pessoas_lista, ArrayList<String> lista) throws RemoteException;
    public void cria_mesa(String dep) throws RemoteException;
    public void remove_mesa(String dep) throws RemoteException;
    public void consulta_estado_mesas() throws RemoteException;
    public void consulta_info_voto() throws RemoteException;
    public void consulta_eleitores() throws RemoteException;
    public void consulta_resultados() throws RemoteException;
}
 