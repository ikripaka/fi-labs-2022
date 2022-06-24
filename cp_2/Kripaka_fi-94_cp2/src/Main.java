import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


public class Main {
    private static final File text = new File("/home/ikripaka/Documents/University/fi-labs-2022/cp_2/Kripaka_fi-94_cp2/text.txt");
    private static final File deadSouls = new File("/home/ikripaka/Documents/University/fi-labs-2022/cp_2/Kripaka_fi-94_cp2/dead_souls.txt");
    private static final File filteredDeadSouls = new File("/home/ikripaka/Documents/University/fi-labs-2022/cp_2/Kripaka_fi-94_cp2/filtered_dead_souls.txt");

    private static final File message = new File("/home/ikripaka/Documents/University/fi-labs-2022/cp_2/Kripaka_fi-94_cp2/message.txt");
    private static final File filteredText = new File("/home/ikripaka/Documents/University/fi-labs-2022/cp_2/Kripaka_fi-94_cp2/filtered_text.txt");
    private static final File cipherText = new File("/home/ikripaka/Documents/University/fi-labs-2022/cp_2/Kripaka_fi-94_cp2/ciphertext.txt");

    private static final File decipheredText = new File("/home/ikripaka/Documents/University/fi-labs-2022/cp_2/Kripaka_fi-94_cp2/deciphered_text.txt");
    private static final Character[] alphabetWithoutGap = {'а', 'б', 'в', 'г', 'д', 'е', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю', 'я'};

    public static void main(String[] args) throws IOException {
        ArrayList<Character> alphabet = new ArrayList<Character>(Arrays.asList(alphabetWithoutGap));

        filterText(text, filteredText);
        filterText(deadSouls, filteredDeadSouls);
//        filterText(message, cipherText);

        VingenereCipher cipher = new VingenereCipher();
//        cipher.encodeFile(filteredText, "ин", alphabet, cipherText);
//        System.out.println("conformity index r = 2, "+ cipher.calculateConformityIndex(cipherText, alphabet));
//        cipher.encodeFile(filteredText, "инф", alphabet, cipherText);
//        System.out.println("conformity index r = 3, "+ cipher.calculateConformityIndex(cipherText, alphabet));
//        cipher.encodeFile(filteredText, "инфо", alphabet, cipherText);
//        System.out.println("conformity index r = 4, "+ cipher.calculateConformityIndex(cipherText, alphabet));
//        cipher.encodeFile(filteredText, "информа", alphabet, cipherText);
//        System.out.println("conformity index r = 7, "+ cipher.calculateConformityIndex(cipherText, alphabet));
//        cipher.encodeFile(filteredText, "информацияз", alphabet, cipherText);
//        System.out.println("conformity index r = 11, "+ cipher.calculateConformityIndex(cipherText, alphabet));
//        cipher.encodeFile(filteredText, "информациязалло", alphabet, cipherText);
//        System.out.println("conformity index r = 15, "+ cipher.calculateConformityIndex(cipherText, alphabet));
//        cipher.decodeFile(cipherText,"ключ", alphabet, decipheredText);

//        cipher.decodeFile(cipherText,"информа", alphabet, decipheredText);
//        cipher.decodeFile(cipherText,"ин", alphabet, decipheredText);
        cipher.decodeFileWithMatchIndex(filteredText, alphabet, decipheredText);
//        cipher.decodeFileWithFrequency(filteredText, alphabet, decipheredText);
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
            withoutGaps = fileLine.replaceAll("[^А-я]", "");
            filtered.write(withoutGaps);
        }
        filtered.flush();
        filtered.close();
    }
}
