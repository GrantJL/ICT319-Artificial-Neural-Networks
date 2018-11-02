import java.util.Random;

/**
 * Multi-Layer Perceptron with Back Propagation
 * With 2 input nodes, 1 hidden layer with 2 nodes and a single output node.
 *
 * @author Jayden Grant
 * @date 13-Sep-18
 */
public class MLP
{
    private final int MAX_EPOCH = 100000;

    // Number of nodes (not including bias node)
    private int INPUT_NODES = 2;
    private int HIDDEN_NODES = 2;
    private int OUTPUT_NODES = 1;

    private float minError = 0.001f;
    private float learningRate = 0.15f;

    // Holds the nodes output value. +1 indicates a bias node
    private float inputs[];
    private float hidden[];
    private float outputs[];
    // The current target output
    private float target[];

    // Weights between the input and hidden nodes (+1 for the bias input node)
    private float hWeights[][];
    // Weights between the hidden and output nodes (+1 for the bias hidden node)
    private float oWeights[][];

    // Error (+1 as hidden layer has a bias node)
    private float oError[];
    private float hError[];

    // RNG for initializing weights
    private Random random = new Random();

    /**
     * Initialize a MLP with given number of nodes.
     * @param inputNodes the number of input nodes.
     * @param hiddenNodes the number of hidden nodes.
     * @param outputNodes the number of output nodes.
     */
    public MLP(int inputNodes, int hiddenNodes, int outputNodes)
    {
        INPUT_NODES = inputNodes;
        HIDDEN_NODES = hiddenNodes;
        OUTPUT_NODES = outputNodes;
        
        initMLP();
    }
    
    // Initializes the MLPs internal data structure.
    // Called after the MLP layers are set.
    private void initMLP()
    {
        // Holds the nodes output value. +1 indicates a bias node
        inputs = new float[INPUT_NODES + 1];
        hidden = new float[HIDDEN_NODES + 1];
        outputs = new float[OUTPUT_NODES];
        
        target = new float[OUTPUT_NODES];

        hWeights = new float[INPUT_NODES + 1][HIDDEN_NODES];
        oWeights = new float[HIDDEN_NODES + 1][OUTPUT_NODES];

        hError = new float[HIDDEN_NODES + 1];
        oError = new float[OUTPUT_NODES];
    }

    public void setLearningRate(float learningRate)
    {
        this.learningRate = learningRate;
    }
    public float getLearningRate()
    {
        return learningRate;
    }
    public void setMinError(float minError) { this.minError = minError; }
    public float getMinError() { return minError; }

    /**
     * Run the provided inputs through the trained mlp and return the results.
     *
     * Test the MLP with the provided test data, the results are returned in an array where the results
     * have been run through an step activation function, with a threshold of 0.5f.
     * @param testInputData array of input test data, each inner array is expected to have a size
     *                      equal to the number of input nodes.
     * @return An array or results, the number of arrays returned will be equal to the number of testInput data.
     *         If the number or input nodes in a test is invalid, the array for that test will have a size of 0.
     */
    public float[][] test(float[][] testInputData)
    {
        return test(testInputData, true);
    }

    /**
     * Run the provided inputs through the trained mlp and return the results.
     *
     * Test the MLP with the provided test data returning the results.
     * @param testInputData array of input test data, each inner array is expected to have a size
     *                      equal to the number of input nodes.
     * @param useStepFunc whether to run the output results through a step function.
     * @return An array or results, the number of arrays returned will be equal to the number of testInput data.
     *         If the number or input nodes in a test is invalid, the array for that test will have a size of 0.
     */
    public float[][] test(float[][] testInputData, boolean useStepFunc)
    {
        if (testInputData.length > 0)
        {
            // An Array holding the results of the test.
            float[][] results = new float[testInputData.length][OUTPUT_NODES];

            // Test each set and its pairs
            for (int t = 0; t < testInputData.length; t++)
            {   // Temp array holding inputs
                float[] testInputs = testInputData[t];

                // Only perform test when there is the correct number of inputs
                if (testInputs.length == INPUT_NODES)
                {
                    // Copy the test data into the input nodes
                    for (int i = 0; i < INPUT_NODES; i++)
                    {
                        inputs[i] = testInputs[i];
                    }

                    // Run the input through the network
                    calculateHiddenLayer();
                    calculateOutputLayer();

                    // run the results through the step function?
                    if (useStepFunc)
                    {
                        // add the results to the results
                        for (int i = 0; i < outputs.length; i++)
                        {
                            results[t][i] = stepFunction(outputs[i]);
                        }
                    }
                    else
                    {
                        // add the results to the results
                        for (int i = 0; i < outputs.length; i++)
                        {
                            results[t][i] = outputs[i];
                        }
                    }

                }
                else
                {
                    // Returns a size 0 array if inputs aren't valid.
                    results[t] = new float[0];
                }
            }

            return results;
        }
        else
        {
            return new float[0][0];
        }
    }

