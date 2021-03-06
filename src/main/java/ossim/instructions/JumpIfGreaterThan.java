package ossim.instructions;

import ossim.exceptions.SimulatorRuntimeException;
import ossim.simulator.UserModeProcess;

public class JumpIfGreaterThan implements Instruction{
    
    private String leftVariableName;
    private String rightVariableName;
    private int positionOffset;

    public JumpIfGreaterThan(String leftVariableName, String rightVariableName, int positionOffset) {
        super();
        this.leftVariableName = leftVariableName;
        this.rightVariableName = rightVariableName;
        this.positionOffset = positionOffset;
    }

    @Override
    public void execute(UserModeProcess process) throws SimulatorRuntimeException {
        String leftValue = process.readVariable(leftVariableName);
        String rightValue = process.readVariable(rightVariableName);
        int leftIntValue;
        int rigthIntValue;
        try{
            leftIntValue = Integer.parseInt(leftValue);
            rigthIntValue = Integer.parseInt(rightValue);    
        }
        catch(NumberFormatException e){
            throw new SimulatorRuntimeException("Failed to parse integer " + e.getMessage());
        }
        if(leftIntValue > rigthIntValue){
            process.setProgramCounter(process.getProgramCounter() + positionOffset);
        }
    }

    @Override
    public String toString() {
        return "jgt " + leftVariableName + " " + rightVariableName + " " + positionOffset; 
    }
}
