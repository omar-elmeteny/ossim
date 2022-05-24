package ossim.simulator;

import java.util.Hashtable;

import ossim.instructions.Instruction;
import ossim.view.DisplayWindow;
import ossim.exceptions.SimulatorRuntimeException;

// Represents a simulated process
public class UserModeProcess {
    final private PCB pcb;
    // The hashtable is used to store the program variables and the variable name is the search key 
    private Hashtable<String, String> variables; 
    final private String programPath;

    public UserModeProcess(String programPath, PCB pcb) {
        super();
        this.pcb = pcb;
        this.programPath = programPath;
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

    public int getProgramCounter() {
        return pcb.getProgramCounter();
    }

    public void setProgramCounter(int programCounter) {
        pcb.setProgramCounter(programCounter);
    }

    public int getProcessID() {
        return pcb.getProcessID();
    }

    public int getProgramSize() throws SimulatorRuntimeException{
        return (Integer) OperatingSystem.readMemory(this, (1 << OperatingSystem.logicalMemorySizeBits) - 1 - (1 << OperatingSystem.pageSizeBits));
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
    public Instruction getNextInstruction() throws SimulatorRuntimeException{
        if(pcb.getProgramCounter() >= getProgramSize()){
            return null;
        }
        pcb.setProgramCounter(pcb.getProgramCounter()+1);
        return (Instruction) OperatingSystem.readMemory(this, pcb.getProgramCounter()-1);
    }

    // Checks whether instructions are finished or not
    // When finished the OperatingSystem class will terminate the process
    public boolean hasInstructions() throws SimulatorRuntimeException{
        return pcb.getProgramCounter() < getProgramSize(); 
    }

    public PCB getPcb() {
        return pcb;
    }

}
