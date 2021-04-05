import java.io.*;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.*;

public class RMIServer extends UnicastRemoteObject implements RMIServer_I {
    private static final long serialVersionUID = 1L;

    final static String outputFilePath = "fs.txt";

    // ArrayList das Admin Consoles que se ligam ao RMI Server
    static ArrayList<AdminConsole_I> admin_consoles = new ArrayList<>();

    // Objeto que é escrito para o fs.txt e que tem os dados todos
    public Objeto objeto;

    // Builder
    public RMIServer() throws RemoteException {
        super();
    }

    public synchronized void print_on_rmi_server(String s) throws RemoteException {
        System.out.println("> " + s);
    }

    // Função que adiciona as Admin Consoles à ArrayList
    public void subscribe(String name, AdminConsole_I ac) throws RemoteException {
        System.out.println("Subscribing " + name);
        admin_consoles.add(ac);
        System.out.println("Just added AdminConsole to admin_consoles");
    }

    // Função que remove as Admin Consoles da ArrayList
    public void unsubscribe(AdminConsole_I ac) throws RemoteException {
        System.out.println("Unsubscribing Admin Console");
        admin_consoles.remove(admin_consoles.indexOf(ac));
        System.out.println("Just removed AdminConsole to admin_consoles");
    }

    public void regista_pessoa(Pessoa pessoa) throws RemoteException {
        System.out.println("RMI SERVER - regista_pessoa");

        HashMap<String, Pessoa> mapp = this.objeto.hmp.hmp.get("HashMapPessoas");
        mapp.put(pessoa.num_cc, new Pessoa(pessoa.nome, pessoa.funcao, pessoa.password, pessoa.dep, pessoa.contacto,
                pessoa.morada, pessoa.num_cc, pessoa.val_cc));
        this.objeto.hmp.hmp.put("HashMapPessoas", mapp);
        WriteObjectToFile(this.objeto);
    }

    public void cria_eleicao(Eleicao eleicao) throws RemoteException {
        System.out.println("RMI SERVER - cria_eleicao");
        this.objeto.hme.mape.put(eleicao.titulo,
                new Eleicao(eleicao.ano_i, eleicao.mes_i, eleicao.dia_i, eleicao.hora_i, eleicao.minuto_i,
                        eleicao.ano_f, eleicao.mes_f, eleicao.dia_f, eleicao.hora_f, eleicao.minuto_f, eleicao.titulo,
                        eleicao.descricao, eleicao.restricao, "", eleicao.lista_lista_candidato, eleicao.date_i,
                        eleicao.date_f));
        WriteObjectToFile(this.objeto);
    }

    public boolean check_eleicao_before(String titulo) throws RemoteException {
        Eleicao e = this.objeto.hme.mape.get(titulo);
        Date d = new Date(); // Current date
        return e.getDate_i().before(d);
    }

    public boolean check_eleicao_after(String titulo) throws RemoteException {
        Eleicao e = this.objeto.hme.mape.get(titulo);
        Date d = new Date(); // Current date
        return e.getDate_i().after(d);
    }

    public boolean check_eleicao_voto(String titulo) throws RemoteException {
        Eleicao e = this.objeto.hme.mape.get(titulo);
        Date d = new Date(); // Current date
        return (e.getDate_i().before(d) && e.getDate_f().after(d));
    }

    public boolean check_consulta_resultados(String titulo) throws RemoteException {
        Eleicao e = this.objeto.hme.mape.get(titulo);
        Date d = new Date(); // Current date
        return e.getDate_f().before(d);
    }

