package com.dusza;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Workspace {
    public static final String VERSION_CONTROL_DIRECTORY = ".dusza";
    public static final String HEAD_FILE = "head.txt";

    private Path workspacePath;
    private Path versionControlPath;
    private Path headPath;
    private final int currentCommit = 0;

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

    public void setWorkspacePath(Path workspacePath) {
        this.workspacePath = workspacePath;
        updatePaths();
    }

    private void updatePaths() {
        versionControlPath = workspacePath.resolve(VERSION_CONTROL_DIRECTORY);
        headPath = versionControlPath.resolve(HEAD_FILE);
    }

    public Path getWorkspacePath() {
        return workspacePath;
    }

    public Path getVersionControlPath() {
        return versionControlPath;
    }

    public Path getHeadPath() {
        return headPath;
    }
}
