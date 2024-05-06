
package Project;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import Project.Node;
import Hufmann;
import Token;
import CreateHuffmanTree;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Jautā lietotājam, vai vēlas kompresēt vai dekompresēt failu
        System.out.println("Vai vēlaties kompresēt (c) vai dekompresēt (d) failu?");
        String choice = scanner.nextLine();

        // Jautā faila nosaukumu
        System.out.println("Lūdzu, ievadiet faila nosaukumu:");
        String fileName = scanner.nextLine();
        scanner.close();

        // Pārbauda, vai faila nosaukums satur paplašinājumu '.html'
        if (!fileName.endsWith(".html")) {
            System.out.println("Nepareizs faila nosaukums. Failam jābūt ar paplašinājumu '.html'.");
            return;
        }

        // Pārbauda, vai fails eksistē
        File file = new File(fileName);
        if (!file.exists()) {
            System.out.println("Norādītais fails neeksistē.");
            return;
        }

        // Nolasa visu faila saturu un saglabā to string mainīgajā
        String fileContent = readFileContent(file);

        // Izvēlas kompresēt vai dekompresēt atkarībā no lietotāja izvēles
        if (choice.equalsIgnoreCase("c")) {
            compress(fileContent);
        } else if (choice.equalsIgnoreCase("d")) {
            decompress(fileContent);
        } else {
            System.out.println("Nepareiza izvēle. Lūdzu, izvēlieties 'c' vai 'd'.");
        }
    }

    // Metode, kas nolasa faila visu saturu un atgriež to kā string
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

    // Kompresijas metode
    private static void compress(String fileContent) {
        byte[] input = fileContent.getBytes();
        byte[] lzout = LZ77.compress(input);
        byte[] hufmanout = Huffman.compress(lzout);
        // rakstīt jaunu failu ???
    }

    // Dekompresijas metode
    private static void decompress(String fileContent) {
        byte[] input = fileContent.getBytes();


        byte[] huffmanout = Huffman.decompress(input);
        byte[] lzout = LZ77.decompress(huffmanout);
        // rakstīt jaunu failu ???
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
                    nextCharacter = input[currentIndex + matchLength];
                } else {
                    nextCharacter = 0;
                }
                LZ77Token tokenToAdd = new LZ77Token(currentIndex - matchStartIndex, matchLength, nextCharacter);

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
            if (token.length != 0) {
                int startIndex = decompressedVal.size() - token.offset;
                for (int i = 0; i < token.length; i++) {
                    decompressedVal.add(decompressedVal.get(startIndex + i));
                }
                if (token.character != 0) {
                    decompressedVal.add(token.character);
                }
            } else {
                decompressedVal.add(token.character);
            }
        }

        byte[] decompressedResult = new byte[decompressedVal.size()];
        for (int i = 0; i < decompressedVal.size(); i++) {
            decompressedresult[i] = decompressedVal.get(i);
        }
        return decompressedresult;
    }
}


// LZ77 token klase kas paredzēta LZ77 tokena izveidei objekta formā, lai varētu vieglāk piekļūt pie tokena informācijas pirms tā pārveidošanas
class LZ77Token{
    private int offset;
    private int lenght;
    private byte character;
    public LZ77Token(int offset, int lenght, byte character){
        this.offset = offset;
        this.lenght = lenght;
        this.character = character;
    }

    public int getOffset(){
        return this.offset;
    }

    public int getLenght(){
        return this.lenght;
    }

    public int getChar(){
        return this.character;
    }
    // pārveido LZ77 tokenu par bināro masīvu, lai to varētu tālāk sūtit Huffman
    public byte[] toBinnary(){
        byte[] convert = new byte[3];
        convert[0] = (byte)offset;
        convert[1] = (byte)lenght;
        convert[2] = character;
        return convert;

    }
}
