import java.rmi.*;

public interface RMIServer_I extends Remote {
    public void print_on_rmi_server(String s) throws RemoteException;

    public void subscribe(String name, AdminConsole_I ac) throws RemoteException;

    public void regista_pessoa(Pessoa pessoa) throws RemoteException;

    public void cria_eleicao(Eleicao eleicao) throws RemoteException;

    public void altera_eleicao(Eleicao eleicao) throws RemoteException;

    public void cria_lista_candidatos(ListaCandidato lista_candidato) throws RemoteException;

    public void remove_lista_candidatos(ListaCandidato lista_candidato) throws RemoteException;

    public void cria_mesa(Mesa mesa) throws RemoteException;

    public void remove_mesa(Mesa mesa) throws RemoteException;

    public void consulta_estado_mesas() throws RemoteException;

    public void consulta_info_voto() throws RemoteException;

    public void consulta_eleitores() throws RemoteException;

    public void consulta_resultados() throws RemoteException;

    public void boletim_voto() throws RemoteException;
}
 