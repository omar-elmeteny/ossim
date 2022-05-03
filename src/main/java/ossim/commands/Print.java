package ossim.commands;

import ossim.exceptions.SimulatorRuntimeException;
import ossim.simulator.UserModeProcess;

public class Print implements Command{

    private String variableName;

    public Print(String variableName) {
        super();
        this.variableName = variableName;
    }

    @Override
    public void execute(UserModeProcess process) throws SimulatorRuntimeException {
        System.out.println(process.readVariable(variableName));
    }
    
}
