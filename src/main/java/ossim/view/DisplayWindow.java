package ossim.view;

import java.awt.Color;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import ossim.instructions.Instruction;
import ossim.simulator.Frame;
import ossim.simulator.OperatingSystem;
import ossim.simulator.ProcessState;
import ossim.simulator.Scheduler;
import ossim.simulator.UserModeProcess;

// Displays every scheduling event and process output and executing instruction
public class DisplayWindow extends JFrame{

    private static String blue = "#21a1f1";
    private static DisplayWindow mainWindow;
    private JTextPane outputTextPane;
    private static StringBuilder outputText = new StringBuilder();
    private JScrollPane outputTextScroller;
    private HTMLEditorKit editorKit;
    private HTMLDocument htmlDoc;

    private DisplayWindow() {
        super();
        setTitle("OS Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1500,800);
        // https://stackoverflow.com/questions/5133240/add-html-content-to-document-associated-with-jtextpane
        editorKit = new HTMLEditorKit();
        htmlDoc = new HTMLDocument();
        outputTextPane = new JTextPane();
        outputTextPane.setEditorKit(editorKit);
        outputTextPane.setDocument(htmlDoc);
        outputTextPane.setEditable(false);
        outputTextPane.setBackground(Color.getHSBColor(216,0.28f,0.07f));
        outputTextPane.setContentType("text/html");
        outputTextPane.setCaretColor(outputTextPane.getBackground());
        outputTextPane.getCaret().setBlinkRate(0);
        outputTextScroller = new JScrollPane(outputTextPane);
        outputTextScroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        outputTextScroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(outputTextScroller);
        setVisible(true);
        // whenever we add text to the outputTextPane and the size changes, we scroll to the end 
        outputTextPane.addComponentListener(new ComponentAdapter(){
            @Override
            public void componentResized(ComponentEvent e) {
                JScrollBar vertical = outputTextScroller.getVerticalScrollBar();
                vertical.setValue(vertical.getMaximum());
            }
        });
        addTableStyle();
    }

    // singleton design pattern because we can only have one window
    public static synchronized DisplayWindow getMainWindow(){
        if(mainWindow == null){
            mainWindow = new DisplayWindow();
        }
        return mainWindow;
    }

    public static synchronized void printExecutingInstruction(UserModeProcess process, Instruction instruction){
        addText("PID: ", blue);
        addText(process.getProcessID() + " ,", "white");
        addText(" INS: ", blue);
        addText(instruction.toString(), "white");
        addNewLine();
    }

    public static synchronized void printIOTime(UserModeProcess process, String ioOperation, long startTime, long endTime){
        addText("PID: ", blue);
        addText(process.getProcessID() + " ", "white");
        addText(ioOperation + " Operation took " + (endTime-startTime) + " cycles", "yellow");
        addNewLine();
    }

    public static String askForInput(UserModeProcess process){
        String text = JOptionPane.showInputDialog(getMainWindow(),"Enter input for PID " + process.getProcessID() + ": ");
        if(text == null)
            return "";
        return text;
    }


    public static synchronized void printProcessOutput(UserModeProcess process, String output){
        addText("Output from PID ", "#d287a5");
        addText(process.getProcessID() + ": ", "white");
        addText(output ,"white");
        addNewLine();
    }

    public static synchronized void printProcess(UserModeProcess process){
        addText("Process created with PID ", blue);
        addText(process.getProcessID() + " ", "white");
        addText("from ", blue);
        addText(process.getProgramPath(), "white");
        addNewLine();
    }

    public static synchronized void printQueues(Scheduler scheduler){
        if(scheduler.getRunningProcess() != null){
            addText("Running Process: ", "green");
            addText("PID: ", blue);
            addText(scheduler.getRunningProcess().getProcessID() + " ", "white");
        }
        addText("Ready Queue: ", "green");
        boolean flag = false;
        for(UserModeProcess process : scheduler.getReadyQueue()){
            if(flag)
                addText(", ", "white");
            else
                flag = true;    
            addText("PID: ", blue);
            addText(process.getProcessID() + " ", "white");
        }
        addText("Blocked Queue: ", "green");
        flag = false;
        for(UserModeProcess process : scheduler.getBlockedProcesses()){
            if(flag)
                addText(", ", "white");
            else
                flag = true;    
            addText("PID: ", blue);
            addText(process.getProcessID() + " ", "white");
        }
        addText("Finished Queue: ", "green");
        flag = false;
        for(UserModeProcess process : scheduler.getFinishedProcesses()){
            if(flag)
                addText(", ", "white");
            else
                flag = true;    
            addText("PID: ", blue);
            addText(process.getProcessID() + " ", "white");
        }
        addNewLine();
    }

    public static synchronized void displayProcessErrorMessage(UserModeProcess process, String errorMessage){
        addText("Error in Process ", "red");
        addText("PID ", blue);
        addText(process.getProcessID() + " ", "white");
        addText(errorMessage, "red");
        addNewLine();
    }

    public static synchronized void printLaunchError(String programPath, String errorMessage) {
        addText("Failed to launch program: ", "red");
        addText("path ", blue);
        addText(programPath + " ", "white");
        addText(errorMessage, "red");
        addNewLine();
    }

    public static synchronized void printProcessState(UserModeProcess process, ProcessState oldProcessState){
        addText("State Change ", "yellow");
        addText("PID: ", blue);
        addText(Integer.toString(process.getProcessID()) + " ","white");
        addText(oldProcessState + " ", "#d287a5");
        addText("-> ", "white");
        addText(process.getState() + "", "green");
        addNewLine();
    }

    private void addTableStyle(){
        outputText.append("<html><head>");
        outputText.append("<style>");
        outputText.append("table{width:100%;border-spacing:0px;margin:20px;border-collapse:collapse;border:1px solid white;font-family:monospace;font-size:16px;}");
        outputText.append("th{border:1px solid white;color:white;text-align:center;font-weight:bold;}");
        outputText.append("td{border:1px solid white;color:white;padding:2px;}");
        outputText.append("</style>");
        outputText.append("</head><body>");

        addHTML(outputText.toString());
        outputText.delete(0, outputText.length());
    }

    public static synchronized void addMemoryTable(Frame[] physicalMemory){
        outputText.append("<table>");
        outputText.append("<tr>");
        for(int i = 0;i < physicalMemory.length;i++){
            outputText.append("<td>");
            outputText.append("Frame");
            outputText.append(i);
            outputText.append("</td>");
        }
        outputText.append("</tr>");
        outputText.append("<tr>");
        for(int i = 0;i < physicalMemory.length;i++){
            outputText.append("<td>");
            String text = "";
            if(i == 0){
                text = "OS Data";
            }
            else if(physicalMemory[i] == null){
                text = "Free";
            }
            else{
                text = "Page: " + physicalMemory[i].getPage() + " PID: " + physicalMemory[i].getProcessID();
            }
            outputText.append(text);
            outputText.append("</td>");
        }
        outputText.append("</tr>");
        int pageSize = 4;
        for(int j = 0;j < pageSize;j++){
            outputText.append("<tr>");
            for(int i = 0;i < physicalMemory.length;i++){
                outputText.append("<td>");
                String text = "";
                if(physicalMemory[i] == null){
                    text = "Free";
                }
                else if(i == 0){
                    if(j == 0){
                        text = "OS Mutexes";
                    }
                    else if(j == 1){
                        text = "Next PID: " + physicalMemory[i].getObjectAt(j);
                    }
                    else if (j == 2){
                        text = "OS Scheduler";
                    }
                    else{
                        text = "Processes";
                    }
                }
                else if(physicalMemory[i].getPage() == 7){
                    if(j == 0){
                        text = "PID: " + physicalMemory[i].getObjectAt(j);
                    }
                    else if(j == 1){
                        text = "PC: " + physicalMemory[i].getObjectAt(j);
                    }
                    else if (j == 2){
                        text = physicalMemory[i].getObjectAt(j).toString();
                    }
                    else{
                        text = "Page Table";
                    }
                }
                else{
                    Object value = physicalMemory[i].getObjectAt(j);
                    if(value != null){
                        text = value.toString();
                    }
                }
                outputText.append(text);
                outputText.append("</td>");
            }
            outputText.append("</tr>");
        }
        outputText.append("</table>");
        addNewLine();
    } 

    public static synchronized void addSwapOut(int processID, int page, int frame){
        addText("Swap out ", "#005aff");
        addText("PID: " , blue);
        addText(processID + " , ", "white");
        addText("Page: ", blue);
        addText(page + " , ", "white");
        addText("Page saved to file ", "#005aff");
        addText(processID + "_" + page + ".mem ", "white");
        addText("from frame: ", "#005aff");
        addText(frame + ".", "white");
        addNewLine();
    }

    public static synchronized void addSwapIn(int processID, int page, int frame){
        addText("Swap in ", "orange");
        addText("PID: " , blue);
        addText(processID + " , ", "white");
        addText("Page: ", blue);
        addText(page + " , ", "white");
        addText("Page loaded from file ", "orange");
        addText(processID + "_" + page + ".mem ", "white");
        addText("into frame: ", "orange");
        addText(frame + ".", "white");
        addNewLine();
        addMemoryTable(OperatingSystem.getPhysicalmemory());
    }

    private static synchronized void addText(String text, String color){
        outputText.append("<span style='font-family: monospace;font-size: 18px;font-weigth: bold;color: ");
        outputText.append(color);
        outputText.append("'>");
        outputText.append(text.replaceAll("\n", "\n<br>"));
        outputText.append("</span>");     
    }

    private static synchronized void addNewLine(){
        getMainWindow().addHTML(outputText.toString());
        outputText.delete(0, outputText.length());
    }

    public void addHTML(String html){
        try {
            editorKit.insertHTML(htmlDoc,htmlDoc.getLength(), html,0, 0,  null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } 
    }
}
