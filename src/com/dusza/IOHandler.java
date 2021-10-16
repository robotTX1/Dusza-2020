package com.dusza;


import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class IOHandler {
        public static IOHandler single_instance = null;
        private Path mainPath;

        // constructor
        private IOHandler(Path path) {
            mainPath=path;
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
                System.out.println("File named " + file + " not found.");
            }

            return data;
        }

        public List<String> readFiles() {
            return readFiles(mainPath);
        }


        public List<String> readFiles(Path path) {
            List<String> data = new ArrayList<>();
            File folder = path.toFile();

            try {
                DirectoryStream<Path> stream = Files.newDirectoryStream(path);
                for (Path p : stream) {
                    if (!Files.isDirectory(p)) {
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
                System.out.println("File copy failed" + o.toString() + " " + d.toString());
                e.printStackTrace();
            }
        }

        public Commit loadCommit(int commitIndex) {
            Commit newCommit = new Commit();

            return newCommit;
        }

        public HashMap<Path,ChangeType> getChanges() {
            HashMap<Path,ChangeType> changes = new HashMap<Path, ChangeType>();
            List<String> files = readFiles();




            return changes;
        }

        public void createCommit() {
            List<String> files = readFiles();

        }

        public void initWorkspace() {

        }

        public int getCurrentCommit() {
            List<String> head = readFile(Paths.get(mainPath.toString()+"/.dusza/head.txt"));
            return Integer.parseInt(head.get(0));
        }





    }
