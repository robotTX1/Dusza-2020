package com.dusza;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Workspace {
    private Path path;
    private int currentCommit = 0;

    public boolean init() {
        return true;
    }

    public boolean commit() {
        return true;
    }

    public List<String> getCommits() {
        List<String> result = new ArrayList<>();

        return result;
    }

    public String getCommitDetails(int index) {
        return "";
    }

    public int getCurrentCommit() {
        return currentCommit;
    }

    public void setPath(Path path) {
        this.path = path;
    }
}
