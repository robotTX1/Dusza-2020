package com.dusza;

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

    public static List<String> readFiles(Workspace workspace) {
        return readFiles(workspace.getWorkspacePath(), false);
    }

    public static List<String> readFiles(Workspace workspace, boolean directoriesAllowed) {
        return readFiles(workspace.getWorkspacePath(), directoriesAllowed);
    }

    public static List<String> readFiles(Path path, boolean directoriesAllowed) {
        List<String> data = new ArrayList<>();

        try {
            DirectoryStream<Path> stream = Files.newDirectoryStream(path);
            for (Path p : stream) {
                if (directoriesAllowed) {
                    data.add(p.toString());
                } else if (!Files.isDirectory(p)) {
                    data.add(p.toString());
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
        List<String> files = readFiles(workspace.getVersionControlPath(), true);
        files.remove(files.size()-1); // remove head.txt

        return files;
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

    public static HashMap<Path,ChangeType> getChanges(Workspace workspace) {
        HashMap<Path,ChangeType> changes = new HashMap<>();
        List<String> files = readFiles(workspace);

        return changes;
    }

    public static void createCommit(Workspace workspace) {
        List<String> files = readFiles(workspace);

    }

    public static boolean initWorkspace(Workspace workspace) {
        try {
            if(Files.notExists(workspace.getVersionControlPath())) {
                Files.createDirectory(workspace.getVersionControlPath());
                Files.createFile(workspace.getHeadPath());
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
}
