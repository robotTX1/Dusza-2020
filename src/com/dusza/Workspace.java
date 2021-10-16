package com.dusza;

import java.nio.file.Path;
import java.util.*;

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

        int fresh = IOHandler.getCurrentCommit(this);

        Commit newCommit = new Commit(
                fresh+1,
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

    public boolean changeCommit(int index) {
        int size = IOHandler.readFiles(this).size();

        if(!(index > 0 && index < size+1)) return false;
        setCommitNumber(index);

        IOHandler.copyCommitToWorkspace(this, index);

        return true;
    }

    public String getCommitTree() {
        StringBuilder builder = new StringBuilder();
        int max = IOHandler.readAllCommits(this).size();

        List<Commit> commitList = new ArrayList<>();

        for(int i=1; i<=max; i++) {
            commitList.add(IOHandler.loadCommit(this, i));
        }

        Collections.sort(commitList, Comparator.comparing(Commit::getParent));

        for(Commit c : commitList) {
            builder.append(" ".repeat(c.getParent().equals("-") ? 0 : Integer.parseInt(c.getParent())));
            builder.append(c.getId()+".commit").append("\n");
        }

        return builder.toString();
    }

    private void setCommitNumber(int index) {
        currentCommit = index;
    }

    private void incrementCommitNumber() {
        currentCommit++;
        IOHandler.incrementHead(this);
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

    public boolean isInitialized() {
        return IOHandler.workspaceIsInitialized(this);
    }
}
