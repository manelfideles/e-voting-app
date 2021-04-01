
//import java.rmi.Naming;
//import java.rmi.*;
//import java.rmi.server.*;
//import java.net.*;
//import java.rmi.registry.Registry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.io.*;
import java.util.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.RMISecurityManager;

public class AdminConsole extends UnicastRemoteObject implements AdminConsole_I {
    private static final long serialVersionUID = 1L;

    public AdminConsole() throws RemoteException {
        super();
    }

    public void print_on_admin_console(String s) throws RemoteException {
        System.out.println("> PRINT ON ADMIN CONSOLE\n> " + s);
    }

    public static void main(String[] args) {
        // Variáveis Gerais
        String option, opcao;

        // Variáveis OPTION 1
        String nome, funcao, password, dep_fac, contacto, morada, num_cc, val_cc;
        Pessoa pessoa;

        // Variáveis OPTION 2/3
        String data_i, hora_i, minuto_i, data_f, hora_f, minuto_f, titulo, descricao, restricao, old_titulo;
        ArrayList<HashMap<String, ListaCandidato>> lista_lista_candidato = new ArrayList<>();
        Eleicao eleicao;

        // Variáveis OPTION 4
        String nome_lista, tipo_lista, nome_pessoa;
        int num_pessoas_lista;
        ArrayList<String> lista = new ArrayList<>();
        ListaCandidato lista_candidato;
        String nome_eleicao;

        // Variáveis OPTION 5
        String dep;
        Mesa mesa;

        System.getProperties().put("java.security.policy", "policy.all");
        System.setSecurityManager(new RMISecurityManager());

        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        try {
            RMIServer_I rmis = (RMIServer_I) LocateRegistry.getRegistry(6969).lookup("RMI_Server");
            AdminConsole ac = new AdminConsole();
            rmis.subscribe("Admin Console", ac);
            System.out.println("Client sent subscription to RMIServer");

            do {
                System.out.println("------------MENU PRINCIPAL------------\n"
                        + "* ESCOLHA UMA DAS OPCOES DISPONIVEIS *\n" + "______________________________________\n"
                        + "1.Registar Pessoa\n" + "2.Criar Eleição\n" + "3.Alterar Propriedades de uma Eleição\n"
                        + "4.Gerir Candidatos\n" + "5.Gerir Mesas de Voto\n" + "6.Consultar Informação de Voto\n"
                        + "7.Consultar Número de Eleitores\n" + "8.Consultar Resultados\n" + "9.Sair\n"
                        + "______________________________________\n");
                System.out.print("Escolha: ");
                option = reader.readLine();

                switch (option) {
                    case "1": // "1.Registar Pessoas"
                        System.out.println("> Escolha uma das opções abaixo disponíveis de acordo com a Função da pessoa:\n"
                                + "_____________________________________________________________________________\n"
                                + "1.Estudante\n" + "2.Docente\n" + "3.Funcionári@\n" + "4.Sair\n"
                                + "_____________________________________________________________________________\n");
                        System.out.print("Escolha: ");
                        funcao = reader.readLine();

                        if (funcao.equals("4")) {
                            break;
                        }

                        System.out.print("> Departamento: ");
                        dep_fac = reader.readLine();
                        System.out.print("> Nome: ");
                        nome = reader.readLine();
                        System.out.print("> Password: ");
                        password = reader.readLine();
                        System.out.print("> Contacto Telefónico: ");
                        contacto = reader.readLine();
                        System.out.print("> Morada: ");
                        morada = reader.readLine();
                        System.out.print("> Número do Cartão de Cidadão: ");
                        num_cc = reader.readLine();
                        System.out.print("> Validade do Cartão de Cidadão: ");
                        val_cc = reader.readLine();

                        switch (funcao) {
                            case "1":
                                System.out.println("Vou guardar os dados de um(a) Estudante");
                                break;
                            case "2":
                                System.out.println("Vou guardar os dados de um(a) Docente");
                                break;
                            case "3":
                                System.out.println("Vou guardar os dados de um(a) Funcionári@");
                                break;
                        }
                        System.out.println("> Nome: " + nome);
                        System.out.println("> Password: " + password);
                        System.out.println("> Departamento: " + dep_fac);
                        System.out.println("> Contacto Telefónico: " + contacto);
                        System.out.println("> Morada: " + morada);
                        System.out.println("> Número do Cartão de Cidadão: " + num_cc);
                        System.out.println("> Validade do Cartão de Cidadão: " + val_cc);
                        System.out.println("----------------------------------------------------\n");

                        pessoa = new Pessoa(nome, funcao, password, dep_fac, contacto, morada, num_cc, val_cc);

                        rmis.regista_pessoa(pessoa);

                        break;
                    case "2": // "2.Criar Eleição"
                        System.out.print("> Data Início: ");
                        data_i = reader.readLine();
                        System.out.print("> Hora Início: ");
                        hora_i = reader.readLine();
                        System.out.print("> Minuto Início: ");
                        minuto_i = reader.readLine();
                        System.out.print("> Data Fim: ");
                        data_f = reader.readLine();
                        System.out.print("> Hora Fim: ");
                        hora_f = reader.readLine();
                        System.out.print("> Minuto Fim: ");
                        minuto_f = reader.readLine();
                        System.out.print("> Título: ");
                        titulo = reader.readLine();
                        System.out.print("> Descrição: ");
                        descricao = reader.readLine();
                        System.out
                                .print("> Restrição:\n" + "_____________________________________________________________\n"
                                        + "* ESCREVA O NOME DO DEP OU '0' PARA NÃO HAVER RESTRIÇÃO *\n"
                                        + "_____________________________________________________________\n");
                        System.out.print("Escolha: ");
                        restricao = reader.readLine();

                        if (restricao.equals("0")) {
                            System.out.println("Vou guardar os dados de uma eleicao que não tem restrição");
                        } else {
                            System.out.println(
                                    "Vou guardar os dados de uma eleição que tem a restrição de apenas poderem votar pessoas do Departamento: "
                                            + restricao);
                        }

                        System.out.println("> Data Início: " + data_i);
                        System.out.println("> Hora Início: " + hora_i);
                        System.out.println("> Minuto Início: " + minuto_i);
                        System.out.println("> Data Fim: " + data_f);
                        System.out.println("> Hora Fim: " + hora_f);
                        System.out.println("> Minuto Fim: " + minuto_f);
                        System.out.println("> Título: " + titulo);
                        System.out.println("> Descrição: " + descricao);
                        System.out.println(
                                "____________________________________________________________________________________________________________________________________\n");

                        eleicao = new Eleicao(data_i, hora_i, minuto_i, data_f, hora_f, minuto_f, titulo, descricao,
                                restricao, "", lista_lista_candidato);

                        rmis.cria_eleicao(eleicao);

                        break;

                    case "3": // "3.Alterar Propriedades de uma Eleição"
                        System.out.print("> Título da Eleição: ");
                        old_titulo = reader.readLine();

                        System.out.print("> Nova Data Início: ");
                        data_i = reader.readLine();
                        System.out.print("> Nova Hora Início: ");
                        hora_i = reader.readLine();
                        System.out.print("> Novo Minuto Início: ");
                        minuto_i = reader.readLine();
                        System.out.print("> Nova Data Fim: ");
                        data_f = reader.readLine();
                        System.out.print("> Nova Hora Fim: ");
                        hora_f = reader.readLine();
                        System.out.print("> Novo Minuto Fim: ");
                        minuto_f = reader.readLine();
                        System.out.print("> Novo Título: ");
                        titulo = reader.readLine();
                        System.out.print("> Nova Descrição: ");
                        descricao = reader.readLine();
                        System.out.print(
                                "> Nova Restricao:\n" + "_____________________________________________________________\n"
                                        + "* ESCREVA O NOME DO DEP OU '0' PARA NÃO HAVER RESTRIÇÃO *\n"
                                        + "_____________________________________________________________\n");
                        System.out.print("Escolha: ");
                        restricao = reader.readLine();

                        if (restricao.equals("0")) {
                            System.out.println("Vou substituir os dados de uma eleição que não tem restrição");
                        } else {
                            System.out.println(
                                    "Vou substituir os dados de uma eleição que tem a restrição de apenas poderem votar pessoas do Departamento: "
                                            + restricao);
                        }

                        System.out.println("> Data Início: " + data_i);
                        System.out.println("> Hora Início: " + hora_i);
                        System.out.println("> Minuto Início: " + minuto_i);
                        System.out.println("> Data Fim: " + data_f);
                        System.out.println("> Hora Fim: " + hora_f);
                        System.out.println("> Minuto Fim: " + minuto_f);
                        System.out.println("> Título: " + titulo);
                        System.out.println("> Descrição: " + descricao);
                        System.out.println(
                                "____________________________________________________________________________________________________________________________________\n");

                        eleicao = new Eleicao(data_i, hora_i, minuto_i, data_f, hora_f, minuto_f, titulo, descricao,
                                restricao, old_titulo, lista_lista_candidato);

                        rmis.altera_eleicao(eleicao);

                        break;
                    case "4": // "4.Gerir Candidatos"
                        try {
                            System.out.println("-----MENU GERE LISTAS CANDIDATOS------\n"
                                    + "* ESCOLHA UMA DAS OPCOES DISPONIVEIS *\n"
                                    + "______________________________________\n" + "1.Adicionar Lista\n"
                                    + "2.Remover Lista\n" + "3.Sair\n" + "______________________________________\n");
                            System.out.print("Escolha: ");
                            opcao = reader.readLine();

                            switch (opcao) {
                                case "1":
                                    System.out.println("> --------MENU ADICIONA LISTA---------\n"
                                            + "* ESCOLHA UMA DAS OPCOES DISPONIVEIS *\n"
                                            + "______________________________________\n" + "1.Estudantes\n" + "2.Docentes\n"
                                            + "3.Funcionári@s\n" + "4.Sair\n" + "______________________________________\n");
                                    System.out.print("Escolha: ");
                                    tipo_lista = reader.readLine();

                                    if (tipo_lista.equals("4")) {
                                        break;
                                    }
                                    System.out.print("> Nome da Eleição: ");
                                    nome_eleicao = reader.readLine();
                                    System.out.print("> Nome da Lista: ");
                                    nome_lista = reader.readLine();
                                    System.out.print("> Número de Pessoas Pertencentes à Lista: ");
                                    Scanner scanner = new Scanner(System.in);
                                    num_pessoas_lista = Integer.parseInt(scanner.nextLine());

                                    lista.clear();

                                    for (int i = 0; i < num_pessoas_lista; i++) {
                                        System.out.print("> Nome: ");
                                        nome_pessoa = reader.readLine();
                                        lista.add(nome_pessoa);
                                    }

                                    Collections.sort(lista);

                                    switch (tipo_lista) {
                                        case "1":
                                            System.out.println("Vou guardar os dados de uma lista candidata do tipo Estudantes");
                                            break;
                                        case "2":
                                            System.out.println("Vou guardar os dados de uma lista candidata do tipo Docentes");
                                            break;
                                        case "3":
                                            System.out.println("Vou guardar os dados de uma lista candidata do tipo Funcionári@s");
                                            break;
                                    }
                                    System.out.println("> Nome da Lista: " + nome_lista);
                                    System.out.println("> Número de Pessoas Pertencentes à Lista: " + num_pessoas_lista);
                                    System.out.println("Vou printar o nome das pessoas pertencentes à lista");
                                    for (String l : lista) {
                                        System.out.println("> " + l);
                                    }
                                    System.out.println("----------------------------------------------------------------\n");

                                    lista_candidato = new ListaCandidato(nome_lista, tipo_lista, num_pessoas_lista, lista,
                                            nome_eleicao);

                                    rmis.cria_lista_candidatos(lista_candidato);

                                    break;
                                case "2":
                                    System.out.print("> Nome da Eleição: ");
                                    nome_eleicao = reader.readLine();
                                    System.out.print("> Nome da Lista: ");
                                    nome_lista = reader.readLine();

                                    System.out.println("Vou remover os dados de uma lista candidata");
                                    System.out.println("> Nome da Lista: " + nome_lista);
                                    System.out.println("----------------------------------------------------------------\n");

                                    lista_candidato = new ListaCandidato(nome_lista, "0", 0, lista, nome_eleicao);

                                    rmis.remove_lista_candidatos(lista_candidato);

                                    break;
                                case "3":
                                    break;
                            }

                        } catch (Exception re) {
                            System.out.println("Exception in AdminConsole.gere_lista_candidatos: " + re);
                        }
                        break;
                    case "5": // "5.Gerir Mesas de Voto"
                        try {
                            System.out.println(
                                    "-----------MENU GERE MESAS------------\n" + "* ESCOLHA UMA DAS OPCOES DISPONIVEIS *\n"
                                            + "______________________________________\n" + "1.Criar Mesa\n"
                                            + "2.Remover Mesa\n" + "3.Consultar Estado das Mesas\n" + "4.Sair\n"
                                            + "______________________________________\n");
                            System.out.print("Escolha: ");
                            opcao = reader.readLine();

                            switch (opcao) {
                                case "1":
                                    System.out.print("> Departamento onde quer adicionar uma mesa: ");
                                    dep = reader.readLine();
                                    System.out.println("Vou guardar os dados de uma mesa de voto");
                                    System.out.println("> Departamento onde está localizada: " + dep);
                                    System.out.println("-----------------------------------------------------\n");

                                    mesa = new Mesa(dep);

                                    rmis.cria_mesa(mesa);

                                    break;
                                case "2":
                                    System.out.print("> Departamento onde está localizada: ");
                                    dep = reader.readLine();
                                    System.out.println("Vou apagar os dados de uma mesa de voto");
                                    System.out.println("> Departamento onde estava localizada: " + dep);
                                    System.out.println("-----------------------------------------------------\n");

                                    mesa = new Mesa(dep);

                                    rmis.remove_mesa(mesa);

                                    break;
                                case "3":
                                    System.out.println("> Consultar Estado das Mesas");

                                    rmis.consulta_estado_mesas();

                                    break;
                                case "4":
                                    break;
                            }
                        } catch (Exception re) {
                            System.out.println("Exception in AdminConsole.gere_mesas: " + re);
                        }
                        break;
                    case "6": // "6.Consultar Informação de Voto"
                        System.out.println("AdminConsole - consulta_info_voto");
                        HashMap<String, HashMap<String, Pessoa>> hmp = rmis.consulta_info_voto();
                        for (Map.Entry mapElement : hmp.entrySet()) {
                            HashMap<String, Pessoa> hm = (HashMap<String, Pessoa>) mapElement.getValue();
                            for (Map.Entry mapElement2 : hm.entrySet()) {
                                Pessoa p = (Pessoa) mapElement2.getValue();
                                System.out.println(p.getNome() + ": " + p.getLocal_momento_voto());
                            }
                        }
                        break;
                    case "7": // "7.Consultar Número de Eleitores"
                        System.out.println("AdminConsole - consulta_eleitores");
                        HashMap<String, Mesa> mapm = rmis.consulta_eleitores();
                        for (Map.Entry mapElement : mapm.entrySet()) {
                            Mesa m = (Mesa) mapElement.getValue();
                            for (Map.Entry mapElement2 : m.getNum_eleitores().entrySet()) {
                                System.out.println(m.dep + ":");
                                System.out.println(mapElement2.getKey() + ": " + mapElement2.getValue());
                            }
                        }
                        break;
                    case "8": // "8.Consultar Resultados"
                        System.out.println("AdminConsole - consulta_resultados");

                        HashMap<String, Eleicao> mape = rmis.consulta_resultados();

                        // Percorrer eleições
                        for (Map.Entry mapElement : mape.entrySet()) {

                            Eleicao e = (Eleicao) mapElement.getValue();

                            System.out.println("-------------------------------------------------\n"
                                    + "***RESULTADOS DA ELEIÇÃO " + e.getTitulo() + "***\n"
                                    + "-------------------------------------------------\n" + "Número de votos em Branco: "
                                    + e.getNum_votos_branco() + "("
                                    + ((double) e.getNum_votos_branco() * 100 / (double) e.getNum_total_votos()) + "%)");

                            // if (tempo atual > tempo término eleição)

                            // Percorrer lista de listas de candidatos
                            for (HashMap<String, ListaCandidato> llc : e.lista_lista_candidato) {

                                // Percorrer lista de candidatos
                                for (Map.Entry<String, ListaCandidato> entry : llc.entrySet()) {
                                    System.out.print(
                                            "Número de votos da lista de candidatos '" + entry.getValue().getNome_lista()
                                                    + '\'' + ": " + entry.getValue().getNum_votos() + "("
                                                    + ((double) entry.getValue().getNum_votos() * 100
                                                    / (double) e.getNum_total_votos())
                                                    + "%)");

                                }
                            }

                        }
                        break;
                    case "9": // "9.Sair"
                        break;

                    case "10":
                        Objeto ob = (Objeto) rmis.ReadObjectFromFile("fs.txt");
                        System.out.println(ob.toString());
                        break;
                }
            } while (!option.equals("9"));
        } catch (Exception re) {
            System.out.println("Exception in AdminConsole.main: " + re);
        }
    }
}