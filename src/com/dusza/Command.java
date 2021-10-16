package com.dusza;

public class Command {
    private String name;
    private String description;
    private Runnable runnable;

    public Command(String name, String description, Runnable runnable) {
        this.name = name;
        this.description = description;
        this.runnable = runnable;
    }

    public void run() {
        runnable.run();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
