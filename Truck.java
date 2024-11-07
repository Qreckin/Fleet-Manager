public class Truck {
    private final int ID; // Unique ID of the truck
    private final int capacity; // Maximum load capacity of the truck

    public int load; // Current load of the truck (initially 0)

    Truck(int ID, int capacity, int load){
        this.ID = ID;
        this.capacity = capacity;
        this.load = load;
    }

    public int getID(){
        return ID;
    }

    public int getCapacity(){
        return capacity;
    }


}
