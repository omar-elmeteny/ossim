package ossim.simulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import ossim.commands.*;

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

    public static ArrayList<Command> parseLine(String line){
        String[] args = line.split(" ");
        ArrayList<Command> commands = new ArrayList<Command>(); 
        switch (args[0]) {
            case "semWait":
                commands.add(new SemWait(args[1]));
                break;
            case "semSignal":
                commands.add(new SemSignal(args[1]));
                break;
            case "writeFile":
                commands.add(new WriteFile(args[1],args[2]));
                break;
            case "print":
                commands.add(new Print(args[1]));
                break;    
            case "printFromTo" :   
                commands.add(new Assign("counter", args[1]));
                commands.add(new JumpIfGreaterThan("counter", args[2], 3));
                commands.add(new Print("counter"));
                commands.add(new Increment("counter"));
                commands.add(new Jump(-4));
                break; 
            case "assign" :
                if(args.length == 4){
                    commands.add(new ReadFile(args[3], args[1]));
                }    
                else{
                    if(args[2].equals("input"))
                        commands.add(new Input(args[1]));
                    else
                        commands.add(new Assign(args[1], args[2]));    
                }
            default:
                break;
        }
        return commands;
    }

}
