package Project;

public class Node {
    char symbol;
    int count;
    Node left;
    Node right;
    byte HTsymbol;
    int HTsymbolLength;

    public Node(char symbol, int count, byte HTsymbol, int HTsymbolLength){
        this.symbol = symbol;
        this.count = count;
        this.left = null;
        this.right = null;
        this.HTsymbol = HTsymbol;
        this.HTsymbolLength = HTsymbolLength;
    }

    public boolean isLeaf() {
        return left == null && right == null;
    }
}