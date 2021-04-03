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
import java.util.*;

public class RMIServer extends UnicastRemoteObject implements RMIServer_I {
    private static final long serialVersionUID = 1L;

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
    static HashMap<String, Mesa> mapm = new HashMap<String, Mesa>();
    static {
        mapm.put("DARQ", new Mesa("DARQ", null));
        mapm.put("DCT", new Mesa("DCT", null));
        mapm.put("DEC", new Mesa("DEC", null));
        mapm.put("DEEC", new Mesa("DEEC", null));
        mapm.put("DEI", new Mesa("DEI", null));
        mapm.put("DEM", new Mesa("DEM", null));
        mapm.put("DEQ", new Mesa("DEQ", null));
        mapm.put("DF", new Mesa("DF", null));
        mapm.put("DM", new Mesa("DM", null));
        mapm.put("DQ", new Mesa("DQ", null));
        mapm.put("FLUC", new Mesa("FLUC", null));
        mapm.put("FDUC", new Mesa("FDUC", null));
        mapm.put("FMUC", new Mesa("FMUC", null));
        mapm.put("FMUC", new Mesa("FMUC", null));
        mapm.put("FFUC", new Mesa("FFUC", null));
        mapm.put("FEUC", new Mesa("FEUC", null));
        mapm.put("FPCEUC", new Mesa("FPCEUC", null));
        mapm.put("FCDEFUC", new Mesa("FCDEFUC", null));
        mapm.put("CdA", new Mesa("CdA", null));
    }

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

