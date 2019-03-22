

public class Perceptron {
    private double[] weights;
    private double threshold;

    public double[] getWeights() {
        return weights;
    }

    public void setWeights(double[] weights) {
        this.weights = weights;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }


    public double outFunction(double[] inputVector) throws Exception {
        return findNet(inputVector)-threshold;

    }

    public double findNet(double[] inputVector){
        double net = 0.0;
        for (int i = 0; i < inputVector.length; i++)
            net += weights[i] * inputVector[i];
        return net;
    }

    //using Delta rule here
    public void changeWeights(double[] inputVector, int expectedOutput, int realOtput) throws Exception {
        if (inputVector.length == weights.length) {
            for (int i = 0; i < weights.length; i++)
                weights[i] = weights[i] + (expectedOutput - realOtput) * inputVector[i];
        } else
            throw new Exception("dimension of input vector!=dimension of weights vector");
    }
}
