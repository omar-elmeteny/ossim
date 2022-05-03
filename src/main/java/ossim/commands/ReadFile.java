package ossim.commands;

import ossim.exceptions.SimulatorRuntimeException;
import ossim.simulator.UserModeProcess;

public class ReadFile implements Command{
    
    private String fileNameVariable;
    private String outputVariable;

    public ReadFile(String fileNameVariable, String outputVariable) {
        super();
        this.fileNameVariable = fileNameVariable;
        this.outputVariable = outputVariable;
    }

    @Override
    public void execute(UserModeProcess process) throws SimulatorRuntimeException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String toString() {
        return "assign " + outputVariable + " readFile " + fileNameVariable;
    }
}