    public void unsubscribe(AdminConsole_I ac) throws RemoteException {
        System.out.println("Unsubscribing Admin Console");
        admin_consoles.remove(admin_consoles.indexOf(ac));
        System.out.println("Just removed AdminConsole to admin_consoles");
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
                new Eleicao(eleicao.ano_i, eleicao.mes_i, eleicao.dia_i, eleicao.hora_i, eleicao.minuto_i,
                        eleicao.ano_f, eleicao.mes_f, eleicao.dia_f, eleicao.hora_f, eleicao.minuto_f, eleicao.titulo,
                        eleicao.descricao, eleicao.restricao, "", eleicao.lista_lista_candidato, eleicao.date_i,
                        eleicao.date_f));
        WriteObjectToFile(objeto);
    }

    public boolean check_eleicao_before(String old_titulo) throws RemoteException {
        Eleicao e = mape.get(old_titulo);
        Date d = new Date(); // Current date
        return e.getDate_i().before(d);
    }

    public boolean check_eleicao_after(String old_titulo) throws RemoteException {
        Eleicao e = mape.get(old_titulo);
        Date d = new Date(); // Current date
        return e.getDate_i().after(d);
    }

    public boolean check_eleicao_voto(String old_titulo) throws RemoteException {
        Eleicao e = mape.get(old_titulo);
        Date d = new Date(); // Current date
        return (e.getDate_i().before(d) && e.getDate_f().after(d));
    }

    public boolean check_consulta_resultados(String old_titulo) throws RemoteException {
        Eleicao e = mape.get(old_titulo);
        Date d = new Date(); // Current date
        return e.getDate_f().before(d);
    }

    public void altera_eleicao(Eleicao eleicao) throws RemoteException {
        System.out.println("RMI SERVER - altera_eleicao");
        mape.replace(eleicao.old_titulo,
                new Eleicao(eleicao.ano_i, eleicao.mes_i, eleicao.dia_i, eleicao.hora_i, eleicao.minuto_i,
                        eleicao.ano_f, eleicao.mes_f, eleicao.dia_f, eleicao.hora_f, eleicao.minuto_f, eleicao.titulo,
                        eleicao.descricao, eleicao.restricao, eleicao.old_titulo, eleicao.lista_lista_candidato,
                        eleicao.date_i, eleicao.date_f));
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
                            WriteObjectToFile(objeto);
                            break;
                        }
                        i++;
                    }
                }
            }
        }
    }

    public void cria_mesa(Mesa mesa) throws RemoteException {
        System.out.println("RMI SERVER - cria_mesa");
        mapm.put(mesa.dep, mesa);
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

    public HashMap<String, HashMap<String, Pessoa>> consulta_info_voto() throws RemoteException {
        System.out.println("RMI SERVER - consulta_info_voto");
        return hmp;
    }

    public HashMap<String, Mesa> consulta_eleitores() throws RemoteException {
        System.out.println("RMI SERVER - consulta_eleitores");
        return mapm;
    }

    public HashMap<String, Eleicao> consulta_resultados() throws RemoteException {
        System.out.println("RMI SERVER - consulta_resultados");
        return mape;
    }

    public HashMap<Integer, Eleicao> getBulletin(Pessoa p) throws RemoteException {
        int i = 1;
        HashMap<Integer, Eleicao> hme = new HashMap<>();

        // Popular
        for (Map.Entry mapElement : mape.entrySet()) {
            Eleicao e = (Eleicao) mapElement.getValue();
            HashMap<String, String> hmss = p.getLocal_momento_voto();

            // só pode votar em eleicoes que ja tenham comecado e ainda n tenham acabado
            boolean check_voto = check_eleicao_voto(e.getTitulo());
            if (!check_voto) {
                break;
            }

            if (hmss == null) {
                if (e.getDescricao().equals(p.getFuncao())
                        && (e.getRestricao().equals(p.getDep()) || e.getRestricao().equals("0"))) {
                    hme.put(i, e);
                    i++;
                }
            } else {
                if (!hmss.containsKey(e.getTitulo())) {
                    if (e.getDescricao().equals(p.getFuncao())
                            && (e.getRestricao().equals(p.getDep()) || e.getRestricao().equals("0"))) {
                        hme.put(i, e);
                        i++;
                    }
                }
            }
        }
        return hme;
    }

    public Pessoa getVoter(String cc) throws RemoteException {
        // query à BD para ver se existe uma pessoa com 'cc'.
        return mapp.get(cc); // 2019292498 : {nome, ...}
    }

    public Mesa getMesaByDep(String dep) throws RemoteException {
        return mapm.get(dep);
    }

    public void printMesasExistentes() throws RemoteException {
        for (Map.Entry m : mapm.entrySet()) {
            System.out.println(m.toString());
        }
    }

    public void checkActiveMesas(AdminConsole_I ac) throws RemoteException {
        // ping a todas as mesas de mapm
        for (Map.Entry<String, Mesa> m : mapm.entrySet()) {
            if (m.getValue().remoteServerObj != null)
                this.ping(m.getValue(), ac);
        }
    }

    // ping às mesas de voto por rmi
    public void ping(Mesa m, AdminConsole_I ac) throws RemoteException {
        int i = 0;
        ac.print_on_admin_console("\nPinging Mesa " + m.getDep() + "\n");
        while (i < 3) {
            // pergunta
            try {
                ac.print_on_admin_console("Ping " + i + " > ");
                m.remoteServerObj.ping();
                ac.print_on_admin_console("\n");
            } catch (RemoteException ex) {
                m.setState(false);
                unsubscribeMesa(m.getDep());
            }
            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
            i++;
        }
    }

    public void subscribeMesa(String dep, RemoteMulticastServerObj_Impl remoteServerObj) throws RemoteException {
        Mesa m = mapm.get(dep);
        m.remoteServerObj = remoteServerObj;
        m.setState(true);
        for (AdminConsole_I ac : admin_consoles) {
            ac.print_on_admin_console("Mesa " + dep + " ligou-se de RMIServer\n");
        }
    }

    public void unsubscribeMesa(String dep) throws RemoteException {
        Mesa m = mapm.get(dep);
        m.remoteServerObj = null;
        m.setState(false);
        for (AdminConsole_I ac : admin_consoles) {
            ac.print_on_admin_console("Mesa " + dep + " desligou-se de RMIServer\n");
        }
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

    public void atualiza(String num_cc, String nome_lista, String nome_eleicao, String DEP) throws RemoteException {
        mapp.get(num_cc).local_momento_voto.put(nome_eleicao, DEP); // falta adicionar local e momento
        mape.get(nome_eleicao).num_total_votos++;

        switch (nome_lista) {
        case "voto_branco":
            mape.get(nome_eleicao).num_votos_branco++;
            break;
        case "voto_nulo":
            mape.get(nome_eleicao).num_votos_nulo++;
            break;
        default:
            ArrayList<HashMap<String, ListaCandidato>> llc = mape.get(nome_eleicao).lista_lista_candidato;
            for (HashMap<String, ListaCandidato> elem : llc) {
                for (Entry<String, ListaCandidato> entry : elem.entrySet()) {
                    if (entry.getKey().equals(nome_lista)) {
                        entry.getValue().num_votos++;
                    }
                }
            }
            break;
        }
        WriteObjectToFile(objeto);
    }

    public HashMap<Integer, String> getListasFromEleicaoEscolhida(Eleicao e) throws RemoteException {
        int j = 1;
        HashMap<Integer, String> out = new HashMap<>();
        for (HashMap<String, ListaCandidato> llc : e.lista_lista_candidato) {
            for (Entry<String, ListaCandidato> entry : llc.entrySet()) {
                out.put(j, entry.getValue().nome_lista);
                j++;
            }
        }
        out.put(j, "voto_branco");
        j++;
        out.put(j, "voto_nulo");
        return out;
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
        try {
            Date date = new Date(); // Current date
            System.out.println(date);
            RMIServer rmis = new RMIServer();
            Registry r = LocateRegistry.createRegistry(PORT_r);
            r.rebind("RMI_Server", rmis);
            System.out.println("RMIServer ready.");

            // pings continuos a todas as mesas associadas
            // while (true) {
            // for (AdminConsole_I ac : RMIServer.admin_consoles) {
            // System.out.println("ruben");
            // for (Map.Entry mesa : mapm.entrySet()) {
            // Mesa m = mapm.get(mesa.getKey());
            // rmis.ping(m, ac);
            // }
            // }
            // }
        } catch (Exception re) {
            System.out.println("Exception in RMIServer.main: " + re);
        }
    }
}
