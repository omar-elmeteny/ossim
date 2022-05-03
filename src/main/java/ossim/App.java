package ossim;

import java.io.IOException;
import java.util.ArrayList;

import ossim.commands.Command;
import ossim.exceptions.SimulatorRuntimeException;
import ossim.exceptions.SimulatorSyntaxException;
import ossim.simulator.Parser;
import ossim.simulator.UserModeProcess;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException, SimulatorSyntaxException, SimulatorRuntimeException 
    {
        UserModeProcess process1 = new UserModeProcess(1, "programs\\Program_1.txt");
        Command cmd;
        while((cmd = process1.getNextCommand()) != null){
            cmd.execute(process1);
        }
    }
}
