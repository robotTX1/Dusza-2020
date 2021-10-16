package com.dusza;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CommandLineInterface {

    private final Scanner input = new Scanner(System.in);
    private static final List<Command> commandList = new ArrayList<>();
    private final Workspace workspace;

    public CommandLineInterface(Workspace workspace) {
        this.workspace = workspace;

        Command commit = new Command("commit", "Lementi a mappa állapotát.", workspace::commit);
        Command init = new Command("init", "Előkészíti a mappát a verzió követésre.", () -> {
            if(workspace.init()) System.out.println("Mappa készen áll a verzió követésre!");
            else System.out.println("A mappa már volt inicializálva!");
        });
        Command list = new Command("list", "Kilistázza az összes commitot.", () -> {

        });
    }

    public void start() {
        System.out.println("Üdv a Dusza verzió követő programban!");
        System.out.println("Lehetséges parancsokért írd be: help");



    }

    private void exit() {
        System.out.println("Köszönöm, hogy használtad a programot!");
        input.close();
    }

    private void help() {

    }

}
