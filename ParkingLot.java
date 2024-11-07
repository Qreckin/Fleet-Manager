
public class ParkingLot implements Comparable<ParkingLot>{
    private final int capacityConstraint;
    private final int truckLimit;

    private final MyQueue waitingSection;
    private final MyQueue readySection;

    ParkingLot(int capacityConstraint, int truckLimit){
        this.capacityConstraint = capacityConstraint;
        this.truckLimit = truckLimit;
        waitingSection = new MyQueue(truckLimit);
        readySection = new MyQueue(truckLimit);
    }

    // compareTo method is needed in order to compare parking lots easily
    @Override
    public int compareTo(ParkingLot other) {
        // Comparing parking lots according to their capacityConstraint
        return Integer.compare(this.capacityConstraint, other.capacityConstraint);
    }

    // If truck count is smaller than truck limit, we can still add trucks
    public boolean canAddTruck(){
        return (getTruckCount() < truckLimit);
    }

    // If there is at least 1 truck in the waiting section, we can move this to ready section
    public boolean canBeReady(){
        return !waitingSection.isEmpty();
    }

    // Given ID, capacity, load, creates a truck and adds to waiting section, if no space left, not added
    public void addTruck(int ID, int capacity, int load){
        waitingSection.enqueue(new Truck(ID, capacity, load));
    }

    public Truck getTruck(){
        return readySection.dequeue();
    }

    public boolean isReadyEmpty(){
        return readySection.isEmpty();
    }

    public Truck readyTruck(){
        Truck truck = waitingSection.dequeue(); // Poll from waiting section
        readySection.enqueue(truck); // Add to ready section
        return truck;
    }

    public int getTruckCount(){
        return waitingSection.getSize() + readySection.getSize();
    }

    public int getCapacityConstraint(){
        return capacityConstraint;
    }


    public int getReadySize(){
        return readySection.getSize();
    }

    public int getWaitingSize(){
        return waitingSection.getSize();
    }

}
