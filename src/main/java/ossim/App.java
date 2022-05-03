package ossim;

import java.io.IOException;
import java.util.ArrayList;

import ossim.commands.Command;
import ossim.exceptions.SimulatorSyntaxException;
import ossim.simulator.Parser;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException, SimulatorSyntaxException 
    {
        ArrayList<Command> program1 = Parser.parseFile("programs\\Program_1.txt");
        ArrayList<Command> program2 = Parser.parseFile("programs\\Program_2.txt");
        ArrayList<Command> program3 = Parser.parseFile("programs\\Program_3.txt");
    }
}