    public void altera_eleicao(Eleicao eleicao) throws RemoteException {
        System.out.println("RMI SERVER - altera_eleicao");
        Eleicao e = this.objeto.hme.mape.get(eleicao.old_titulo);
        this.objeto.hme.mape.replace(eleicao.old_titulo,
                new Eleicao(eleicao.ano_i, eleicao.mes_i, eleicao.dia_i, eleicao.hora_i, eleicao.minuto_i,
                        eleicao.ano_f, eleicao.mes_f, eleicao.dia_f, eleicao.hora_f, eleicao.minuto_f, eleicao.titulo,
                        e.descricao, e.restricao, eleicao.old_titulo, e.lista_lista_candidato,
                        eleicao.date_i, eleicao.date_f));
        this.objeto.hme.mape.put(eleicao.titulo, this.objeto.hme.mape.remove(eleicao.old_titulo));
        WriteObjectToFile(this.objeto);
    }

    public boolean check_eleicao_exists(String titulo) throws RemoteException {
        return this.objeto.hme.mape.containsKey(titulo);
    }

    public void cria_lista_candidatos(ListaCandidato lista_candidato) throws RemoteException {
        System.out.println("RMI SERVER - cria_lista_candidatos");
        for (Map.Entry mapElement : this.objeto.hme.mape.entrySet()) {
            Eleicao e = (Eleicao) mapElement.getValue();
            if (e.titulo.equals(lista_candidato.nome_eleicao)) {
                HashMap<String, ListaCandidato> hm = new HashMap<>();
                hm.put(lista_candidato.nome_lista, lista_candidato);
                e.lista_lista_candidato.add(hm);
            }
        }
        WriteObjectToFile(this.objeto);
    }

    public String returnTipo_lista(String titulo) throws RemoteException {
        Eleicao e = this.objeto.hme.mape.get(titulo);
        return e.getDescricao();
    }

    public void remove_lista_candidatos(ListaCandidato lista_candidato) throws RemoteException {
        System.out.println("RMI SERVER - remove_lista_candidatos");
        int i = 0;
        boolean v = false;
        for (Map.Entry mapElement : this.objeto.hme.mape.entrySet()) {
            Eleicao e = (Eleicao) mapElement.getValue();
            if (e.titulo.equals(lista_candidato.nome_eleicao)) {
                for (HashMap<String, ListaCandidato> elem : e.lista_lista_candidato) {
                    for (Map.Entry mapElement2 : elem.entrySet()) {
                        String lc = (String) mapElement2.getKey();
                        if (lista_candidato.nome_lista.equals(lc)) {
                            e.lista_lista_candidato.remove(i);
                            v=true;
                            WriteObjectToFile(this.objeto);
                            break;
                        }
                        i++;
                    }
                    if (v) {
                        break;
                    }
                }
            }
        }
    }

    public void cria_mesa(Mesa mesa) throws RemoteException {
        System.out.println("RMI SERVER - cria_mesa");
        this.objeto.hme.mapm.put(mesa.dep, mesa);
        WriteObjectToFile(this.objeto);
    }

    public void remove_mesa(Mesa mesa) throws RemoteException {
        System.out.println("RMI SERVER - remove_mesa");
        this.objeto.hme.mapm.remove(mesa.dep);
        WriteObjectToFile(this.objeto);
    }

    public void consulta_estado_mesas() throws RemoteException {
        System.out.println("RMI SERVER - consulta_estado_mesas");
    }

    public HashMap<String, HashMap<String, Pessoa>> consulta_info_voto() throws RemoteException {
        System.out.println("RMI SERVER - consulta_info_voto");
        return this.objeto.hmp.hmp;
    }

    public HashMap<String, Mesa> consulta_eleitores() throws RemoteException {
        System.out.println("RMI SERVER - consulta_eleitores");
        return this.objeto.hme.mapm;
    }

    public HashMap<String, Eleicao> consulta_resultados() throws RemoteException {
        System.out.println("RMI SERVER - consulta_resultados");
        return this.objeto.hme.mape;
    }

