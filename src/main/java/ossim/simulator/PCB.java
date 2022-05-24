package ossim.simulator;

public class PCB {
    
    private final int physicalAddress;

    public PCB(int physicalAddress) {
        super();
        this.physicalAddress = physicalAddress;
    }

    public int getProcessID(){
        return (Integer) OperatingSystem.getObjectAtPhysicalAddress(physicalAddress);
    }

    public int getProgramCounter(){
        return (Integer) OperatingSystem.getObjectAtPhysicalAddress(physicalAddress+1);
    }

    public void setProgramCounter(int programCounter){
        OperatingSystem.setObjectAtPhysicalAddress(physicalAddress+1, programCounter);
    }

    public ProcessState getProcessState(){
        return (ProcessState) OperatingSystem.getObjectAtPhysicalAddress(physicalAddress+2);
    }

    public void setProcessState(ProcessState processState){
        OperatingSystem.setObjectAtPhysicalAddress(physicalAddress+2, processState);
    }

    public int[] getPageTable(){
        return (int[]) OperatingSystem.getObjectAtPhysicalAddress(physicalAddress+3);
    }

}
