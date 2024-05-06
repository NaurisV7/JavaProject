import java.util.ArrayList;

public class LZ77 {
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