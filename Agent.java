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
        for(Integer a : ks.getMoves()){
            while (!shouldStop()) {
                if (ks.isLegalMove(a)) {
                    KalahState copy = new KalahState(ks);
                    copy.doMove(a);
                    state = minmax(copy, depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
                    if (state > bestState) {
                        bestState = state;
                        bestMove = a;
                    }
                }
            }
            depth++;
        }
        if(ks.isLegalMove(bestMove)){
            submitMove(bestMove);
        }
        else{
            submitMove(ks.randomLegalMove());
        }
    }

    private int minmax(KalahState a, int depth, int alpha, int beta) {
        int bestState = 0;
        int state;
        if(depth <= 0) return  evaluation(a);
        bestState = a.getSideToMove() == KalahState.Player.SOUTH ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        for(int i = 0; i<a.getBoardSize();i++){
            if(a.isLegalMove(i)){
                KalahState copy = new KalahState(a);
                copy.doMove(i);
                if (copy.getSideToMove() == KalahState.Player.SOUTH){
                    //bestState = Integer.MAX_VALUE;
                    state = minmax(copy,--depth,alpha,beta);
                    bestState = Math.min(bestState,state);
                    beta = Math.min(beta,bestState);
                    if(beta <= alpha){
                        break;
                    }
                }
                else {
                    //bestState = -1000000000;
                    state = minmax(copy, --depth,alpha,beta);
                    bestState = Math.max(bestState, state);
                    alpha = Math.max(alpha, bestState);
                    if(beta <= alpha){
                        break;
                    }
                }
            }
        }

        return bestState;
    }

    public int evaluation(KalahState a){
            return a.getStoreSouth() - a.getStoreNorth();
    }



    public static void main(String[] args) throws IOException {
        new Agent().run();
    }
}
