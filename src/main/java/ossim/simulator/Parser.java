package ossim.simulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import ossim.commands.*;
import ossim.exceptions.SimulatorSyntaxException;

public class Parser {

    public static ArrayList<Command> parseFile(String pathname) throws IOException, SimulatorSyntaxException{
        ArrayList<String> lines = readFile(pathname);
        ArrayList<Command> commands = new ArrayList<>();
        for(String line : lines){
            commands.addAll(parseLine(line));
        }
        return commands;
    }
    
    private static ArrayList<String> readFile(String pathname) throws IOException {
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

    private static ArrayList<Command> parseLine(String line) throws SimulatorSyntaxException {
        int index = line.indexOf("#");
        if(index >= 0){
            line = line.substring(0,index);
        }
        line = line.trim();
        String[] args = line.split("\\s+");
        ArrayList<Command> commands = new ArrayList<Command>();
        if(args.length == 0)
            return commands;
        switch (args[0]) {
            case "semWait":
                if (args.length != 2) {
                    throw new SimulatorSyntaxException("Command " + args[0] + " has wrong number of arguments");
                }
                commands.add(new SemWait(args[1]));
                break;
            case "semSignal":
                if (args.length != 2) {
                    throw new SimulatorSyntaxException("Command " + args[0] + " has wrong number of arguments");
                }
                commands.add(new SemSignal(args[1]));
                break;
            case "writeFile":
                if (args.length != 3) {
                    throw new SimulatorSyntaxException("Command " + args[0] + " has wrong number of arguments");
                }
                commands.add(new WriteFile(args[1], args[2]));
                break;
            case "print":
                if (args.length != 2) {
                    throw new SimulatorSyntaxException("Command " + args[0] + " has wrong number of arguments");
                }
                commands.add(new Print(args[1]));
                break;
            case "printFromTo":
                if (args.length != 3) {
                    throw new SimulatorSyntaxException("Command " + args[0] + " has wrong number of arguments");
                }
                commands.add(new Assign("counter", args[1]));
                commands.add(new JumpIfGreaterThan("counter", args[2], 3));
                commands.add(new Print("counter"));
                commands.add(new Increment("counter"));
                commands.add(new Jump(-4));
                break;
            case "assign":
                if (args.length == 4) {
                    if (!args[2].equals("readFile"))
                        throw new SimulatorSyntaxException(
                                "When assign command has 3 arguments, the second must be readFile command");
                    commands.add(new ReadFile(args[3], args[1]));
                } else if (args.length != 3) {
                    throw new SimulatorSyntaxException("Command " + args[0] + " has wrong number of arguments");
                } else {
                    if (args[2].equals("input"))
                        commands.add(new Input(args[1]));
                    else
                        commands.add(new Assign(args[1], args[2]));
                }
                break;
            default:
                throw new SimulatorSyntaxException("Unknown Command : " + args[0]);
        }
        return commands;
    }

}
