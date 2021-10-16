package com.dusza;

import java.nio.file.Path;
import java.util.List;

public class Workspace {
    private Path path;
    private int currentCommit = 0;

    public Workspace(Path path) {
        this.path = path;
    }

    public boolean init() {
        return true;
    }

    public boolean commit() {
        return true;
    }

}
