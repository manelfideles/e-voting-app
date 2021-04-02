import java.rmi.*;
import java.util.HashMap;
import java.util.*;

public interface RMIServer_I extends Remote {
    public void print_on_rmi_server(String s) throws RemoteException;

    public void subscribe(String name, AdminConsole_I ac) throws RemoteException;

    public void regista_pessoa(Pessoa pessoa) throws RemoteException;

    public void cria_eleicao(Eleicao eleicao) throws RemoteException;

    public boolean check_eleicao_before(String old_titulo) throws RemoteException;

    public boolean check_eleicao_after(String old_titulo) throws RemoteException;

    public boolean check_eleicao_voto(String old_titulo) throws RemoteException;

    public boolean check_consulta_resultados(String old_titulo) throws RemoteException;

    public void altera_eleicao(Eleicao eleicao) throws RemoteException;

    public void cria_lista_candidatos(ListaCandidato lista_candidato) throws RemoteException;

    public void remove_lista_candidatos(ListaCandidato lista_candidato) throws RemoteException;

    // public void cria_mesa(Mesa mesa) throws RemoteException;

    // public void remove_mesa(Mesa mesa) throws RemoteException;

    // public void consulta_estado_mesas() throws RemoteException;

    public HashMap<String, HashMap<String, Pessoa>> consulta_info_voto() throws RemoteException;

    public HashMap<String, Mesa> consulta_eleitores() throws RemoteException;

    public HashMap<String, Eleicao> consulta_resultados() throws RemoteException;

    public HashMap<Integer, Eleicao> getBulletin(Pessoa p) throws RemoteException;

    public void WriteObjectToFile(Object obj) throws RemoteException;

    public Object ReadObjectFromFile(String outputFilePath) throws RemoteException;

    public Pessoa getVoter(String cc) throws RemoteException;

    public Mesa getMesaByDep(String dep) throws RemoteException;

    public void printMesasExistentes() throws RemoteException;

    public void subscribeMesa(String dep, RemoteMulticastServerObj_Impl remoteServerObj) throws RemoteException;

    public void checkActiveMesas(AdminConsole_I ac) throws RemoteException;

    public void ping(Mesa m, AdminConsole_I ac) throws RemoteException;

    public void atualiza(String num_cc, String nome_lista, String nome_eleicao) throws RemoteException;

    public HashMap<Integer, String> getListasFromEleicaoEscolhida(Eleicao e) throws RemoteException;

}
