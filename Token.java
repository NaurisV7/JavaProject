package Project;

// LZ77 token klase kas paredzēta LZ77 tokena izveidei objekta formā, lai varētu vieglāk piekļūt pie tokena informācijas pirms tā pārveidošanas
public class Token {
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