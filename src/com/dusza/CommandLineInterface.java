package com.dusza;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Scanner;

public class CommandLineInterface {
    private static final String exitCommand = "exit";
    private static final String helpCommand = "help";

    private final Scanner input = new Scanner(System.in);
    private final List<Command> commandList = new ArrayList<>();
    private final Workspace workspace;

    public CommandLineInterface(Workspace workspace) {
        this.workspace = workspace;

        Command commit = new Command("commit", "Lementi a mappa állapotát.", () -> {
            if(!workspace.isInitialized()) {
                System.out.println("Csak inicializálás után elérhető!");
                return;
            }


            System.out.print("Commit leírása: ");
            String description = input.nextLine();

            if(workspace.commit(description)) {
                System.out.println("Commit létrehozva");
            } else {
                System.out.println("Nem történt változás a fájlokban!");
            }
        });

        Command init = new Command("init", "Előkészíti a mappát a verzió követésre.", () -> {
            if(workspace.init()) System.out.println("Mappa készen áll a verzió követésre!");
            else System.out.println("A mappa már volt inicializálva!");
        });

        Command list = new Command("list", "Kilistázza az összes commitot.", () -> {
            if(!workspace.isInitialized()) {
                System.out.println("Csak inicializálás után elérhető!");
                return;
            }
            List<String> commits = workspace.getCommits();
            System.out.println("Commitok:");

            for(String s : commits) {
                System.out.println("\t" + s);
            }
        });

        Command readCommitDetails = new Command("commit-details", "Kiírja az adott commit leírását.", () -> {
            if(!workspace.isInitialized()) {
                System.out.println("Csak inicializálás után elérhető!");
                return;
            }
            System.out.print("Adja meg a commit számát: ");

            int index;
            index = input.nextInt();
            input.nextLine();

            String details = workspace.getCommitDetails(index);

            System.out.println(details);
        });

        Command changeCommit = new Command("change", "Betölti a megadott commitot", () -> {
            if(!workspace.isInitialized()) {
                System.out.println("Csak inicializálás után elérhető!");
                return;
            }
            System.out.print("Commit száma: ");
            int index = input.nextInt();
            input.nextLine();

            System.out.println("Biztosan betöltöd? (igen/nem) ");
            String command;

            while(true) {
                command = input.nextLine();

                if(command.equalsIgnoreCase("igen") || command.equalsIgnoreCase("i")) {
                    break;
                }
                if(command.equalsIgnoreCase("nem") || command.equalsIgnoreCase("n")) {
                    return;
                }

                System.out.println("Nincs ilyen opció!");
            }

            if(workspace.changeCommit(index)) {
                System.out.println("Commit betöltve!");
            } else {
                System.out.println("Nem létezik ilyen számú commit!");
            }
        });

        Command tree = new Command("tree", "Commit fát rajzol", () -> {
            if(!workspace.isInitialized()) {
                System.out.println("Csak inicializálás után elérhető!");
                return;
            }

            System.out.println(workspace.getCommitTree());
        });

        commandList.add(commit);
        commandList.add(init);
        commandList.add(list);
        commandList.add(readCommitDetails);
        commandList.add(changeCommit);
        //commandList.add(tree);
    }

    public void start() {
        System.out.println("Üdv a Dusza verzió követő programban!");
        System.out.println("Adja meg a veriókövetett mappa elérési útját vagy írja be, hogy " + exitCommand + " a kilépéshez!");


        String path;
        while(true) {
            System.out.print("Mappa elérési útja: ");
            path = input.nextLine().trim();
            if(path.equalsIgnoreCase(exitCommand)) {
                exit();
                return;
            }

            Path workPath = Paths.get(path);
            if(Files.exists(workPath) && Files.isDirectory(workPath)) {
                workspace.setWorkspacePath(workPath);
                break;
            }

            System.out.println("Érvénytelen elérési út: " + workPath.toAbsolutePath());
        }



        System.out.print("Kérem a felhasználó nevét: ");
        workspace.setAuthor(input.nextLine());

        System.out.println("Lehetséges parancsokért írd be: " + helpCommand);
        String command;
        while(true) {
            System.out.print("> ");
            command = input.nextLine().trim();

            // Commands
            for(Command c : commandList) {
                if(c.getName().equalsIgnoreCase(command)) {
                    c.run();
                    break;
                }
            }

            // Help
            if(command.equalsIgnoreCase(helpCommand)) help();

            // Exit
            if(command.equalsIgnoreCase(exitCommand)) {
                exit();
                return;
            }
        }
    }

    private void exit() {
        System.out.println("Köszönöm, hogy használtad a programot!");
        input.close();
    }

    private void help() {
        for(Command c : commandList) {
            System.out.printf("%s\t%s\n", c.getName(), c.getDescription());
        }
    }

}
