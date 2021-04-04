import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.io.*;
import java.util.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.RMISecurityManager;
import java.util.concurrent.ConcurrentHashMap;

public class AdminConsole extends UnicastRemoteObject implements AdminConsole_I {
    private static final long serialVersionUID = 1L;
    private static RMIServer_I rmis;
    private static AdminConsole_I ac;

    public AdminConsole() throws RemoteException {
        super();
    }

    public void print_on_admin_console(String s) throws RemoteException {
        System.out.print(s);
        try {
            rmis.sayHello();
        }
        catch(RemoteException e){
            int contador=0;
            while(contador<30)
            {
                try {
                    Thread.sleep(1000);
                    rmis = (RMIServer_I) LocateRegistry.getRegistry(6969).lookup("RMI_Server");
                    break;
                }catch(NotBoundException | InterruptedException | RemoteException m){
                    contador++;
                    if(contador==30)
                        System.exit(-1);
                }
            }
        }
    }

    public static void main(String[] args) {
        // Variáveis Gerais
        String option, opcao;

        // Variáveis OPTION 1
        String nome, funcao, password, dep_fac, contacto, morada, num_cc, val_cc;
        Pessoa pessoa;

        // Variáveis OPTION 2/3
        int ano_i, mes_i, dia_i, hora_i, minuto_i, ano_f, mes_f, dia_f, hora_f, minuto_f;
        Date date_i;
        Date date_f;
        String titulo, descricao, restricao, old_titulo;
        ArrayList<HashMap<String, ListaCandidato>> lista_lista_candidato = new ArrayList<>();
        Eleicao eleicao;

        // Variáveis OPTION 4
        String nome_lista, tipo_lista="", nome_pessoa;
        int num_pessoas_lista;
        ArrayList<String> lista = new ArrayList<>();
        ListaCandidato lista_candidato;
        String nome_eleicao;

        // Variáveis OPTION 5
        String dep;

        System.getProperties().put("java.security.policy", "policy.all");
        System.setSecurityManager(new RMISecurityManager());

        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        Scanner scanner = new Scanner(System.in);

        while (true) {
            try {
                rmis = (RMIServer_I) LocateRegistry.getRegistry(6969).lookup("RMI_Server");
                ac = new AdminConsole();
                rmis.subscribe("Admin Console", ac);
                System.out.println("Client sent subscription to RMIServer");

                do {
                    System.out.println("------------MENU PRINCIPAL------------\n"
                            + "* ESCOLHA UMA DAS OPCOES DISPONIVEIS *\n" + "______________________________________\n"
                            + "1.Registar Pessoa\n" + "2.Criar Eleicao\n" + "3.Alterar Propriedades de uma Eleicao\n"
                            + "4.Gerir Candidatos\n" + "5.Gerir Mesas\n" + "6.Consultar Informacao de Voto\n"
                            + "7.Consultar Numero de Eleitores\n" + "8.Consultar Resultados\n" + "9.Print Objeto\n"
                            + "10.Sair\n" + "______________________________________\n");
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
                            while (true) {
                                try {
                                    rmis.regista_pessoa(pessoa);
                                    break;
                                } catch (RemoteException e) {
                                    int contador=0;
                                    while(contador<30) {
                                        try {
                                            Thread.sleep(1000);
                                            rmis = (RMIServer_I) LocateRegistry.getRegistry(6969).lookup("RMI_Server");
                                            rmis.sayHello();
                                            break;
                                        }catch(NotBoundException | InterruptedException | RemoteException m){
                                            contador++;
                                            if(contador==30)
                                                System.exit(-1);
                                        }
                                    }
                                }
                            }
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
                            Date de_i = new Date(ano_i - 1900, mes_i - 1, dia_i, hora_i, minuto_i); // Date inicial que queremos criar
                            Date de_f = new Date(ano_f - 1900, mes_f - 1, dia_f, hora_f, minuto_f); // Date final que queremos criar
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

                            System.out.print("> Restricao:\n" + "_____________________________________________________________\n"
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
                            Date d2 = new Date(); // Current date
                            boolean check_i2 = de_i.before(d2);
                            if (check_i2) {
                                System.out.println("Nao pode criar uma eleicao com uma Date inicial que ja passou!");
                                break;
                            }
                            eleicao = new Eleicao(ano_i, mes_i, dia_i, hora_i, minuto_i, ano_f, mes_f, dia_f, hora_f, minuto_f,
                                    titulo, descricao, restricao, "", lista_lista_candidato, date_i, date_f);
                            while (true) {
                                try {
                                    rmis.cria_eleicao(eleicao);
                                    break;
                                } catch (RemoteException e) {
                                    int contador=0;
                                    while(contador<30) {
                                        try {
                                            Thread.sleep(1000);
                                            rmis = (RMIServer_I) LocateRegistry.getRegistry(6969).lookup("RMI_Server");
                                            rmis.sayHello();
                                            break;
                                        }catch(NotBoundException | InterruptedException | RemoteException m){
                                            contador++;
                                            if(contador==30)
                                                System.exit(-1);
                                        }
                                    }
                                }
                            }
                            break;
                        case "3": // "3.Alterar Propriedades de uma Eleição"
                            boolean check_date = true, check_name;
                            System.out.print("> Título da Eleicao: ");
                            old_titulo = reader.readLine();
                            while (true) {
                                try {
                                    check_name = rmis.check_eleicao_exists(old_titulo);
                                    if (check_name) {
                                        check_date = rmis.check_eleicao_before(old_titulo);
                                    }
                                    break;
                                } catch (RemoteException e) {
                                    int contador=0;
                                    while(contador<30) {
                                        try {
                                            Thread.sleep(1000);
                                            rmis = (RMIServer_I) LocateRegistry.getRegistry(6969).lookup("RMI_Server");
                                            rmis.sayHello();
                                            break;
                                        }catch(NotBoundException | InterruptedException | RemoteException m){
                                            contador++;
                                            if(contador==30)
                                                System.exit(-1);
                                        }
                                    }
                                }
                            }
                            if (!check_name) {
                                System.out.println("Nao pode alterar as propriedades de uma eleicao que nao existe!");
                                break;
                            }
                            if (check_date) {
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
                            date_i = new Date(ano_i - 1900, mes_i - 1, dia_i, hora_i, minuto_i);
                            date_f = new Date(ano_f - 1900, mes_f - 1, dia_f, hora_f, minuto_f);
                            Date cd = new Date(); // Current date
                            boolean check_2 = date_i.before(cd);
                            if (check_2) {
                                System.out.println("Nao pode criar uma eleicao com uma Date inicial que ja passou!");
                                break;
                            }
                            while (true) {
                                try {
                                    check_date = rmis.check_eleicao_before(old_titulo);
                                    break;
                                } catch (RemoteException e) {
                                    int contador=0;
                                    while(contador<30) {
                                        try {
                                            Thread.sleep(1000);
                                            rmis = (RMIServer_I) LocateRegistry.getRegistry(6969).lookup("RMI_Server");
                                            rmis.sayHello();
                                            break;
                                        }catch(NotBoundException | InterruptedException | RemoteException m){
                                            contador++;
                                            if(contador==30)
                                                System.exit(-1);
                                        }
                                    }
                                }
                            }
                            if (check_date) {
                                System.out.println("Nao pode alterar as propriedades da eleicao pois ja comecou!");
                                break;
                            }
                            System.out.println("Vou substituir os dados de uma eleicao");
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
                            System.out.println("____________________________________________________________________________________________________________________________________\n");
                            eleicao = new Eleicao(ano_i, mes_i, dia_i, hora_i, minuto_i, ano_f, mes_f, dia_f, hora_f, minuto_f,
                                    titulo, "", "", old_titulo, lista_lista_candidato, date_i, date_f);
                            while (true) {
                                try {
                                    rmis.altera_eleicao(eleicao);
                                    break;
                                } catch (RemoteException e) {
                                    int contador=0;
                                    while(contador<30) {
                                        try {
                                            Thread.sleep(1000);
                                            rmis = (RMIServer_I) LocateRegistry.getRegistry(6969).lookup("RMI_Server");
                                            rmis.sayHello();
                                            break;
                                        }catch(NotBoundException | InterruptedException | RemoteException m){
                                            contador++;
                                            if(contador==30)
                                                System.exit(-1);
                                        }
                                    }
                                }
                            }
                            break;
                        case "4": // "4.Gerir Candidatos"
                            try {
                                boolean verify_date = true, verify_nome_eleicao;
                                System.out.println("-----MENU GERE LISTAS CANDIDATOS------\n"
                                        + "* ESCOLHA UMA DAS OPCOES DISPONIVEIS *\n"
                                        + "______________________________________\n" + "1.Adicionar Lista\n"
                                        + "2.Remover Lista\n" + "3.Sair\n" + "______________________________________\n");
                                System.out.print("Escolha: ");
                                opcao = reader.readLine();
                                switch (opcao) {
                                    case "1":
                                        System.out.print("> Nome da Eleicao: ");
                                        nome_eleicao = reader.readLine();
                                        while (true) {
                                            try {
                                                verify_nome_eleicao = rmis.check_eleicao_exists(nome_eleicao);
                                                if (verify_nome_eleicao) {
                                                    tipo_lista = rmis.returnTipo_lista(nome_eleicao);
                                                    verify_date = rmis.check_eleicao_before(nome_eleicao);
                                                }
                                                break;
                                            } catch (RemoteException e) {
                                                int contador=0;
                                                while(contador<30) {
                                                    try {
                                                        Thread.sleep(1000);
                                                        rmis = (RMIServer_I) LocateRegistry.getRegistry(6969).lookup("RMI_Server");
                                                        rmis.sayHello();
                                                        break;
                                                    }catch(NotBoundException | InterruptedException | RemoteException m){
                                                        contador++;
                                                        if(contador==30)
                                                            System.exit(-1);
                                                    }
                                                }
                                            }
                                        }
                                        if (!verify_nome_eleicao) {
                                            System.out.println("Nao e possivel criar uma lista pois a eleicao nao existe!");
                                            break;
                                        }
                                        if (verify_date) {
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
                                        while (true) {
                                            try {
                                                verify_date = rmis.check_eleicao_before(nome_eleicao);
                                                break;
                                            } catch (RemoteException e) {
                                                int contador=0;
                                                while(contador<30) {
                                                    try {
                                                        Thread.sleep(1000);
                                                        rmis = (RMIServer_I) LocateRegistry.getRegistry(6969).lookup("RMI_Server");
                                                        rmis.sayHello();
                                                        break;
                                                    }catch(NotBoundException | InterruptedException | RemoteException m){
                                                        contador++;
                                                        if(contador==30)
                                                            System.exit(-1);
                                                    }
                                                }
                                            }
                                        }
                                        if (verify_date) {
                                            System.out.println("Nao e possivel criar uma lista pois a eleicao ja comecou!");
                                            break;
                                        }
                                        System.out.println("Vou guardar os dados de uma lista candidata da eleicao " + nome_eleicao);
                                        System.out.println("> Nome da Lista: " + nome_lista);
                                        System.out.println("> Numero de Pessoas Pertencentes a Lista: " + num_pessoas_lista);
                                        System.out.println("Vou printar o nome das pessoas pertencentes a lista");
                                        for (String l : lista) {
                                            System.out.println("> " + l);
                                        }
                                        System.out.println("----------------------------------------------------------------\n");
                                        lista_candidato = new ListaCandidato(nome_lista, tipo_lista, num_pessoas_lista, lista, nome_eleicao);
                                        while (true) {
                                            try {
                                                rmis.cria_lista_candidatos(lista_candidato);
                                                break;
                                            } catch (RemoteException e) {
                                                int contador=0;
                                                while(contador<30) {
                                                    try {
                                                        Thread.sleep(1000);
                                                        rmis = (RMIServer_I) LocateRegistry.getRegistry(6969).lookup("RMI_Server");
                                                        rmis.sayHello();
                                                        break;
                                                    }catch(NotBoundException | InterruptedException | RemoteException m){
                                                        contador++;
                                                        if(contador==30)
                                                            System.exit(-1);
                                                    }
                                                }
                                            }
                                        }
                                        break;
                                    case "2":
                                        boolean check_eleicao_before = true;
                                        System.out.print("> Nome da Eleicao: ");
                                        nome_eleicao = reader.readLine();
                                        while (true) {
                                            try {
                                                verify_nome_eleicao = rmis.check_eleicao_exists(nome_eleicao);
                                                if (verify_nome_eleicao) {
                                                    check_eleicao_before = rmis.check_eleicao_before(nome_eleicao);
                                                }
                                                break;
                                            } catch (RemoteException e) {
                                                int contador=0;
                                                while(contador<30) {
                                                    try {
                                                        Thread.sleep(1000);
                                                        rmis = (RMIServer_I) LocateRegistry.getRegistry(6969).lookup("RMI_Server");
                                                        rmis.sayHello();
                                                        break;

                                                    }catch(NotBoundException | InterruptedException | RemoteException m){
                                                        contador++;
                                                        if(contador==30)
                                                            System.exit(-1);
                                                    }
                                                }
                                            }
                                        }
                                        if (!verify_nome_eleicao) {
                                            System.out.println("Nao e possivel criar uma lista pois a eleicao nao existe!");
                                            break;
                                        }
                                        if (check_eleicao_before) {
                                            System.out.println("Nao e possivel remover uma lista pois a eleicao ja comecou!");
                                            break;
                                        }
                                        System.out.print("> Nome da Lista: ");
                                        nome_lista = reader.readLine();
                                        while (true) {
                                            try {
                                                check_eleicao_before = rmis.check_eleicao_before(nome_eleicao);
                                                break;
                                            } catch (RemoteException e) {
                                                int contador=0;
                                                while(contador<30) {
                                                    try {
                                                        Thread.sleep(1000);
                                                        rmis = (RMIServer_I) LocateRegistry.getRegistry(6969).lookup("RMI_Server");
                                                        rmis.sayHello();
                                                        break;

                                                    }catch(NotBoundException | InterruptedException | RemoteException m){
                                                        contador++;
                                                        if(contador==30)
                                                            System.exit(-1);
                                                    }
                                                }
                                            }
                                        }
                                        if (check_eleicao_before) {
                                            System.out.println("Nao e possivel remover uma lista pois a eleicao ja comecou!");
                                            break;
                                        }
                                        System.out.println("Vou remover os dados de uma lista candidata");
                                        System.out.println("> Nome da Lista: " + nome_lista);
                                        System.out.println("----------------------------------------------------------------\n");
                                        lista_candidato = new ListaCandidato(nome_lista, "0", 0, lista, nome_eleicao);
                                        while (true) {
                                            try {
                                                rmis.remove_lista_candidatos(lista_candidato);
                                                break;
                                            } catch (RemoteException e) {
                                                int contador=0;
                                                while(contador<30) {
                                                    try {
                                                        Thread.sleep(1000);
                                                        rmis = (RMIServer_I) LocateRegistry.getRegistry(6969).lookup("RMI_Server");
                                                        rmis.sayHello();
                                                        break;
                                                    }catch(NotBoundException | InterruptedException | RemoteException m){
                                                        contador++;
                                                        if(contador==30)
                                                            System.exit(-1);
                                                    }
                                                }
                                            }
                                        }
                                        break;
                                    case "3":
                                        break;
                                }

                            }  catch (RemoteException e) {
                                int contador=0;
                                while(contador<30) {
                                    try {
                                        Thread.sleep(1000);
                                        rmis = (RMIServer_I) LocateRegistry.getRegistry(6969).lookup("RMI_Server");
                                        rmis.sayHello();
                                        break;
                                    }catch(NotBoundException | InterruptedException | RemoteException m){
                                        contador++;
                                        if(contador==30)
                                            System.exit(-1);
                                    }
                                }
                            }
                            break;
                        case "5":
                            try {
                                System.out.println("---------MENU GERE MESAS----------\n"
                                        + "* ESCOLHA UMA DAS OPCOES DISPONIVEIS *\n"
                                        + "______________________________________\n" + "1.Adicionar Mesa\n" + "2.Remover Mesa\n"
                                        + "3.Pingar\n" + "4.Sair\n" + "______________________________________\n");
                                System.out.print("Escolha: ");
                                opcao = reader.readLine();
                                switch (opcao) {
                                    case "1":
                                        System.out.print("> Departamento onde quer adicionar a Mesa: ");
                                        dep = reader.readLine();
                                        System.out.println("Vou guardar os dados de uma Mesa");
                                        System.out.print("> Departamento onde esta a Mesa: " + dep);
                                        System.out.println("----------------------------------------------------------------\n");
                                        /*while (true) {
                                            try {
                                                rmis.subscribeMesa(dep);
                                                break;
                                            } catch (RemoteException e) {
                                                int contador=0;
                                                while(contador<30) {
                                                    try {
                                                        Thread.sleep(1000);
                                                        rmis = (RMIServer_I) LocateRegistry.getRegistry(6969).lookup("RMI_Server");
                                                        rmis.sayHello();
                                                        break;

                                                    }catch(NotBoundException | InterruptedException | RemoteException m){
                                                        contador++;
                                                        if(contador==30)
                                                            System.exit(-1);
                                                    }
                                                }
                                            }
                                        }*/
                                        break;
                                    case "2":
                                        System.out.print("> Departamento onde esta a Mesa: ");
                                        dep = reader.readLine();
                                        System.out.println("Vou remover os dados de uma Mesa");
                                        System.out.print("> Departamento onde estava a Mesa: " + dep);
                                        System.out.println("----------------------------------------------------------------\n");
                                        while (true) {
                                            try {
                                                rmis.unsubscribeMesa(dep);
                                                break;
                                            } catch (RemoteException e) {
                                                int contador=0;
                                                while(contador<30) {
                                                    try {
                                                        Thread.sleep(1000);
                                                        rmis = (RMIServer_I) LocateRegistry.getRegistry(6969).lookup("RMI_Server");
                                                        rmis.sayHello();
                                                        break;
                                                    }catch(NotBoundException | InterruptedException | RemoteException m){
                                                        contador++;
                                                        if(contador==30)
                                                            System.exit(-1);
                                                    }
                                                }
                                            }
                                        }
                                        break;
                                    case "3":
                                        while (true) {
                                            try {
                                                rmis.checkActiveMesas(ac);
                                                break;
                                            } catch (RemoteException e) {
                                                int contador=0;
                                                while(contador<30) {
                                                    try {
                                                        Thread.sleep(1000);
                                                        rmis = (RMIServer_I) LocateRegistry.getRegistry(6969).lookup("RMI_Server");
                                                        rmis.sayHello();
                                                        break;

                                                    }catch(NotBoundException | InterruptedException | RemoteException m){
                                                        contador++;
                                                        if(contador==30)
                                                            System.exit(-1);
                                                    }
                                                }
                                            }
                                        }
                                        break;
                                    case "4":
                                        break;
                                }

                            } catch (RemoteException e) {
                                int contador=0;
                                while(contador<30) {
                                    try {
                                        Thread.sleep(1000);
                                        rmis = (RMIServer_I) LocateRegistry.getRegistry(6969).lookup("RMI_Server");
                                        rmis.sayHello();
                                        break;
                                    }catch(NotBoundException | InterruptedException | RemoteException m){
                                        contador++;
                                        if(contador==30)
                                            System.exit(-1);
                                    }
                                }
                            }
                            break;
                        case "6": // "6.Consultar Informação de Voto"
                            System.out.println("AdminConsole - consulta_info_voto");
                            while (true) {
                                try {
                                    HashMap<String, HashMap<String, Pessoa>> hmp = rmis.consulta_info_voto();
                                    for (Map.Entry mapElement : hmp.entrySet()) {
                                        ConcurrentHashMap<String, Pessoa> hm = (ConcurrentHashMap<String, Pessoa>) mapElement.getValue();
                                        for (Map.Entry mapElement2 : hm.entrySet()) {
                                            Pessoa p = (Pessoa) mapElement2.getValue();
                                            System.out.println(p.getNome() + ": " + p.getLocal_momento_voto());
                                        }
                                    }
                                    break;
                                } catch (RemoteException e) {
                                    int contador=0;
                                    while(contador<30) {
                                        try {
                                            Thread.sleep(1000);
                                            rmis = (RMIServer_I) LocateRegistry.getRegistry(6969).lookup("RMI_Server");
                                            rmis.sayHello();
                                            break;
                                        }catch(NotBoundException | InterruptedException | RemoteException m){
                                            contador++;
                                            if(contador==30)
                                                System.exit(-1);
                                        }
                                    }
                                }
                            }

                            break;
                        case "7": // "7.Consultar Número de Eleitores"
                            System.out.println("AdminConsole - consulta_eleitores");
                            while (true) {
                                try {
                                    HashMap<String, Mesa> mapm = rmis.consulta_eleitores();
                                    for (Map.Entry mapElement : mapm.entrySet()) {
                                        Mesa m = (Mesa) mapElement.getValue();
                                        System.out.println(m.getDep() + ": " + m.getNum_eleitores());
                                    }
                                    break;
                                } catch (RemoteException e) {
                                    int contador = 0;
                                    while (contador < 30) {
                                        try {
                                            Thread.sleep(1000);
                                            rmis = (RMIServer_I) LocateRegistry.getRegistry(6969).lookup("RMI_Server");
                                            rmis.sayHello();
                                            break;

                                        } catch (NotBoundException | InterruptedException | RemoteException m) {
                                            contador++;
                                            if (contador == 30)
                                                System.exit(-1);
                                        }
                                    }
                                }
                            }
                            break;
                        case "8": // "8.Consultar Resultados"
                            System.out.println("AdminConsole - consulta_resultados");
                            while (true) {
                                try {
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
                                } catch (RemoteException e) {
                                    int contador=0;
                                    while(contador<30) {
                                        try {
                                            Thread.sleep(1000);
                                            rmis = (RMIServer_I) LocateRegistry.getRegistry(6969).lookup("RMI_Server");
                                            rmis.sayHello();
                                            break;
                                        }catch(NotBoundException | InterruptedException | RemoteException m){
                                            contador++;
                                            if(contador==30)
                                                System.exit(-1);
                                        }
                                    }
                                }
                            }
                            break;
                        case "9": // "9.Print Objeto"
                            while (true) {
                                try {
                                    if (rmis.returnObjeto() != null) {
                                        System.out.println(rmis.returnObjeto().toString());
                                    }
                                    else {
                                        System.out.println("fs.txt nao tem dados escritos!");
                                    }
                                    break;
                                } catch (RemoteException e) {
                                    int contador=0;
                                    while(contador<30) {
                                        try {
                                            Thread.sleep(1000);
                                            rmis = (RMIServer_I) LocateRegistry.getRegistry(6969).lookup("RMI_Server");
                                            rmis.sayHello();
                                            break;
                                        }catch(NotBoundException | InterruptedException | RemoteException m){
                                            contador++;
                                            if(contador==30)
                                                System.exit(-1);
                                        }
                                    }
                                }
                            }
                            break;
                        case "10": // "10.Sair"
                            while (true) {
                                try {
                                    rmis.unsubscribe(ac);
                                    System.exit(0);
                                    break;
                                } catch (RemoteException e) {
                                    int contador=0;
                                    while(contador<30) {
                                        try {
                                            Thread.sleep(1000);
                                            rmis = (RMIServer_I) LocateRegistry.getRegistry(6969).lookup("RMI_Server");
                                            rmis.sayHello();
                                            break;
                                        }catch(NotBoundException | InterruptedException | RemoteException m){
                                            contador++;
                                            if(contador==30)
                                                System.exit(-1);
                                        }
                                    }
                                }
                            }
                            break;
                    }
                } while (!option.equals("10"));
            } catch (RemoteException | NotBoundException e) {
                int contador=0;
                while(contador<30) {
                    try {
                        Thread.sleep(1000);
                        rmis = (RMIServer_I) LocateRegistry.getRegistry(6969).lookup("RMI_Server");
                        rmis.sayHello();
                        break;
                    }catch(NotBoundException | InterruptedException | RemoteException m){
                        contador++;
                        if(contador==30)
                            System.exit(-1);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}