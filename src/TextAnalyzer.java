import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class TextAnalyzer extends SimpleFileVisitor<Path> {
    private Map<String, float[]> avgLettersOfLang = new TreeMap<>();
    private Map<String, float[][]> avgLettersInFiles = new TreeMap<>();
    private float[][] lettersInFile;
    private float numSymb;
    private int currentFileIndex = 0;


    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        if(!dir.getFileName().toString().equals("Training")) {
            numSymb = 0f;
            lettersInFile = new float[5][26];//columns=number of files rows=number of latins letter
            avgLettersInFiles.put(dir.getFileName().toString(), lettersInFile);
        }if(dir.getFileName().toString().equals("Test")){
            avgLettersOfLang = new TreeMap<>();
            avgLettersInFiles = new TreeMap<>();
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (attrs.isRegularFile()) {
            Map<Character, Integer> characterLineMap = new HashMap<>();//less memory efficiency but more human-readable
            prepareLetterMap(characterLineMap);
            Files.lines(file, Charset.forName("ISO-8859-1")).forEach(i -> {
                for (int j = 0; j < i.length(); j++) { //go through line
                    char atPos = i.toLowerCase().charAt(j);
                    if (characterLineMap.containsKey(atPos)) { //if char at j is a-z, then it's latin symbol
                        int currentNumOfCharJ = characterLineMap.get(atPos);
                        characterLineMap.put(atPos, currentNumOfCharJ+1);//increment the number of occurancies of this symbol
                        numSymb++;
                    }
                }
            });
            for (int i = 0; i < lettersInFile[currentFileIndex].length; i++) {//mapping num of occurencies of chars
                lettersInFile[currentFileIndex][i] = characterLineMap.get((char) (97 + i));
            }
            currentFileIndex++;
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        if (!dir.getFileName().toString().equals("Training")) {
            float[] avgOfLetter = computeAvgUsageOfLettersInLang(lettersInFile);//computing overall avager(need for weight vector)
            avgLettersOfLang.put(dir.getFileName().toString(), avgOfLetter);
            computeAvgUsageOfLettersInFile(lettersInFile);//finding avg frequency vector in separate files
            currentFileIndex=0;
        }
        return FileVisitResult.CONTINUE;
    }

    //initialize map with a-z chars as key
    private void prepareLetterMap(Map<Character, Integer> numberOfLetters) {
        for (int i = 97; i <= 122; i++)
            numberOfLetters.put((char) (i), 0);
    }

    public Map<String, float[]> getAvgLettersOfLangs() {
        return avgLettersOfLang;
    }

    public Map<String, float[][]> getAvgLettersInLangFiles(){
        return avgLettersInFiles;
    }

    public float[] computeAvgUsageOfLettersInLang(float[][] filesChars) {
        float[] avgOfLetter = new float[26];

        for (int i = 0; i < 26; i++) { //computing avg by columns
            float sumChar = 0;
            for (int j = 0; j < filesChars.length; j++)
                sumChar+= filesChars[j][i];
            avgOfLetter[i]=sumChar/numSymb;
        }
        return avgOfLetter;
    }

    public void computeAvgUsageOfLettersInFile(float[][] lettersInFile) {
        for (int i = 0; i < lettersInFile.length; i++) {
            float sumCharsInFile = 0;
            for (int j = 0; j < lettersInFile[i].length; j++)
                sumCharsInFile += lettersInFile[i][j];
            for (int j = 0; j < lettersInFile[i].length; j++)
                lettersInFile[i][j] /= sumCharsInFile;

        }
    }

}
