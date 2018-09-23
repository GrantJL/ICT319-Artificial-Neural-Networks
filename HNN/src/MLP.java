/**
 * @author Jayden Grant
 * @date 13-Sep-18
 */

import java.util.Random;

/**
 * Multi-Layer Perceptron with Back Propagation
 * With 2 input nodes, 1 hidden layer with 2 nodes and a single output node.
 */
public class MLP
{
    private final int MAX_EPOCH = 100000;

    // Number of nodes (not including bias node)
    private final int INPUT_NODES = 2;
    private final int HIDDEN_NODES = 2;
    private final int OUTPUT_NODES = 1;

    private final float MIN_ERROR = 0.001f;
    private float learningRate = 0.15f;

    // Holds the nodes output value. +1 indicates a bias node
    private float inputs[] = new float[INPUT_NODES + 1];
    private float hidden[] = new float[HIDDEN_NODES + 1];
    private float outputs[] = new float[OUTPUT_NODES];

    // Weights between the input and hidden nodes (+1 for the bias input node)
    private float hWeights[][] = new float[INPUT_NODES + 1][HIDDEN_NODES];
    // Weights between the hidden and output nodes (+1 for the bias hidden node)
    private float oWeights[][] = new float[HIDDEN_NODES + 1][OUTPUT_NODES];

    // Error (+1 as hidden layer has a bias node)
    private float oError[] = new float[OUTPUT_NODES];
    private float hError[] = new float[HIDDEN_NODES + 1];

    // RNG for initializing weights
    private Random random = new Random();

    // The current target output
    private float target;

    // Objects used to load and store input data.
    private PatternLoader trainingSet;
    private PatternLoader trainingResults;
    private String testSetPath;

    public MLP()
    {
        this("patterns/MLP/XORtrain.txt",
                "patterns/MLP/result/XORtrain.txt",
                "patterns/MLP/result/XORtest.txt");
    }

    public MLP(String trainingSet, String trainingTarget, String testSet)
    {
        this.trainingSet = new PatternLoader(trainingSet);
        this.trainingResults = new PatternLoader(trainingTarget);
        this.testSetPath = testSet;
    }

    /**
     * Initializes the weights between all nodes.
     *
     * Range: -2.4/(num input nodes)  to  2.4/(num input nodes)
     */
    private void initializeWeights()
    {
        float range = (2.4f/INPUT_NODES);
        for (int i = 0; i < INPUT_NODES + 1; i++)
        {
            for (int j = 0; j < HIDDEN_NODES; j++)
            {
                hWeights[i][j] = (random.nextFloat() * 2 * range) - range;
            }
        }

        range = (2.4f/HIDDEN_NODES);
        for (int j = 0; j < HIDDEN_NODES + 1; j++)
        {
            for (int k = 0; k < OUTPUT_NODES; k++)
            {
                oWeights[j][k] = (random.nextFloat() * 2 * range) - range;
            }
        }
    }

