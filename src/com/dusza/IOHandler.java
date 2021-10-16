package com.dusza;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class IOHandler {
    // Methods
    private static List<String> readFile(Path path){
        List<String> data = new ArrayList<>();
        try(Scanner input = new Scanner(Files.newBufferedReader(path)))
        {
            while(input.hasNextLine()) {
                data.add(input.nextLine());
            }
        } catch (IOException e) {
            System.out.println("File named " + path + " not found.");
        }

        return data;
    }

    public static List<Path> readFiles(Workspace workspace) {
        return readFiles(workspace.getWorkspacePath(), false);
    }

    public static List<Path> readFiles(Workspace workspace, boolean directoriesAllowed) {
        return readFiles(workspace.getWorkspacePath(), directoriesAllowed);
    }

    public static List<Path> readFiles(Path path, boolean directoriesAllowed) {
        List<Path> data = new ArrayList<>();

        try {
            DirectoryStream<Path> stream = Files.newDirectoryStream(path);
            for (Path p : stream) {
                if (directoriesAllowed) {
                    data.add(p);
                } else if (!Files.isDirectory(p)) {
                    data.add(p);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    public static void copyFile(Path o, Path d) {
        try {
            Files.copy(o, d, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println("File copy failed" + o + " " + d);
            e.printStackTrace();
        }
    }

    public static List<String> readAllCommits(Workspace workspace) {
        List<Path> files = readFiles(workspace.getVersionControlPath(), true);
        List<String> sFiles = new ArrayList<>();

        files.remove(files.size()-1); // remove head.txt

        for (Path p : files) {
            sFiles.add(p.getFileName().toString());
        }

        return sFiles;
    }

    public static String readCommitDetails(Workspace workspace, int commitIndex) {
        Path commitPath = getCommitDetailsPath(workspace, commitIndex);
        return String.join("\n", readFile(commitPath));
    }

    public static Path getCommitDetailsPath(Workspace workspace, int commitIndex) {
        return workspace.getVersionControlPath().resolve(commitIndex + ".commit/commit.details");
    }

    public static Commit loadCommit(Workspace workspace, int commitIndex) {
        Commit newCommit = null;
        Path commitPath = getCommitDetailsPath(workspace, commitIndex);
        List<String> commitData = readFile(commitPath);

        try(Scanner scanner = new Scanner(commitPath)) {
            scanner.useDelimiter(" ");

            scanner.next();
            scanner.skip(scanner.delimiter());
            String parent = scanner.nextLine();

            scanner.next();
            scanner.skip(scanner.delimiter());
            String author = scanner.nextLine();

            scanner.next();
            scanner.skip(scanner.delimiter());
            String date = scanner.nextLine();

            scanner.next();
            scanner.skip(scanner.delimiter());
            String decs = scanner.nextLine();

            List<String> changes = new ArrayList<>();
            while (scanner.hasNextLine()) {
                changes.add(scanner.nextLine());
            }

            newCommit = new Commit(commitIndex, parent, author, Commit.getDateFromString(date), decs, changes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return newCommit;
    }

    public static String pathToDate(Path p) {
        Date date = new Date(p.toFile().lastModified());
        return Commit.formatDate(date);
    }

    public static List<String> getChanges(Workspace workspace) {
        List<String> changes = new ArrayList<>();
        List<Path> files = readFiles(workspace);
        List<Path> oldFiles = readFiles(workspace.getVersionControlPath().resolve(getCurrentCommit(workspace) + ".commit"), false);

        Path commitDetailsPath = workspace.getVersionControlPath().resolve(getCurrentCommit(workspace) + ".commit").resolve("commit.details");

        oldFiles.remove(commitDetailsPath);

        // uj
        boolean found;
        for(Path n : files) {
            found = false;
            for(Path o : oldFiles) {
                if(n.getFileName().toString().equals(o.getFileName().toString())) {
                    found = true;
                    break;
                }
            }
            if(!found) changes.add(String.format("uj %s %s", n.getFileName(), pathToDate(n)));
        }

        // torolt

        for(Path o : oldFiles) {
            found = false;
            for(Path n : files) {
                if(n.getFileName().toString().equals(o.getFileName().toString())) {
                    found = true;
                    break;
                }
            }
            if(!found) changes.add(String.format("torolt %s %s", o.getFileName(), pathToDate(o)));
        }

        // modositott

        for(Path n : files) {
            for(Path o : oldFiles) {
                if(n.getFileName().toString().equals(o.getFileName().toString())) {
                    if(!pathToDate(n).equals(pathToDate(o))) {
                        changes.add(String.format("valtozott %s %s", n.getFileName(), pathToDate(n)));
                    }
                    break;
                }
            }
        }

        return changes;
    }

    public static boolean initWorkspace(Workspace workspace) {
        try {
            if(Files.notExists(workspace.getVersionControlPath())) {
                Files.createDirectory(workspace.getVersionControlPath());
                Files.createFile(workspace.getHeadPath());
                writeHead(workspace, 0);
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static int getCurrentCommit(Workspace workspace) {
        List<String> head = readFile(workspace.getHeadPath());
        return Integer.parseInt(head.get(0));
    }

    public static void writeHead(Workspace workspace, int index) {
        try(BufferedWriter writer = Files.newBufferedWriter(workspace.getHeadPath())) {
            writer.write(index + "");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void copyFiles(List<Path> inputPaths, Path copyPath) {
        for (Path p : inputPaths) {
            copyFile(p, copyPath.resolve(p.getFileName()));
        }

    }

    public static void createCommit(Workspace workspace, Commit commit) {
        try {
            Path newCommitP = workspace.getVersionControlPath().resolve(commit.getId() + ".commit");
            Files.createDirectory(newCommitP);

            List<Path> paths = readFiles(workspace, false);
            copyFiles(paths, newCommitP);

            // create commit.details
            Path commitDetails = newCommitP.resolve("commit.details");
            Files.createFile(commitDetails);


            try (BufferedWriter writer = new BufferedWriter(new FileWriter(String.valueOf(commitDetails)))) {
                writer.write(String.format("Szulo: %s\n", commit.getParent()));
                writer.write(String.format("Szerzo: %s\n", commit.getAuthor()));
                writer.write(String.format("Datum: %s\n", Commit.formatDate(commit.getCreationDate())));
                writer.write(String.format("Commit leiras: %s\n", commit.getDescription()));
                writer.write(String.format("Valtozott: \n%s", String.join("\n", commit.getChanges())));
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void deleteFiles(Workspace workspace) {
        List<Path> files = readFiles(workspace, false);
        for(Path p : files) {
            try {
                Files.delete(p);
            } catch (IOException e) {
                System.out.println("Unable to delete " + p + ".");
                e.printStackTrace();
            }
        }
    }

    public static void copyCommitToWorkspace(Workspace workspace, int index) {
        deleteFiles(workspace);
        Path commitDir = workspace.getVersionControlPath().resolve(index + ".commit");
        List<Path> files = readFiles(commitDir, false);
        files.remove(commitDir.resolve("commit.details"));
        copyFiles(files, workspace.getWorkspacePath());
    }

}
