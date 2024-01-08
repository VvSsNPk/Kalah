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
        int bestMove = 0;
        int bestState = -1000000000;
        int state;
        int depth = 1;
        for(Integer a : ks.getMoves()){
            while(true){
                if(shouldStop()){
                    break;
                }
                if(ks.isLegalMove(a)){
                    KalahState copy = new KalahState(ks);
                    copy.doMove(a);
                    state = minmax(copy,depth, -1000000000,1000000000);
                    if(state > bestState){
                        bestState = state;
                        bestMove = a;
                    }
                }
            }
            depth++;
        }
        submitMove(bestMove);
    }

    private int minmax(KalahState a, int depth, int alpha, int beta) {
        int bestState = 0;
        int state;
        if(depth <= 0) return  evaluation(a);
        for(int i = 0; i<a.getBoardSize();i++){
            if(a.isLegalMove(i)){
                KalahState copy = new KalahState(a);
                copy.doMove(i);
                if (copy.getSideToMove() == KalahState.Player.SOUTH){
                    bestState = 1000000000;
                    state = minmax(copy,--depth,alpha,beta);
                    bestState = Math.min(bestState,state);
                    beta = Math.min(beta,bestState);
                    if(beta <= alpha){
                        break;
                    }
                }
                else {
                    bestState = -1000000000;
                    state = minmax(copy, --depth,alpha,beta);
                    bestState = Math.max(bestState, state);
                    alpha = Math.max(alpha, bestState);
                    if(beta <= alpha){
                        break;
                    }
                }
            }
        }

        return bestState;
    }

    public int evaluation(KalahState a){
        if(a.getSideToMove() == KalahState.Player.SOUTH){
            return a.getHouseSumSouth() - a.getHouseSumNorth();
        }
        else{
            return a.getHouseSumNorth() - a.getHouseSumSouth();
        }
    }



    public static void main(String[] args) throws IOException {

    /*    KalahState state = new KalahState(6, 4);
        var list = state.getMoves();
        //System.out.println(list);
        KalahState copy = new KalahState(state);

        //copy.doMove(2);
        var test = copy.isDoubleMove(2);
        //System.out.println(copy);
        copy.doMove(2);
        System.out.println(test);
        //System.out.println(copy);
        list = copy.getMoves();*/
        //System.out.println(list);
 /*       List<KalahState> store = new ArrayList<>();
        for (Integer integer : list) {
            KalahState copier = new KalahState(state);
            copier.doMove(integer);
            store.add(copier);
        }
        for (KalahState state1 : store){
            System.out.println(state1);
        }*/
        new Agent().run();
    }
}
