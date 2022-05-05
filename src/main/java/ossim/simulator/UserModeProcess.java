package ossim.simulator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import ossim.commands.Command;
import ossim.exceptions.SimulatorRuntimeException;
import ossim.exceptions.SimulatorSyntaxException;

// Represents a simulated process
public class UserModeProcess {
    // assigned by the OperatingSystem class
    final private int processID;
    private int programCounter = 0;
    final private ArrayList<Command> commands;
    private ProcessState state;
    // The hashtable is used to store the program variables and the variable name is the search key 
    private Hashtable<String, String> variables; 
    final private String programPath;

    public UserModeProcess(int processID, String programPath) throws SimulatorSyntaxException, IOException {
        super();
        this.processID = processID;
        this.programPath = programPath;
        commands = Parser.parseFile(programPath);
        state = ProcessState.NEW;
        variables = new Hashtable<>();
        DisplayWindow.printProcess(this);
    }

    public String getProgramPath() {
        return programPath;
    }

    public ProcessState getState() {
        return state;
    }

    public synchronized void setState(ProcessState state) {
        ProcessState oldProcessState = this.state;
        this.state = state;
        DisplayWindow.printProcessState(this, oldProcessState);
    }

    public ArrayList<Command> getCommands() {
        return commands;
    }

    public int getProgramCounter() {
        return programCounter;
    }

    public void setProgramCounter(int programCounter) {
        this.programCounter = programCounter;
    }

    public int getProcessID() {
        return processID;
    }

    // Writes the value in hashtable
    public synchronized void writeVariable(String variableName, String value) {
        variables.put(variableName, value);
    }

    // Reads the value from the hashtable and fails if the variable name is not existing
    public synchronized String readVariable(String variableName) throws SimulatorRuntimeException {
        if (!variables.containsKey(variableName))
            throw new SimulatorRuntimeException("Variable " + variableName + " is not found");
        return variables.get(variableName);
    }

    // This is called by the interpreter(OperatingSystem class) to get the next command to be executed
    // This also increments the program counter
    public Command getNextCommand(){
        if(programCounter >= commands.size()){
            return null;
        }
        return commands.get(programCounter++);
    }

    // Checks whether commands are finished or not
    // When finished the OperatingSystem class will terminate the process
    public boolean hasCommands(){
        return programCounter < commands.size(); 
    }
}