    // TODO: Document this method better :))
    /**
     * Trains the MLP with provided training data.
     *
     * @param trainingSets A vector of float arrays, each arrays size should match the number of input nodes.
     * @param targetSet A vector of float arrays, each arrays size should match the number of output nodes.
     */
    public void train(float[][] trainingSets, float[][] targetSet)
    {
        System.out.println("Training MLP...");
        // Set random weights (all node inc. bias)
        initializeWeights();

        boolean training = true;
        int epoch = 0;

        float sqError;

        // Until we reach our error target (or exceed training limit)
        while(training)
        {
            // Reset sum of squared errors for next epoch
            sqError = 0.0f;

            // Train the MLP with each data set
            for (int set = 0; set < trainingSets.length; set++)
            {
                {   // Copy the data set into the input nodes
                    for (int i = 0; i < INPUT_NODES; i++)
                    {
                        inputs[i] = trainingSets[set][i];
                    }
                    // Copy the targets into the target array
                    for (int i = 0; i < OUTPUT_NODES; i++)
                    {
                        target[i] = targetSet[set][i];
                    }
                    // Reset the bias (they shouldn't change anyway)
                    inputs[INPUT_NODES] = 1.0f;
                    hidden[HIDDEN_NODES] = 1.0f;
                }

                // Calculate the hidden and output layer from the input
                calculateHiddenLayer();
                calculateOutputLayer();

                // Calculate the sum of squared errors
                for (int o = 0; o < OUTPUT_NODES; o++)
                {
                    sqError += Math.pow((outputs[o] - target[o]), 2.0);
                }

                // Calculate the error
                calculateOutputError();
                calculateHiddenError();

                // Update the weights based on error
                updateOutputWeights();
                updateHiddenWeights();
            }

            epoch++;

            // Determine whether we have met our target error
            if((sqError < minError && epoch >= 1000) || epoch > MAX_EPOCH)
                training = false;
        }

        // Print training info
        if (epoch <= MAX_EPOCH)
            System.out.println("MLP finished training in " + epoch + " epochs.");
        else
            System.out.println("MLP didn't not finish training within " + MAX_EPOCH + " epochs.");
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
     * Calculate the actual output at each hidden layer node
     */
    private void calculateHiddenLayer()
    {
        // Hidden layers are in index 0 and 1
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
            oError[k] = outputs[k] * (1 - outputs[k]) * (target[k] - outputs[k]);
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
     * Sigmoid activation function
     * @param value the value to pass through the activation function.
     * @return the value after being passed through the activation function.
     */
    private float activationFunction(float value)
    {
        return 1 / (1 + (float)Math.exp(-value));
    }

    /**
     * Returns 0.0f if the value is less than 0.5, 1.0f otherwise.
     *
     * Applies the value to a basic step function with a threshold of 0.5.
     *
     * @param value the value to pass through the step function.
     * @return the value after it has been processed by the step function.
     */
    private float stepFunction(float value)
    {
        return stepFunction(value, 0.5f);
    }

    /**
     * Returns 0.0f if the value is less than the <code>threshold</code>, 1.0f otherwise.
     *
     * Applies the value to a basic step function with a threshold of 0.5.
     *
     * @param value the value to pass through the step function.
     * @param threshold the threshold used by the step function.
     * @return the value after it has been processed by the step function.
     */
    private float stepFunction(float value, float threshold)
    {
        return (value < threshold ? 0.0f : 1.0f);
    }
}