    /**
     * Test the MLP with given file, uses default test data if <code>testSetFile</code> is null.
     * @param testSetFile the file containing test data.
     */
    public void test(String testSetFile)
    {
        // Use default test set (loaded at construction)
        if (testSetFile == null)
            testSetFile = testSetPath;

        // Used to calc accuracy to the XOR problem
        int count = 0;
        int corr = 0;

        // Load testing data
        PatternLoader testingSet = new PatternLoader(testSetFile);

        // Heading
        System.out.println("XOR Tests");
        System.out.printf("\nInputs  Output (Error/Difference)");

        // Test each set and its pairs
        for (int set = 0; set < testingSet.getNumPatterns(); set++)
        {
            for (int pair = 0; pair < testingSet.getPatternSize() / 2; pair++)
            {
                {// Set the input to the MLP
                    inputs[2] = 1.0f; //bias

                    // Load pair into 2nd and 3rd input node
                    inputs[0] = testingSet.getPattern(set).elementAt(pair * 2);
                    inputs[1] = testingSet.getPattern(set).elementAt(pair * 2 + 1);
                }

                // Run the input through the netwrok
                calculateHiddenLayer();
                calculateOutputLayer();

                // Run the result through a step function:
                int result;
                if (outputs[0] < 0.5)
                    result = 0;
                else
                    result = 1;

                // Display the output (as if XOR problem)
                boolean a = (inputs[0] < 0.5)? false : true;
                boolean b = (inputs[1] < 0.5)? false : true;
                int actual = (a ^ b)? 1 : 0;

                // Increment accuracy data
                count++;
                if (actual == result)
                    corr++;

                // Print each pairs out
                System.out.printf("\n%d ^ %d = %d (%3.2f)", (int)inputs[0], (int)inputs[1], result, (double)(outputs[0] - (float)actual));
            }
        }
        // Print the accuracy (assuming the MLP is trained for the XOR problem
        System.out.printf("\nXOR Success Rate: %d/%d (%4.1f%%)\n", corr, count, 100*(corr/(float)count));
    }

    /**
     * Train the MLP
     */
    public void train()
    {
        System.out.println("Training MLP...");
        // Set random weights (all node inc. bias)
        initializeWeights();

        boolean training = true;
        int epoch = 0;

        float overallErrr = 0;
        float num = 0;
        float sqError = 0.0f;
        float avgErr = 0.0f;

        int noPats = trainingSet.getNumPatterns();
        int patSize = trainingSet.getPatternSize();

        // Until we reach our error target (or exceed training limit)
        while(training)
        {
            sqError = 0.0f;
            // open training data
            // for each training patter
            for (int set = 0; set < noPats; set++)
            {
                for (int pair = 0; pair < patSize; pair+=2)
                {//for each training pair
                    // read input + bias into input vec
                    // read targeta into target vector
                    loadPairData(set, pair);

                    //resetStuff();
                    // reset net, actual out, error vector to 0
                    // calc net, actual output HIDDEN
                    calculateHiddenLayer();
                    // calc net, actual output OUTPUT LAYER
                    calculateOutputLayer();

                    // Calc/Sum error
                    sqError += Math.pow((outputs[0] - target), 2.0);
                    overallErrr += Math.abs(outputs[0] - target);
                    num++;

                    calculateOutputError();
                    calculateHiddenError();

                    // update outer weights
                    updateOutputWeights();
                    // update hidden weights
                    updateHiddenWeights();
                }
            }
            avgErr = overallErrr / num;

            epoch++;
            //System.out.printf("%d: %f | %f\n", epoch, avgErr, sqError / pairCount);//, overallErrr, num);

            // Using sum of squared errors over a set (4 pairs) for error
            if(((sqError / patSize) < MIN_ERROR && epoch >= 1000) || epoch > MAX_EPOCH) // && avgErr < MIN_ERROR);
                training = false;
        }

        // Print training info
        if (epoch <= MAX_EPOCH)
            System.out.println("MLP finished training in " + epoch + " epochs. (Avg. error: " + avgErr + ")");
        else
        {
            System.out.println("MLP didn't not finish training within " + MAX_EPOCH + " epochs.");
            System.out.println("Epochs: " + epoch + " Avg. Error: " + avgErr);
            System.out.println("Last sets sum of squared errors: " + (sqError / patSize));
        }
    }

    /**
     * Calculate the actual output at each hidden layer node
     */
    private void calculateHiddenLayer()
    { // Hidden layers are in index 0 and 1

        hidden[HIDDEN_NODES] = 1.0f; //bias

        // Eq 7 Update each node (hidden)
        for(int j = 0; j < HIDDEN_NODES; j++)
        {
            // sum the weighted inputs for this node j
            float sum = 0.0f;

            for (int i = 0; i < INPUT_NODES + 1; i++)
            {
                // Sum the input, multiplied by the connecting
                //   weight between that input and the hidden layer node
                // sum += Xi * Wij
                sum += inputs[i] * hWeights[i][j];
            }

            // Run the summa of node j through act func
            hidden[j] = activationFunction(sum);
        }
    }

