import java.io.*;
import java.util.*;

class HuffmanNode {
    int data;
    char ch;
    HuffmanNode left, right;

    HuffmanNode(char ch, int data, HuffmanNode left, HuffmanNode right) {
        this.data = data;
        this.ch = ch;
        this.left = left;
        this.right = right;
    }
}

class HuffmanComparator implements Comparator<HuffmanNode> {
    public int compare(HuffmanNode x, HuffmanNode y) {
        return x.data - y.data;
    }
}

class Huffman {
    static Map<Character, String> codes = new HashMap<>();
    static HuffmanNode root;

    public static Map<Character, Integer> buildFrequencyTable(String input) {
        Map<Character, Integer> freqMap = new HashMap<>();
        for (char c : input.toCharArray()) {
            freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);
        }
        return freqMap;
    }

    public static HuffmanNode buildHuffmanTree(Map<Character, Integer> freqMap) {
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>(new HuffmanComparator());
        for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
            pq.add(new HuffmanNode(entry.getKey(), entry.getValue(), null, null));
        }
        while (pq.size() > 1) {
            HuffmanNode left = pq.poll();
            HuffmanNode right = pq.poll();
            HuffmanNode parent = new HuffmanNode('\0', left.data + right.data, left, right);
            pq.add(parent);
        }
        return pq.poll();
    }

    public static void buildCodes(HuffmanNode root, String code) {
        if (root == null) return;
        if (root.left == null && root.right == null) {
            codes.put(root.ch, code);
        }
        buildCodes(root.left, code + "0");
        buildCodes(root.right, code + "1");
    }

    public static String encode(String input) {
        Map<Character, Integer> freqMap = buildFrequencyTable(input);
        root = buildHuffmanTree(freqMap);
        buildCodes(root, "");
        StringBuilder encoded = new StringBuilder();
        for (char c : input.toCharArray()) {
            encoded.append(codes.get(c));
        }
        return encoded.toString();
    }

    public static String decode(String encodedData) {
        StringBuilder decoded = new StringBuilder();
        int i = 0;
        while (i < encodedData.length()) {
            HuffmanNode current = root;
            while (current.left != null && current.right != null && i < encodedData.length()) {
                if (encodedData.charAt(i) == '0') {
                    current = current.left;
                } else {
                    current = current.right;
                }
                i++;
            }
            decoded.append(current.ch);
        }
        return decoded.toString();
    }
}

class LZ77 {
    static class Pair {
        int offset;
        int length;
        char nextChar;

        Pair(int offset, int length, char nextChar) {
            this.offset = offset;
            this.length = length;
            this.nextChar = nextChar;
        }
    }

    public static List<Pair> compress(String input) {
        List<Pair> compressed = new ArrayList<>();
        int windowSize = 4096;
        int lookAheadBufferSize = 15;

        int index = 0;
        while (index < input.length()) {
            int matchLength = 0;
            int matchIndex = 0;
            for (int i = Math.max(0, index - windowSize); i < index; i++) {
                int len = 0;
                while (len < lookAheadBufferSize && index + len < input.length() && i + len < index && input.charAt(i + len) == input.charAt(index + len)) {
                    len++;
                }
                if (len > matchLength) {
                    matchLength = len;
                    matchIndex = i;
                }
            }
            if (matchLength > 0) {
                compressed.add(new Pair(index - matchIndex, matchLength, index + matchLength < input.length() ? input.charAt(index + matchLength) : '\0'));
                index += matchLength + 1;
            } else {
                compressed.add(new Pair(0, 0, input.charAt(index)));
                index++;
            }
        }
        return compressed;
    }

    public static String decompress(List<Pair> compressed) {
        StringBuilder decompressed = new StringBuilder();
        for (Pair pair : compressed) {
            if (pair.length == 0) {
                decompressed.append(pair.nextChar);
            } else {
                int startIndex = decompressed.length() - pair.offset;
                for (int i = 0; i < pair.length; i++) {
                    decompressed.append(decompressed.charAt(startIndex + i));
                }
                decompressed.append(pair.nextChar);
            }
        }
        return decompressed.toString();
    }
}

public class Deflate64 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the path of the HTML file: ");
        String filePath = scanner.nextLine();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            String htmlContent = sb.toString();

            // Compression
            String compressedHtml = Huffman.encode(htmlContent);
            List<LZ77.Pair> compressedPairs = LZ77.compress(compressedHtml);

            // Decompression
            String decompressedHtml = LZ77.decompress(compressedPairs);
            String decompressedContent = Huffman.decode(decompressedHtml);

            System.out.println("Original HTML size: " + htmlContent.length() + " bytes");
            System.out.println("Compressed HTML size: " + compressedPairs.size() + " bytes");
            System.out.println("Decompressed HTML size: " + decompressedContent.length() + " bytes");
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + filePath);
        } catch (IOException e) {
            System.err.println("Error reading the file: " + e.getMessage());
        }
    }
}
