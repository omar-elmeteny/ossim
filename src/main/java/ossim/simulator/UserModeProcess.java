package ossim.simulator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

import ossim.instructions.Instruction;
import ossim.view.DisplayWindow;
import ossim.exceptions.SimulatorRuntimeException;
import ossim.exceptions.SimulatorSyntaxException;

// Represents a simulated process
public class UserModeProcess {
    final private PCB pcb;
    final private ArrayList<Instruction> instructions;
    // The hashtable is used to store the program variables and the variable name is the search key 
    private Hashtable<String, String> variables; 
    final private String programPath;

    public UserModeProcess(String programPath, PCB pcb) throws SimulatorSyntaxException, IOException {
        super();
        this.pcb = pcb;
        this.programPath = programPath;
        instructions = Parser.parseFile(programPath);
        variables = new Hashtable<>();
        DisplayWindow.printProcess(this);
    }

    public String getProgramPath() {
        return programPath;
    }

    public ProcessState getState() {
        return pcb.getProcessState();
    }

    public synchronized void setState(ProcessState state) {
        ProcessState oldProcessState = pcb.getProcessState();
        pcb.setProcessState(state);
        DisplayWindow.printProcessState(this, oldProcessState);
    }

    public ArrayList<Instruction> getInstructions() {
        return instructions;
    }

    public int getProgramCounter() {
        return pcb.getProgramCounter();
    }

    public void setProgramCounter(int programCounter) {
        pcb.setProgramCounter(programCounter);
    }

    public int getProcessID() {
        return pcb.getProcessID();
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

    // This is called by the interpreter(OperatingSystem class) to get the next instruction to be executed
    // This also increments the program counter
    public Instruction getNextInstruction(){
        if(pcb.getProgramCounter() >= instructions.size()){
            return null;
        }
        pcb.setProgramCounter(pcb.getProgramCounter()+1);
        return instructions.get(pcb.getProgramCounter()-1);
    }

    // Checks whether instructions are finished or not
    // When finished the OperatingSystem class will terminate the process
    public boolean hasInstructions(){
        return pcb.getProgramCounter() < instructions.size(); 
    }
}
