import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class BigramCipher {
    //a = bx mod m^2
    private static class EquationSolution {
        int a = 0;
        int b = 0;

        @Override
        public String toString() {
            return "[a: " + a + ", b: " + b + "]";
        }
    }

    private static final File deadSouls = new File("/home/ikripaka/Documents/University/fi-labs-2022/cp_3/Kripaka_fi-94_cp3/dead_souls.txt");
    private static final File filteredDeadSouls = new File("/home/ikripaka/Documents/University/fi-labs-2022/cp_3/Kripaka_fi-94_cp3/filtered_dead_souls.txt");

    private static final int NUMBER_OF_MOST_COMMON_BIGRAM = 15;
    private static final String[] MOST_COMMON_BIGRAMS = {"ст", "то", "но", "тт", "на", "ен", "на", "но", "то", "ен", "тт", "но", "то", "на", "ен"};

    public void decodeFile(File ciphertext, ArrayList<Character> alphabet, File deciphered) throws IOException {
        HashMap<String, Integer> bigramOriginal = makeBigramFrequencyDiagram(filteredDeadSouls.getCanonicalPath(), alphabet, 2);
        HashMap<String, Integer> bigramCiphertext = makeBigramFrequencyDiagram(ciphertext.getCanonicalPath(), alphabet, 2);

        ArrayList<String> mostCommonBigramOriginal = new ArrayList<>(Arrays.asList(MOST_COMMON_BIGRAMS));
        ArrayList<String> mostCommonBigramCiphertext = new ArrayList<>();
//        ArrayList<String> mostCommonBigramOriginal = new ArrayList<>();

        for (int i = 0; i < NUMBER_OF_MOST_COMMON_BIGRAM; i++) {
            mostCommonBigramCiphertext.add(Collections.max(bigramCiphertext.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey());
//            mostCommonBigramOriginal.add(Collections.max(bigramOriginal.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey());
            bigramCiphertext.remove(mostCommonBigramCiphertext.get(i));
//            bigramOriginal.remove(mostCommonBigramOriginal.get(i));
        }
        System.out.println("most Common Bigram Ciphertext: " + mostCommonBigramCiphertext);
        System.out.println("most Common Bigram Original: " + mostCommonBigramOriginal);

        ArrayList<EquationSolution> solutions = new ArrayList<>();
        for (int i = 0; i < mostCommonBigramOriginal.size(); i++) {
            for (int j = 0; j < mostCommonBigramCiphertext.size(); j++) {
                ArrayList<EquationSolution> solutions1 = solveEquation(
                        getXi(mostCommonBigramCiphertext.get(i), alphabet),
                        getXi(mostCommonBigramCiphertext.get(j), alphabet),
                        getXi(mostCommonBigramOriginal.get(i), alphabet),
                        getXi(mostCommonBigramOriginal.get(j), alphabet),
                        alphabet.size() * alphabet.size());
                if (solutions1 == null) {
                    continue;
                }
                solutions.addAll(solutions1);
            }
        }
        System.out.println("probable keys: " + solutions);

        FileInputStream fstream = new FileInputStream(ciphertext);
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        String fileLine;

        BufferedWriter decipheredText = new BufferedWriter(new FileWriter("/home/ikripaka/Documents/University/fi-labs-2022/cp_3/Kripaka_fi-94_cp3/test-file.txt"));

        if ((fileLine = br.readLine()) != null) {
            int i = 0;
            for (EquationSolution solution : solutions) {
                String decodedText = decode(fileLine, solution.a, solution.b, alphabet);
                decipheredText.write("\na: " + solution.a + ", b: " + solution.b + " : " + decodedText);
                decipheredText.write("\ni: " + i + ", coincidence index: " + calculateConformityIndex(decodedText, alphabet));
                if (calculateConformityIndex(decodedText, alphabet) > 0.049 && calculateConformityIndex(decodedText, alphabet) < 0.07) {
                    System.out.println("a: " + solution.a + ", b: " + solution.b + " : " + decodedText);
                    System.exit(0);
                }
                i++;
            }
            decipheredText.flush();
            decipheredText.close();
        }
    }

    private ArrayList<EquationSolution> solveEquation(int a, int b, int n) {
        //Y* - Y** = x( X* - X** ) mod m^2
        a = (a) % n < 0 ? (a) % n + n : (a) % n;
        b = (b) % n < 0 ? (b) % n + n : (b) % n;
        ArrayList<EquationSolution> eq_list = new ArrayList<>();
        int divider = gcd(a, n);
        if (divider == 1) {
            int a_inverse = inverse(a, n);
            EquationSolution solution = new EquationSolution();
            solution.a = (a_inverse * b) % n < 0 ? (a_inverse * b) % n + n : (a_inverse * b) % n;
            eq_list.add(solution);
        } else if (divider > 1) {
            if (b % divider != 0) {
                return null;
            }
            int a_1 = a / divider;
            int b_1 = b / divider;
            int n_1 = n / divider;
            int a_1_inverse = inverse(a_1, n_1);
            int x_0 = b_1 * a_1_inverse % n_1 < 0 ? b_1 * a_1_inverse % n_1 + n_1 : b_1 * a_1_inverse % n_1;
            for (int i = 0; i < divider; i++) {
                EquationSolution solution = new EquationSolution();
                solution.a += i * n_1 + x_0;
                eq_list.add(solution);
            }
        }

        return eq_list;
    }

    private ArrayList<EquationSolution> solveEquation(int y_1, int y_2, int x_1, int x_2, int n) {
        //Y* - Y** = x( X* - X** ) mod m^2
        int a = (x_1 - x_2) % n < 0 ? (x_1 - x_2) % n + n : (x_1 - x_2) % n;
        int b = (y_1 - y_2) % n < 0 ? (y_1 - y_2) % n + n : (y_1 - y_2) % n;
        ArrayList<EquationSolution> eq_list = new ArrayList<>();
        int divider = gcd(a, n);
        if (divider == 1) {
            int a_inverse = inverse(a, n);
            EquationSolution solution = new EquationSolution();
            solution.a = (a_inverse * b) % n < 0 ? (a_inverse * b) % n + n : (a_inverse * b) % n;
            eq_list.add(solution);
        } else if (divider > 1) {
            if (b % divider != 0) {
                return null;
            }
            int a_1 = a / divider;
            int b_1 = b / divider;
            int n_1 = n / divider;
            int a_1_inverse = inverse(a_1, n_1);
            int x_0 = b_1 * a_1_inverse % n_1 < 0 ? b_1 * a_1_inverse % n_1 + n_1 : b_1 * a_1_inverse % n_1;
            for (int i = 0; i < divider; i++) {
                EquationSolution solution = new EquationSolution();
                solution.a += i * n_1 + x_0;
                eq_list.add(solution);
            }
        }

        for (EquationSolution solution : eq_list) {
            solution.b = (y_1 - solution.a * x_1) % n < 0 ?
                    (y_1 - solution.a * x_1) % n + n :
                    (y_1 - solution.a * x_1) % n;
        }


        return eq_list;
    }

    public void encodeFile(File message, int keyA, int keyB, ArrayList<Character> alphabet, File ciphertext) throws IOException {
        FileInputStream fstream = new FileInputStream(message);
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

        BufferedWriter encodedText = new BufferedWriter(new FileWriter(ciphertext.getCanonicalPath()));

        String fileLine;

        while ((fileLine = br.readLine()) != null) {
            char[] messageArr = fileLine.toCharArray();
            for (int i = 0; i < messageArr.length - 1; i += 2) {
                encodedText.append(extractTwoLetters(getYi(keyA, keyB, getXi(messageArr[i], messageArr[i + 1], alphabet), alphabet), alphabet));
            }
        }
        encodedText.flush();
        encodedText.close();
    }

    public void decodeFile(File ciphertext, int keyA, int keyB, ArrayList<Character> alphabet, File deciphered) throws IOException {
        FileInputStream fstream = new FileInputStream(ciphertext);
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

        BufferedWriter decodedText = new BufferedWriter(new FileWriter(deciphered.getCanonicalPath()));

        String fileLine;

        while ((fileLine = br.readLine()) != null) {
            char[] messageArr = fileLine.toCharArray();
            for (int i = 0; i < messageArr.length; i += 2) {
                // extractTwoLetters(getXiDecode(... , getYi(...), ...)
                decodedText.append(extractTwoLetters(getXiDecode(keyA, keyB, getXi(messageArr[i], messageArr[i + 1], alphabet), alphabet), alphabet));
            }
        }
        decodedText.flush();
        decodedText.close();
    }

    public String encode(String message, int keyA, int keyB, ArrayList<Character> alphabet) {
        StringBuilder builder = new StringBuilder();
        char[] messageArr = message.toCharArray();
        for (int i = 0, k = 0; i < message.length() - 1; i += 2) {
            builder.append(extractTwoLetters(getYi(keyA, keyB, getXi(messageArr[i], messageArr[i + 1], alphabet), alphabet), alphabet));
        }
        return builder.toString();
    }

    public String decode(String message, int keyA, int keyB, ArrayList<Character> alphabet) {
        StringBuilder builder = new StringBuilder();
        char[] messageArr = message.toCharArray();
        for (int i = 0, k = 0; i < message.length() - 1; i += 2) {
            // extractTwoLetters(getXiDecode(... , getYi(...), ...)
            builder.append(extractTwoLetters(getXiDecode(keyA, keyB, getXi(messageArr[i], messageArr[i + 1], alphabet), alphabet), alphabet));
        }
        return builder.toString();
    }

    private String extractTwoLetters(int a, ArrayList<Character> alphabet) {
//        System.out.println(" first letter: " +(a / alphabet.size()) + " + second letter: " + alphabet.get(a- alphabet.size() * ((int)(a / alphabet.size()))));
        return Character.toString(alphabet.get(a / alphabet.size())) + Character.toString(alphabet.get(a - alphabet.size() * ((int) (a / alphabet.size()))));
    }

    private int getXi(char x_1, char x_2, ArrayList<Character> alphabet) {
        return alphabet.indexOf(x_1) * alphabet.size() + alphabet.indexOf(x_2);
    }

    private int getXi(String X, ArrayList<Character> alphabet) {
        return alphabet.indexOf(X.charAt(0)) * alphabet.size() + alphabet.indexOf(X.charAt(1));
    }

    private int getYi(int keyA, int keyB, int x_i, ArrayList<Character> alphabet) {
        return (keyA * x_i + keyB) % (alphabet.size() * alphabet.size()) < 0 ? (keyA * x_i + keyB) % (alphabet.size() * alphabet.size()) + (alphabet.size() * alphabet.size()) : (keyA * x_i + keyB) % (alphabet.size() * alphabet.size());
    }

    private int getXiDecode(int keyA, int keyB, int y_i, ArrayList<Character> alphabet) {
//        System.out.println("inverse: " + inverse(keyA, alphabet.size() * alphabet.size()) + ", sub: " + (y_i - keyB) + ", mod: " + (inverse(keyA, alphabet.size() * alphabet.size()) * (y_i - keyB)) % (alphabet.size() * alphabet.size()));
        return (inverse(keyA, alphabet.size() * alphabet.size()) * (y_i - keyB)) % (alphabet.size() * alphabet.size()) < 0 ? (inverse(keyA, alphabet.size() * alphabet.size()) * (y_i - keyB)) % (alphabet.size() * alphabet.size()) + alphabet.size() * alphabet.size() : (inverse(keyA, alphabet.size() * alphabet.size()) * (y_i - keyB)) % (alphabet.size() * alphabet.size());
    }


    /// Using euclid algorithm to calculate GCD
    private int gcd(int a, int b) {
        if (a == 0 || b == 0) {
            return 1;
        }
        int d = 1;

        while ((a & 1) == 0 && (b & 1) == 0) {
            a >>= 1;
            b >>= 1;
            d <<= 1;
        }
        while ((a & 1) == 0) {
            a >>= 1;
        }
        while (b != 0) {
            while ((b & 1) == 0) {
                b >>= 1;
            }

            if (a > b) {
                int temp = b; //min (a,b)
                b = a - b;
                a = temp;
            } else {
                // a = min (a,b) = a
                b = b - a;
            }
        }

        return d * a;
    }

    private int inverse(int a, int n) {
        if (a >= n) a = a % n;
        int t = 0, r = n, newt = 1, newr = a;
        while (newr != 0) {
            int quotient = r / newr;
            int newt_1 = t;
            t = newt;
            newt = newt_1 - quotient * newt;
            int newr_1 = r;
            r = newr;
            newr = newr_1 - quotient * newr;
        }
        return t % n < 0 ? t % n + n : t % n;
    }

    private static HashMap<String, Integer> makeBigramFrequencyDiagram(String filePath, ArrayList<Character> alphabet, int offset) throws IOException {
        HashMap<String, Integer> bigram = new HashMap<>();
        for (char letter1 : alphabet) {
            for (char letter2 : alphabet) {
                bigram.put(letter1 + "" + letter2, 0);
            }
        }
        FileInputStream fstream = new FileInputStream(filePath);
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        String fileLine;
        while ((fileLine = br.readLine()) != null) {
            for (int i = 0; i < fileLine.length() - 1; i += offset) {
                String newKey = fileLine.charAt(i) + "" + fileLine.charAt(i + 1);
                if (bigram.containsKey(newKey)) {
                    int value = bigram.get(newKey);
                    bigram.put(newKey, value + 1);
                }

            }
        }
        return bigram;
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

    private static HashMap<String, Integer> makeFrequencyDiagram(ArrayList<Character> alphabet, String text, int offset, int beginningOffset) throws IOException {
        HashMap<String, Integer> frequencyTable = new HashMap<>();
        for (char letter : alphabet) {
            frequencyTable.put(Character.toString(letter), 0);
        }

        char[] fileLineChar = text.toCharArray();
        for (int i = beginningOffset; i < fileLineChar.length - ((fileLineChar.length - 1 - beginningOffset) % offset == 0 ? 0 : offset); i += offset) {
            if (frequencyTable.containsKey(Character.toString(fileLineChar[i]))) {
                int value = frequencyTable.get(Character.toString(fileLineChar[i]));
                frequencyTable.put(Character.toString(fileLineChar[i]), value + 1);
            }
        }

        return frequencyTable;
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
            System.out.println("letter: " + Character.toString(letter) + ", table: " + frequencyTableForOffset.get(Character.toString(letter)));
            System.out.println(frequencyTableForOffset.get(Character.toString(letter)) + " * " + (frequencyTableForOffset.get(Character.toString(letter)) - 1));
            sum += frequencyTableForOffset.get(Character.toString(letter)) * (frequencyTableForOffset.get(Character.toString(letter)) - 1);
        }
        System.out.println("sum: " + sum);
        System.out.println(frequencyTableForOffset);

        System.out.println("value: " + sum / (n * (n - 1)) + ", n: " + n);
        return sum / (n * (n - 1));
    }

    public double calculateConformityIndex(String ciphertext, ArrayList<Character> alphabet) throws IOException {
        int n = ciphertext.length();

        HashMap<String, Integer> frequencyTableForOffset = makeFrequencyDiagram(alphabet, ciphertext, 1, 0);
        double sum = 0;
        for (char letter : alphabet) {
            sum += frequencyTableForOffset.get(Character.toString(letter)) * (frequencyTableForOffset.get(Character.toString(letter)) - 1);
        }

        return sum / (n * (n - 1));
    }
}
