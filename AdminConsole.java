
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
        System.out.print(s);
    }

    public static void main(String[] args) {
        // Variáveis Gerais
        String option, opcao;

        // Variáveis OPTION 1
        String nome, funcao, password, dep_fac, contacto, morada, num_cc, val_cc;
        Pessoa pessoa;

        // Variáveis OPTION 2/3
        int ano_i, mes_i, dia_i, hora_i, minuto_i, ano_f, mes_f, dia_f, hora_f, minuto_f;
        Date date_i = new Date();
        Date date_f = new Date();
        String titulo, descricao, restricao, old_titulo;
        ArrayList<HashMap<String, ListaCandidato>> lista_lista_candidato = new ArrayList<>();
        Eleicao eleicao;

        // Variáveis OPTION 4
        String nome_lista, tipo_lista, nome_pessoa;
        int num_pessoas_lista;
        ArrayList<String> lista = new ArrayList<>();
        ListaCandidato lista_candidato;
        String nome_eleicao;

        System.getProperties().put("java.security.policy", "policy.all");
        System.setSecurityManager(new RMISecurityManager());

        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        Scanner scanner = new Scanner(System.in);

        try {
            RMIServer_I rmis = (RMIServer_I) LocateRegistry.getRegistry(6969).lookup("RMI_Server");
            AdminConsole ac = new AdminConsole();
            rmis.subscribe("Admin Console", ac);
            System.out.println("Client sent subscription to RMIServer");

            do {
                System.out.println("------------MENU PRINCIPAL------------\n"
                        + "* ESCOLHA UMA DAS OPCOES DISPONIVEIS *\n" + "______________________________________\n"
                        + "1.Registar Pessoa\n" + "2.Criar Eleicao\n" + "3.Alterar Propriedades de uma Eleicao\n"
                        + "4.Gerir Candidatos\n" + "5.Consultar Informacao de Voto\n"
                        + "6.Consultar Numero de Eleitores\n" + "7.Consultar Resultados\n" + "8.Print Objeto\n"
                        + "9.Sair\n" + "______________________________________\n");
                System.out.print("Escolha: ");
                option = reader.readLine();

                switch (option) {
                case "1": // "1.Registar Pessoas"
                    System.out.println("> Escolha uma das opcoes abaixo disponiveis de acordo com a Funcao da pessoa:\n"
                            + "_____________________________________________________________________________\n"
                            + "1.Estudante\n" + "2.Docente\n" + "3.Funcionario\n" + "4.Sair\n"
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
                    System.out.print("> Contacto Telefonico: ");
                    contacto = reader.readLine();
                    System.out.print("> Morada: ");
                    morada = reader.readLine();
                    System.out.print("> Numero do Cartao de Cidadao: ");
                    num_cc = reader.readLine();
                    System.out.print("> Validade do Cartao de Cidadao: ");
                    val_cc = reader.readLine();

                    switch (funcao) {
                    case "1":
                        System.out.println("Vou guardar os dados de um(a) Estudante");
                        break;
                    case "2":
                        System.out.println("Vou guardar os dados de um(a) Docente");
                        break;
                    case "3":
                        System.out.println("Vou guardar os dados de um(a) Funcionario");
                        break;
                    }
                    System.out.println("> Nome: " + nome);
                    System.out.println("> Password: " + password);
                    System.out.println("> Departamento: " + dep_fac);
                    System.out.println("> Contacto Telefonico: " + contacto);
                    System.out.println("> Morada: " + morada);
                    System.out.println("> Numero do Cartao de Cidadao: " + num_cc);
                    System.out.println("> Validade do Cartao de Cidadao: " + val_cc);
                    System.out.println("----------------------------------------------------\n");

                    pessoa = new Pessoa(nome, funcao, password, dep_fac, contacto, morada, num_cc, val_cc);

                    rmis.regista_pessoa(pessoa);

                    break;
                case "2": // "2.Criar Eleição"
                    System.out.print("> Ano Inicio: ");
                    ano_i = Integer.parseInt(scanner.nextLine());
                    System.out.print("> Mes Inicio: ");
                    mes_i = Integer.parseInt(scanner.nextLine());
                    System.out.print("> Dia Inicio: ");
                    dia_i = Integer.parseInt(scanner.nextLine());
                    System.out.print("> Hora Inicio: ");
                    hora_i = Integer.parseInt(scanner.nextLine());
                    System.out.print("> Minuto Inicio: ");
                    minuto_i = Integer.parseInt(scanner.nextLine());
                    System.out.print("> Ano Fim: ");
                    ano_f = Integer.parseInt(scanner.nextLine());
                    System.out.print("> Mes Fim: ");
                    mes_f = Integer.parseInt(scanner.nextLine());
                    System.out.print("> Dia Fim: ");
                    dia_f = Integer.parseInt(scanner.nextLine());
                    System.out.print("> Hora Fim: ");
                    hora_f = Integer.parseInt(scanner.nextLine());
                    System.out.print("> Minuto Fim: ");
                    minuto_f = Integer.parseInt(scanner.nextLine());

                    Date de_i = new Date(ano_i, mes_i, dia_i, hora_i, minuto_i); // Date inicial que queremos criar
                    Date de_f = new Date(ano_f, mes_f, dia_f, hora_f, minuto_f); // Date final que queremos criar
                    Date d = new Date(); // Current date

                    boolean check_i = de_i.before(d);
                    boolean check_f = de_f.before(de_i);

                    if (check_i) {
                        System.out.println("Nao pode criar uma eleicao com uma Date inicial que ja passou!");
                        break;
                    }

                    if (check_f) {
                        System.out.println("Nao pode criar um eleicao cuja Date final seja anterior a Date inicial!");
                        break;
                    }

                    System.out.print("> Titulo: ");
                    titulo = reader.readLine();
                    System.out.println("> Descricao: ");
                    System.out.println(
                            "> Escolha uma das opcoes abaixo disponiveis de acordo com a Descricao da eleicao:\n"
                                    + "_____________________________________________________________________________\n"
                                    + "1.Estudante\n" + "2.Docente\n" + "3.Funcionario\n"
                                    + "_____________________________________________________________________________\n");
                    System.out.print("Escolha: ");
                    descricao = reader.readLine();

                    System.out
                            .print("> Restricao:\n" + "_____________________________________________________________\n"
                                    + "* ESCREVA O NOME DO DEP OU '0' PARA NAO HAVER RESTRICAO *\n"
                                    + "_____________________________________________________________\n");
                    System.out.print("Escolha: ");
                    restricao = reader.readLine();

                    if (restricao.equals("0")) {
                        System.out.println("Vou guardar os dados de uma eleicao que nao tem restricao");
                    } else {
                        System.out.println(
                                "Vou guardar os dados de uma eleicao que tem a restricao de apenas poderem votar pessoas do Departamento: "
                                        + restricao);
                    }

                    System.out.println("> Ano Inicio: " + ano_i);
                    System.out.println("> Mes Inicio: " + mes_i);
                    System.out.println("> Dia Inicio: " + dia_i);
                    System.out.println("> Hora Inicio: " + hora_i);
                    System.out.println("> Minuto Inicio: " + minuto_i);
                    System.out.println("> Ano Fim: " + ano_f);
                    System.out.println("> Mes Fim: " + mes_f);
                    System.out.println("> Dia Fim: " + dia_f);
                    System.out.println("> Hora Fim: " + hora_f);
                    System.out.println("> Minuto Fim: " + minuto_f);
                    System.out.println("> Titulo: " + titulo);
                    System.out.println("> Descricao: " + descricao);
                    System.out.println(
                            "____________________________________________________________________________________________________________________________________\n");

                    date_i = new Date(ano_i - 1900, mes_i - 1, dia_i, hora_i, minuto_i);
                    date_f = new Date(ano_f - 1900, mes_f - 1, dia_f, hora_f, minuto_f);

                    eleicao = new Eleicao(ano_i, mes_i, dia_i, hora_i, minuto_i, ano_f, mes_f, dia_f, hora_f, minuto_f,
                            titulo, descricao, restricao, "", lista_lista_candidato, date_i, date_f);

                    rmis.cria_eleicao(eleicao);

                    break;

                case "3": // "3.Alterar Propriedades de uma Eleição"
                    System.out.print("> Título da Eleicao: ");
                    old_titulo = reader.readLine();

                    boolean check = rmis.check_eleicao_before(old_titulo);

                    if (check) {
                        System.out.println("Nao pode alterar as propriedades da eleicao pois ja comecou!");
                        break;
                    }

                    System.out.print("> Novo Ano Inicio: ");
                    ano_i = Integer.parseInt(scanner.nextLine());
                    System.out.print("> Novo Mes Inicio: ");
                    mes_i = Integer.parseInt(scanner.nextLine());
                    System.out.print("> Novo Dia Inicio: ");
                    dia_i = Integer.parseInt(scanner.nextLine());
                    System.out.print("> Nova Hora Inicio: ");
                    hora_i = Integer.parseInt(scanner.nextLine());
                    System.out.print("> Novo Minuto Inicio: ");
                    minuto_i = Integer.parseInt(scanner.nextLine());
                    System.out.print("> Novo Ano Fim: ");
                    ano_f = Integer.parseInt(scanner.nextLine());
                    System.out.print("> Novo Mes Fim: ");
                    mes_f = Integer.parseInt(scanner.nextLine());
                    System.out.print("> Novo Dia Fim: ");
                    dia_f = Integer.parseInt(scanner.nextLine());
                    System.out.print("> Nova Hora Fim: ");
                    hora_f = Integer.parseInt(scanner.nextLine());
                    System.out.print("> Novo Minuto Fim: ");
                    minuto_f = Integer.parseInt(scanner.nextLine());
                    System.out.print("> Novo Titulo: ");
                    titulo = reader.readLine();
                    System.out.println("> Nova Descricao: ");
                    System.out.println(
                            "> Escolha uma das opcoes abaixo disponiveis de acordo com a Descricao da eleicao:\n"
                                    + "_____________________________________________________________________________\n"
                                    + "1.Estudante\n" + "2.Docente\n" + "3.Funcionario\n"
                                    + "_____________________________________________________________________________\n");
                    System.out.print("Escolha: ");
                    descricao = reader.readLine();
                    System.out.print(
                            "> Nova Restricao:\n" + "_____________________________________________________________\n"
                                    + "* ESCREVA O NOME DO DEP OU '0' PARA NAO HAVER RESTRICAO *\n"
                                    + "_____________________________________________________________\n");
                    System.out.print("Escolha: ");
                    restricao = reader.readLine();

                    if (restricao.equals("0")) {
                        System.out.println("Vou substituir os dados de uma eleicao que nao tem restricao");
                    } else {
                        System.out.println(
                                "Vou substituir os dados de uma eleicao que tem a restricao de apenas poderem votar pessoas do Departamento: "
                                        + restricao);
                    }

                    System.out.println("> Ano Inicio: " + ano_i);
                    System.out.println("> Mes Inicio: " + mes_i);
                    System.out.println("> Dia Inicio: " + dia_i);
                    System.out.println("> Hora Inicio: " + hora_i);
                    System.out.println("> Minuto Inicio: " + minuto_i);
                    System.out.println("> Ano Fim: " + ano_f);
                    System.out.println("> Mes Fim: " + mes_f);
                    System.out.println("> Dia Fim: " + dia_f);
                    System.out.println("> Hora Fim: " + hora_f);
                    System.out.println("> Minuto Fim: " + minuto_f);
                    System.out.println("> Titulo: " + titulo);
                    System.out.println("> Descricao: " + descricao);
                    System.out.println(
                            "____________________________________________________________________________________________________________________________________\n");

                    date_i = new Date(ano_i - 1900, mes_i - 1, dia_i, hora_i, minuto_i);
                    date_f = new Date(ano_f - 1900, mes_f - 1, dia_f, hora_f, minuto_f);

                    eleicao = new Eleicao(ano_i, mes_i, dia_i, hora_i, minuto_i, ano_f, mes_f, dia_f, hora_f, minuto_f,
                            titulo, descricao, restricao, old_titulo, lista_lista_candidato, date_i, date_f);

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
                                    + "3.Funcionarios\n" + "4.Sair\n" + "______________________________________\n");
                            System.out.print("Escolha: ");
                            tipo_lista = reader.readLine();

                            if (tipo_lista.equals("4")) {
                                break;
                            }
                            System.out.print("> Nome da Eleicao: ");
                            nome_eleicao = reader.readLine();

                            boolean verify = rmis.check_eleicao_before(nome_eleicao);
                            if (verify) {
                                System.out.println("Nao e possivel criar uma lista pois a eleicao ja comecou!");
                                break;
                            }

                            System.out.print("> Nome da Lista: ");
                            nome_lista = reader.readLine();
                            System.out.print("> Numero de Pessoas Pertencentes a Lista: ");
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
                                System.out.println("Vou guardar os dados de uma lista candidata do tipo Funcionarios");
                                break;
                            }
                            System.out.println("> Nome da Lista: " + nome_lista);
                            System.out.println("> Numero de Pessoas Pertencentes a Lista: " + num_pessoas_lista);
                            System.out.println("Vou printar o nome das pessoas pertencentes a lista");
                            for (String l : lista) {
                                System.out.println("> " + l);
                            }
                            System.out.println("----------------------------------------------------------------\n");

                            lista_candidato = new ListaCandidato(nome_lista, tipo_lista, num_pessoas_lista, lista,
                                    nome_eleicao);

                            rmis.cria_lista_candidatos(lista_candidato);

                            break;
                        case "2":
                            System.out.print("> Nome da Eleicao: ");
                            nome_eleicao = reader.readLine();

                            boolean c = rmis.check_eleicao_before(nome_eleicao);
                            if (c) {
                                System.out.println("Nao e possivel remover uma lista pois a eleicao ja comecou!");
                                break;
                            }

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
                case "5": // "6.Consultar Informação de Voto"
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
                case "6": // "7.Consultar Número de Eleitores"
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
                case "7": // "8.Consultar Resultados"
                    System.out.println("AdminConsole - consulta_resultados");

                    HashMap<String, Eleicao> mape = rmis.consulta_resultados();

                    // Percorrer eleições
                    for (Map.Entry mapElement : mape.entrySet()) {
                        Eleicao e = (Eleicao) mapElement.getValue();
                        boolean verifica = rmis.check_consulta_resultados(e.getTitulo());
                        if (verifica) {
                            System.out.println("-------------------------------------------------\n"
                                    + "***RESULTADOS DA ELEICAO " + e.getTitulo() + "***\n"
                                    + "-------------------------------------------------\n"
                                    + "Numero de votos em Branco: " + e.getNum_votos_branco() + " ("
                                    + ((double) e.getNum_votos_branco() * 100 / (double) e.getNum_total_votos())
                                    + "%)");

                            // Percorrer lista de listas de candidatos
                            for (HashMap<String, ListaCandidato> llc : e.lista_lista_candidato) {

                                // Percorrer lista de candidatos
                                for (Map.Entry<String, ListaCandidato> entry : llc.entrySet()) {
                                    System.out.println("Numero de votos da lista de candidatos '"
                                            + entry.getValue().getNome_lista() + '\'' + ": "
                                            + entry.getValue().getNum_votos() + " ("
                                            + ((double) entry.getValue().getNum_votos() * 100
                                                    / (double) e.getNum_total_votos())
                                            + "%)");
                                }
                            }
                        }
                    }
                    break;
                case "8":
                    Objeto ob = (Objeto) rmis.ReadObjectFromFile("fs.txt");
                    System.out.println(ob.toString());
                    break;
                case "9": // "9.Sair"
                    rmis.unsubscribe(ac);
                    break;
                }
            } while (!option.equals("9"));
        } catch (Exception re) {
            System.out.println("Exception in AdminConsole.main: " + re);
        }
    }
}