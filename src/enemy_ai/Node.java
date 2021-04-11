package enemy_ai;
/**
 * Created by Suwadith 2015214 on 3/28/2017.
 */

public class Node {

    int x;
    int y;
    int movCost;
    double hValue;
    int gValue;
    double fValue;
    Node parent;


    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public void setMoveCost(int moveCost) {
    	movCost = moveCost;
    }

    public int getMoveCost() {
    	return movCost;
    }
}