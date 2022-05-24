package ossim.simulator;

public class PCB {
    
    private Frame frame;

    public PCB(Frame frame, int processID) {
        super();
        this.frame = frame;
        setProgramCounter(0);
        setProcessState(ProcessState.NEW);
        frame.setObjectAt(0, processID);
        int[] pageTable = new int[1 << (OperatingSystem.logicalMemorySizeBits - OperatingSystem.pageSizeBits)];
        for(int i = 0;i < pageTable.length;i++){
            pageTable[i] = -1;
        }
        pageTable[pageTable.length - 1] = frame.getFrameIndex();
        frame.setObjectAt(3, pageTable);
    }

    public int getProcessID(){
        return (Integer) frame.getObjectAt(0);
    }

    public int getProgramCounter(){
        return (Integer) frame.getObjectAt(1);
    }

    public void setProgramCounter(int programCounter){
        frame.setObjectAt(1, programCounter);
    }

    public ProcessState getProcessState(){
        return (ProcessState) frame.getObjectAt(2);
    }

    public void setProcessState(ProcessState processState){
        frame.setObjectAt(2, processState);
    }

    public int[] getPageTable(){
        return (int[]) frame.getObjectAt(3);
    }

}
