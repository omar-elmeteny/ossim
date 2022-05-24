package ossim.simulator;

import java.io.Serializable;

public class Variable implements Serializable{
    
    private final String variableName;
    private String variableValue;

    public Variable(String variableName, String variableValue) {
        super();
        this.variableName = variableName;
        this.setVariableValue(variableValue);
    }

    public String getVariableValue() {
        return variableValue;
    }

    public void setVariableValue(String variableValue) {
        this.variableValue = variableValue;
    }

    public String getVariableName() {
        return variableName;
    }

}
