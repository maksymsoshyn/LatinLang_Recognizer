import java.util.*;

class Trainer {
    private Map<Perceptron, String> langPerceptrons = new LinkedHashMap<>();
    private Map<String, float[]> langLetterUsing;
    private int[][] classificationMatrix;

    Trainer(Map<String, float[]> langLetterUsing, Map<String, float[][]> trainData) {
        this.langLetterUsing = langLetterUsing;
        initializePerceptronsLangMap();
        for (Perceptron perceptron : langPerceptrons.keySet()) {
            trainPerceptron(perceptron, langPerceptrons.get(perceptron), trainData);
        }
    }

    //configuring new perceptrons and add them to map
    private void initializePerceptronsLangMap() {
        float threshold = langLetterUsing.size();
        for (String lang : langLetterUsing.keySet()) {
            Perceptron langPerceptron = new Perceptron();
            float[] weights = langLetterUsing.get(lang);
            langPerceptron.setWeights(Arrays.copyOf(weights, weights.length));
            langPerceptron.setThreshold(threshold);
            langPerceptrons.put(langPerceptron, lang);
            threshold--;
        }
    }

    private void trainPerceptron(Perceptron perceptron, String perceptronLang, Map<String, float[][]> trainData) {
        float vecNum = 0;
        int rightBehaveTime = 0;
        for (String lang : trainData.keySet()) {//going through frequency vectors of files
            float[][] langVectors = trainData.get(lang);
            vecNum += langVectors.length;
            for (float[] langVector : langVectors) {
                int output = perceptron.outFunction(langVector);
                if (output == 1 && !perceptronLang.equals(lang)) {
                    perceptron.changeWeights(langVector, 0, 1);
                } else if (output == 0 && perceptronLang.equals(lang)) {
                    perceptron.changeWeights(langVector, 1, 0);
                } else {//unactivated but should
                    rightBehaveTime++;
                }
            }
        }
        if (rightBehaveTime / vecNum < 1f)//check if accuracy is acceptable(perceptron should activate himself only for his language)
            trainPerceptron(perceptron, perceptronLang, trainData);
    }

    void startNeuralTest(Map<String, float[][]> testData) {
        int testDataSize = testData.size();
        classificationMatrix = new int[testDataSize][testDataSize];
        int langIndex = 0;
        //go through avg letter using of particular files of particular languages
        for (String lang : testData.keySet()) {
            float[][] vectors = testData.get(lang);
            for (float[] vector : vectors) {
                classifyVector(vector, langIndex);
            }
            langIndex++;
        }
    }

    private void classifyVector(float[] inputVector, int indexOfLang) {
        int iterationIdx = 0;
        int indexOfClassification = 0;
        Perceptron activated = null;
        for (Perceptron perceptron : langPerceptrons.keySet()) {
            int out = perceptron.outFunction(inputVector);
            if (out == 1)
                if (activated != null && (perceptron.findNet(inputVector) > activated.findNet(inputVector))) {
                    indexOfClassification = iterationIdx;
                    activated = perceptron;
                } else {
                    activated = perceptron;
                }
            if (activated == null)
                indexOfClassification++;
            iterationIdx++;
        }
        classificationMatrix[indexOfLang][indexOfClassification]++;
    }


    void printClassificationMatrix() {
        Set<Perceptron> langSet = langPerceptrons.keySet();
        Iterator<Perceptron> langSetIter = langSet.iterator();
        String firstLang = langPerceptrons.get(langSetIter.next());
        System.out.print("Classified as ->");
        System.out.print(firstLang);

        while (langSetIter.hasNext())
            System.out.print(" " + langPerceptrons.get(langSetIter.next()));
        System.out.println();
        langSetIter = langSet.iterator();
        for (int[] classificationMatrix1 : classificationMatrix) {
            System.out.print(langPerceptrons.get(langSetIter.next()));
            for (int i : classificationMatrix1)
                System.out.print(" " + i);
            System.out.println();
        }
        System.out.println();
    }

    void printInfo() {
        System.out.println("Accuracy: " + findAccuracy());
        float[] precisions = findPrecisions();
        float[] recalls = findRecalls();
        int currentIndex = 0;
        Set<Perceptron> langSet = langPerceptrons.keySet();
        for (Perceptron perceptron : langSet) {
            String lang = langPerceptrons.get(perceptron);
            System.out.println(lang + ": ");
            System.out.println("Precision: " + precisions[currentIndex]);
            System.out.println("Recall: " + recalls[currentIndex]);
            System.out.println("F-measure: " + fMeasure(precisions[currentIndex], recalls[currentIndex]));
            System.out.println();
            currentIndex++;
        }

    }

    private float findAccuracy() {
        float numberOfThruth = 0f;
        float overall = 0f;
        for (int i = 0; i < classificationMatrix.length; i++) {
            numberOfThruth += classificationMatrix[i][i];
            for (int j = 0; j < classificationMatrix.length; j++) {
                overall += classificationMatrix[i][j];
            }
        }
        return (numberOfThruth / overall);
    }

    private float[] findPrecisions() {
        float[] precisions = new float[classificationMatrix.length];
        for (int i = 0; i < classificationMatrix.length; i++) {
            float factClassified = classificationMatrix[i][i];
            float overallClassified = 0;
            for (int[] classificationMatrix1 : classificationMatrix) {
                overallClassified += classificationMatrix1[i];
            }
            if (overallClassified == 0)
                precisions[i] = 0;
            else
                precisions[i] = factClassified / overallClassified;
        }
        return precisions;
    }

    private float[] findRecalls() {
        float[] recalls = new float[classificationMatrix.length];
        for (int i = 0; i < classificationMatrix.length; i++) {
            float trueClassified = classificationMatrix[i][i];
            float overallClassified = 0;
            for (int j = 0; j < classificationMatrix[i].length; j++) {
                overallClassified += classificationMatrix[i][j];
            }
            if (overallClassified == 0) {
                recalls[i] = 0;
            } else
                recalls[i] = trueClassified / overallClassified;
        }
        return recalls;
    }

    private float fMeasure(float p, float r) {
        if (p == 0 || r == 0)
            return 0;
        return (2 * p * r) / (p + r);
    }

    String classifyDedicatedVector(float[] inputVector) {
        Perceptron activated = null;
        for (Perceptron perceptron : langPerceptrons.keySet()) {
            float out = perceptron.outFunction(inputVector);
            if (out == 1) {
                if (activated == null)
                    activated = perceptron;
                else
                    if (activated.findNet(inputVector) > perceptron.findNet(inputVector))
                        activated = perceptron;
            }
        }

        return langPerceptrons.get(activated);
    }
}
