package com.dusza;

public enum ChangeType {
    CHANGED("valtozott"),
    NEW("uj"),
    DELETED("torolt");

    private final String name;

    ChangeType(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

}
