import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Trainer {
    private Map<String, Perceptron> langPerceptrons = new HashMap<>();
    private Map<String, float[]> langLetterUsing;
    private

    Trainer(Map<String, float[]> langLetterUsing){
        this.langLetterUsing = langLetterUsing;
        initializePerceptronsLangMap();
    }

    private void initializePerceptronsLangMap(){
        for(String lang : langLetterUsing.keySet()){
            Perceptron langPerceptron = new Perceptron();
            float[] weights = langLetterUsing.get(lang);
            langPerceptron.setWeights(Arrays.copyOf(weights, weights.length));
            langPerceptrons.put(lang, langPerceptron);
        }
    }

    private void trainPerceptrons(){
        for(int i = 0; i<langLetterUsing.size(); i++){
            Map<String, Integer> langActiveCoefficient = new HashMap<>();

        }
    }
}
