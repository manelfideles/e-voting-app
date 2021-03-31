import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.io.*;
import java.util.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.HashMap;
import java.util.Map.Entry;

public class RMIServer extends UnicastRemoteObject implements RMIServer_I {
    private static final long serialVersionUID = 1L;

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

    final static String outputFilePath = "fs.txt";

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
        File file = new File(outputFilePath);
        BufferedWriter bf = null;
        try {
            bf = new BufferedWriter(new FileWriter(file));
            bf.write(objeto.toString());
            bf.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert bf != null;
                bf.close();
            } catch (NullPointerException | IOException e) {
                System.out.print("NullPointerException caught");
            }
        }
    }

    public void cria_eleicao(Eleicao eleicao) throws RemoteException {
        System.out.println("RMI SERVER - cria_eleicao");

        mape.put(eleicao.titulo,
                new Eleicao(eleicao.data_i, eleicao.data_i, eleicao.minuto_i, eleicao.data_f, eleicao.hora_f,
                        eleicao.minuto_f, eleicao.titulo, eleicao.descricao, eleicao.restricao, "",
                        eleicao.lista_lista_candidato));

        File file = new File(outputFilePath);
        BufferedWriter bf = null;
        try {
            bf = new BufferedWriter(new FileWriter(file));
            bf.write(objeto.toString());
            bf.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert bf != null;
                bf.close();
            } catch (NullPointerException | IOException e) {
                System.out.print("NullPointerException caught");
            }
        }
    }

    public void altera_eleicao(Eleicao eleicao) throws RemoteException {
        System.out.println("RMI SERVER - altera_eleicao");

        mape.replace(eleicao.old_titulo,
                new Eleicao(eleicao.data_i, eleicao.data_i, eleicao.minuto_i, eleicao.data_f, eleicao.hora_f,
                        eleicao.minuto_f, eleicao.titulo, eleicao.descricao, eleicao.restricao, eleicao.old_titulo,
                        eleicao.lista_lista_candidato));

        mape.put(eleicao.titulo, mape.remove(eleicao.old_titulo));
        File file = new File(outputFilePath);
        BufferedWriter bf = null;
        try {
            bf = new BufferedWriter(new FileWriter(file));
            bf.write(objeto.toString());
            bf.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert bf != null;
                bf.close();
            } catch (NullPointerException | IOException e) {
                System.out.print("NullPointerException caught");
            }
        }
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

        File file = new File(outputFilePath);
        BufferedWriter bf = null;
        try {
            bf = new BufferedWriter(new FileWriter(file));
            bf.write(objeto.toString());
            bf.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert bf != null;
                bf.close();
            } catch (NullPointerException | IOException e) {
                System.out.print("NullPointerException caught");
            }
        }
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

        File file = new File(outputFilePath);
        BufferedWriter bf = null;
        try {
            bf = new BufferedWriter(new FileWriter(file));
            bf.write(objeto.toString());
            bf.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert bf != null;
                bf.close();
            } catch (NullPointerException | IOException e) {
                System.out.print("NullPointerException caught");
            }
        }
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
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert bf != null;
                bf.close();
            } catch (NullPointerException | IOException e) {
                System.out.print("NullPointerException caught");
            }
        }
    }

    public void remove_mesa(Mesa mesa) throws RemoteException {
        System.out.println("RMI SERVER - remove_mesa");
        mapm.remove(mesa.dep);
        File file = new File(outputFilePath);
        BufferedWriter bf = null;
        try {
            bf = new BufferedWriter(new FileWriter(file));
            bf.write(objeto.toString());
            bf.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                assert bf != null;
                bf.close();
            } catch (NullPointerException | IOException e) {
                System.out.print("NullPointerException caught");
            }
        }
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

    public String getBulletin() throws RemoteException {
        int i = 1, j = 1;
        String bulletin = "";

        // Popular
        for (Map.Entry mapElement : mape.entrySet()) {
            Eleicao e = (Eleicao) mapElement.getValue();
            bulletin += "\n" + i + " - " + e.titulo;
            for (HashMap<String, ListaCandidato> llc : e.lista_lista_candidato) {
                for (Entry<String, ListaCandidato> entry : llc.entrySet()) {
                    bulletin += "\n   " + i + "." + j + " - " + entry.getKey();
                    j++;
                }
            }
            i++;
        }
        return bulletin;
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

    public static void main(String[] args) {

        // System.getProperties().put("java.security.policy", "policy.all");
        // System.setSecurityManager(new RMISecurityManager());
        try {
            RMIServer rmis = new RMIServer();
            Registry r = LocateRegistry.createRegistry(6969);
            r.rebind("RMI_Server", rmis);
            System.out.println("RMIServer ready.");

        } catch (Exception re) {
            System.out.println("Exception in RMIServer.main: " + re);
        }
    }
}
