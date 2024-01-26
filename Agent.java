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
        super(System.getenv("USE_WEBSOCKET") == null
              ? "kalah.kwarc.info/socket" : "localhost",
              System.getenv("USE_WEBSOCKET") == null
              ? null : 2671,
              System.getenv("USE_WEBSOCKET") == null
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
        while (!shouldStop()) {
            for(Integer a : ks.getMoves()){

                if (ks.isLegalMove(a)) {
                    KalahState copy = new KalahState(ks);
                    copy.doMove(a);
                    boolean player = copy.getSideToMove()== KalahState.Player.SOUTH;
                    state = minmax(copy, depth, Integer.MIN_VALUE, Integer.MAX_VALUE,player);
                    if (state > bestState) {
                        bestState = state;
                        bestMove = a;
                    }
                }
            }
            System.out.println("============================================");
            System.out.println("eval : " + bestState + "\ndepth : " + depth + "\nmove :" + bestMove);
            System.out.println("============================================");
            submitMove(bestMove);
            sendComment("eval : " + bestState + "\ndepth : " + depth + "\nmove :" + bestMove);
            depth++;
        }

    }

    private int minmax(KalahState a, int depth, int alpha, int beta, boolean maximizingPlayer) {
        if(depth <= 0 || game_over(a)) {
            KalahState.GameResult result = a.result();
            if(result == KalahState.GameResult.UNDECIDED) {
                return a.getStoreSouth() - a.getStoreNorth();
            }else{
                if(result == KalahState.GameResult.KNOWN_WIN || result == KalahState.GameResult.WIN){
                    return Integer.MAX_VALUE;
                }else if(result == KalahState.GameResult.KNOWN_LOSS || result == KalahState.GameResult.LOSS){
                    return  Integer.MIN_VALUE + 1;
                }else{
                    return 0;
                }
            }
        }
        int value;
        if(maximizingPlayer){
            value = Integer.MIN_VALUE;
            for(Integer n : move_ordering(a)){
                KalahState copier = new KalahState(a);
                copier.doMove(n);
                boolean player = copier.getSideToMove() == KalahState.Player.SOUTH;
                value = Math.max(value,minmax(copier,depth-1,alpha,beta,player));
                alpha = Math.max(alpha,value);
                if(value >= beta){
                    return value;
                }
            }
        }else{
            value = Integer.MAX_VALUE;
            for(Integer n : move_ordering(a)){
                KalahState copier = new KalahState(a);
                copier.doMove(n);
                boolean player = copier.getSideToMove() == KalahState.Player.SOUTH;
                value = Math.min(value,minmax(a,depth-1,alpha,beta,player));
                beta = Math.min(beta,value);
                if( value <= alpha) return value;
            }
            return value;
        }


        return value;
    }


    private boolean game_over(KalahState state){
        KalahState.GameResult result = state.result();
        return result == KalahState.GameResult.LOSS || result == KalahState.GameResult.WIN || result == KalahState.GameResult.DRAW;
    }

    private ArrayList<Integer> move_ordering(KalahState state){
        ArrayList<Integer> moves = new ArrayList<>();
        ArrayList<Integer> store = state.getMoves();
        for(Integer n : store){
            if(state.isCaptureMove(n)){
                moves.add(n);
            }
        }
        for(Integer n : store){
            if(state.isDoubleMove(n)){
                moves.add(n);
            }
        }
        for(Integer n: store){
            if(!state.isDoubleMove(n) && !state.isCaptureMove(n)){
                moves.add(n);
            }
        }
        return moves;
    }



    public static void main(String[] args) throws IOException {
        new Agent().run();
//        KalahState ks = new KalahState(8,8);
//        System.out.println(ks.getSideToMove());
//        ks.doMove(1);
    }

}