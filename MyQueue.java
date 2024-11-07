public class MyQueue {

    // Queue with circular array
    private final Truck[] arr;

    private int size; // Truck count in the queue
    private final int length; // Maximum truck capacity
    private int first; // Index of the first element
    private int last; // Index of the last element

    MyQueue(int length){
        arr = new Truck[length];
        first = 0; // Initially 0 because first dequeued element is always at 0th index
        last = -1; // Initially -1 because when we add the first element we want it to be on 0
        size = 0; // Initially no trucks in the queue
        this.length = length;
    }

    public void enqueue(Truck t){
        if (isFull()) // Can not add truck
            return;
        last = (last+1)%length; // Move to right circularly
        arr[last] = t; // Add the truck
        size++; // Increment the size
    }

    public Truck dequeue(){
        if (isEmpty()){ // Can not remove truck
            return null;
        }
        Truck t = arr[first]; // This truck will be removed
        first = (first+1)%length; // Move to right circularly
        size--; // Decrement the size
        return t; // Return the truck
    }

    public int getSize(){
        return size;
    }

    public boolean isEmpty(){
        return size == 0;
    }

    public boolean isFull(){
        return size == length;
    }


}
