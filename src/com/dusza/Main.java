package com.dusza;

public class Main {

    public static void main(String[] args) {
        Workspace workspace = new Workspace();
	    CommandLineInterface cli = new CommandLineInterface(workspace);

        cli.start();
    }
}
