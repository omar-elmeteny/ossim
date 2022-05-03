package ossim.simulator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import ossim.commands.Command;
import ossim.exceptions.SimulatorRuntimeException;
import ossim.exceptions.SimulatorSyntaxException;

public class UserModeProcess {
    final private int processID;
    private int programCounter = 0;
    final private ArrayList<Command> commands;
    private ProcessState state;
    private Hashtable<String, String> variables;

    public UserModeProcess(int processID, String programPath) throws SimulatorSyntaxException, IOException {
        super();
        this.processID = processID;
        commands = Parser.parseFile(programPath);
        setState(ProcessState.NEW);
        variables = new Hashtable<>();
    }

    public ProcessState getState() {
        return state;
    }

    public void setState(ProcessState state) {
        this.state = state;
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

    public void writeVariable(String variableName, String value) {
        variables.put(variableName, value);
    }

    public String readVariable(String variableName) throws SimulatorRuntimeException {
        if (!variables.containsKey(variableName))
            throw new SimulatorRuntimeException("Variable " + variableName + " is not found");
        return variables.get(variableName);
    }
}
