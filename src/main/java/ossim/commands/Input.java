package ossim.commands;

import java.util.Scanner;

import ossim.exceptions.SimulatorRuntimeException;
import ossim.simulator.UserModeProcess;

public class Input implements Command{

    private String variableName;
    
    public Input(String variableName) {
        super();
        this.variableName = variableName;
    }
    @Override
    public void execute(UserModeProcess process) throws SimulatorRuntimeException {
        Scanner sc = new Scanner(System.in);
        String nextLine = sc.nextLine();
        process.writeVariable(variableName, nextLine);        
    }
    
}
