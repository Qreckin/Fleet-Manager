import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        File inputFile = new File(args[0]);
        File outputFile = new File(args[1]);

        BufferedWriter outWriter; // To output in a file we will use BufferedWriter class
        try{
            outWriter =new BufferedWriter(new FileWriter(outputFile));
        }catch (FileNotFoundException e2){
            e2.printStackTrace();
            return;
        }

        Scanner reader; // Scanner for the input file
        try{
            reader = new Scanner(inputFile);
        }catch (FileNotFoundException e){
            System.out.println("Cannot find input file");
            return;
        }


        TruckSimulator manager = new TruckSimulator(); // The object which we use to manage our fleet


        String line;
        String[] temp;
        while (reader.hasNextLine()){
            line = reader.nextLine();
            temp = line.split(" ");
            String command = temp[0];

            if (command.equals("create_parking_lot")){
                int capacityConstraint = Integer.parseInt(temp[1]);
                int truckLimit = Integer.parseInt(temp[2]);
                manager.createParkingLot(capacityConstraint, truckLimit);
            }
            else if (command.equals("add_truck")){
                int truckID = Integer.parseInt(temp[1]);
                int truckCapacity = Integer.parseInt(temp[2]);
                outWriter.write(manager.addTruck(truckID, truckCapacity));
                outWriter.newLine();
            }
            else if (command.equals("delete_parking_lot")){
                int capacityConstraint = Integer.parseInt(temp[1]);
                manager.deleteParkingLot(capacityConstraint);
            }
            else if (command.equals("ready")){
                int capacityConstraint = Integer.parseInt(temp[1]);
                outWriter.write(manager.ready(capacityConstraint));
                outWriter.newLine();
            }
            else if (command.equals("load")){
                int capacityConstraint = Integer.parseInt(temp[1]);
                int loadAmount = Integer.parseInt(temp[2]);
                outWriter.write(manager.load(capacityConstraint, loadAmount));
                outWriter.newLine();
            }
            else if (command.equals("count")){
                int capacityConstraint = Integer.parseInt(temp[1]);
                outWriter.write(manager.count(capacityConstraint));
                outWriter.newLine();
            }
        }

        reader.close();
        outWriter.close();
    }
}