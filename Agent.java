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
              null, // agent name
              null, // author list
              null, // description
              null, // agent token
              false  // suppress network
              );
    }

    @Override
    public void search(KalahState ks) throws IOException {
        this.submitMove(minmax(ks));
    }

    private int minmax(KalahState state) {
        int bestMove = -1;
        int bestScore = Integer.MIN_VALUE;
        ArrayList<Integer> a = state.getMoves();
        for (int i = 0; i < a.size(); i++) {
            if (state.getHouse(KalahState.Player.SOUTH,a.get(i)) != 0) { // Checking if the pit is not empty
                KalahState copyState = new KalahState(state);
                copyState.doMove(i);
                int score = evaluateMove(copyState);

                if (score > bestScore) {
                    bestScore = score;
                    bestMove = i;
                }
            }
        }

        return bestMove;
    }

    private int evaluateMove(KalahState state){
        return state.getHouseSumSouth() - state.getHouseSumNorth();
    }

    private void max(KalahState a){
        min(a);
    }

    private void min(KalahState a){
        max(a);
    }



    public static void main(String[] args) throws IOException {

        KalahState state = new KalahState(6, 4);
        var list = state.getMoves();
        //System.out.println(list);
        KalahState copy = new KalahState(state);

        //copy.doMove(2);
        var test = copy.isDoubleMove(2);
        //System.out.println(copy);
        copy.doMove(3);
        System.out.println(copy);
        //System.out.println(test);
        //System.out.println(copy);
        //list = copy.getMoves();
        //System.out.println(list);
        /*List<KalahState> store = new ArrayList<>();
        for (Integer integer : list) {
            KalahState copier = new KalahState(state);
            copier.doMove(integer);
            store.add(copier);
        }
        for (KalahState state1 : store){
            System.out.println(state1);
        }*/
        //new Agent().run();
    }
}
