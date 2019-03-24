

public class Perceptron {
    private float[] weights=new float[26];
    private float threshold;

    public float[] getWeights() {
        return weights;
    }

    public void setWeights(float[] weights) {
        this.weights = weights;
    }

    public float getThreshold() {
        return threshold;
    }

    public void setThreshold(float threshold) {
        this.threshold = threshold;
    }


    public int outFunction(float[] inputVector){
        float net = findNet(inputVector);
        if(net>=threshold)
            return 1;
        else
            return 0;
    }

    public float findNet(float[] inputVector){
        float net = 0f;
        for (int i = 0; i < inputVector.length; i++)
            net += weights[i] * inputVector[i];
        return net;
    }

    //using Delta rule here
    public void changeWeights(float[] inputVector, int expectedOutput, int realOtput){
            for (int i = 0; i < weights.length; i++)
                weights[i] = weights[i] + (expectedOutput - realOtput) * inputVector[i];
    }
}
