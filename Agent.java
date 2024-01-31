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
        int bestValue = Integer.MIN_VALUE;
        int bestMove = ks.randomLegalMove();
        int depth = 0;
        System.out.println("Search started !");
        while(!shouldStop()){
            for(Integer n : ks.getMoves()){
                KalahState copy = new KalahState(ks);
                KalahState.Player before = copy.getSideToMove();
                copy.doMove(n);
                KalahState.Player after = copy.getSideToMove();
                int value;
                if(before != after) {
                    value = Integer.max(bestValue,  -NegaMax(copy, depth - 1, Integer.MIN_VALUE, Integer.MAX_VALUE));
                }else{
                    value = Integer.max(bestValue, NegaMax(copy,depth-1,Integer.MAX_VALUE,Integer.MIN_VALUE));
                }
                if(value > bestValue){
                    bestValue = value;
                    bestMove = n;
                }

            }
            submitMove(bestMove);
            depth++;
        }

    }

    private int NegaMax(KalahState state, int depth, double alpha, double beta){
        if(depth == 0 || game_over(state)){
            KalahState.GameResult result = state.result();
            if(result == KalahState.GameResult.UNDECIDED) {
                return state.getStoreLead();
            }else{
                if(result == KalahState.GameResult.KNOWN_LOSS||result == KalahState.GameResult.LOSS){
                    return  Integer.MIN_VALUE + 1;
                }else if(result == KalahState.GameResult.WIN || result == KalahState.GameResult.KNOWN_WIN) {
                    return Integer.MAX_VALUE;
                }else{
                    return 0;

                }
            }
        }else{
            int bestValue = Integer.MIN_VALUE;
            for(Integer n : move_ordering(state)){
                KalahState copy = new KalahState(state);
                KalahState.Player before = copy.getSideToMove();
                copy.doMove(n);
                KalahState.Player after = copy.getSideToMove();
                if(before != after) {
                    bestValue = Integer.max(bestValue, -NegaMax(copy, depth - 1, -beta, -alpha));
                }else{
                    bestValue = Integer.max(bestValue, NegaMax(copy,depth-1,alpha,beta));
                }
                alpha = Double.max(alpha,bestValue);
                if(alpha >= beta){
                    break;
                }

            }
            return bestValue;
        }
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

    private int doubleMoves(KalahState state){
        ArrayList<Integer> moves = state.getMoves();
        int counter = 0;
        int counter2 = 0;
        for(Integer n : moves){
            if(state.isDoubleMove(n)) {
                counter++;
            }
            if(state.isCaptureMove(n)){
                counter2++;
            }
        }
        return counter + counter2;
    }



    public static void main(String[] args) throws IOException {
        new Agent().run();
    }

}