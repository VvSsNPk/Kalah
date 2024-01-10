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
        int bestMove = 0;
        int bestState = Integer.MIN_VALUE;
        int state;
        int depth = 1;
        MINMAX south;
        if(ks.getSideToMove() == KalahState.Player.SOUTH){
            south = MINMAX.MAX;
        }else{
            south = MINMAX.MIN;
        }
        for(Integer a : ks.getMoves()){
            while (!shouldStop()) {
                if (ks.isLegalMove(a)) {
                    KalahState copy = new KalahState(ks);
                    copy.doMove(a);
                    state = minmax(copy, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, south);
                    if (state > bestState) {
                        bestState = state;
                        bestMove = a;
                    }
                }
            }
            depth++;
        }
        submitMove(bestMove);
    }

    private int minmax(KalahState a, int depth, int alpha, int beta, MINMAX minmax) {
        int bestState = 0;
        int state;
        if(depth <= 0) return  evaluation(a,minmax);
        //bestState = (a.getSideToMove() == KalahState.Player.SOUTH) ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        for(int i = 0; i<a.getBoardSize();i++){
            if(a.isLegalMove(i)){
                KalahState copy = new KalahState(a);
                copy.doMove(i);
                if (minmax == MINMAX.MIN){
                    bestState = -1000000000;
                    state = minmax(copy,--depth,alpha,beta, minmax);
                    bestState = Math.min(bestState,state);
                    alpha = Math.min(alpha,bestState);
                }
                else {
                    bestState = 1000000000;
                    state = minmax(copy, --depth,alpha,beta, minmax);
                    bestState = Math.max(bestState, state);
                    beta = Math.max(beta, bestState);
                }
                if(beta <= alpha){
                    break;
                }
            }
        }

        return bestState;
    }

    public int evaluation(KalahState a,MINMAX minmax){
        int eval = a.getStoreSouth() -a.getStoreNorth();
        if(minmax == MINMAX.MAX){
            if(eval > 0){
                return eval;
            }else{
                return -eval;
            }
        }else{
            if(eval < 0){
                return eval;
            }else{
                return  -eval;
            }
        }
    }



    public static void main(String[] args) throws IOException {
        new Agent().run();
//        KalahState ks = new KalahState(8,8);
//        System.out.println(ks.getSideToMove());
//        ks.doMove(1);
    }

    public enum MINMAX{
        MIN,
        MAX,
    }
}