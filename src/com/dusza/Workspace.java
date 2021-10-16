package com.dusza;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Workspace {
    public static final String VERSION_CONTROL_DIRECTORY = ".dusza";
    public static final String HEAD_FILE = "head.txt";

    private Path workspacePath;
    private Path versionControlPath;
    private Path headPath;
    private int currentCommit = 0;
    private String author = "undefined";

    public boolean init() {
        if(IOHandler.initWorkspace(this)) {
            currentCommit = IOHandler.getCurrentCommit(this);

            List<Path> files = IOHandler.readFiles(this);
            List<String> changes = new ArrayList<>();
            files.forEach(p -> changes.add(String.format("uj %s %s", p.getFileName().toString(), IOHandler.pathToDate(p))));

            Commit newCommit = new Commit(
                    currentCommit+1,
                    currentCommit == 0 ? "-" : currentCommit+"",
                    author,
                    new Date(System.currentTimeMillis()),
                    "Initial commit",
                    changes
            );

            incrementCommitNumber();

            IOHandler.createCommit(this, newCommit);

            return true;
        }
        return false;
    }

    public boolean commit(String description) {
        List<String> changes = IOHandler.getChanges(this);

        if(changes.size() == 0) return false;

        Commit newCommit = new Commit(
                currentCommit+1,
                currentCommit == 0 ? "-" : currentCommit+"",
                author,
                new Date(System.currentTimeMillis()),
                description,
                changes
        );

        incrementCommitNumber();

        IOHandler.createCommit(this, newCommit);

        return true;
    }

    private void incrementCommitNumber() {
        currentCommit++;
        IOHandler.writeHead(this, currentCommit);
    }

    public List<String> getCommits() {
        return IOHandler.readAllCommits(this);
    }

    public String getCommitDetails(int index) {
        return IOHandler.readCommitDetails(this, index);
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
