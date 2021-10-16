package com.dusza;


import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class IOHandler {
    public static final String VERSION_CONTROL_DIRECTORY = ".dusza";
    public static final String HEAD_FILE = "head.txt";

    public static IOHandler single_instance = null;
    private Path mainPath;
    private Path versionControlPath;
    private Path headPath;
    private final String data_pattern = "yyyy.MM.dd HH.mm.ss";


        // constructor
        private IOHandler(Path path) {
            this.mainPath = path;
            this.versionControlPath = mainPath.resolve(VERSION_CONTROL_DIRECTORY);
            this.headPath = versionControlPath.resolve(HEAD_FILE);
        }

        public static IOHandler init(Path path) {
            if (single_instance != null)
            {
                throw new AssertionError("You already initialized me");
            }

            single_instance = new IOHandler(path);
            return single_instance;
        }

        public static IOHandler getInstance() {
            if(single_instance == null) {
                throw new AssertionError("You have to call init first");
            }

            return single_instance;
        }


        // Methods
        private List<String> readFile(Path path){
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

        public List<String> readFiles() {
            return readFiles(mainPath, false);
        }

        public List<String> readFiles(boolean directoriesAllowed) {
            return readFiles(mainPath, directoriesAllowed);
        }

        public List<String> readFiles(Path path, boolean directoriesAllowed) {
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

        public void copyFile(Path o, Path d) {
            try {
                Files.copy(o, d, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                System.out.println("File copy failed" + o + " " + d);
                e.printStackTrace();
            }
        }

        public List<String> readAllCommits() {
            List<String> files = readFiles(versionControlPath, true);
            files.remove(files.size()-1); // remove head.txt

            return files;
        }

        public String readCommitDetails(int commitIndex) {
            Path commitPath = getCommitDetailsPath(commitIndex);
            return String.join("\n", readFile(commitPath));
        }

        public Path getCommitDetailsPath(int commitIndex) {
            return mainPath.resolve(VERSION_CONTROL_DIRECTORY + commitIndex + ".commit/commit.details");
        }

        public Commit loadCommit(int commitIndex) {
            Commit newCommit = null;
            Path commitPath = getCommitDetailsPath(commitIndex);
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

        public HashMap<Path,ChangeType> getChanges() {
            HashMap<Path,ChangeType> changes = new HashMap<>();
            List<String> files = readFiles();



            return changes;
        }

        public void createCommit() {
            List<String> files = readFiles();

        }

        public boolean initWorkspace() {
            //if(Files.notExists())


            return true;
        }

        public int getCurrentCommit() {
            List<String> head = readFile(Paths.get(mainPath.toString()+"/.dusza/head.txt"));
            return Integer.parseInt(head.get(0));
        }





    }
