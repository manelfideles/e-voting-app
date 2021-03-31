import java.util.Scanner;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class RMIServer extends UnicastRemoteObject implements RMIServer_I {
    private static final long serialVersionUID = 1L;

    // final static String outputFilePath =
    // "C:\\Users\\Zen\\IdeaProjects\\e-voting\\fs.txt";
    final static String outputFilePath = "fs.txt";
    static private int PORT_r = 6969;

    // Admin Consoles
    static ArrayList<AdminConsole_I> admin_consoles = new ArrayList<>();

    // Pessoas
    HashMap<String, Pessoa> mapp = new HashMap<>();
    HashMap<String, HashMap<String, Pessoa>> hmp = new HashMap<>();

    // Eleicoes
    HashMap<String, Eleicao> mape = new HashMap<>();

    // Mesas
    HashMap<String, Mesa> mapm = new HashMap<>();

    // Objeto
    HashMapPessoas hashmappessoas = new HashMapPessoas(hmp);
    HashMapEleicao hashmapeleicao = new HashMapEleicao(mape, mapm);
    Objeto objeto = new Objeto(hashmappessoas, hashmapeleicao);

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
        mapp.put(pessoa.num_cc, new Pessoa(pessoa.nome, pessoa.funcao, pessoa.password, pessoa.dep, pessoa.contacto,
                pessoa.morada, pessoa.num_cc, pessoa.val_cc));
        hmp.put("HashMapPessoas", mapp);
        WriteObjectToFile(objeto);
    }

    public void cria_eleicao(Eleicao eleicao) throws RemoteException {
        System.out.println("RMI SERVER - cria_eleicao");
        mape.put(eleicao.titulo,
                new Eleicao(eleicao.data_i, eleicao.data_i, eleicao.minuto_i, eleicao.data_f, eleicao.hora_f,
                        eleicao.minuto_f, eleicao.titulo, eleicao.descricao, eleicao.restricao, "",
                        eleicao.lista_lista_candidato));
        WriteObjectToFile(objeto);
    }

    public void altera_eleicao(Eleicao eleicao) throws RemoteException {
        System.out.println("RMI SERVER - altera_eleicao");
        mape.replace(eleicao.old_titulo,
                new Eleicao(eleicao.data_i, eleicao.data_i, eleicao.minuto_i, eleicao.data_f, eleicao.hora_f,
                        eleicao.minuto_f, eleicao.titulo, eleicao.descricao, eleicao.restricao, eleicao.old_titulo,
                        eleicao.lista_lista_candidato));
        mape.put(eleicao.titulo, mape.remove(eleicao.old_titulo));
        WriteObjectToFile(objeto);
    }

    public void cria_lista_candidatos(ListaCandidato lista_candidato) throws RemoteException {
        System.out.println("RMI SERVER - cria_lista_candidatos");
        for (Map.Entry mapElement : mape.entrySet()) {
            Eleicao e = (Eleicao) mapElement.getValue();
            if (e.titulo.equals(lista_candidato.nome_eleicao)) {
                HashMap<String, ListaCandidato> hm = new HashMap<>();
                hm.put(lista_candidato.nome_lista, lista_candidato);
                e.lista_lista_candidato.add(hm);
            }
        }
        WriteObjectToFile(objeto);
    }

    public void remove_lista_candidatos(ListaCandidato lista_candidato) throws RemoteException {
        System.out.println("RMI SERVER - remove_lista_candidatos");
        int i = 0;
        for (Map.Entry mapElement : mape.entrySet()) {
            Eleicao e = (Eleicao) mapElement.getValue();
            if (e.titulo.equals(lista_candidato.nome_eleicao)) {
                for (HashMap<String, ListaCandidato> elem : e.lista_lista_candidato) {
                    for (Map.Entry mapElement2 : elem.entrySet()) {
                        String lc = (String) mapElement2.getKey();
                        if (lista_candidato.nome_lista.equals(lc)) {
                            e.lista_lista_candidato.remove(i);
                            break;
                        }
                        i++;
                    }
                    break;
                }
            }
        }

        WriteObjectToFile(objeto);
    }

    public void cria_mesa(Mesa mesa) throws RemoteException {
        System.out.println("RMI SERVER - cria_mesa");
        mapm.put(mesa.dep, new Mesa(mesa.dep));

        WriteObjectToFile(objeto);
    }

    public void remove_mesa(Mesa mesa) throws RemoteException {
        System.out.println("RMI SERVER - remove_mesa");
        mapm.remove(mesa.dep);

        WriteObjectToFile(objeto);
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

    public HashMap<String, Eleicao> consulta_resultados() throws RemoteException {
        System.out.println("RMI SERVER - consulta_resultados");
        return mape;
    }

    public HashMap<Integer, Eleicao> getBulletin(Pessoa p) throws RemoteException {
        int i = 1;
        HashMap<Integer, Eleicao> hme = new HashMap<>();
        // HashMap<Integer, ListaCandidato> hmlc = new HashMap<>(); // so no terminal

        // Popular
        for (Map.Entry mapElement : mape.entrySet()) {
            Eleicao e = (Eleicao) mapElement.getValue();
            if (!p.getVerifica_eleicoes().contains(e.titulo)) {
                if (e.getDescricao().equals(p.funcao)
                        && (e.getRestricao().equals(p.dep) || e.getRestricao().equals("0"))) {
                    hme.put(i, e);
                    i++;
                }
            }
        }
        return hme;

        // if (!resultado.equals("")) {
        // System.out.print("Selecione a eleição na qual pretende exercer o seu voto:");
        // System.out.println(resultado);
        // System.out.print("Escolha: ");

        // opcao_eleicao = Integer.parseInt(scanner.nextLine());

        // Eleicao eleicao = hme.get(opcao_eleicao);

        // resultado = "";

        // // Percorrer a ArrayList das listas

        // for (HashMap<String, ListaCandidato> llc : eleicao.lista_lista_candidato) {

        // for (Entry<String, ListaCandidato> entry : llc.entrySet()) {
        // resultado = resultado.concat("\n" + j + " - " + entry.getKey());
        // hmlc.put(j, entry.getValue());
        // j++;
        // }
        // }
        // i++;
        // }
    }

    public Pessoa getVoter(String cc) throws RemoteException {
        // query à BD para ver se existe uma pessoa com 'cc'.
        return mapp.get(cc); // 2019292498 : {nome, ...}
    }

    public boolean confereLogin(String cc, String password) throws RemoteException {
        Pessoa p = getVoter(cc);
        if (p != null) {
            if (p.getPassword().equals(password))
                return true;
            return false;
        }
        return false;
    }

    public void WriteObjectToFile(Object obj) throws RemoteException {
        try {
            FileOutputStream fileOut = new FileOutputStream(outputFilePath);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(obj);
            objectOut.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Object ReadObjectFromFile(String outputFilePath) throws RemoteException {
        try {
            FileInputStream fileIn = new FileInputStream(outputFilePath);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            Object obj = objectIn.readObject();
            objectIn.close();
            return obj;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) throws RemoteException {

        System.getProperties().put("java.security.policy", "policy.all");
        System.setSecurityManager(new RMISecurityManager());

        // InputStreamReader input = new InputStreamReader(System.in);
        // BufferedReader reader = new BufferedReader(input);

        try {
            RMIServer rmis = new RMIServer();
            Registry r = LocateRegistry.createRegistry(PORT_r);
            r.rebind("RMI_Server", rmis);
            System.out.println("RMIServer ready.");

            // Objeto ob = (Objeto) rmis.ReadObjectFromFile(outputFilePath);
            // System.out.println(ob.toString());

        } catch (Exception re) {
            System.out.println("Exception in RMIServer.main: " + re);
        }
    }

}
