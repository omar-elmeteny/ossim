package ossim.instructions;

import ossim.exceptions.SimulatorRuntimeException;
import ossim.simulator.UserModeProcess;

public class Increment implements Instruction{
    
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
            throw new SimulatorRuntimeException("Failed to parse integer " + e.getMessage());
        }
        process.writeVariable(variableName, Integer.toString(++intValue));
    }

    @Override
    public String toString() {
        return "increment " + variableName;
    }
}
