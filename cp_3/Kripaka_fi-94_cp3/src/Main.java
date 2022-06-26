import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    private static final File text = new File("/home/ikripaka/Documents/University/fi-labs-2022/cp_3/Kripaka_fi-94_cp3/text.txt");
    private static final File deadSouls = new File("/home/ikripaka/Documents/University/fi-labs-2022/cp_3/Kripaka_fi-94_cp3/dead_souls.txt");
    private static final File filteredDeadSouls = new File("/home/ikripaka/Documents/University/fi-labs-2022/cp_3/Kripaka_fi-94_cp3/filtered_dead_souls.txt");

    private static final File message = new File("/home/ikripaka/Documents/University/fi-labs-2022/cp_3/Kripaka_fi-94_cp3/message.txt");
    private static final File filteredText = new File("/home/ikripaka/Documents/University/fi-labs-2022/cp_3/Kripaka_fi-94_cp3/filtered_text.txt");
    private static final File cipherText = new File("/home/ikripaka/Documents/University/fi-labs-2022/cp_3/Kripaka_fi-94_cp3/ciphertext.txt");

    private static final File decipheredText = new File("/home/ikripaka/Documents/University/fi-labs-2022/cp_3/Kripaka_fi-94_cp3/deciphered_text.txt");
    private static final Character[] alphabetWithoutGap = {'а', 'б', 'в', 'г', 'д', 'е', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ',  'ы','ь', 'э', 'ю', 'я'};


    public static void main(String[] args) throws IOException {
        ArrayList<Character> alphabet = new ArrayList<Character>(Arrays.asList(alphabetWithoutGap));

        filterText(message, filteredText);
        BigramCipher cipher = new BigramCipher();

//        String ciphered = cipher.encode("лоавлаолвоалвоалаовлдаоывлдаоывафывашуао", 7, 29, alphabet);
//        System.out.println("encoded: " + ciphered);
//        System.out.println("decoded: " + cipher.decode(ciphered, 7, 29, alphabet));

//        cipher.encodeFile(filteredText, 11, 23, alphabet, cipherText);
//        cipher.decodeFile(cipherText, 11, 23, alphabet, decipheredText);
        cipher.decodeFile(filteredText, alphabet, decipheredText);
    }

    public static void filterText(File text, File filteredText) throws IOException {
        FileInputStream fstream = new FileInputStream(text.getCanonicalPath());
        DataInputStream in = new DataInputStream(fstream);
        BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

        BufferedWriter filtered = new BufferedWriter(new FileWriter(filteredText.getCanonicalPath()));

        String fileLine, withoutGaps;
        while ((fileLine = br.readLine()) != null) {
            if (fileLine.length() == 0) continue;
            fileLine = fileLine.toLowerCase();
            fileLine = fileLine.replaceAll("ё", "e");
            fileLine = fileLine.replaceAll("ъ", "ь");
            withoutGaps = fileLine.replaceAll("[^А-я]", "");
            filtered.write(withoutGaps);
        }
        filtered.flush();
        filtered.close();
    }
}