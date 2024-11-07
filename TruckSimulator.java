public class TruckSimulator {

    public AVLTree addTruckAvailable; // AVL tree of parking lots which are suitable for addTruck method

    public AVLTree readyAvailable; // AVL tree of parking lots which are suitable for ready method

    public AVLTree loadAvailable; // AVL tree of parking lots which are suitable for load method

    public AVLTree everyParkingLot; // AVL tree containing every parking lot
    TruckSimulator(){
        // We have 4 distinct AVL trees in our application to increase efficiency of searches
        addTruckAvailable = new AVLTree();
        readyAvailable = new AVLTree();
        loadAvailable = new AVLTree();
        everyParkingLot = new AVLTree();
    }

    public void createParkingLot(int capacityConstraint, int truckLimit){
        // Insert method returns the root, then we assign it to the root
        ParkingLot p = new ParkingLot(capacityConstraint, truckLimit); // We do not create 2 distinct parking lots, both of them share the same parking lot
        everyParkingLot.insert(p);
        addTruckAvailable.insert(p);
    }


    public String addTruck(int truckID, int capacity){
        AVLTree.AVLNode candidate = addTruckAvailable.findSmallerThan(capacity); // Possible node that contains the suitable parking lot

        if (candidate == null) // No such parking lot exists
            return "-1";
        ParkingLot p = candidate.p;

        p.addTruck(truckID, capacity, 0); // Initial load is 0 since the truck is newly created
        if (p.getWaitingSize()==1) // If this is the first truck in the parking lot, add it to readyAvailable avl
            readyAvailable.insert(p);
        if (!p.canAddTruck()) // If we cant add more trucks to this parking lot, delete the node of the corresponding parking lot in addTruckAvailable
            addTruckAvailable.delete(p.getCapacityConstraint());
        return Integer.toString(p.getCapacityConstraint());
    }


    public void deleteParkingLot(int capacityConstraint){
        // Delete method returns the root, then we assign it to the root
        addTruckAvailable.delete(capacityConstraint);
        readyAvailable.delete(capacityConstraint);
        loadAvailable.delete(capacityConstraint);
        everyParkingLot.delete(capacityConstraint);
    }

    public String ready(int capacityConstraint){
        AVLTree.AVLNode candidate = readyAvailable.findGreaterThan(capacityConstraint); // Possible node that contains the suitable parking lot
        if (candidate == null) // No such parking lot exists
            return "-1";

        ParkingLot p = candidate.p;


        // If we are out of the loop, that means we can make a truck ready
        Truck t = p.readyTruck(); // Moving the first truck in waiting to ready section
        if (p.getReadySize() == 1) // If the truck is the first truck in the ready section, add to loadAvailable
            loadAvailable.insert(p);
        if (!p.canBeReady()) // If no waiting trucks left in waiting section, remove it from readyAvailable
            readyAvailable.delete(p.getCapacityConstraint());
        return t.getID() + " " + p.getCapacityConstraint();
    }

    public String load(int capacityConstraint, int loadAmount){
        StringBuilder str = new StringBuilder(); // Response string
        AVLTree.AVLNode candidate = loadAvailable.findGreaterThan(capacityConstraint); // Possible node that contains the suitable parking lot
        if (candidate == null){ // No such parking lot exists
            return "-1";
        }

        while (candidate != null){
            ParkingLot p = candidate.p;


            int maxLoad = p.getReadySize() * p.getCapacityConstraint(); // Maximum amount of load a parking lot can take
            int truckInReady = p.getReadySize(); // We take the initial ready size and store it since it changes during for loop
            // This information is very important, If you try to write p.getReadySize() in the loop, you will get errors

            // Case-1 load amount is large compared to maxLoad, therefore it is guaranteed that every truck in this parking lot will get full load
            if (loadAmount >= maxLoad){
                for (int i = 0; i < truckInReady; i++){
                    Truck t = p.getTruck();
                    // IMPORTANT! After the removal of a truck, we must immediately check for if we must add the parking lot to addTruckAvailable
                    if (p.canAddTruck())
                        addTruckAvailable.insert(p);
                    t.load += p.getCapacityConstraint(); // Increment the load of the truck
                    String response = reassignTruck(t); // Since the truck's load has changed, we shall send it to a new parking lot
                    // Here, response contains the ID of the truck and the capacityConstraint of its new parking lot, (If it is not added -1)
                    str.append(response).append(" - ");
                }
                loadAmount -= maxLoad; // Decremented after the loop to increase efficiency

                // Case-1-a load amount was exactly the maxLoad
                if (loadAmount == 0) {
                    // No loads left to distribute, so terminate
                    str.delete(str.length() - 3, str.length());
                    return str.toString();
                }

                // Case-1-b there are still load left, so new parking lot search starts
                int capacityy = p.getCapacityConstraint(); // Store the capacityConstraint of the parking lot in case if the nodes gets deleted after this line
                loadAvailable.delete(capacityy); // This parking lot have no trucks in ready section, remove from loadAvailable
                candidate = loadAvailable.findGreaterThan(capacityy); // Find the new candidate

            }
            // Case-2 load amount is less than maxLoad
            else{
                int fullLoadedTruckCount = loadAmount / p.getCapacityConstraint(); // For example 170/50 = 3 trucks will get fully loaded
                for (int i = 0; i < fullLoadedTruckCount; i++){
                    // Same process explained in Case-1
                    Truck t = p.getTruck();
                    if (p.canAddTruck())
                        addTruckAvailable.insert(p);
                    t.load += p.getCapacityConstraint();
                    String response = reassignTruck(t);
                    str.append(response).append(" - ");
                }
                loadAmount %= p.getCapacityConstraint(); // Again load amount is updated after the loop for efficiency


                // Case-2-a no loads left ---> then, our job is done
                if (loadAmount == 0){
                    str.delete(str.length() - 3, str.length());
                    return str.toString();
                }


                // Case 2-b there are some loads left (note that loadAmount < capacity)
                // Add the remaining load to the truck then, our job is done
                Truck t = p.getTruck();
                if (p.canAddTruck())
                    addTruckAvailable.insert(p);
                t.load += loadAmount;
                String response = reassignTruck(t);
                str.append(response).append(" - ");
                // If p's ready section becomes empty, remove it from loadAvailable
                if (p.isReadyEmpty())
                    loadAvailable.delete(p.getCapacityConstraint());

                // Our job is done since no load has left
                str.delete(str.length() - 3, str.length());
                return str.toString();
            }
        }
        // If we are out of the loop, no candidates left for load distribution, so return

        if (!str.isEmpty()) {
            str.delete(str.length() - 3, str.length());
            return str.toString();
        }
        return "-1";
    }

    private String reassignTruck(Truck t){

        if (t.getCapacity() == t.load) // If the truck has full load, empty it
            t.load = 0;

        AVLTree.AVLNode candidate = addTruckAvailable.findSmallerThan(t.getCapacity() - t.load); // Possible node that contains the suitable parking lot
        // We send capacity - load since it might have some loads in it

        if (candidate == null){ // No such parking lots exists
            return t.getID() + " -1";
        }

        ParkingLot p = candidate.p;

        p.addTruck(t.getID(), t.getCapacity(), t.load); // Creating this truck in the parking lot with initial load
        if (p.getWaitingSize() == 1) // If the truck is the first truck in the waiting section, add this parking lot to readyAvailable
            readyAvailable.insert(p);
        if (!p.canAddTruck()) // If no more trucks can be added, remove this parking lot from addTruckAvailable
            addTruckAvailable.delete(p.getCapacityConstraint());

        return t.getID() + " " + p.getCapacityConstraint();
    }

    public String count(int capacity){
        // Counts the number of trucks in parking lots with capacityConstraint greater than given capacity
        return everyParkingLot.count(capacity);
    }


}
