import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.*;
import java.rmi.server.*;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class RMIServer extends UnicastRemoteObject implements RMIServer_I {
    static ArrayList<AdminConsole_I> admin_consoles = new ArrayList<AdminConsole_I>();

    public RMIServer() throws RemoteException {
        super();
    }

    public synchronized void print_on_rmi_server(String s) throws RemoteException {
        System.out.println("PRINT ON RMI SERVER\n> " + s);
    }

    public void subscribe(String name, AdminConsole_I ac) throws RemoteException {
        System.out.println("Subscribing " + name);
        admin_consoles.add(ac);
        System.out.println("Just added AdminConsole to admin_consoles");
    }

    public void regista_pessoa(String nome, String funcao, String password, String dep_fac, String contacto, String morada, String num_cc, String val_cc) throws RemoteException {
        System.out.println("RMI SERVER - regista_pessoa");
    }

    public void cria_eleicao(String data_i, String hora_i, String minuto_i, String data_f, String hora_f, String minuto_f, String titulo, String descricao, String restricao) throws RemoteException {
        System.out.println("RMI SERVER - cria_eleicao");
    }

    public void altera_eleicao(String data_i, String hora_i, String minuto_i, String data_f, String hora_f, String minuto_f, String titulo, String descricao, String restricao) throws RemoteException {
        System.out.println("RMI SERVER - altera_eleicao");
    }

    public void cria_lista_candidatos(String nome_lista, String tipo_lista, int num_pessoas_lista, ArrayList<String> lista) throws RemoteException {
        System.out.println("RMI SERVER - cria_lista_candidatos");
    }

    public void remove_lista_candidatos(String nome_lista, String tipo_lista) throws RemoteException {
        System.out.println("RMI SERVER - remove_lista_candidatos");
    }

    public void altera_lista_candidatos(String nome_lista, String tipo_lista, String novo_nome_lista, int num_pessoas_lista, ArrayList<String> lista) throws RemoteException {
        System.out.println("RMI SERVER - altera_lista_candidatos");
    }

    public void cria_mesa(String dep) throws RemoteException {
        System.out.println("RMI SERVER - cria_mesa");
    }

    public void remove_mesa(String dep) throws RemoteException {
        System.out.println("RMI SERVER - remove_mesa");
    }

    public void consulta_estado_mesas() throws RemoteException {
        System.out.println("RMI SERVER - consulta_estado_mesas");
    }

    public void consulta_info_voto() throws RemoteException {
        System.out.println("RMI SERVER - consulta_info_voto");
    }

    public void consulta_eleitores() throws RemoteException {
        System.out.println("RMI SERVER - consulta_eleitores");
    }

    public void consulta_resultados() throws RemoteException {
        System.out.println("RMI SERVER - consulta_resultados");
    }

    public static void main(String args[]) {
        String s;

        //System.getProperties().put("java.security.policy", "policy.all");
        //System.setSecurityManager(new RMISecurityManager());

        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        try {
            RMIServer rmis = new RMIServer();
            Registry r = LocateRegistry.createRegistry(6969);
            r.rebind("RMI_Server", rmis);
            System.out.println("Hello Server ready.");

            /*while (true) {

                System.out.println("> Introduza word para print_on_admin_console");
                s = reader.readLine();

                for (AdminConsole_I ac : admin_consoles) {
                    ac.print_on_admin_console(s);
                }
            }*/
        } catch (Exception re) {
            System.out.println("Exception in HelloImpl.main: " + re);
        }
    }
}
