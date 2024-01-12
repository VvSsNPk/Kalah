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
        int  depth = 1;
        int final_move = Integer.MIN_VALUE;
        int move_to = ks.randomLegalMove();
        do {

            for (Integer n : ks.getMoves()) {
                KalahState copier = new KalahState(ks);
                ks.doMove(n);
                int min = minvalue(copier, depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
                if (min >= final_move) {
                    final_move = min;
                    move_to = n;
                }
            }
            depth++;
        } while (!shouldStop());
        submitMove(move_to);
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
                maxVal = Math.max(maxVal,store);
                if(maxVal >= beta){
                    return maxVal;
                }
                alpha = Math.max(alpha,maxVal);

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
            minVal = Math.min(minVal,store);
            if(minVal <= alpha){
                return minVal;

            }
            beta = Math.min(beta, minVal);
        }

        return minVal;
    }

    public int evaluation(KalahState a,Integer n){
        int val = 0;
        if(n == 1){
            if(a.getSideToMove() == KalahState.Player.NORTH){
                val = a.getHouseSum()-a.getStoreNorth();
            }else{
                val = a.getHouseSum() - a.getStoreSouth();
            }
        }else if(n == 0){
            if(a.getSideToMove() == KalahState.Player.NORTH){
                val = a.getHouseSum()-a.getStoreNorth();
            }else{
                val = a.getHouseSum()-a.getStoreSouth();
            }
        }
        return val;
    }

    private int no_of_captures(KalahState ks){
        int count = 0;
        for(int i : ks.getMoves()){
            if(ks.isCaptureMove(i)) count++;
        }
        return  count;
    }

    private int no_of_doubles(KalahState ks){
        int count = 0;
        for(int i: ks.getMoves()){
            if(ks.isDoubleMove(i))count++;
        }
        return count;
    }


    public static void main(String[] args) throws IOException {
        new Agent().run();
//        KalahState ks = new KalahState(8,8);
//        System.out.println(ks.getSideToMove());
//        ks.doMove(1);
    }

}