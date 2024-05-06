
import java.util.HashMap;
import java.util.PriorityQueue;

public class CreateHuffmanTree {
    
    public Node root; // now public
    private HashMap<Character, Integer> frequencyMap;
    
    public CreateHuffmanTree() {
        frequencyMap = new HashMap<>();
    }
    
    // Metode, lai pievienotu baitu informāciju un izvēlētos kompresijas vai dekompresijas režīmu
    public void addData(String data, String mode) {
        if (mode.equals("comp")) {
            buildHuffmanTree(data);
        } else if (mode.equals("decomp")) {
            buildDecompHuffmanTree(data);
        } else {
            System.out.println("Invalid mode!");
        }
    }
    
    // Metode, lai uztaisītu Huffman koku kompresijai
    private void buildHuffmanTree(String data) {
        frequencyMap.clear();
        for (char c : data.toCharArray()) {
            frequencyMap.put(c, frequencyMap.getOrDefault(c, 0) + 1);
        }
        
        PriorityQueue<Node> pq = new PriorityQueue<>((a, b) -> a.count - b.count);
        for (char c : frequencyMap.keySet()) {
            pq.add(new Node(c, frequencyMap.get(c), (byte) 0, 0));
        }
        
        while (pq.size() > 1) {
            Node left = pq.poll();
            Node right = pq.poll();
            Node parent = new Node('\0', left.count + right.count, (byte) 0, 0);
            parent.left = left;
            parent.right = right;
            pq.add(parent);
        }
        
        root = pq.poll();
    }    
    
    // Metode, lai uztaisītu Huffman koku dekompresijai
private void buildDecompHuffmanTree(String data) {
    // Izveidojam tukšu sakni
    root = new Node('\0', 0, (byte) 0, 0);
    Node current = root;
    
    // Pārlūkojam visus bitus, lai veidotu Huffman koku
    for (int i = 0; i < data.length(); i++) {
        char bit = data.charAt(i);
        
        // Ja bitu vērtība ir 0, tad pārvietojamies pa kreiso bērnu, ja ir 1, tad pa labo bērnu
        if (bit == '0') {
            if (current.left == null) {
                current.left = new Node('\0', 0, (byte) 0, 0);
            }
            current = current.left;
        } else if (bit == '1') {
            if (current.right == null) {
                current.right = new Node('\0', 0, (byte) 0, 0);
            }
            current = current.right;
        }
    }
}

    
    // Metode, lai izskaitītu simbolus Huffman kokā
    public void countSymbols() {
        if (root == null) {
            System.out.println("Huffman tree not built yet!");
            return;
        }
        countSymbols(root);
    }
    
    private void countSymbols(Node node) {
        if (node == null) {
            return;
        }
        if (node.isLeaf()) {
            System.out.println("Symbol: " + node.symbol + ", Frequency: " + node.count);
        }
        countSymbols(node.left);
        countSymbols(node.right);
    }
    
    //Klase, lai pārstāvētu mezglus Huffman kokā
    // public class Node {
    //     char symbol;
    //     int frequency;
    //     Node left;
    //     Node right;
        
    //     public Node(char symbol, int frequency) {
    //         this.symbol = symbol;
    //         this.frequency = frequency;
    //         this.left = null;
    //         this.right = null;
    //     }
        
    //     public boolean isLeaf() {
    //         return left == null && right == null;
    //     }
    // }
    
    // Main metode testēšanai
    public static void main(String[] args) {
        CreateHuffmanTree huffmanTree = new CreateHuffmanTree();
        
        // Testēšana
        String data = "aaabbc";
        String mode = "comp";
        huffmanTree.addData(data, mode);
        huffmanTree.countSymbols();
    }
}