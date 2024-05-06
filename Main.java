
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.LinkedList;
import javax.swing.tree.TreeNode;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Ask the user whether to compress (c) or decompress (d) the file
        System.out.println("Vai vēlaties kompresēt (c) vai dekompresēt (d) failu?");
        String choice = scanner.nextLine();

        // Ask for the file name
        System.out.println("Lūdzu, ievadiet faila nosaukumu:");
        String fileName = scanner.nextLine();

        // Check if the file name ends with '.html'
        if (!fileName.endsWith(".html")) {
            System.out.println("Nepareizs faila nosaukums. Failam jābūt ar paplašinājumu '.html'.");
            return;
        }

        // Check if the file exists
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("Norādītais fails neeksistē.");
            return;
        }

        // Read the entire content of the file and store it as a string
        String fileContent = readFileContent(file);

        // Choose whether to compress or decompress based on the user's choice
        if (choice.equalsIgnoreCase("c")) {
            compress(fileContent);
        } else if (choice.equalsIgnoreCase("d")) {
            decompress(fileContent);
        } else {
            System.out.println("Nepareiza izvēle. Lūdzu, izvēlieties 'c' vai 'd'.");
        }
    }

    // Method to read the content of a file and return it as a string
    private static String readFileContent(File file) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            System.out.println("Kļūda, lasot failu: " + e.getMessage());
        }
        return content.toString();
    }

    // Compression method
    private static void compress(String fileContent) {
        byte[] input = fileContent.getBytes();
        ArrayList<LZ77Token> lz77out = compress(input);
        System.out.println(lz77out);
        byte[] hufmanout = Huffman.compress(lz77out);
    }

    // Decompression method
    private static void decompress(String fileContent) {
        byte[] input = fileContent.getBytes();
        // byte[] hufmanout = Huffman.decompress(lzout);
        // byte[] lzout = LZ77.decompress(input);
    }

    public static ArrayList<LZ77Token> compress(byte[] input) {
        ArrayList<LZ77Token> compressedVal = new ArrayList<>();
        int slidingWindowSize = 15;
        int bufferAheadSize = 3;
        int currentIndex = 0;

        while (currentIndex < input.length) {
            int matchLength = 0;
            int matchStartIndex = -10;
            int windowStartIndex = Math.max(0, currentIndex - slidingWindowSize);
            for (int i = windowStartIndex; i < currentIndex; i++) {
                int j = 0;

                while (j < bufferAheadSize && currentIndex + j < input.length && input[i + j] == input[currentIndex + j]) {
                    j++;
                }
                if (j > matchLength) {
                    matchLength = j;
                    matchStartIndex = i;
                }
            }

            if (matchLength > 0) {
                byte nextChar;
                if (currentIndex + matchLength < input.length) {
                    nextChar = input[currentIndex + matchLength];
                } else {
                    nextChar = 0;
                }
                LZ77Token tokenToAdd = new LZ77Token(currentIndex - matchStartIndex, matchLength, nextChar);

                compressedVal.add(tokenToAdd);
                currentIndex = matchLength + currentIndex + 1;

            } else {
                LZ77Token tokenToAdd = new LZ77Token(0, 0, input[currentIndex]);
                compressedVal.add(tokenToAdd);
                currentIndex = currentIndex + 1;
            }
        }
        return compressedVal;
    }

    public static byte[] decompress(ArrayList<LZ77Token> compressedVal) {
        ArrayList<Byte> decompressedVal = new ArrayList<>();
        for (LZ77Token token : compressedVal) {
            if (token.getLenght() != 0) {
                int startIndex = decompressedVal.size() - token.getOffset();
                for (int i = 0; i < token.getLenght(); i++) {
                    decompressedVal.add(decompressedVal.get(startIndex + i));
                }
                if (token.getChar() != 0) {
                    decompressedVal.add(token.getChar());
                }
            } else {
                decompressedVal.add((byte) token.getChar());
            }
        }

        byte[] decompressedResult = new byte[decompressedVal.size()];
        for (int i = 0; i < decompressedVal.size(); i++) {
            decompressedResult[i] = decompressedVal.get(i);
        }
        return decompressedResult;
    }
}

class LZ77Token {
    private int offset;
    private int length;
    private byte character;

    public LZ77Token(int offset, int length, byte character) {
        this.offset = offset;
        this.length = length;
        this.character = character;
    }

    public int getOffset() {
        return this.offset;
    }

    public int getLenght() {
        return this.length;
    }

    public byte getChar() {
        return this.character;
    }

    // Convert LZ77 token to a binary array for further processing
    public byte[] toBinary() {
        byte[] convert = new byte[3];
        convert[0] = (byte) offset;
        convert[1] = (byte) length;
        convert[2] = character;
        return convert;
    }
}

