import info.kwarc.kalah.*;
import java.util.*;
import java.io.*;

// A java agent is implemented by extending the abstract class Agent.
// When doing so, keep in mind to invoke the super-constructor and to
// implement the "search" method.
//
// This agent is just a simple, minimal example that returns a random
// legal move.  You may use this as a foundation.
class Agent extends info.kwarc.kalah.Agent {
    private final Random rng = new Random();

    public Agent() {
        super(System.getenv("USE_WEBSOCKET") != null
              ? "kalah.kwarc.info/socket" : "localhost",
              System.getenv("USE_WEBSOCKET") != null
              ? null : 2671,
              System.getenv("USE_WEBSOCKET") != null
              ? ProtocolManager.ConnectionType.WebSocketSecure
              : ProtocolManager.ConnectionType.TCP,
              "NoobMax Algorithm", // agent name
              "Praveen Kumar, Sagar Sikdar", // author list
              "Minmax noob algorithm", // description
              "GODISALSOHUMAN", // agent token
              false  // suppress network
              );
    }

    @Override
    public void search(KalahState ks) throws IOException {
       submitMove(ks.randomLegalMove());
    }

    private int minmax(KalahState a, int depth, int alpha, int beta) {

        return 0;

    }

    private int maxvalue(KalahState a, int depth, int alpha, int beta){
            if (depth == 0) return evaluation(a, 1);
            int maxVal = Integer.MIN_VALUE;
            for(Integer n: a.getMoves()){
                KalahState copy = new KalahState(a);
                copy.doMove(n);
                depth = depth -1;
                int store = minvalue(copy, depth, alpha, beta);
                if(store > maxVal){
                    maxVal = store;
                }
            }
        return maxVal;
    }

    private int minvalue(KalahState a, int depth,int alpha, int beta){
        if (depth == 0) return evaluation(a, 0);
        int minVal = Integer.MAX_VALUE;
        for(Integer n : a.getMoves()){
            KalahState copy = new KalahState(a);
            copy.doMove(n);
            depth = depth -1;
            int store = maxvalue(copy,depth,alpha,beta);
            if(store < minVal){
                minVal = store;
            }
        }
        return minVal;
    }

    public int evaluation(KalahState a,Integer n){
        int val = a.getHouseSumNorth()-a.getHouseSumSouth();
        if(n == 0){
            if(val > 0) return val;
            else return -val;
        }else{
            if(val<0) return val;
            else return -val;
        }

    }



    public static void main(String[] args) throws IOException {
        //new Agent().run();
//        KalahState ks = new KalahState(8,8);
//        System.out.println(ks.getSideToMove());
//        ks.doMove(1);
    }

}