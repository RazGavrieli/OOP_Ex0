package ex0.algo;

import ex0.Building;
import ex0.CallForElevator;
import ex0.Elevator;
import ex0.simulator.Call_A;
import ex0.simulator.Simulator_A;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class egcAlgoTest {

    Building b1;
    egcAlgo egcAlgo1;
    @Test
    void allocateAnElevator() {
        Simulator_A.initData(5,null);
        b1=Simulator_A.getBuilding();
        egcAlgo1=new egcAlgo(Simulator_A.getBuilding());

        Call_A call_a=new Call_A(9,4,8);
        Call_A call_b=new Call_A(9,4,8);
        Call_A call_c=new Call_A(2,2,2);
        Call_A call_d=new Call_A(5,1,1);
        Call_A call_e=new Call_A(3,2,3);

        Simulator_A.initAlgo(egcAlgo1);
        Simulator_A.runSim();
        Simulator_A.report();
        b1.getElevetor(0).goTo(5);
        b1.getElevetor(1).goTo(19);
        b1.getElevetor(2).goTo(-2);
        b1.getElevetor(3).goTo(6);
        b1.getElevetor(4).goTo(1);
        b1.getElevetor(5).goTo(0);

        assertEquals(1,egcAlgo1.allocateAnElevator(call_a));
        assertEquals(1,egcAlgo1.allocateAnElevator(call_b));
        assertEquals(8,egcAlgo1.allocateAnElevator(call_c));
        assertEquals(0,egcAlgo1.allocateAnElevator(call_d));
        assertEquals(1,egcAlgo1.allocateAnElevator(call_e));

    }

    @Test
    void cmdElevator() {
        Simulator_A.initData(5,null);
        b1=Simulator_A.getBuilding();
        egcAlgo1=new egcAlgo(Simulator_A.getBuilding());

        Simulator_A.initAlgo(egcAlgo1);
        Simulator_A.runSim();
        Simulator_A.report();
        b1.getElevetor(0).goTo(5);
        b1.getElevetor(1).goTo(19);
        b1.getElevetor(2).goTo(-2);
        b1.getElevetor(3).goTo(6);
        b1.getElevetor(4).goTo(1);
        b1.getElevetor(5).goTo(0);

        assertEquals(egcAlgo1.getJobs()[0].get(0),-4);
        assertEquals(egcAlgo1.getJobs()[1].get(0),47);
        assertEquals(egcAlgo1.getJobs()[2].get(2),-1);
        assertEquals(egcAlgo1.getJobs()[3].get(2),21);
        assertEquals(egcAlgo1.getJobs()[4].get(0),16);
        assertEquals(egcAlgo1.getJobs()[5].get(0),-2);



    }
}