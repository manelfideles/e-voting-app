//import java.rmi.Naming;
//import java.util.Iterator;
//import java.util.Map;
//import java.util.stream.Stream;
//import java.rmi.*;
//import java.rmi.server.*;
//import java.net.*;
//import java.util.*;
//import java.io.FileNotFoundException;
//import java.util.Scanner;
//import java.util.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.io.*;
import java.util.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.Map.Entry;


public class RMIServer extends UnicastRemoteObject implements RMIServer_I {

    // Admin Consoles
    static ArrayList<AdminConsole_I> admin_consoles = new ArrayList<>();

    // Pessoas
    HashMap<String,Pessoa> mapp = new HashMap<>();
    HashMap<String,HashMap<String,Pessoa>> hmp = new HashMap<>();

    // Eleicoes
    HashMap<String,Eleicao> mape = new HashMap<>();

    // Mesas
    HashMap<String,Mesa> mapm = new HashMap<>();

    // Objeto
    HashMapPessoas hashmappessoas = new HashMapPessoas(hmp);
    HashMapEleicao hashmapeleicao = new HashMapEleicao(mape, mapm);
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

        mapp.put(pessoa.num_cc, new Pessoa(pessoa.nome, pessoa.funcao, pessoa.password, pessoa.dep, pessoa.contacto, pessoa.morada, pessoa.num_cc, pessoa.val_cc));

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
            try {
                assert bf != null;
                bf.close(); }
            catch(NullPointerException | IOException e) {
                System.out.print("NullPointerException caught");
            }
        }
    }

    public void cria_eleicao(Eleicao eleicao) throws RemoteException {
        System.out.println("RMI SERVER - cria_eleicao");

        mape.put(eleicao.titulo, new Eleicao(eleicao.data_i, eleicao.data_i, eleicao.minuto_i, eleicao.data_f, eleicao.hora_f, eleicao.minuto_f, eleicao.titulo, eleicao.descricao, eleicao.restricao, "", eleicao.lista_lista_candidato));

        File file = new File(outputFilePath);

        BufferedWriter bf = null;

        try {
            bf = new BufferedWriter(new FileWriter(file));

            bf.write(objeto.toString());

            bf.flush();

        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                assert bf != null;
                bf.close(); }
            catch(NullPointerException | IOException e) {
                System.out.print("NullPointerException caught");
            }
        }
    }

    public void altera_eleicao(Eleicao eleicao) throws RemoteException {
        System.out.println("RMI SERVER - altera_eleicao");

        mape.replace(eleicao.old_titulo, new Eleicao(eleicao.data_i, eleicao.data_i, eleicao.minuto_i, eleicao.data_f, eleicao.hora_f, eleicao.minuto_f, eleicao.titulo, eleicao.descricao, eleicao.restricao, eleicao.old_titulo, eleicao.lista_lista_candidato));

        mape.put(eleicao.titulo, mape.remove(eleicao.old_titulo));

        File file = new File(outputFilePath);

        BufferedWriter bf = null;

        try {
            bf = new BufferedWriter(new FileWriter(file));

            bf.write(objeto.toString());

            bf.flush();

        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                assert bf != null;
                bf.close(); }
            catch(NullPointerException | IOException e) {
                System.out.print("NullPointerException caught");
            }
        }
    }

    public void cria_lista_candidatos(ListaCandidato lista_candidato) throws RemoteException {
        System.out.println("RMI SERVER - cria_lista_candidatos");

        for (Map.Entry mapElement : mape.entrySet()) {
            Eleicao e = (Eleicao) mapElement.getValue();

            if(e.titulo.equals(lista_candidato.nome_eleicao)) {

                HashMap<String,ListaCandidato> hm = new HashMap<>();

                hm.put(lista_candidato.nome_lista,lista_candidato);

                e.lista_lista_candidato.add(hm);

            }
        }

        File file = new File(outputFilePath);

        BufferedWriter bf = null;

        try {
            bf = new BufferedWriter(new FileWriter(file));

            bf.write(objeto.toString());

            bf.flush();

        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                assert bf != null;
                bf.close(); }
            catch(NullPointerException | IOException e) {
                System.out.print("NullPointerException caught");
            }
        }
    }

    public void remove_lista_candidatos(ListaCandidato lista_candidato) throws RemoteException {
        System.out.println("RMI SERVER - remove_lista_candidatos");

        int i = 0;

        for (Map.Entry mapElement : mape.entrySet()) {
            Eleicao e = (Eleicao) mapElement.getValue();

            if(e.titulo.equals(lista_candidato.nome_eleicao)) {
                for (HashMap<String,ListaCandidato> elem : e.lista_lista_candidato) {
                    for (Map.Entry mapElement2 : elem.entrySet()) {

                        String lc = (String) mapElement2.getKey();

                        if(lista_candidato.nome_lista.equals(lc)) {
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

        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                assert bf != null;
                bf.close(); }
            catch(NullPointerException | IOException e) {
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

        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                assert bf != null;
                bf.close(); }
            catch(NullPointerException | IOException e) {
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

        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                assert bf != null;
                bf.close(); }
            catch(NullPointerException | IOException e) {
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

    public void boletim_voto() throws RemoteException {

        Scanner scanner = new Scanner(System.in);

        Pessoa p = new Pessoa("Pedro", "Estudante", "pedrocas", "DEI", "934725545", "funchal", "122312123", "12/02/2025");

        int i = 1, j = 1, opcao_eleicao, opcao_lista;

        String resultado = "";

        HashMap<Integer,Eleicao> hme = new HashMap<>();
        HashMap<Integer,ListaCandidato> hmlc = new HashMap<>();


        // Percorrer eleições

        for (Map.Entry mapElement : mape.entrySet()) {
            Eleicao e = (Eleicao) mapElement.getValue();

            if(e.descricao.equals(p.funcao) && (e.restricao.equals(p.dep) || e.restricao.equals("0"))) {
                resultado = resultado.concat("\n" + i + " - " + e.titulo);
                hme.put(i,e);
                i++;
            }
        }

        if(!resultado.equals("")) {
            System.out.print("Selecione a eleição na qual pretende exercer o seu voto:");
            System.out.println(resultado);
            System.out.print("Escolha: ");

            opcao_eleicao = Integer.parseInt(scanner.nextLine());

            Eleicao eleicao = hme.get(opcao_eleicao);

            resultado = "";


            // Percorrer a ArrayList das listas

            for (HashMap<String,ListaCandidato> llc : eleicao.lista_lista_candidato) {

                for(Entry<String,ListaCandidato> entry: llc.entrySet()) {
                    resultado = resultado.concat("\n" + j + " - " + entry.getKey());
                    hmlc.put(j,entry.getValue());
                    j++;
                }
            }

            if(!resultado.equals("")) {
                System.out.print("Selecione a lista na qual pretende exercer o seu voto:");
                System.out.println(resultado);

                HashMap<Integer,String> hmbn = new HashMap<>();

                System.out.println(j + " - " + "Branco");
                hmbn.put(j,"Branco");
                j++;
                System.out.println(j + " - " + "Nulo");
                hmbn.put(j,"Nulo");

                System.out.print("Escolha: ");

                opcao_lista = Integer.parseInt(scanner.nextLine());

                if(hmlc.containsKey(opcao_lista)) {
                    ListaCandidato lista = hmlc.get(opcao_lista);
                    System.out.println("Vou votar em " + lista.nome_lista);
                }

                else { System.out.println("Vou votar em " + hmbn.get(opcao_lista)); }

            }

            else { System.out.println("Não existe nenhuma lista na qual possa exercer o seu voto!"); }
        }

        else { System.out.println("Não existe nenhuma eleição na qual possa exercer o seu voto!"); }
    }

    public static void main(String[] args) {

        //System.getProperties().put("java.security.policy", "policy.all");
        //System.setSecurityManager(new RMISecurityManager());

        //InputStreamReader input = new InputStreamReader(System.in);
        //BufferedReader reader = new BufferedReader(input);

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
