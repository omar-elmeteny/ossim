package ossim.commands;

import ossim.exceptions.SimulatorRuntimeException;
import ossim.simulator.UserModeProcess;

public class Assign implements Command{
    
    private String toVariableName;
    private String fromVariableName;

    public Assign(String toVariable, String fromVariable) {
        super();
        this.toVariableName = toVariable;
        this.fromVariableName = fromVariable;
    }

    @Override
    public void execute(UserModeProcess process) throws SimulatorRuntimeException {
        String value = process.readVariable(fromVariableName);
        process.writeVariable(toVariableName, value);
    }
}
