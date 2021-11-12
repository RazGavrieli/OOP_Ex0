package ex0.algo;

import ex0.Building;
import ex0.CallForElevator;
import ex0.Elevator;

import java.util.ArrayList;
import java.util.Collections;

public class egcAlgo implements ElevatorAlgo{
    private Building _building;
    private Elevator[] elevators;
    private ArrayList<Integer>[] jobs;
    private final int  MAXJOBS=1000;
    private final int  MAXDIST=15;
    private final int TIMETOGET=30;
    private final double EXPRESS=6;
    private boolean smartselect = false, expresselevators = true;
    public egcAlgo(Building b) {

        _building = b;
        elevators = new Elevator[_building.numberOfElevetors()];
        jobs = new ArrayList[_building.numberOfElevetors()];
        for (int i = 0; i < elevators.length; i++) {
            elevators[i] = _building.getElevetor(i);
            jobs[i] = new ArrayList<>();
        }
    }

    @Override
    public Building getBuilding() {
        return _building;
    }

    @Override
    public String algoName() {
        return "Advanced egc Algo";
    }

    @Override
    public int allocateAnElevator(CallForElevator c) {
        //THIS NEXT CODE BLOCK WILL WORK AS EXPRESS ELEVATORS FILTER OR WILL MAKE THE EXPRESS ELEVATOR WORK MORE
        if (expresselevators&&Math.abs(c.getDest()-c.getSrc())>MAXDIST) {
            for (int i = 0; i < elevators.length; i++) {
                if (GetState(c, elevators[i]) == 1 && elevators[i].getSpeed() >= EXPRESS) {
                    elevators[i].goTo(c.getSrc());
                    if (!jobs[i].contains(c.getSrc())) jobs[i].add(c.getSrc());
                    if (!jobs[i].contains(c.getDest())) jobs[i].add(c.getDest());
                    return i;
                }
            }
        }

        //IF THE DISTANCE IS NOT FOR EXPRESS, OR IF IT IS FOR EXPRESS BUT THERE ARE NO EXPRESS ELEVATORS IN REST
        double min = Integer.MAX_VALUE; int index = Integer.MIN_VALUE;
        for (int i = 0; i < elevators.length; i++) {
            if (GetState(c,elevators[i])==1&&timeToGet(c.getSrc(),elevators[i])<min) {
                min = timeToGet(c.getSrc(),elevators[i]);
                index = i;
            }
        }
        //IF WE FOUND THE RIGHT ELEVATOR FROM THE LAST FOR
        if (index>=0) {
            elevators[index].goTo(c.getSrc());
            if (!jobs[index].contains(c.getSrc())) jobs[index].add(c.getSrc());
            if (!jobs[index].contains(c.getDest()))jobs[index].add(c.getDest());
            return index;
        }
        //IF THERE ARE NO IN REST ELEVATORS
        for (int i = 0; i < elevators.length; i++) {
            if (GetState(c,elevators[i])==2&&timeToGet(c.getSrc(),elevators[i])<TIMETOGET&&elevators[i].getSpeed()<EXPRESS) {
                if (elevators[i].getState()!=0) elevators[i].stop(c.getSrc());
                if (!jobs[i].contains(c.getSrc()))  jobs[i].add(c.getSrc());
                if (!jobs[i].contains(c.getDest())) jobs[i].add(c.getDest());
               return i;
            }
        }
        //IF THERE ARE NO ELEVATORS IN STATE 2 (THAT CAN'T STOP NOW AT THE SRC)
        for (int i = 0; i < elevators.length; i++) {
            if (jobs[i].size()<MAXJOBS&&GetState(c,elevators[i])==3&&elevators[i].getSpeed()<EXPRESS) {
                if (!jobs[i].contains(c.getSrc()))jobs[i].add(c.getSrc());
                if (!jobs[i].contains(c.getDest()))jobs[i].add(c.getDest());
                return i;
            }
        }
        //AT THE RARE CASE WHEN THERE ARE NO ELEVATORS IN ANY OF THE STATES ABOVE,
        // WE WILL FIRST CHECK IF THERE IS
        int r = 0; double max=0;
        for (int i = 0; i < elevators.length; i++) {
            if (jobs[i].contains(c.getDest())&&jobs[i].contains(c.getSrc())) return i;
            if (elevators[i].getSpeed()>max) {
                r=i;
                max=elevators[i].getSpeed();
            }
        }

        if (!jobs[r].contains(c.getSrc())) jobs[r].add(c.getSrc());
        if (!jobs[r].contains(c.getDest()))jobs[r].add(c.getDest());
        return r;
    }

    @Override
    public void cmdElevator(int elev) {

        if (jobs[elev].isEmpty()) {
            if (smartselect) {
                if (Math.random() > 0.3) jobs[elev].add(_building.minFloor());
                else jobs[elev].add(_building.maxFloor());
            }
            return;
        }
         Collections.sort(jobs[elev]);

        if (jobs[elev].contains(elevators[elev].getPos())&&elevators[elev].getState()==0) {
            jobs[elev].remove(jobs[elev].indexOf(elevators[elev].getPos()));
            if (jobs[elev].isEmpty()) return;
        }

        if (elevators[elev].getPos()<jobs[elev].get(0)) {
           if (elevators[elev].getState()==0) elevators[elev].goTo(jobs[elev].get(0));

        }
        else if (elevators[elev].getPos()>jobs[elev].get(jobs[elev].size()-1)) {
            if (elevators[elev].getState()==0)elevators[elev].goTo(jobs[elev].get(jobs[elev].size()-1));

        }
        else { //IF THE ELEVATOR IS IN THE MIDDLE OF ITS QUEUE, THEN GOTO THE CLOSEST FLOOR
            int p = elevators[elev].getPos();
            if (elevators[elev].getState()==0){
                for (int i = 0; i < jobs[elev].size(); i++) {
                    if (jobs[elev].get(i)>p) {
                        if (Math.abs(p-jobs[elev].get(i))<Math.abs(p-jobs[elev].get(i-1)))
                            elevators[elev].goTo(jobs[elev].get(i));
                        else
                            elevators[elev].goTo(jobs[elev].get(i-1));
                        return;
                    }
                }
            }
        }
    }

    private int GetState(CallForElevator c, Elevator e) {
        if (e.getState()==0&&jobs[e.getID()].isEmpty()) return 1;
        if (e.getState()==1&&c.getSrc()<=jobs[e.getID()].get(0)&&c.getType()==1) return 2;
        if (e.getState()==1&&c.getSrc()>=jobs[e.getID()].get(0)&&c.getType()==1) return 3;
        if (e.getState()==-1&&c.getSrc()>=jobs[e.getID()].get(jobs[e.getID()].size()-1)&&c.getType()==-1) return 2;
        if (e.getState()==-1&&c.getSrc()<=jobs[e.getID()].get(jobs[e.getID()].size()-1)&&c.getType()==-1) return 3;

        return 4;
    }


    private double timeToGet(int f, Elevator e) {
        return (Math.abs(e.getPos()-f))*(e.getSpeed());
        //THIS WONT RETURN THE EXACT CORRECT VALUE, BUT IT WILL RETURN A STABLE VALUE
        //WHICH IS COMPARABLE BETWEEN ALL ELEVATORS.
    }

    public ArrayList<Integer>[] getJobs() {
        return jobs;
    }
}
