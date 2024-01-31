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
        int depth = 1;
        while(!shouldStop()){
            int eval = minmax_search(ks,0,depth,Integer.MIN_VALUE,Integer.MAX_VALUE);
            if(eval == Integer.MAX_VALUE || eval ==Integer.MIN_VALUE +1){
                break;
            }
            depth ++;
        }


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

    private int minmax_search(KalahState game_state,int initial_depth, int final_depth, int alpha, int beta) throws IOException {
            KalahState.GameResult result = game_state.result();
            if(result != KalahState.GameResult.UNDECIDED){
                if(result == KalahState.GameResult.KNOWN_WIN || result == KalahState.GameResult.WIN){
                    return Integer.MAX_VALUE;
                }else if(result == KalahState.GameResult.LOSS || result == KalahState.GameResult.KNOWN_LOSS){
                    return Integer.MIN_VALUE +1;
                }else{
                    return 0;
                }
            }else{
                if(initial_depth >= final_depth || game_over(game_state)){
                    return game_state.getStoreSouth() - game_state.getStoreNorth();
                }
            int best_move = Integer.MIN_VALUE;
            int best_eval = -1000000;
            for(int n : move_ordering(game_state)){
                KalahState copier = new KalahState(game_state);
                KalahState.Player player = copier.getSideToMove();
                copier.doMove(n);
                KalahState.Player after = copier.getSideToMove();
                int eval = minmax_search(copier,initial_depth+1,final_depth,alpha,beta);
                if(after == KalahState.Player.SOUTH){
                    eval = -eval;
                }
                if(player == KalahState.Player.SOUTH){
                    best_eval = Math.max(eval,best_eval);
                }else{
                    best_eval = Math.min(eval,best_eval);
                }
                best_move = n;
                if(player == KalahState.Player.SOUTH){
                    alpha = Integer.max(alpha,best_eval);
                }else{
                    beta = Integer.min(beta,best_eval);
                }

                if(alpha >= beta){
                    return best_eval;
                }
                if(initial_depth == 0){
                    submitMove(best_move);
                    String comment = "move : " + (best_move + 1) + "\n"+
                            "eval : " + best_eval + "\n" +
                            "depth : " + final_depth;
                    sendComment(comment);
                }


            }
            return best_eval;
        }


    }


    private boolean game_over(KalahState state){
        KalahState.GameResult result = state.result();
        return result == KalahState.GameResult.WIN || result == KalahState.GameResult.LOSS || result == KalahState.GameResult.DRAW;
    }


    public static void main(String[] args) throws IOException {

        Agent agent = new Agent();
        agent.run();
    }

}