    public HashMap<Integer, Eleicao> getBulletin(Pessoa p) throws RemoteException {
        int i = 1;
        HashMap<Integer, Eleicao> hme = new HashMap<>();
        // Popular
        for (Map.Entry mapElement : this.objeto.hme.mape.entrySet()) {
            Eleicao e = (Eleicao) mapElement.getValue();
            HashMap<String, String> hmss = p.getLocal_momento_voto();
            // só pode votar em eleicoes que ja tenham comecado e ainda n tenham acabado
            boolean check_voto = check_eleicao_voto(e.getTitulo());
            if (check_voto) {
                if (hmss.isEmpty()) {
                    if (e.getDescricao().equals(p.getFuncao()) && (e.getRestricao().equals(p.getDep()) || e.getRestricao().equals("0"))) {
                        hme.put(i, e);
                        i++;
                    }
                } else {
                    if (!hmss.containsKey(e.getTitulo())) {
                        if (e.getDescricao().equals(p.getFuncao()) && (e.getRestricao().equals(p.getDep()) || e.getRestricao().equals("0"))) {
                            hme.put(i, e);
                            i++;
                        }
                    }
                }
            }
        }
        return hme;
    }

    public Pessoa getVoter(String cc) throws RemoteException {
        // query à BD para ver se existe uma pessoa com 'cc'.
        return this.objeto.hmp.hmp.get("HashMapPessoas").get(cc);
    }

    public Mesa getMesaByDep(String dep) throws RemoteException {
        return this.objeto.hme.mapm.get(dep);
    }

    public void printMesasExistentes() throws RemoteException {
        for (Map.Entry m : this.objeto.hme.mapm.entrySet()) {
            System.out.println(m.toString());
        }
    }

