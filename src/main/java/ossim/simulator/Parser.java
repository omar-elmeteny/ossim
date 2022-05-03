package ossim.simulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Parser {

    public static ArrayList<String> readFile(String pathname) throws IOException {
        File file = new File(pathname);
        BufferedReader br = new BufferedReader(new FileReader(file));
        try {
            String st;
            ArrayList<String> program = new ArrayList<>();
            while ((st = br.readLine()) != null) {
                program.add(st);
            }
            return program;
        } finally {
            br.close();
        }
    }

}
