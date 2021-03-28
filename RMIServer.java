import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.*;
import java.rmi.server.*;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.ArrayList;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;

public class RMIServer extends UnicastRemoteObject implements RMIServer_I {
    static ArrayList<AdminConsole_I> admin_consoles = new ArrayList<AdminConsole_I>();

    // Pessoas
    HashMap<String,Pessoa> mapp = new HashMap<>();
    HashMap<String,HashMap<String,Pessoa>> hmp = new HashMap<>();

    // Eleicoes
    HashMap<String,Eleicao> mape = new HashMap<>();

    // Candidatos
    HashMap<String,ListaCandidato> mapc = new HashMap<>();

    // Mesas
    HashMap<String,Mesa> mapm = new HashMap<>();

    // Objeto
    HashMapPessoas hashmappessoas = new HashMapPessoas(hmp);
    HashMapEleicao hashmapeleicao = new HashMapEleicao(mape, mapc, mapm);
    Objeto objeto = new Objeto(hashmappessoas, hashmapeleicao);

    final static String outputFilePath = "C:\\Users\\Zen\\IdeaProjects\\e-voting\\fs.txt";

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

    public void regista_pessoa(Pessoa pessoa) throws RemoteException {
        System.out.println("RMI SERVER - regista_pessoa");

        mapp.put(pessoa.num_cc, new Pessoa(pessoa.funcao, pessoa.nome, pessoa.password, pessoa.dep_fac, pessoa.contacto, pessoa.morada, pessoa.num_cc, pessoa.val_cc));

        hmp.put("HashMapPessoas", mapp);

        File file = new File(outputFilePath);

        BufferedWriter bf = null;

        try {
            bf = new BufferedWriter(new FileWriter(file));

            bf.write(objeto.toString());

            bf.flush();

        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try { bf.close(); }
            catch (Exception e) {}
        }
    }

    public void cria_eleicao(Eleicao eleicao) throws RemoteException {
        System.out.println("RMI SERVER - cria_eleicao");

        mape.put(eleicao.titulo, new Eleicao(eleicao.data_i, eleicao.data_i, eleicao.minuto_i, eleicao.data_f, eleicao.hora_f, eleicao.minuto_f, eleicao.titulo, eleicao.descricao, eleicao.restricao));

        File file = new File(outputFilePath);

        BufferedWriter bf = null;

        try {
            bf = new BufferedWriter(new FileWriter(file));

            bf.write(objeto.toString());

            bf.flush();

        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try { bf.close(); }
            catch (Exception e) {}
        }
    }

    public void altera_eleicao(Eleicao eleicao) throws RemoteException {
        System.out.println("RMI SERVER - altera_eleicao");
    }

    public void cria_lista_candidatos(ListaCandidato lista_candidato) throws RemoteException {
        System.out.println("RMI SERVER - cria_lista_candidatos");

        mapc.put(lista_candidato.nome_lista, new ListaCandidato(lista_candidato.nome_lista, lista_candidato.tipo_lista, lista_candidato.num_pessoas_lista, lista_candidato.lista));

        File file = new File(outputFilePath);

        BufferedWriter bf = null;

        try {
            bf = new BufferedWriter(new FileWriter(file));

            bf.write(objeto.toString());

            bf.flush();

        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try { bf.close(); }
            catch (Exception e) {}
        }
    }

    public void remove_lista_candidatos(ListaCandidato lista_candidato) throws RemoteException {
        System.out.println("RMI SERVER - remove_lista_candidatos");
    }

    public void cria_mesa(Mesa mesa) throws RemoteException {
        System.out.println("RMI SERVER - cria_mesa");

        mapm.put(mesa.dep, new Mesa(mesa.dep));

        File file = new File(outputFilePath);

        BufferedWriter bf = null;

        try {
            bf = new BufferedWriter(new FileWriter(file));

            bf.write(objeto.toString());

            bf.flush();

        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try { bf.close(); }
            catch (Exception e) {}
        }
    }

    public void remove_mesa(Mesa mesa) throws RemoteException {
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

        } catch (Exception re) {
            System.out.println("Exception in HelloImpl.main: " + re);
        }
    }
}
