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
              "NoobMax Algorithm 1", // agent name
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
        while (!shouldStop()) {
            for(Integer a : ks.getMoves()){
                if (ks.isLegalMove(a)) {
                    KalahState copy = new KalahState(ks);
                    copy.doMove(a);
                    state = minmax(copy, depth, Integer.MIN_VALUE, Integer.MAX_VALUE,copy.getSideToMove() == KalahState.Player.SOUTH);
                    if (state > bestState) {
                        bestState = state;
                        bestMove = a;
                    }
                }
            }
            depth++;
            submitMove(bestMove);
        }

    }

    private int minmax(KalahState a, int depth, int alpha, int beta, boolean maximizingPlayer) {
        if(depth == 0 || game_over(a)) {
            return a.getStoreSouth() -a.getStoreNorth();
        }
        int value;
        if(maximizingPlayer){
            value = Integer.MIN_VALUE;
            for(Integer n : a.getMoves()){
                KalahState copy = new KalahState(a);
                copy.doMove(n);
                value = Math.max(value,minmax(copy,depth,alpha,beta,copy.getSideToMove() == KalahState.Player.SOUTH));
                alpha = Math.max(alpha,value);
                if(value >= beta){
                    return value;
                }
            }
        }else{
            value = Integer.MAX_VALUE;
            for(Integer n : a.getMoves()){
                KalahState copy = new KalahState(a);
                copy.doMove(n);
                value = Math.min(value,minmax(copy,depth-1,alpha,beta,copy.getSideToMove() == KalahState.Player.SOUTH));
                beta = Math.min(beta,value);
                if( value <= alpha) return value;
            }
        }
        return value;

    }


    private boolean game_over(KalahState state){
        KalahState.GameResult result = state.result();
        return result == KalahState.GameResult.LOSS || result == KalahState.GameResult.WIN || result == KalahState.GameResult.DRAW;
    }



    public static void main(String[] args) throws IOException {
        new Agent().run();
//        KalahState ks = new KalahState(8,8);
//        System.out.println(ks.getSideToMove());
//        ks.doMove(1);
    }

}