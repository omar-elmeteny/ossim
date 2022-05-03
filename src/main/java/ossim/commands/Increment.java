package ossim.commands;

import ossim.exceptions.SimulatorRuntimeException;
import ossim.simulator.UserModeProcess;

public class Increment implements Command{
    
    private String variableName;

    public Increment(String variableName) {
        super();
        this.variableName = variableName;
    }

    @Override
    public void execute(UserModeProcess process) throws SimulatorRuntimeException {
        String value = process.readVariable(variableName);
        int intValue;
        try{
            intValue = Integer.parseInt(value);    
        }
        catch(NumberFormatException e){
            throw new SimulatorRuntimeException(e.getMessage());
        }
        process.writeVariable(variableName, Integer.toString(++intValue));
    }

    @Override
    public String toString() {
        return "increment " + variableName;
    }
}
