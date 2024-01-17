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
        int  depth = 1;
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
                if (min > final_move) {
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
            if (depth <= 0 || game_over(a)){
                KalahState.GameResult result = a.result();
                if(result == KalahState.GameResult.KNOWN_WIN || result == KalahState.GameResult.WIN){
                    return 100000000;
                }else if(result == KalahState.GameResult.LOSS || result == KalahState.GameResult.KNOWN_LOSS){
                    return -100000000;
                }
                //if(a.result() != KalahState.GameResult.UNDECIDED) eval = static_eval(a);
               return a.getStoreSouth() - a.getStoreNorth();
            }
            else{
            int maxVal = Integer.MIN_VALUE;
            for(Integer n: move_ordering(a)){
                KalahState copy = new KalahState(a);
                copy.doMove(n);
                int store;
                if(a.getSideToMove() == KalahState.Player.SOUTH){
                    store = maxvalue(copy,depth-1,alpha,beta);
                }else {
                    store = minvalue(copy, depth - 1, alpha, beta);
                }
                maxVal = Math.max(maxVal,store);
                if(maxVal >= beta){
                    return maxVal;
                }
                alpha = Math.max(alpha,maxVal);

            }

        return maxVal;}
    }

    private int minvalue(KalahState a, int depth,int alpha, int beta){
        if (depth <= 0 || game_over(a)){
            KalahState.GameResult result = a.result();
            if(result == KalahState.GameResult.KNOWN_WIN || result== KalahState.GameResult.WIN){
                return -100000000;
            }else if(result == KalahState.GameResult.LOSS || result == KalahState.GameResult.KNOWN_LOSS){
                return 100000000;
            }
            //if(a.result() != KalahState.GameResult.UNDECIDED) eval = static_eval(a);
            return a.getStoreSouth()-a.getStoreNorth();
        }
        else{
        int minVal = Integer.MAX_VALUE;
        for(Integer n : move_ordering(a)){
            KalahState copy = new KalahState(a);
            copy.doMove(n);
            int store ;
            if(a.getSideToMove() == KalahState.Player.NORTH){
                store = minvalue(copy,depth-1,alpha,beta);
            }else {
                store = maxvalue(copy, depth - 1, alpha, beta);
            }
            minVal = Math.min(minVal,store);
            if(minVal <= alpha){
                return minVal;
            }
            beta = Math.min(beta, minVal);
        }

        return minVal;}
    }

    private ArrayList<Integer> move_ordering(KalahState ks){
        ArrayList<Integer> moves = new ArrayList<>();
        ArrayList<Integer> check = ks.getMoves();
        for(Integer a:check){
            if(ks.isCaptureMove(a)){
                moves.add(a);
            }
        }
        for(Integer b:check){
            if(ks.isDoubleMove(b)){
                moves.add(b);
            }
        }
        for(Integer b: check){
            if(!ks.isDoubleMove(b) && !ks.isCaptureMove(b)){
                moves.add(b);
            }
        }

        return moves;
    }

    private boolean game_over(KalahState a){
        KalahState.GameResult result = a.result();
        return result == KalahState.GameResult.DRAW || result == KalahState.GameResult.LOSS || result == KalahState.GameResult.WIN;
    }

    private int static_eval(KalahState a){
        KalahState.GameResult result = a.result();
        if(result == KalahState.GameResult.KNOWN_WIN || result == KalahState.GameResult.WIN){
                return Integer.MAX_VALUE;
            }else if(result == KalahState.GameResult.LOSS || result== KalahState.GameResult.KNOWN_LOSS){
            return Integer.MIN_VALUE +1;
        }else{
            return 0;
        }

    }


    public static void main(String[] args) throws IOException {

        Agent agent = new Agent();
        agent.run();
    }

}