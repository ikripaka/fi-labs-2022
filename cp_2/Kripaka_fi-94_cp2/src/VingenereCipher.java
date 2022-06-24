import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;

public class VingenereCipher {
    private static final int MATCH_BEGINNING_INDEX = 6;
    private static final File deadSouls = new File("/home/ikripaka/Documents/University/fi-labs-2022/cp_2/Kripaka_fi-94_cp2/dead_souls.txt");
    private static final File filteredDeadSouls = new File("/home/ikripaka/Documents/University/fi-labs-2022/cp_2/Kripaka_fi-94_cp2/filtered_dead_souls.txt");
    private static final int DEFAULT_KEY_LENGTH = 30;

    public void encodeFile(File message, String key, ArrayList<Character> alphabet, File ciphertext) throws IOException {
        FileInputStream fstream = new FileInputStream(message);
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

        BufferedWriter encodedText = new BufferedWriter(new FileWriter(ciphertext.getCanonicalPath()));

        String fileLine;
        char[] keyArr = key.toCharArray();
        int keySize = keyArr.length;

        while ((fileLine = br.readLine()) != null) {
            char[] messageArr = fileLine.toCharArray();
            for (int i = 0, k = 0; i < messageArr.length; i++) {
                encodedText.append(getOffsetLetter(messageArr[i], keyArr[i % keySize], alphabet, true));
            }
        }
        encodedText.flush();
        encodedText.close();
    }

    public String encode(String message, String key, ArrayList<Character> alphabet) {
        StringBuilder builder = new StringBuilder();
        char[] messageArr = message.toCharArray();
        char[] keyArr = key.toCharArray();
        int keySize = keyArr.length;
        for (int i = 0, k = 0; i < message.length(); i++) {
            builder.append(getOffsetLetter(messageArr[i], keyArr[i % keySize], alphabet, true));
        }
        return builder.toString();
    }

    public String decode(String message, String key, ArrayList<Character> alphabet) {
        StringBuilder builder = new StringBuilder();
        char[] messageArr = message.toCharArray();
        char[] keyArr = key.toCharArray();
        int keySize = keyArr.length;
        for (int i = 0, k = 0; i < message.length(); i++) {
            builder.append(getOffsetLetter(messageArr[i], keyArr[i % keySize], alphabet, false));
        }
        return builder.toString();
    }

    public void decodeFile(File ciphertext, String key, ArrayList<Character> alphabet, File deciphered) throws IOException {
        FileInputStream fstream = new FileInputStream(ciphertext);
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

        BufferedWriter decodedText = new BufferedWriter(new FileWriter(deciphered.getCanonicalPath()));

        String fileLine;
        char[] keyArr = key.toCharArray();
        int keySize = keyArr.length;

        while ((fileLine = br.readLine()) != null) {
            char[] messageArr = fileLine.toCharArray();
            for (int i = 0, k = 0; i < messageArr.length; i++) {
                decodedText.append(getOffsetLetter(messageArr[i], keyArr[i % keySize], alphabet, false));
            }
        }
        decodedText.flush();
        decodedText.close();
    }

    public void decodeFileWithMatchIndex(File ciphertext, ArrayList<Character> alphabet, File deciphered) throws IOException {
        HashMap<String, Integer> frequencyTable = makeFrequencyDiagram(filteredDeadSouls.getCanonicalPath(), alphabet, 1, 0);
        HashMap<String, Double> probabilityTable = makeProbabilityTable(frequencyTable);
        int matchIndex = matchIndex(ciphertext, DEFAULT_KEY_LENGTH);


        System.out.println("Frequency table: " + frequencyTable);
        System.out.println("Probability table: " + probabilityTable);
        System.out.println("match index: " + matchIndex);

        String key = pickUpKey(ciphertext, matchIndex, probabilityTable, alphabet);
        System.out.println("key: " + key);
        decodeFile(ciphertext, key, alphabet, deciphered);
    }

    public void decodeFileWithFrequency(File ciphertext, ArrayList<Character> alphabet, File deciphered) throws IOException {
        int matchIndex = matchIndex(ciphertext, DEFAULT_KEY_LENGTH);

        StringBuilder key = new StringBuilder("");
        for(int i =0; i < matchIndex; i++){
            HashMap<String, Integer> frequencyTableCT = makeFrequencyDiagram(ciphertext.getCanonicalPath(), alphabet, matchIndex, i);
            int maxRepetitions = 0;
            char maxLetter = 'а';
            for (char letter: alphabet){
                if(frequencyTableCT.get(Character.toString(letter)) > maxRepetitions){
                    maxRepetitions = frequencyTableCT.get(Character.toString(letter));
                    maxLetter = letter;
                }
            }
            key.append(getOffsetLetter(maxLetter, 'о', alphabet, false));
        }
        System.out.println("key: " + key);
        decodeFile(ciphertext, key.toString(), alphabet, deciphered);
    }

