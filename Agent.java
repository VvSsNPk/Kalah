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
        ArrayList<Integer> moves = ks.getMoves();
        int rnd = rng.nextInt(moves.size());
        this.submitMove(moves.get(rnd));
    }

    private KalahState minmax(KalahState a) {
        max(a);

        return a;
    }

    private void max(KalahState a){
        min(a);
    }

    private void min(KalahState a){
        max(a);
    }



    public static void main(String[] args) throws IOException {
        new Agent().run();
    }
}
