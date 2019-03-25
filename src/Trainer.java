import java.util.*;

public class Trainer {
    private Map<Perceptron, String> langPerceptrons = new LinkedHashMap<>();
    private Map<String, float[]> langLetterUsing;
    private int[][] classificationMatrix;

    Trainer(Map<String, float[]> langLetterUsing, Map<String, float[][]> trainData){
        this.langLetterUsing = langLetterUsing;
        initializePerceptronsLangMap();
        for(Perceptron perceptron : langPerceptrons.keySet()){
            trainPerceptron(perceptron, langPerceptrons.get(perceptron), trainData);
        }
    }

    //configuring new perceptrons and add them to map
    private void initializePerceptronsLangMap(){
        float threshold = langLetterUsing.keySet().size();
        for(String lang : langLetterUsing.keySet()){
            Perceptron langPerceptron = new Perceptron();
            float[] weights = langLetterUsing.get(lang);
            langPerceptron.setWeights(Arrays.copyOf(weights, weights.length));
            langPerceptron.setThreshold(threshold);
            langPerceptrons.put(langPerceptron, lang);
            threshold--;
        }
    }

    private void trainPerceptron(Perceptron perceptron, String perceptronLang, Map<String, float[][]> trainData){
        float vecNum=0;
        int rightBehaveTime=0;
        for(String lang : trainData.keySet()){//going through frequency vectors of files
            float[][] langVectors=trainData.get(lang);
            vecNum+=langVectors.length;
            for(int i=0; i<langVectors.length; i++){
                int output = perceptron.outFunction(langVectors[i]);
                if(output==1) {
                    if (perceptronLang.equals(lang))//activated and should
                        rightBehaveTime++;
                    else//activated but shouldnt
                        perceptron.changeWeights(langVectors[i], 0, 1);
                }else if(output==0){
                    if(!perceptronLang.equals(lang))//unactivated and shouldn't
                        rightBehaveTime++;
                    else//unactivated but should
                        perceptron.changeWeights(langVectors[i], 1, 0);
                }
            }
        }
        if(rightBehaveTime/vecNum<1f)//check if accuracy is acceptable(perceptron should activate himself only for his language)
            trainPerceptron(perceptron, perceptronLang, trainData);
    }

    public void startNeuralTest(Map<String, float[][]> testData){
        int testDataSize = testData.size();
        classificationMatrix = new int[testDataSize][testDataSize];
        int langIndex=0;
        //go through avg letter using of particular files of particular languages
        for(String lang : testData.keySet()){
            float[][] vectors = testData.get(lang);
            for(int i = 0; i<vectors.length; i++)
                classifyVector(vectors[i], classificationMatrix, langIndex);
            langIndex++;
        }
    }

    private void classifyVector(float[] inputVector, int[][] classificationMatrix, int indexOfLang){
        int iterationIdx=0;
        int indexOfClassification = 0;
        Perceptron activated = null;
        boolean atLeastOneActivated=false;
        for(Perceptron perceptron: langPerceptrons.keySet()) {
            int out = perceptron.outFunction(inputVector);
            if (out == 1)
                if(atLeastOneActivated){
                    if(perceptron.findNet(inputVector)-perceptron.getThreshold()>activated.findNet(inputVector)-activated.getThreshold()){
                        indexOfClassification=iterationIdx;
                        activated=perceptron;
                    }
                }else {
                    atLeastOneActivated = true;
                    activated=perceptron;
                }
            if(!atLeastOneActivated&&indexOfClassification<classificationMatrix.length-1)
                indexOfClassification++;
            iterationIdx++;
        }
        classificationMatrix[indexOfLang][indexOfClassification]++;
    }

    private Perceptron findPerceptronWithHighestThreshold(Map<Perceptron, Integer> perceptronIMap){
        Set<Perceptron> activatedPerces=perceptronIMap.keySet();
        Iterator<Perceptron> perceIterator=activatedPerces.iterator();
        Perceptron perceptronWithHighestThreshold = perceIterator.next();
        while(perceIterator.hasNext()){
            Perceptron currentPerc=perceIterator.next();
            if(perceptronWithHighestThreshold.getThreshold() < currentPerc.getThreshold())
                perceptronWithHighestThreshold=currentPerc;
        }
        return perceptronWithHighestThreshold;
    }

    public void printClassificationMatrix(){
        Set<Perceptron> langSet = langPerceptrons.keySet();
        Iterator<Perceptron> langSetIter=langSet.iterator();
        String firstLang = langPerceptrons.get(langSetIter.next());
        System.out.print("Classified as ->");
        System.out.print(firstLang);

        while(langSetIter.hasNext())
            System.out.print(" "+langPerceptrons.get(langSetIter.next()));
        System.out.println();
        langSetIter=langSet.iterator();
        for(int i = 0; i<classificationMatrix.length; i++){
            System.out.print(langPerceptrons.get(langSetIter.next()));
            for(int j = 0; j<classificationMatrix[i].length; j++)
                System.out.print(" "+classificationMatrix[i][j]);
            System.out.println();
        }
        System.out.println();
    }

    public void printInfo(){
        System.out.println("Accuracy: "+findAccuracy());
        float[] precisions=findPrecisions();
        float[] recalls=findRecalls();
        int currentIndex=0;
        Set<Perceptron> langSet = langPerceptrons.keySet();
        Iterator<Perceptron> langSetIter=langSet.iterator();
        while(langSetIter.hasNext()){
            String lang = langPerceptrons.get(langSetIter.next());
            System.out.println(lang+": ");
            System.out.println("Precision: "+precisions[currentIndex]);
            System.out.println("Recall: "+ recalls[currentIndex]);
            System.out.println("F-measure: "+fMeasure(precisions[currentIndex], recalls[currentIndex]));
            System.out.println();
            currentIndex++;
        }

    }

    public float findAccuracy(){
        float numberOfThruth=0f;
        float overall = 0f;
        for(int i = 0; i<classificationMatrix.length; i++){
            numberOfThruth+=classificationMatrix[i][i];
            for(int j =0; j<classificationMatrix.length;j++){
                overall+=classificationMatrix[i][j];
            }
        }
        return (numberOfThruth/overall);
    }

    public float[] findPrecisions(){
        float[] precisions=new float[classificationMatrix.length];
        for(int i=0;i<classificationMatrix.length;i++){
            float factClassified=classificationMatrix[i][i];
            float overallClassified=0;
            for(int j=0; j<classificationMatrix.length; j++){
                overallClassified+=classificationMatrix[j][i];
            }
            if(overallClassified==0)
                precisions[i]=0;
            else
                precisions[i]=factClassified/overallClassified;
        }
        return precisions;
    }

    public float[] findRecalls(){
        float[] recalls = new float[classificationMatrix.length];
        for(int i=0;i<classificationMatrix.length;i++){
            float trueClassified=classificationMatrix[i][i];
            float overallClassified=0;
            for(int j=0; j<classificationMatrix[i].length; j++){
                overallClassified+=classificationMatrix[i][j];
            }if (overallClassified == 0) {
                recalls[i]= 0;
            }else
                recalls[i]= trueClassified/overallClassified;
        }
        return recalls;
    }

    public float fMeasure(float p, float r){
        if(p==0||r==0)
            return 0;
        return(2*p*r)/(p+r);
    }
}
