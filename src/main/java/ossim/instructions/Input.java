package ossim.instructions;


import ossim.exceptions.SimulatorRuntimeException;
import ossim.simulator.OperatingSystem;
import ossim.simulator.UserModeProcess;

public class Input implements Instruction{

    private String variableName;
    
    public Input(String variableName) {
        super();
        this.variableName = variableName;
    }

    @Override
    public void execute(UserModeProcess process) throws SimulatorRuntimeException { 
        OperatingSystem.input(variableName);     
    }
    
    @Override
    public String toString() {
        return "assign " + variableName + " input";
    }
}
