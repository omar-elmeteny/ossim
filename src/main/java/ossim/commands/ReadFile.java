package ossim.commands;

import ossim.exceptions.SimulatorRuntimeException;
import ossim.simulator.UserModeProccess;

public class ReadFile implements Command{
    
    private String fileNameVariable;
    private String outputVariable;

    public ReadFile(String fileNameVariable, String outputVariable) {
        super();
        this.fileNameVariable = fileNameVariable;
        this.outputVariable = outputVariable;
    }

    @Override
    public void execute(UserModeProccess proccess) throws SimulatorRuntimeException {
        // TODO Auto-generated method stub
        
    }
}
