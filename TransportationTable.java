import java.util.HashMap;

public class TransportationTable {

    private final HashMap<Integer,Integer> table;


    public TransportationTable() {
        this.table = new HashMap<>();
    }

    public void store(int key, int value){
        table.put(key, value);
    }

    public int lookup(int key){
        return table.getOrDefault(key, 0);
    }
}
