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
                KalahState.Player player = copier.getSideToMove();
                int min;
                if(player == KalahState.Player.SOUTH){
                    min = evaluateKalahState(copier,depth,Integer.MIN_VALUE,Integer.MAX_VALUE,true);
                }else {
                    min = evaluateKalahState(copier,depth,Integer.MIN_VALUE,Integer.MAX_VALUE,false);
                }
                if (min > final_move) {
                    final_move = min;
                    move_to = n;
                }
            }
            KalahState printer = new KalahState(ks);
            printer.doMove(move_to);
            System.out.println(printer);
            submitMove(move_to);
            depth++;
        } while (!shouldStop());
        String comment = "evaluation : " + final_move + "\n" +
                "final move : " + move_to + "\n" +
                "depth : " + depth ;
        sendComment(comment);



    }
    private int evaluateKalahState(KalahState a, int depth, int alpha, int beta, boolean isMaximizingPlayer) {
        if (depth <= 0 || game_over(a)) {
            KalahState.GameResult result = a.result();

            if (result == KalahState.GameResult.KNOWN_WIN || result == KalahState.GameResult.WIN) {
                return isMaximizingPlayer ? 100000000 : -100000000;
            } else if (result == KalahState.GameResult.LOSS || result == KalahState.GameResult.KNOWN_LOSS) {
                return isMaximizingPlayer ? -100000000 : 100000000;
            }

            return a.getStoreSouth() - a.getStoreNorth();
        } else {
            int bestValue = isMaximizingPlayer ? Integer.MIN_VALUE : Integer.MAX_VALUE;

            for (Integer n : move_ordering(a)) {
                KalahState copy = new KalahState(a);
                copy.doMove(n);
                isMaximizingPlayer = copy.getSideToMove() == KalahState.Player.SOUTH;
                int store;
                if (isMaximizingPlayer) {
                    store = evaluateKalahState(copy, depth - 1, alpha, beta, false);
                    bestValue = Math.max(bestValue, store);
                    alpha = Math.max(alpha, bestValue);
                } else {
                    store = evaluateKalahState(copy, depth - 1, alpha, beta, true);
                    bestValue = Math.min(bestValue, store);
                    beta = Math.min(beta, bestValue);
                }

                if (bestValue >= beta && isMaximizingPlayer) {
                    return bestValue;
                }

                if (bestValue <= alpha && !isMaximizingPlayer) {
                    return bestValue;
                }
            }

            return bestValue;
        }
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

    private int no_of_doubles(KalahState a){
        int count = 0;
        for(Integer n: a.getMoves()){
            if(a.isDoubleMove(n)){
                count++;
            }
        }
        return count;
    }
    private int total_capture(KalahState a){
        int capture = 0;
        KalahState.Player player;
        for(Integer n : a.getMoves()){
            if(a.isCaptureMove(n)){
                if(a.getSideToMove() == KalahState.Player.SOUTH){
                    player = KalahState.Player.NORTH;
                }else{
                    player = KalahState.Player.SOUTH;
                }
                capture = capture + a.getHouse(player,n);
            }
        }
        return capture;
    }


    public static void main(String[] args) throws IOException {

        Agent agent = new Agent();
        agent.run();
    }

}