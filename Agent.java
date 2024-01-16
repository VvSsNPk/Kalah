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
                if(a.getSideToMove() == KalahState.Player.SOUTH){

                    return a.getStoreLead();
                } else{
                    return -a.getStoreLead();
                }
            }
            else{
            int maxVal = Integer.MIN_VALUE;
            ArrayList<Integer> moves = a.getMoves();
            move_ordering(a,moves);
            for(Integer n: moves){
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
            if(a.getSideToMove() == KalahState.Player.NORTH){
                return -a.getStoreLead();
            }else {
                return a.getStoreLead();
            }
        }
        else{
        int minVal = Integer.MAX_VALUE;
        ArrayList<Integer> moves = a.getMoves();
        move_ordering(a,moves);
        for(Integer n : moves){
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

    public int evaluation(KalahState a,Integer n){
            if(n == 0){
                return -a.getStoreLead();
            }else {
                return a.getStoreLead();
            }

    }

    private int double_or_capture(KalahState ks){
        int eval = Integer.MIN_VALUE;

        for(int i: ks.getMoves()){
            KalahState copier = new KalahState(ks);
            if(ks.isDoubleMove(i) || ks.isCaptureMove(i)){
                copier.doMove(i);
                int store = 0;
                if(ks.getSideToMove()==KalahState.Player.SOUTH){
                    store = copier.getStoreLead();
                }else{
                    store = -copier.getStoreLead();
                }
                if(store > eval){
                    eval = store;
                }
            }
        }
        return eval;
    }

    private void move_ordering(KalahState ks, ArrayList<Integer> moves){

        Comparator<Integer> customComparator = (a, b) -> {
            if (ks.isCaptureMove(a) && !ks.isCaptureMove(b)) {
                return -1; // a comes before b
            } else if (ks.isDoubleMove(a) && !ks.isDoubleMove(b)) {
                return -1; // a comes before b
            } else if (ks.isCaptureMove(b) && !ks.isCaptureMove(a)) {
                return 1; // b comes before a
            } else if (ks.isDoubleMove(b) && !ks.isDoubleMove(a)) {
                return 1; // b comes before a
            } else {
                return 0; // no change in order
            }
        };

        // Use the custom comparator to reorder the ArrayList
        moves.sort(customComparator);
    }


    public static void main(String[] args) throws IOException {

        Agent agent = new Agent();
        agent.run();
    }

}