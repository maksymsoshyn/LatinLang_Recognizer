import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
    private static Scanner userIn = new Scanner(System.in);
    public static void main(String[] args) throws IOException {
        TextAnalyzer textAnalyzer = new TextAnalyzer();
        Files.walkFileTree(Paths.get("src/Training"), textAnalyzer);
        Trainer trainer = new Trainer(textAnalyzer.getAvgLettersOfLangs(), textAnalyzer.getAvgLettersInLangFiles());
        Files.walkFileTree(Paths.get("src/Test"), textAnalyzer);
        trainer.startNeuralTest(textAnalyzer.getAvgLettersInLangFiles());
        trainer.printClassificationMatrix();
        trainer.printInfo();
        processInput(trainer, textAnalyzer);
    }

    private static void processInput(Trainer perceptronTrainer, TextAnalyzer analyzer){
        while(true){
            System.out.println("Enter word in known language");
            System.out.println(perceptronTrainer.classifyDedicatedVector(analyzer.analizeString(userIn.nextLine())));
        }
    }
}
