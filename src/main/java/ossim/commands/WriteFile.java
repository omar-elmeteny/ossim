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
    public void execute(UserModeProcess proccess) throws SimulatorRuntimeException {
        // TODO Auto-generated method stub
        
    }
}
