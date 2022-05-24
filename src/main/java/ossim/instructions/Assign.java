package ossim.instructions;

import ossim.exceptions.SimulatorRuntimeException;
import ossim.simulator.UserModeProcess;

public class Assign implements Instruction{
    
    private String toVariableName;
    private String fromVariableName;

    public Assign(String toVariable, String fromVariable) {
        super();
        this.toVariableName = toVariable;
        this.fromVariableName = fromVariable;
    }

    @Override
    public void execute(UserModeProcess process) throws SimulatorRuntimeException {
        try{
            int constant = Integer.parseInt(fromVariableName);
            process.writeVariable(toVariableName, constant + "");
            return;
        }
        catch(NumberFormatException e){

        }
        String value = process.readVariable(fromVariableName);
        process.writeVariable(toVariableName, value);
    }

    @Override
    public String toString() {
        return "assign " + toVariableName + " " + fromVariableName;
    }

}
