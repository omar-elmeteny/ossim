package ossim.commands;

import ossim.exceptions.SimulatorRuntimeException;
import ossim.simulator.UserModeProcess;

public class WriteFile implements Command{
    
    private String fileNameVariable;
    private String dataVariable;

    public WriteFile(String fileNameVariable, String dataVariable) {
        super();
        this.fileNameVariable = fileNameVariable;
        this.dataVariable = dataVariable;
    }

    @Override
    public void execute(UserModeProcess process) throws SimulatorRuntimeException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public String toString() {
        return "writeFile " + fileNameVariable + " " + dataVariable;
    }
}
