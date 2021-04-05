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

    // Persistant Storage - File System
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

    // Método que adiciona as Admin Consoles à ArrayList
    public void subscribe(String name, AdminConsole_I ac) throws RemoteException {
        System.out.println("Subscribing " + name);
        admin_consoles.add(ac);
        System.out.println("Just added AdminConsole to admin_consoles");
    }

    // Método que remove as Admin Consoles da ArrayList
    public void unsubscribe(AdminConsole_I ac) throws RemoteException {
        System.out.println("Unsubscribing Admin Console");
        admin_consoles.remove(admin_consoles.indexOf(ac));
        System.out.println("Just removed AdminConsole to admin_consoles");
    }

    // Método que regista uma pessoa
    public void regista_pessoa(Pessoa pessoa) throws RemoteException {
        System.out.println("RMI SERVER - regista_pessoa");
        // mapp = { num_cc = Pessoa }
        HashMap<String, Pessoa> mapp = this.objeto.hmp.hmp.get("HashMapPessoas");
        // adicionar uma nova pessoa a mapp
        mapp.put(pessoa.num_cc, new Pessoa(pessoa.nome, pessoa.funcao, pessoa.password, pessoa.dep, pessoa.contacto,
                pessoa.morada, pessoa.num_cc, pessoa.val_cc));
        // adicionar mapp ao objeto
        this.objeto.hmp.hmp.put("HashMapPessoas", mapp);
        // escrever para a Persistant Storage
        WriteObjectToFile(this.objeto);
    }

    // Método que cria uma eleição
    public void cria_eleicao(Eleicao eleicao) throws RemoteException {
        System.out.println("RMI SERVER - cria_eleicao");
        // adicionar uma nova eleição a mape ( mape = { titulo = Eleição } )
        this.objeto.hme.mape.put(eleicao.titulo,
                new Eleicao(eleicao.ano_i, eleicao.mes_i, eleicao.dia_i, eleicao.hora_i, eleicao.minuto_i,
                        eleicao.ano_f, eleicao.mes_f, eleicao.dia_f, eleicao.hora_f, eleicao.minuto_f, eleicao.titulo,
                        eleicao.descricao, eleicao.restricao, "", eleicao.lista_lista_candidato, eleicao.date_i,
                        eleicao.date_f));
        // escrever para a Persistant Storage
        WriteObjectToFile(this.objeto);
    }

    // Método que verifica se a date inicial da eleição é anterior à current date
    public boolean check_eleicao_before(String titulo) throws RemoteException {
        Eleicao e = this.objeto.hme.mape.get(titulo);
        Date d = new Date(); // Current date
        return e.getDate_i().before(d);
    }

    // Método que verifica se a date inicial da eleição é posterior à current date
    public boolean check_eleicao_after(String titulo) throws RemoteException {
        Eleicao e = this.objeto.hme.mape.get(titulo);
        Date d = new Date(); // Current date
        return e.getDate_i().after(d);
    }

    // Método que verifica se a date inicial da eleição é anterior à current date
    // e que verifica se a date final da eleição é posterior à current date
    public boolean check_eleicao_voto(String titulo) throws RemoteException {
        Eleicao e = this.objeto.hme.mape.get(titulo);
        Date d = new Date(); // Current date
        return (e.getDate_i().before(d) && e.getDate_f().after(d));
    }

    // Método que verifica se a date final da eleição é anterior à current date
    public boolean check_consulta_resultados(String titulo) throws RemoteException {
        Eleicao e = this.objeto.hme.mape.get(titulo);
        Date d = new Date(); // Current date
        return e.getDate_f().before(d);
    }

    // Método que altera propriedades de uma eleição (propriedades textuais e
    // instantes de início e fim da eleição)
    public void altera_eleicao(Eleicao eleicao) throws RemoteException {
        System.out.println("RMI SERVER - altera_eleicao");
        // e = eleição que queremos alterar
        Eleicao e = this.objeto.hme.mape.get(eleicao.old_titulo);
        // faz alterações no value (descrição, restrição e lista_lista_candidato não são
        // alteradas)
        this.objeto.hme.mape.replace(eleicao.old_titulo,
                new Eleicao(eleicao.ano_i, eleicao.mes_i, eleicao.dia_i, eleicao.hora_i, eleicao.minuto_i,
                        eleicao.ano_f, eleicao.mes_f, eleicao.dia_f, eleicao.hora_f, eleicao.minuto_f, eleicao.titulo,
                        e.descricao, e.restricao, eleicao.old_titulo, e.lista_lista_candidato, eleicao.date_i,
                        eleicao.date_f));
        // faz alterações na key (nome da eleição)
        this.objeto.hme.mape.put(eleicao.titulo, this.objeto.hme.mape.remove(eleicao.old_titulo));
        // escrever para a Persistant Storage
        WriteObjectToFile(this.objeto);
    }

    // Método que verifica se uma eleicao exsite (retorna true ou false)
    public boolean check_eleicao_exists(String titulo) throws RemoteException {
        return this.objeto.hme.mape.containsKey(titulo);
    }

    // Método que cria lista de candidatos
    public void cria_lista_candidatos(ListaCandidato lista_candidato) throws RemoteException {
        System.out.println("RMI SERVER - cria_lista_candidatos");
        // Percorre mape ( { titulo = Eleicao } )
        for (Map.Entry mapElement : this.objeto.hme.mape.entrySet()) {
            Eleicao e = (Eleicao) mapElement.getValue();
            // Verifica se o titulo da eleição é igual ao campo nome_eleicao que pertence à
            // lista de candidatos que queremos criar
            // Se entrar é porque vamos criar a lista de candidatos no e
            if (e.titulo.equals(lista_candidato.nome_eleicao)) {
                // Cria hm ( { nome_lista = ListaCandidato } )
                HashMap<String, ListaCandidato> hm = new HashMap<>();
                hm.put(lista_candidato.nome_lista, lista_candidato);
                // Adiciona hm na lista_lista_candidato do e ( [ { nome_lista = ListaCandidato
                // }, { nome_lista = ListaCandidato }, ... ] )
                e.lista_lista_candidato.add(hm);
            }
        }
        // escrever para a Persistant Storage
        WriteObjectToFile(this.objeto);
    }

    // Método que retorna a descrição de uma dada eleição (Estudante, Docente ou
    // Funcionário)
    public String returnTipo_lista(String titulo) throws RemoteException {
        Eleicao e = this.objeto.hme.mape.get(titulo);
        return e.getDescricao();
    }

    // Método que remove uma lista de candidatos de uma dada eleição
    public void remove_lista_candidatos(ListaCandidato lista_candidato) throws RemoteException {
        System.out.println("RMI SERVER - remove_lista_candidatos");
        int i = 0;
        // Vai servir para saber quando já podemos sair do ciclo de pesquisa pois já
        // removemos o que queriamos
        boolean v = false;
        // Percorre mape ( { titulo = Eleicao } )
        for (Map.Entry mapElement : this.objeto.hme.mape.entrySet()) {
            Eleicao e = (Eleicao) mapElement.getValue();
            // Verifica se o titulo da eleição é igual ao campo nome_eleicao que pertence à
            // lista de candidatos que queremos remover
            // Se entrar é porque vamos remover a lista de candidatos do e
            if (e.titulo.equals(lista_candidato.nome_eleicao)) {
                // Percorre a lista_lista_candidato de e ( [ nome_lista = ListaCandidato ] )
                for (HashMap<String, ListaCandidato> elem : e.lista_lista_candidato) {
                    for (Map.Entry mapElement2 : elem.entrySet()) {
                        String lc = (String) mapElement2.getKey();
                        // Verifica se o nome da lista é igual à key
                        // Se entrar é porque vamos remover a lista de candidatos da
                        // lista_lista_candidato do e
                        if (lista_candidato.nome_lista.equals(lc)) {
                            e.lista_lista_candidato.remove(i);
                            v = true;
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

    // Método que cria uma mesa
    public void cria_mesa(Mesa mesa) throws RemoteException {
        System.out.println("RMI SERVER - cria_mesa");
        // Adiciona uma mesa ( { dep = Mesa } )
        this.objeto.hme.mapm.put(mesa.dep, mesa);
        // escrever para a Persistant Storage
        WriteObjectToFile(this.objeto);
    }

    // Método que remove uma mesa
    public void remove_mesa(Mesa mesa) throws RemoteException {
        System.out.println("RMI SERVER - remove_mesa");
        // Remove uma mesa ( { dep = Mesa } )
        this.objeto.hme.mapm.remove(mesa.dep);
        // escrever para a Persistant Storage
        WriteObjectToFile(this.objeto);
    }

    // Método que consulta o estado das mesas
    public void consulta_estado_mesas() throws RemoteException {
        System.out.println("RMI SERVER - consulta_estado_mesas");
    }

    // Método que consulta a informação de voto (local e momento em votou cada
    // eleitor)
    public HashMap<String, HashMap<String, Pessoa>> consulta_info_voto() throws RemoteException {
        System.out.println("RMI SERVER - consulta_info_voto");
        // Dá return de hmp ( { "HashMapPessoas" = mapp } | mapp = { num_cc = Pessoa } )
        return this.objeto.hmp.hmp;
    }

    // Método que consulta o número de eleitores que votaram até ao momento em cada
    // mesa de voto (numa dada eleição)
    public HashMap<String, Mesa> consulta_eleitores() throws RemoteException {
        System.out.println("RMI SERVER - consulta_eleitores");
        // ( { dep = Mesa } )
        return this.objeto.hme.mapm;
    }

    // Método que consulta resultados detalhados de eleições passadas
    public HashMap<String, Eleicao> consulta_resultados() throws RemoteException {
        System.out.println("RMI SERVER - consulta_resultados");
        // ( mape = { titulo = Eleição } )
        return this.objeto.hme.mape;
    }

    // Método que vai dar return do boletim-voto eleições
    public HashMap<Integer, Eleicao> getBulletin(Pessoa p) throws RemoteException {
        int i = 1;
        // ( { 1=alfa, 2=beta, ... } )
        HashMap<Integer, Eleicao> hme = new HashMap<>();
        // Percorre mape ( { titulo = Eleicao } )
        for (Map.Entry mapElement : this.objeto.hme.mape.entrySet()) {
            Eleicao e = (Eleicao) mapElement.getValue();
            // hmss vai ter as eleições que o eleitor ja votou
            HashMap<String, String> hmss = p.getLocal_momento_voto();
            // só pode votar em eleicoes que ja tenham comecado e ainda n tenham acabado
            boolean check_voto = check_eleicao_voto(e.getTitulo());
            if (check_voto) {
                // Se o eleitor ainda n votou
                if (hmss.isEmpty()) {
                    // O eleitor só pode votar em eleições que correspondam à sua função
                    // &&
                    // O eleitor só pode votar em eleições que não tenham restrições ou cujas
                    // restrições correspondam ao departamento do eleitor
                    if (e.getDescricao().equals(p.getFuncao())
                            && (e.getRestricao().equals(p.getDep()) || e.getRestricao().equals("0"))) {
                        hme.put(i, e);
                        i++;
                    }
                }
                // Se o eleitor já votou pelo menos uma vez
                else {
                    // Se o eleitor ainda não tiver votado na eleição
                    if (!hmss.containsKey(e.getTitulo())) {
                        // O eleitor só pode votar em eleições que correspondam à sua função
                        // &&
                        // O eleitor só pode votar em eleições que não tenham restrições ou cujas
                        // restrições correspondam ao departamento do eleitor
                        if (e.getDescricao().equals(p.getFuncao())
                                && (e.getRestricao().equals(p.getDep()) || e.getRestricao().equals("0"))) {
                            hme.put(i, e);
                            i++;
                        }
                    }
                }
            }
        }
        // retorna uma lista de eleições enumeradas nas quais o eleitor pode votar
        return hme;
    }

    // Método que vai procurar na persistant storage uma pessoa com o cc que
    // pretendemos
    public Pessoa getVoter(String cc) throws RemoteException {
        // dá return de um mapp ( { num_cc = Pessoa } )
        return this.objeto.hmp.hmp.get("HashMapPessoas").get(cc);
    }

    // Método que vai buscar uma Mesa procurando pelo Dep
    public Mesa getMesaByDep(String dep) throws RemoteException {
        // dá return de Mesa
        return this.objeto.hme.mapm.get(dep);
    }

    // Método que dá print das Mesas existentes
    public void printMesasExistentes() throws RemoteException {
        // Percorre mapm ( { dep = Mesa } )
        for (Map.Entry m : this.objeto.hme.mapm.entrySet()) {
            System.out.println(m.toString());
        }
    }

    // Método que verifica as mesas que estão ativas
    public void checkActiveMesas(AdminConsole_I ac) throws RemoteException {
        // ping a todas as mesas de mapm
        for (Map.Entry<String, Mesa> m : this.objeto.hme.mapm.entrySet()) {
            if (m.getValue().remoteServerObj != null)
                ping(m.getValue(), ac);
        }
    }

    // Método que dá ping às mesas de voto por rmi
    public void ping(Mesa m, AdminConsole_I ac) throws RemoteException {
        int i = 0;
        ac.print_on_admin_console("\nPinging Mesa " + m.getDep() + "\n");
        while (i < 3) {
            // pergunta
            try {
                ac.print_on_admin_console("Ping " + i + " > ");
                if (m.remoteServerObj != null) {
                    m.remoteServerObj.ping();
                    ac.print_on_admin_console("Successful\n\n");
                }
                else {
                    ac.print_on_admin_console("A mesa esta desligada!\n");
                }
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

    // Método que torna uma mesa ativa
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

    // Método que torna uma mesa desativa
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

    // Método que vai atualizar os dados da persistant storage conforme as mudanças
    // que foram feitas vindas do MulticastServer
    public void atualiza(String num_cc, String nome_lista, String nome_eleicao, String DEP, Date d)
            throws RemoteException {
        // atualiza o local_momento_voto do eleitor
        this.objeto.hmp.hmp.get("HashMapPessoas").get(num_cc).local_momento_voto.put(nome_eleicao, DEP + " " + d);
        // atualiza o número de votos de uma dada eleição
        this.objeto.hme.mape.get(nome_eleicao).num_total_votos++;
        boolean existe = false;
        // atualiza número de pessoas que votaram em cada uma das mesas
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
        // se até ao momento, numa certa mesa não existe a eleição na qual o eleitor
        // votou, adicionamos essa eleição com 1 voto
        if (!existe) {
            n_e.put(nome_eleicao, 1);
        }
        // atualiza o número de votos da lista de candidatos/branco/nulo
        switch (nome_lista) {
        case "voto_branco":
            this.objeto.hme.mape.get(nome_eleicao).num_votos_branco++;
            break;
        case "voto_nulo":
            this.objeto.hme.mape.get(nome_eleicao).num_votos_nulo++;
            break;
        default:
            // caso tenha votado numa lista de candidatos
            ArrayList<HashMap<String, ListaCandidato>> llc = this.objeto.hme.mape
                    .get(nome_eleicao).lista_lista_candidato;
            for (HashMap<String, ListaCandidato> elem : llc) {
                for (Entry<String, ListaCandidato> entry : elem.entrySet()) {
                    if (entry.getKey().equals(nome_lista)) {
                        entry.getValue().num_votos++;
                    }
                }
                break;
        }
        // escrever para a Persistant Storage
        WriteObjectToFile(this.objeto);
    }
    }

    // Método que vai retornar as listas de candidatos existentes na eleição
    // escolhida pelo eleitor
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
        // adicionamos também voto branco e nulo pois também é são opções de voto
        out.put(j, "voto_branco");
        j++;
        out.put(j, "voto_nulo");
        // retornamos uma lista enumerada de listas de candidatos/branco/nulo
        return out;
    }

    // Método que escreve para a persistant storage
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

    // Método que lê da persistant storage
    public Object ReadObjectFromFile(String outputFilePath) throws Exception {
        FileInputStream fileIn = new FileInputStream(RMIServer.outputFilePath);
        ObjectInputStream objectIn = new ObjectInputStream(fileIn);
        Object obj = objectIn.readObject();
        objectIn.close();
        return obj;
    }

    // Método que dá return do Objeto (aka todos os dados)
    public Objeto returnObjeto() throws RemoteException {
        return this.objeto;
    }

    // Método que dá print de "Running"
    public void sayHello() throws RemoteException {
        System.out.println("Running");
    }

    // Método que dá sempre return true, usamos para saber se um RMI Server está ou
    // não a funcionar
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
            // caso o fs.txt tiver dados escritos
            if (file.length() != 0) {
                rmis.objeto = (Objeto) rmis.ReadObjectFromFile("fs.txt");
            }
            // caso o fs.txt não tiver dados escritos
            else {
                rmis.objeto = new Objeto();
            }
            r = LocateRegistry.createRegistry(PORT_r);
            r.rebind("RMI_Server", rmis);
            System.out.println("RMIServer ready.");

        } catch (FileNotFoundException e) {
            System.out.println("FileNotFoundException!");
            rmis.objeto = new Objeto();
        } catch (Exception re) {
            System.out.println("Exception in RMIServer.main: " + re);

            boolean rmiFails = true;
            while (rmiFails) {
                rmiFails = false;

                // caso o RMI Server principal dê problemas, dar 5 pings a ver se volta a
                // funcionar
                // caso não volte a funcionar, o RMI Server secundário assume-se como primário
                while (ping < 5) {
                    try {
                        r = LocateRegistry.getRegistry(PORT_r);
                        rmis2 = (RMIServer_I) r.lookup("RMI_Server");
                        // se entrar no if é porque o RMI Server primário está funcional
                        if (rmis2.returnIsAlive()) {
                            Thread.sleep(1000);
                            ping = 0;
                            System.out.println("ping: " + ping);
                            System.out.println("RMIServer primario esta funcional");
                        }
                    } catch (Exception ke) {
                        // se entrar aqui é porque o RMI Server primário está a ter problemas e então
                        // vamos dar 5 pings para ver se ele recupera
                        ping++;
                        System.out.println("ping: " + ping);
                    }
                }

                // caso o RMI Server primário não recupere, o RMI Server secundário vai
                // assumir-se como primário
                try {
                    File file = new File(outputFilePath);
                    if (file.length() != 0) {
                        rmis.objeto = (Objeto) rmis.ReadObjectFromFile("fs.txt");
                    } else {
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