    /**
     * Calculate the actual output at the output layer node
     */
    private void calculateOutputLayer()
    {
        // Output layer is in index 2

        // Eq 8 Update each node (output)
        for (int k = 0; k < OUTPUT_NODES; k++)
        {
            // sum the weighted inputs(from hidden) for this ouput node k
            float sum = 0.0f;

            for (int j = 0; j < HIDDEN_NODES + 1; j++)
            {
                // Sum the input, multiplied by the connecting
                //   weight between that input and the hidden layer node
                // sum += Xkj * Wjk
                sum += hidden[j] * oWeights[j][k];
            }

            // Run the summa of node k through act func
            outputs[k] = activationFunction(sum);
        }
    }

    /**
     * Calculate the error at the output layer node
     */
    private void calculateOutputError()
    {
        // Eq 9 Calculate the error at the output node(s)
        for (int k = 0; k < OUTPUT_NODES; k++)
        {
            /* Eq 9
             * Output layer error (error term) =
             * term 1: the actual output
             * term 2: 1 - the actual output
             * term 3: the target - the actual output (actual error)
             * multiple the terms.
             */

            // We know there is only 1 output, so target is not an array
            oError[k] = outputs[k] * (1 - outputs[k]) * (target - outputs[k]);
        }
    }

    /**
     * Calculate the error at each hidden layer node
     */
    private void calculateHiddenError()
    {
        // Eq 10. Error (gradient/term) at each hidden node
        for(int j = 0; j < HIDDEN_NODES + 1; j++)
        {
            // Sum the weighted error for each connected output node
            float sum = 0.0f;
            for (int k = 0; k < OUTPUT_NODES; k++)
            {
                sum += oError[k] * oWeights[j][k];
            }

            // error(j) = output(j) * 1-output(j) * sum(weight output layer errors)
            hError[j] = hidden[j] * (1 - hidden[j]) * sum;
        }
    }

    /**
     * Update the weights connecting the hidden and output layer nodes.
     */
    private void updateOutputWeights()
    {
        // Eq 11 Update weight at each output node
        for (int k = 0; k < OUTPUT_NODES; k++)
        {
            /* weight change between hidden node j and output node k =
             * term 1: learning rate
             * term 2: output at hidden node j
             * term 3: error at output layer node k
             *
             * multiply terms, add to previous weight
             */
            for (int j = 0; j < HIDDEN_NODES + 1; j++)
            {
                oWeights[j][k]+= learningRate * hidden[j] * oError[k];
            }
        }
    }

    /**
     * Update the weights connecting the input and hidden layer nodes.
     */
    private void updateHiddenWeights()
    {
        // Eq 12 Update weight at each hidden node
        for (int j = 0; j < HIDDEN_NODES; j++)
        {
            /* weight change between input node i and hidden node j =
             * term 1: learning rate
             * term 2: input at input node i
             * term 3: error at hidden layer node j
             *
             * multiply terms, add to previous weight
             */
            for (int i = 0; i < INPUT_NODES + 1; i++)
            {
                hWeights[i][j] += learningRate * inputs[i] * hError[j];
            }
        }
    }

    /**
     * Load the pair at specified index from the specified training set
     * @param setIndex
     * @param pairIndex
     */
    private void loadPairData(int setIndex, int pairIndex)
    {
        inputs[0] = trainingSet.getPattern(setIndex).elementAt(pairIndex);
        inputs[1] = trainingSet.getPattern(setIndex).elementAt(pairIndex + 1);

        inputs[2] = 1.0f; //bias

        // Load the pairs target
        target = trainingResults.getPattern(setIndex).elementAt(pairIndex/2);
    }

    /**
     * Sigmoid activation function
     */
    private float activationFunction(float value)
    {
        return 1 / (1 + (float)Math.exp(-value));
    }
}


