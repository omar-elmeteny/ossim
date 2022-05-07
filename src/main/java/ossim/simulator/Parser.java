package ossim.simulator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import ossim.instructions.*;
import ossim.exceptions.SimulatorSyntaxException;

public class Parser {

    // special variable name used for the printFromTo instruction as the loop counter
    private final static String counterVariableName = "counter";

    // parsing every line in the program into instructions to be interpreted by the OperatingSystem class 
    public static ArrayList<Instruction> parseFile(String pathname) throws IOException, SimulatorSyntaxException{
        ArrayList<String> lines = readFile(pathname);
        ArrayList<Instruction> instructions = new ArrayList<>();
        for(String line : lines){
            instructions.addAll(parseLine(line));
        }
        return instructions;
    }
    
    // reading the text file into an array of lines
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

    // using factory method design pattern to create instructions
    private static ArrayList<Instruction> parseLine(String line) throws SimulatorSyntaxException {
        int index = line.indexOf("#"); // handling code comments
        if(index >= 0){
            line = line.substring(0,index);
        }
        line = line.trim();
        String[] args = line.split("\\s+");
        ArrayList<Instruction> instructions = new ArrayList<Instruction>();
        if(args.length == 0)
            return instructions;
        switch (args[0]) {
            case "semWait":
                if (args.length != 2) {
                    throw new SimulatorSyntaxException("Instruction " + args[0] + " has wrong number of arguments");
                }
                instructions.add(new SemWait(args[1]));
                break;
            case "semSignal":
                if (args.length != 2) {
                    throw new SimulatorSyntaxException("Instruction " + args[0] + " has wrong number of arguments");
                }
                instructions.add(new SemSignal(args[1]));
                break;
            case "writeFile":
                if (args.length != 3) {
                    throw new SimulatorSyntaxException("Instruction " + args[0] + " has wrong number of arguments");
                }
                instructions.add(new WriteFile(args[1], args[2]));
                break;
            case "print":
                if (args.length != 2) {
                    throw new SimulatorSyntaxException("Instruction " + args[0] + " has wrong number of arguments");
                }
                instructions.add(new Print(args[1]));
                break;
            case "printFromTo":
                if (args.length != 3) {
                    throw new SimulatorSyntaxException("Instruction " + args[0] + " has wrong number of arguments");
                }
                /* The printFromTo instruction is broken to five smaller instructions 
                Reason: because the printFromTo method may print many number so it doesn't sense to run in a single cycle like other instructions*/
                instructions.add(new Assign(counterVariableName, args[1]));
                instructions.add(new JumpIfGreaterThan(counterVariableName, args[2], 3));
                instructions.add(new Print(counterVariableName));
                instructions.add(new Increment(counterVariableName));
                instructions.add(new Jump(-4));
                break;
            case "assign":
                // This case handles 3 types of assign instruction
                if (args.length == 4) {
                    // assign a readFile b
                    if (!args[2].equals("readFile"))
                        throw new SimulatorSyntaxException(
                                "When assign instruction has 3 arguments, the second must be readFile instruction");
                    instructions.add(new ReadFile(args[3], args[1]));
                } else if (args.length != 3) {
                    throw new SimulatorSyntaxException("Instruction " + args[0] + " has wrong number of arguments");
                } else {
                    if (args[2].equals("input"))
                        // assign a input
                        instructions.add(new Input(args[1]));
                    else
                        // assign a b
                        instructions.add(new Assign(args[1], args[2]));
                }
                break;
            default:
                throw new SimulatorSyntaxException("Unknown Instruction : " + args[0]);
        }
        return instructions;
    }

}
