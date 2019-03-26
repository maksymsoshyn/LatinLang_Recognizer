import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class TextAnalyzer extends SimpleFileVisitor<Path> {
    private Map<String, float[]> avgLettersOfLang = new LinkedHashMap<>();
    private Map<String, float[][]> avgLettersInFiles = new LinkedHashMap<>();
    private float[][] lettersInFile;
    private float numSymb;
    private int currentFileIndex = 0;


    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        if(!dir.getFileName().toString().equals("Training")) {
            numSymb = 0f;
            lettersInFile = new float[Objects.requireNonNull(new File(String.valueOf(dir)).listFiles()).length][26];//columns=number of files rows=number of latins letter
            //here we store name of language as key and two-dimensional array for number of each language in every file of languagedirectory
            avgLettersInFiles.put(dir.getFileName().toString(), lettersInFile);
        }if(dir.getFileName().toString().equals("Test")){
            avgLettersOfLang = new LinkedHashMap<>();
            avgLettersInFiles = new LinkedHashMap<>();
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (attrs.isRegularFile()) {
            Map<Character, Integer> characterLineMap = new LinkedHashMap<>();//less memory efficiency but more human-readable
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
            //No need to store lettersInFile Array in Map again because we did that in preVisitDirectory method
            currentFileIndex=0;
        }
        return FileVisitResult.CONTINUE;
    }

    //initialize map with a-z chars as key
    private void prepareLetterMap(Map<Character, Integer> numberOfLetters) {
        for (int i = 97; i <= 122; i++)//initialize map of char with ascii a-z keys
            numberOfLetters.put((char) (i), 0);
    }

    Map<String, float[]> getAvgLettersOfLangs() {
        return avgLettersOfLang;
    }

    Map<String, float[][]> getAvgLettersInLangFiles(){
        return avgLettersInFiles;
    }

    //this method for overall avg in language file(used for setting initial weight vector of perceptrons)
    private float[] computeAvgUsageOfLettersInLang(float[][] filesChars) {
        float[] avgOfLetter = new float[26];
        for (int i = 0; i < 26; i++) { //computing avg by columns
            float sumChar = 0;
            for (float[] filesChar : filesChars) sumChar += filesChar[i];
            avgOfLetter[i]=sumChar/numSymb;
        }
        return avgOfLetter;
    }

    //compute
    private void computeAvgUsageOfLettersInFile(float[][] lettersInFile) {
        for (int i = 0; i < lettersInFile.length; i++) {
            float sumCharsInFile = 0;
            for (int j = 0; j < lettersInFile[i].length; j++)
                sumCharsInFile += lettersInFile[i][j];
            for (int j = 0; j < lettersInFile[i].length; j++)
                lettersInFile[i][j] /= sumCharsInFile;

        }
    }

    float[] analizeString(String text){
        float[] vector = new float[26];
        Arrays.fill(vector, 0f);
        String txt = text.toLowerCase();
        int symbs = 0;
        for(int i = 0; i<txt.length(); i++){
            char currentChar = txt.charAt(i);
            if((int)currentChar>=97 && (int)currentChar<=122) {
                vector[currentChar - 97]++;
                symbs++;
            }
        }
        for(int i =0; i<vector.length; i++){
                vector[i] = vector[i] / symbs;
        }
        return vector;
    }

}
