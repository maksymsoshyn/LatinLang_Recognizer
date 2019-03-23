import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class TextAnalyzer extends SimpleFileVisitor<Path>{
    private Map<Character, Integer> numberOfLetters = new HashMap<>();
    private float numSymb;
    private Map<String, float[]> avgLettersOfLang = new TreeMap<>();
    TextAnalyzer(){
        prepareLetterMap();
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        numSymb=0f;
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if(attrs.isRegularFile()){
            Files.lines(file, Charset.forName("ISO-8859-1")).forEach(i->{
                for(int j=0; j<i.length(); j++) { //go through line
                    char atPos = i.toLowerCase().charAt(j);
                    if (numberOfLetters.containsKey(atPos)){ //if char at j is a-z, then it's latin symbol
                        numSymb++;
                        numberOfLetters.put(atPos, numberOfLetters.get(atPos)+1);
                    }
                }
            });
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        if(!dir.getFileName().toString().equals("Training")) {
            float[] avgOfLetter = new float[26];
            Set<Character> characterKey = numberOfLetters.keySet();
            Iterator<Character> charIterator = characterKey.iterator();
            for (int i = 0; charIterator.hasNext(); i++) {
                char currentChar = charIterator.next();
                avgOfLetter[i] = numberOfLetters.get(currentChar) / numSymb;
                numberOfLetters.put(currentChar, 0);
            }
            avgLettersOfLang.put(dir.getFileName().toString(), avgOfLetter);
        }else {
            for (Character currentKey : numberOfLetters.keySet())
                numberOfLetters.put(currentKey, 0);
            avgLettersOfLang = new TreeMap<>();
        }
        return FileVisitResult.CONTINUE;
    }

    //initialize map with a-z chars as key
    private void prepareLetterMap(){
        for(int i = 97; i<=122; i++)
            numberOfLetters.put((char)(i), 0);
    }

    public Map<String, float[]> getAvgLettersOfLangs(){
        return avgLettersOfLang;
    }

}