class CreateHuffmanTree {
    
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

class Huffman {
    CreateHuffmanTree HT = new CreateHuffmanTree();
    LinkedList<byte[]> result = new LinkedList<byte[]>();

    public byte[] compress(byte[] lzout){
        // izveido simbolu biežuma koku saskaitot katru bitu kombināciju biežumu failā
        String str = new String(lzout, StandardCharsets.UTF_8);
        HT.addData(str, "comp");
        DefineHTsymbolAndLength(HT.root, (byte) 0, 0);


        // izvadīt bitu kombinācijas kas piešķirtas simboliem pēc bitu skaita
        for(int i=0; i<=8; i++){
            //rekursijas metode: iet cauri kokam pa līmeņiem un atgriež bitus, ja tas ir simbols
            LinkedList<Node> symbols = findSymbols(HT.root, i); // pirmais HT elements 
            int count = symbols.size();
            if(count>0){
                // pievienot "count" un "i", katru 4 bitos un 1 baitā
                byte countb = (byte) count;
                byte ib = (byte) i;
                byte[] marker = {(byte)((countb << 4) | ib)};
                result.add(marker);
                // ieraksta simbolus un to bitu vērtības
                while(symbols.peek()!=null){
                    Node symbol = symbols.poll();
                    //ierkasta "symbol.HTsymbol" un "symbol.symbol", katru savā baitā
                    byte[] HTsymb = {(byte) symbol.HTsymbol};
                    byte[] symb = {(byte) symbol.symbol};
                    result.add(HTsymb);
                    result.add(symb);
                }
            }  
        }


        // ieraksta baitu "0000 0000", lai patektu, ka nav citu simbolu
        byte[] zeros = {(byte) 0};
        result.add(zeros);
        // vajadzētu pārveidot koku lai varētu ātrāk atrast specifisku simbolu
        Object[] savedSymb = result.toArray();


        // pārraksta failu aizstājot simbolus ar tiem piešķirtiem bitu kombinācijām
        byte[] resultb = new byte[1];
        int freeb = 8; // free bits for writing compresed symbols
        for(byte s : lzout){
            // atrast "s" huffman kokā
            int parity = 0, count = 0, length; // parity = 
            byte sb, HTsb; // sb = s bitos, HTsb = HT kombinācija priekš s bitos
            for(Object ob : savedSymb){
                if(count<1){
                    int lc = (int) ob; // lc = length un count vienā baitā
                    length = (byte) (lc & 0b00001111);
                    count = lc >> 4;
                }else{
                    if(parity==0){ // HTsymbol
                        HTsb = (byte) ob;
                    }else{ // symbol
                        byte b = (byte) ob;
                        if(s==b){
                            sb = (byte) HTsb;
                        }
                        count--;
                    }
                }
            }
            // pievienot sarakstā "result"
            if ((freeb-length)>=0){
                resultb[0] = (byte) (sb << (freeb-length));
                freeb -= length;
                if(freeb==0){
                    result.add(resultb);
                    freeb=8;
                }
            }else{
                resultb[0] = (byte) (sb >> (length-freeb));
                result.add(resultb);
                resultb[0] = (byte) (sb << (8-(length-freeb)));
                freeb = 8-(length-freeb);
            }


        }
        result.add(EOFb); // !!! ko izmantojam kā simbolu, kas definē EOF? Cik es saprotu, tam jābut definētam kokā, tādēļ to jau jādefinē, kad veido koku
        return result; // !!! te dabūt uz byte[] vai vnk izmanto linkedList?
    }


    public byte[] decompress(byte[] input){
        // izveido simbolu biežuma koku nolasot informāciju no faila sākuma
        String inputStr = new String(input, StandardCharsets.UTF_8);
        HT.addData(inputStr, "decomp");
        int loopCount = 0, length = 0;
        boolean loopFinished = false;
        Node current = HT.root; // HT pirmais elements
        for(byte s : input){
            // izlaist daļu kur tiek saglabāti simboli
            if(!loopFinished & (loopCount==0 & (s >> 4)!=0)){
                loopCount = s >> 4;
            } else if(loopFinished | (loopCount==0 & (s >> 4)==0)) { // īstais fails
                loopFinished = true;
                // atrast pareizo simbolu kokā
                for(int i=length; i<8; i++){
                    // ja baitam s ir 1 tajā pašā pozīcijā kā nobīdītajam 1
                    if((s & (1 << (8-i)))==(1 << (8-i))){
                        current = current.right;
                    }else{
                        current = current.right;
                    }
                    if(current.left==null && current.right==null){
                        byte[] symb = {(byte) current.symbol};
                        result.add(symb);
                    }
                }
            }
        }
        return result; // !!! te dabūt uz byte[] vai vnk izmanto linkedList?
    }


