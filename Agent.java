import info.kwarc.kalah.*;
import java.util.*;
import java.io.*;
import java.util.logging.Logger;

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
        int  depth = 0;
        int final_move = Integer.MIN_VALUE;
        int move_to = ks.randomLegalMove();
        do {

            for (Integer n : ks.getMoves()) {
                KalahState copier = new KalahState(ks);
                copier.doMove(n);
                int min;
                if(ks.isDoubleMove(n)){
                    min = maxvalue(copier,depth,Integer.MIN_VALUE,Integer.MAX_VALUE);
                }else {
                    min = minvalue(copier, depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
                }
                //if(result == KalahState.GameResult.KNOWN_WIN || result == KalahState.GameResult.WIN) min = Integer.MAX_VALUE;
                if (min >= final_move && ks.isLegalMove(n)) {
                    final_move = min;
                    move_to = n;
                }
            }
            submitMove(move_to);
            depth++;
        } while (!shouldStop());
        String comment = "evaluation : " + final_move + "\n" +
                "final move : " + move_to + "\n" +
                "depth : " + depth ;
        sendComment(comment);



    }

    private int maxvalue(KalahState a, int depth, int alpha, int beta){
            if (depth <= 0){
               return a.getStoreSouth() - a.getStoreNorth();
            }
            else{
            int maxVal = Integer.MIN_VALUE;
            for(Integer n: move_ordering(a)){
                KalahState copy = new KalahState(a);
                copy.doMove(n);
                int store = minvalue(copy, depth-1, alpha, beta);
                maxVal = Math.max(maxVal,store);
                if(maxVal >= beta){
                    return maxVal;
                }
                alpha = Math.max(alpha,maxVal);

            }

        return maxVal;}
    }

    private int minvalue(KalahState a, int depth,int alpha, int beta){
        if (depth <= 0){
            return a.getStoreSouth()-a.getStoreNorth();
        }
        else{
        int minVal = Integer.MAX_VALUE;
        for(Integer n : move_ordering(a)){
            KalahState copy = new KalahState(a);
            copy.doMove(n);
            int store = maxvalue(copy,depth-1,alpha,beta);
            minVal = Math.min(minVal,store);
            if(minVal <= alpha){
                return minVal;
            }
            beta = Math.min(beta, minVal);
        }

        return minVal;}
    }

    private ArrayList<Integer> move_ordering(KalahState ks){
        ArrayList<Integer> moves = ks.getMoves();
        int j = 0;
        for(int i = 0; i< moves.size(); i++){
            if(ks.isDoubleMove(moves.get(i)) || ks.isCaptureMove(moves.get(i))){
                int temp = moves.get(i);
                moves.set(i,moves.get(j));
                moves.set(j,temp);
                j = j + 1;
            }
        }
        return moves;
    }

    private double minmax_search(KalahState game_state,int initial_depth, int final_depth, double alpha, double beta){
        KalahState.GameResult result = game_state.result();
        if(result != KalahState.GameResult.UNDECIDED){
            if(result == KalahState.GameResult.KNOWN_WIN||result== KalahState.GameResult.WIN){
                return Double.POSITIVE_INFINITY;
            }else if(result == KalahState.GameResult.LOSS || result == KalahState.GameResult.KNOWN_LOSS){
                return Double.NEGATIVE_INFINITY;
            }else{
                return 0;
            }
        }else{
            if(initial_depth == final_depth){
                return game_state.getStoreSouth() - game_state.getStoreNorth();
            }else{
                Double best_eval = null;

                for(Integer move: game_state.getMoves()){
                    KalahState copy = new KalahState(game_state);
                    KalahState.Player before = copy.getSideToMove();
                    copy.doMove(move);
                    double eval = minmax_search(copy,initial_depth+1,final_depth,alpha,beta);
                    if(before != copy.getSideToMove()){
                        eval = -eval;
                    }
                    if(best_eval == null || eval > best_eval){
                        best_eval = eval;
                    }
                    alpha = Math.max(alpha,eval);
                    beta = Math.min(beta,eval);
                    if(alpha < beta){
                        return best_eval;
                    }
                }
                return best_eval;
            }
        }


    }


    public static void main(String[] args) throws IOException {

        Agent agent = new Agent();
        agent.run();
    }

}