    public void checkActiveMesas(AdminConsole_I ac) throws RemoteException {
        // ping a todas as mesas de mapm
        for (Map.Entry<String, Mesa> m : this.objeto.hme.mapm.entrySet()) {
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
        Mesa m = this.objeto.hme.mapm.get(dep);
        m.remoteServerObj = remoteServerObj;
        m.setState(true);
        for (AdminConsole_I ac : admin_consoles) {
            try {
                ac.print_on_admin_console("Mesa " + dep + " ligou-se ao RMIServer\n");
            } catch (RemoteException e) {
                System.out.println("Mesa " + dep + " ligou-se ao RMIServer");
            }
        }
    }

    public void unsubscribeMesa(String dep) throws RemoteException {
        Mesa m = this.objeto.hme.mapm.get(dep);
        m.remoteServerObj = null;
        m.setState(false);
        for (AdminConsole_I ac : admin_consoles) {
            try {
                ac.print_on_admin_console("Mesa " + dep + " desligou-se de RMIServer\n");
            } catch (Exception ex) {
                System.out.println("Mesa " + dep + " desligou-se do RMIServer");
            }
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

    public void atualiza(String num_cc, String nome_lista, String nome_eleicao, String DEP, Date d) throws RemoteException {
        this.objeto.hmp.hmp.get("HashMapPessoas").get(num_cc).local_momento_voto.put(nome_eleicao, DEP + " " + d);
        this.objeto.hme.mape.get(nome_eleicao).num_total_votos++;
        boolean existe = false;
        HashMap<String, Integer> n_e = this.objeto.hme.mapm.get(DEP).getNum_eleitores();
        for (Map.Entry mapElement : n_e.entrySet()) {
            if (mapElement.getKey().equals(nome_eleicao)) {
                Integer i = (Integer) mapElement.getValue();
                i++;
                n_e.replace(nome_eleicao, i);
                existe = true;
                break;
            }
        }
        if (!existe) {
            n_e.put(nome_eleicao,1);
        }
        switch (nome_lista) {
        case "voto_branco":
            this.objeto.hme.mape.get(nome_eleicao).num_votos_branco++;
            break;
        case "voto_nulo":
            this.objeto.hme.mape.get(nome_eleicao).num_votos_nulo++;
            break;
        default:
            ArrayList<HashMap<String, ListaCandidato>> llc = this.objeto.hme.mape.get(nome_eleicao).lista_lista_candidato;
            for (HashMap<String, ListaCandidato> elem : llc) {
                for (Entry<String, ListaCandidato> entry : elem.entrySet()) {
                    if (entry.getKey().equals(nome_lista)) {
                        entry.getValue().num_votos++;
                    }
                }
            }
            break;
        }
        WriteObjectToFile(this.objeto);
    }

    public HashMap<Integer, String> getListasFromEleicaoEscolhida(Eleicao e) throws RemoteException {
        int j = 1;
        HashMap<Integer, String> out = new HashMap<>();
        if (!e.lista_lista_candidato.isEmpty()) {
            for (HashMap<String, ListaCandidato> llc : e.lista_lista_candidato) {
                for (Entry<String, ListaCandidato> entry : llc.entrySet()) {
                    out.put(j, entry.getValue().nome_lista);
                    j++;
                }
            }
        }
        out.put(j, "voto_branco");
        j++;
        out.put(j, "voto_nulo");
        return out;
    }

    public void WriteObjectToFile(Object obj) throws RemoteException {
        try {
            FileOutputStream fileOut = new FileOutputStream(RMIServer.outputFilePath);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(obj);
            objectOut.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Object ReadObjectFromFile(String outputFilePath) throws Exception {
        FileInputStream fileIn = new FileInputStream(RMIServer.outputFilePath);
        ObjectInputStream objectIn = new ObjectInputStream(fileIn);
        Object obj = objectIn.readObject();
        objectIn.close();
        return obj;
    }

    public Objeto returnObjeto() throws RemoteException {
        return this.objeto;
    }

    public void sayHello() throws RemoteException {
        System.out.println("Running");
    }

    public boolean returnIsAlive() throws RemoteException {
        return true;
    }

    public static void main(String[] args) throws RemoteException {
        System.getProperties().put("java.security.policy", "policy.all");
        System.setSecurityManager(new RMISecurityManager());

        RMIServer rmis = new RMIServer();
        RMIServer_I rmis2;
        Registry r;
        int ping = 0;
        int PORT_r = 6969;

        try {

            File file = new File(outputFilePath);
            if(file.length() != 0) { // se o fs.txt tiver dados escritos
                rmis.objeto = (Objeto) rmis.ReadObjectFromFile("fs.txt");
            }
            else {
                rmis.objeto = new Objeto();
            }
            r = LocateRegistry.createRegistry(PORT_r);
            r.rebind("RMI_Server", rmis);
            System.out.println("RMIServer ready.");

        } catch (FileNotFoundException e) {
            System.out.println("entrei!");
            rmis.objeto = new Objeto();
        } catch (Exception re) {
            System.out.println("Exception in RMIServer.main: " + re);

            boolean rmiFails = true;
            while (rmiFails) {
                rmiFails = false;

                while (ping<5) {
                    try {
                        r = LocateRegistry.getRegistry(PORT_r);
                        rmis2 = (RMIServer_I) r.lookup("RMI_Server");
                        if (rmis2.returnIsAlive()) {
                            Thread.sleep(1000);
                            ping = 0;
                            System.out.println("ping: " + ping);
                            System.out.println("RMIServer primario esta funcional");
                        }
                    } catch (Exception ke) {
                        ping++;
                        System.out.println("ping: " + ping);
                    }
                }

                try {
                    File file = new File(outputFilePath);
                    if(file.length() != 0) {
                        rmis.objeto = (Objeto) rmis.ReadObjectFromFile("fs.txt");
                    }
                    else {
                        rmis.objeto = new Objeto();
                    }
                    r = LocateRegistry.createRegistry(PORT_r);
                    r.rebind("RMI_Server", rmis);
                    System.out.println("RMIServer ready.");
                } catch (FileNotFoundException e) {
                    rmis.objeto = new Objeto();
                } catch (RemoteException | InterruptedException b) {
                    System.out.println("Main RMI Server working... Waiting for failures");
                    rmiFails = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}