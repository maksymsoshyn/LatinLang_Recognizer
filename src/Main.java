import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
        TextAnalyzer textAnalyzer = new TextAnalyzer();
        Files.walkFileTree(Paths.get("src/Training"), textAnalyzer);
        Trainer trainer = new Trainer(textAnalyzer.getAvgLettersOfLangs(), textAnalyzer.getAvgLettersInLangFiles());
        Files.walkFileTree(Paths.get("src/Test"), textAnalyzer);
        trainer.startNeuralTest(textAnalyzer.getAvgLettersInLangFiles());
        trainer.printClassMatrix();
        trainer.printInfo();
    }
}
