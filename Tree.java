import info.kwarc.kalah.KalahState;

import java.util.ArrayList;

public class Tree {
    private KalahState state;
    private ArrayList<Tree> children;
    private Integer eval;

    public Tree(KalahState state, Integer eval) {
        this.state = state;
        this.children = new ArrayList<>();
        this.eval = eval;
    }
}
