import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class Main {
    private static final char[] alphabetWithoutGap = {'а', 'б', 'в', 'г', 'д', 'е', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ы', 'ь', 'э', 'ю', 'я'};
    private static final char[] alphabetWithGap = {'а', 'б', 'в', 'г', 'д', 'е', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ы', 'ь', 'э', 'ю', 'я', ' '};
    private static final File fileWithGaps = new File("/home/ikripaka/Documents/University/sym_crypto/lab1/with_gaps.txt");
    private static final File fileWithoutGaps = new File("/home/ikripaka/Documents/University/sym_crypto/lab1/without_gaps.txt");
    private static final File inputFile = new File("/home/ikripaka/Documents/University/sym_crypto/lab1/text.txt");

    public static void main(String[] args) {
        try {
            // ъ = ь, ё = е, "   " = " "
            //put new text into text.txt, uncomment "filterText()", and run
//            filterText();
            HashMap<String, Integer> frequencyTableWithGap = makeFrequencyDiagram(fileWithGaps.getCanonicalPath(), alphabetWithGap);
            HashMap<String, Integer> frequencyTableWithoutGap = makeFrequencyDiagram(fileWithoutGaps.getCanonicalPath(), alphabetWithoutGap);

            HashMap<String, Integer> bigramWithGap = makeBigram(fileWithGaps.getCanonicalPath(), alphabetWithGap, 1);
            HashMap<String, Integer> bigramWithoutGaps = makeBigram(fileWithoutGaps.getCanonicalPath(), alphabetWithoutGap, 1);
            HashMap<String, Integer> bigramWithGapWithoutIntersection = makeBigram(fileWithGaps.getCanonicalPath(), alphabetWithGap, 2);
            HashMap<String, Integer> bigramWithoutGapsWithoutIntersection = makeBigram(fileWithoutGaps.getCanonicalPath(), alphabetWithoutGap, 2);


//            System.out.println(frequencyTableWithGap + "\n" + frequencyTableWithoutGap + "\n" + bigramWithGap + "\n" +
//                    bigramWithoutGaps);

            HashMap<String, Double> probabilityTableWithGap = makeProbabilityTable(frequencyTableWithGap);
            HashMap<String, Double> probabilityTableWithoutGap = makeProbabilityTable(frequencyTableWithoutGap);

            HashMap<String, Double> bigramProbabilityTableWithGap = makeProbabilityTable(bigramWithGap);
            HashMap<String, Double> bigramProbabilityTableWithoutGaps = makeProbabilityTable(bigramWithoutGaps);

            HashMap<String, Double> bigramProbabilityTableWithGapWithoutIntersection = makeProbabilityTable(bigramWithGapWithoutIntersection);
            HashMap<String, Double> bigramProbabilityTableWithoutGapsWithoutintersection = makeProbabilityTable(bigramWithoutGapsWithoutIntersection);

//            System.out.println(probabilityTableWithGap + "\n" + probabilityTableWithoutGap + "\n" +
//                    bigramProbabilityTableWithGap + "\n" + bigramProbabilityTableWithoutGaps);

            System.out.println("Entropy for:" + "\n" + "One symbol with gap (H1): " + calculateEntropy(probabilityTableWithGap) + "\n" +
                    "One symbol without gaps (H1): " + calculateEntropy(probabilityTableWithoutGap) + "\n" +
                    "Bigram with gaps/with intersection (H2): " + calculateEntropy(bigramProbabilityTableWithGap) / 2 + "\n" +
                    "Bigram without gaps/with intersection (H2): " + calculateEntropy(bigramProbabilityTableWithoutGaps) / 2 + "\n" +
                    "Bigram with gaps/without intersection (H2): " + calculateEntropy(bigramProbabilityTableWithGapWithoutIntersection) / 2 + "\n" +
                    "Bigram without gaps/without intersection (H2): " + calculateEntropy(bigramProbabilityTableWithoutGapsWithoutintersection) / 2);
            // dividing by 2 to get H2


        } catch (FileNotFoundException e) {
            System.err.println("file not found");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //divide by 2 to get H2!!
    private static double calculateEntropy(HashMap<String, Double> probabilityTable) {
        double entropy = 0;
        for (String key : probabilityTable.keySet()) {
            double probability = probabilityTable.get(key);
            if (probability != 0.0)
                entropy -= probability * (Math.log(probability) / Math.log(2));
        }
        return entropy;
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

    private static HashMap<String, Integer> makeBigram(String filePath, char[] alphabet, int offset) throws IOException {
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

    private static HashMap<String, Integer> makeFrequencyDiagram(String filePath, char[] alphabet) throws IOException {
        HashMap<String, Integer> frequencyTable = new HashMap<>();
        for (char letter : alphabet) {
            frequencyTable.put(Character.toString(letter), 0);
        }
        FileInputStream fstream = new FileInputStream(filePath);
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        String fileLine;
        while ((fileLine = br.readLine()) != null) {
            for (char letter : fileLine.toCharArray()) {
                if (frequencyTable.containsKey(Character.toString(letter))) {
                    int value = frequencyTable.get(Character.toString(letter));
                    frequencyTable.put(Character.toString(letter), value + 1);
                }
            }
        }
        return frequencyTable;
    }

    public static void filterText() throws IOException {
        FileInputStream fstream = new FileInputStream(inputFile.getCanonicalPath());
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

        BufferedWriter withGaps = new BufferedWriter(new FileWriter(fileWithGaps.getCanonicalPath()));
        BufferedWriter withoutGaps = new BufferedWriter(new FileWriter(fileWithoutGaps.getCanonicalPath()));

        String fileLine, strWithGaps, strWithoutGaps;
        while ((fileLine = br.readLine()) != null) {
            if (fileLine.length() == 0) continue;
            fileLine = fileLine.toLowerCase();
            fileLine = fileLine.replaceAll("ё", "e").replaceAll("ъ", "ь");
            strWithGaps = fileLine.replaceAll("\s+", " ").replaceAll("[^А-я\s]", "");
            strWithoutGaps = fileLine.replaceAll("[^А-я]", "");
            withGaps.write(strWithGaps);
            withoutGaps.write(strWithoutGaps);
        }
        withGaps.flush();
        withGaps.close();
        withoutGaps.flush();
        withoutGaps.close();
    }
}
