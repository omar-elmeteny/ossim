package ossim.commands;

import java.util.Scanner;

import ossim.exceptions.SimulatorRuntimeException;
import ossim.simulator.DisplayWindow;
import ossim.simulator.UserModeProcess;

public class Input implements Command{

    private String variableName;
    
    public Input(String variableName) {
        super();
        this.variableName = variableName;
    }

    @Override
    public void execute(UserModeProcess process) throws SimulatorRuntimeException {
        String nextLine = DisplayWindow.askForInput(process);
        process.writeVariable(variableName, nextLine);        
    }
    
    @Override
    public String toString() {
        return "assign " + variableName + " input";
    }
}