    // rekursijas metode, lai iegūtu dilstošā secībā sakārtotus sibolus un to jaunās bitu kombinācijas
    private LinkedList<Node> findSymbols(Node n, int length){
        
        LinkedList<Node> foundSymbols = new LinkedList<Node>();
        // ja elementam Node ir symbols un tā jaunā bitu kombinācija to pievieno sarakstam
        if(n.HTsymbolLength==length){
            foundSymbols.add(n);
            return foundSymbols;
        // ja nav, tad pārbauda elementa Node apakšelementus
        }else if(n.HTsymbolLength==0){
            LinkedList<Node> nRight = findSymbols(n.right, length);
            LinkedList<Node> nLeft = findSymbols(n.left, length);
            while(nRight.peek()!=null){
                foundSymbols.add(nRight.poll());
            }
            while(nLeft.peek()!=null){
                foundSymbols.add(nLeft.poll());
            }
            return foundSymbols;
        }else{
            return null;
        }
    }


    private static void DefineHTsymbolAndLength(Node node, byte HTsymbol, int HTsLength){
        if(node == null){
            return;
        }
        // ja elementam Node nav bērnu tad tas ir gala elements un tam definē HTsymbol un tā garumu
        if (node.left == null && node.right == null){
            node.HTsymbol = HTsymbol;
            node.HTsymbolLength = HTsLength;
            return;
        }
        // pa kreisi ir bits 0
        if(node.left != null){
            HTsymbol = (byte) (HTsymbol << 1);
            DefineHTsymbolAndLength(node.left, HTsymbol, HTsLength + 1);
        }
        // pa labi ar bitu 1
        if(node.right != null){
            HTsymbol = (byte) ((HTsymbol << 1) | 1);
            DefineHTsymbolAndLength(node.right, HTsymbol, HTsLength + 1);
        }
    }
}

class LZ77 {
    public static ArrayList<Token> compress(String input){
        ArrayList<Token> compressedVal = new ArrayList<Token>();
        int slidingWindowSize = 15;
        int bufferAheadSize = 3;
        int currentIndex = 0;
        
        
        while(currentIndex < input.length()){
            int matchLength = 0;
            int matchStartIndex = -10;
            int windowStartIndex = Math.max(0, currentIndex - slidingWindowSize);
            for(int i = windowStartIndex; i < currentIndex; i++){
                int j = 0; 
                while(j<bufferAheadSize && currentIndex +j <input.length() && input.charAt(i+j) == input.charAt(currentIndex+j)){
                j++;
                }
                if(j>matchLength){
                    matchLength = j;
                    matchStartIndex = i;
                }
            } 
        
            if(matchLength >0){
                char nextChar;
                if(currentIndex+matchLength < input.length()){
                    nextChar = input.charAt(currentIndex+matchLength);
                }else{
                    nextChar = '\0';
                }
                Token tokenToAdd = new Token(currentIndex - matchStartIndex,matchLength,(byte) nextChar); 
    
                compressedVal.add(tokenToAdd);
                currentIndex= matchLength+currentIndex+1;
                 
            }else{
                Token tokenToAdd = new Token(0,0,(byte) input.charAt(currentIndex));
                compressedVal.add(tokenToAdd);
                currentIndex = currentIndex+1;
            }    
        }
        return compressedVal;         
    }
        
        
    public static String decompress(ArrayList<Token> compressedVal){
        StringBuilder decompressedVal = new StringBuilder();
        for(Token token : compressedVal){
            if(token.getLength() != 0){
                int startIndex = decompressedVal.length()- token.getOffset();
                for(int i= 0; i< token.getLength(); i++){
                    decompressedVal.append(decompressedVal.charAt(startIndex+i));
                }
                if(token.getChar() != '\0'){
                    decompressedVal.append(token.getChar());
                }
            }else{
                decompressedVal.append(token.getChar());
            }
        }
        return decompressedVal.toString();
    }
}

class Node {
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

class Token {
    private int offset;
    private int length;
    private byte character;
    public Token(int offset, int length, byte character){
        this.offset = offset;
        this.length = length;
        this.character = character;
    }
                
    public int getOffset(){
        return this.offset;
    }
                
    public int getLength(){
        return this.length;
    }
                
    public int getChar(){
        return this.character;
    }
    // pārveido LZ77 tokenu par bināro masīvu, lai to varētu tālāk sūtit Huffman
    public byte[] toBinnary(){
        byte[] convert = new byte[3];
        convert[0] = (byte)offset;
        convert[1] = (byte)length;
        convert[2] = character;
        return convert;
    }
}