    private String pickUpKey(File ciphertext, int matchIndex, HashMap<String, Double> probabilityTable, ArrayList<Character> alphabet) throws IOException {
        StringBuilder builder = new StringBuilder("");
        for (int i = 0; i < matchIndex; i++) {
            char maxLetter = ' ';
            double maxSum = 0.;
            HashMap<String, Integer> frequencyTableForOffset = makeFrequencyDiagram(ciphertext.getCanonicalPath(), alphabet, matchIndex, i);
            System.out.println("-------");
            for (char probableKeyLetter : alphabet) {
                double sum = 0.;
                for (char letter : alphabet) {
                    sum += probabilityTable.get(Character.toString(letter)) * frequencyTableForOffset.get(Character.toString(getOffsetLetter(letter, probableKeyLetter, alphabet, true)));
                }

                System.out.println(Math.round(sum * 100.)/100.);
                if (sum > maxSum) {
                    maxSum = sum;
                    maxLetter = probableKeyLetter;
                }
            }
            builder.append(maxLetter);
        }
        return builder.toString();
    }

    public int matchIndex(File ciphertext, int probableMaxKeyLength) throws IOException {
        FileInputStream fstream = new FileInputStream(ciphertext);
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

        String fileLine;

        int maxMatchIndex = 0, maxSum = 0;
        if ((fileLine = br.readLine()) != null) {
            char[] messageArr = fileLine.toCharArray();
            int messageLength = messageArr.length;
            for (int r = MATCH_BEGINNING_INDEX; r <= probableMaxKeyLength; r++) {
                int sum = 0;
                for (int i = 0; i < messageLength - r; i++) {
                    sum += (messageArr[i] == messageArr[i + r]) ? 1 : 0;
                }
                if (sum >= maxSum) {
                    maxMatchIndex = r;
                    maxSum = sum;
                }
            }
        }

        return maxMatchIndex;
    }

    public double calculateConformityIndex(File ciphertext, ArrayList<Character> alphabet) throws IOException {
        FileInputStream fstream = new FileInputStream(ciphertext);
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        String fileLine;
        int n = 1;
        if ((fileLine = br.readLine()) != null) {
            char[] messageArr = fileLine.toCharArray();
            n = messageArr.length;
        }

        HashMap<String, Integer> frequencyTableForOffset = makeFrequencyDiagram(ciphertext.getCanonicalPath(), alphabet, 1, 0);
        double sum = 0;
        for (char letter : alphabet) {
            sum += frequencyTableForOffset.get(Character.toString(letter)) * (frequencyTableForOffset.get(Character.toString(letter)) - 1);
        }

        System.out.println("value: " + sum / (n * (n - 1)) + ", n: " + n);
        return sum / (n * (n - 1));
    }

    private static HashMap<String, Integer> makeFrequencyDiagram(String filePath, ArrayList<Character> alphabet, int offset, int beginningOffset) throws IOException {
        HashMap<String, Integer> frequencyTable = new HashMap<>();
        for (char letter : alphabet) {
            frequencyTable.put(Character.toString(letter), 0);
        }
        FileInputStream fstream = new FileInputStream(filePath);
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        String fileLine;
        while ((fileLine = br.readLine()) != null) {
            char[] fileLineChar = fileLine.toCharArray();
            for (int i = beginningOffset; i < fileLineChar.length - ((fileLineChar.length - 1 - beginningOffset) % offset == 0 ? 0 : offset); i += offset) {
                if (frequencyTable.containsKey(Character.toString(fileLineChar[i]))) {
                    int value = frequencyTable.get(Character.toString(fileLineChar[i]));
                    frequencyTable.put(Character.toString(fileLineChar[i]), value + 1);
                }
            }
        }
        return frequencyTable;
    }

    private static HashMap<String, Double> makeProbabilityTable(HashMap<String, Integer> frequencyTable) {
        HashMap<String, Double> probabilityTable = new HashMap<>();
        double sum = 0;
        frequencyTable.remove("  ");

        for (String key : frequencyTable.keySet()) {
            sum += frequencyTable.get(key);
        }
        System.out.println("Total letters sum: " + sum);
        for (String key : frequencyTable.keySet()) {
            probabilityTable.put(key, frequencyTable.get(key) / sum);
        }
        return probabilityTable;
    }

    private char getOffsetLetter(char x, char k, ArrayList<Character> alphabet, boolean encode) {
        if (encode) {
//            System.out.printf("encode %s: '%d', %s: '%d', x+k mod2 = %d\n", x, alphabet.indexOf(x), k,
//                    alphabet.indexOf(k),
//                    (alphabet.indexOf(x) + alphabet.indexOf(k)) % alphabet.size());
            return alphabet.get((alphabet.indexOf(x) + alphabet.indexOf(k)) % alphabet.size());
        }

//        System.out.printf("decode %s: '%d', %s: '%d', x-k mod2 = %d\n", x, alphabet.indexOf(x), k, alphabet.indexOf(k),
//                (alphabet.indexOf(x) - alphabet.indexOf(k)) < 0 ?
//                        (alphabet.indexOf(x) - alphabet.indexOf(k)) + alphabet.size() :
//                        (alphabet.indexOf(x) - alphabet.indexOf(k)) % alphabet.size());
        return alphabet.get((alphabet.indexOf(x) - alphabet.indexOf(k)) < 0 ?
                (alphabet.indexOf(x) - alphabet.indexOf(k)) + alphabet.size() :
                (alphabet.indexOf(x) - alphabet.indexOf(k)) % alphabet.size());
    }
}
