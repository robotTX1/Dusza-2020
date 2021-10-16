package com.dusza;

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
            if(workspace.commit()) {
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
            List<String> commits = workspace.getCommits();
            System.out.println("Commitok:");

            for(String s : commits) {
                System.out.println("\t" + s);
            }
        });
        Command readCommitDetails = new Command("commit-details", "Kiírja az adott commit leírását.", () -> {
            System.out.print("Adja meg a commit számát: ");

            int index;
            index = input.nextInt();
            input.nextLine();

            String details = workspace.getCommitDetails(index);

            System.out.println(details);
        });

        commandList.add(commit);
        commandList.add(init);
        commandList.add(list);
        commandList.add(readCommitDetails);
    }

    public void start() {
        System.out.println("Üdv a Dusza verzió követő programban!");
        System.out.println("Lehetséges parancsokért írd be: " + helpCommand);

        String command;
        while(true) {
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
                break;
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
