//import java.util.Vector;
//
///**
// * Hopfield Neural Network
// *
// * @author Jayden Grant
// * @date 30-Aug-18
// */
//public class HNN
//{
//    // The maximum number or epochs before testing will halt
//    private final int MAX_EPOCH = 100;
//
//    // Length of a pattern
//    private int size;
//
//    private int[] currentNodes;         /// The current iterations node values
//    private int[] previousNodes;        /// The previous iterations node values
//    private float[][] connectionWeights;/// The weights between all nodes
//
//    private PatternLoader trainingSet;  /// All training data
//    private PatternLoader testingSet;   /// All testing data
//
//    public HNN()
//    {
//        this("patterns/HNN/testing.txt", "patterns/HNN/training.txt");
//    }
//
//    public HNN(String testSetFile, String trainSetFile)
//    {
//        // Loads training and testing data
//        trainingSet = new PatternLoader(trainSetFile);
//        testingSet = new PatternLoader(testSetFile);
//
//        size = trainingSet.getPatternSize();
//
//        // Java will initialize all values to 0.
//        currentNodes = new int[size];
//        previousNodes = new int[size];
//        connectionWeights = new float[size][size];
//    }
//
//    /**
//     * Train the HNN, with currently loaded training patterns.
//     */
//    public void train()
//    {
//        // The weight ij, is equal to the sum of each training patterns
//        //      i and j multiplied together.
//
//        // weight ij = sum of each training patterns( i*j )
//
//        // For each training pattern
//        for (int p = 0; p < trainingSet.getNumPatterns(); p++)
//        {
//            loadPattern(trainingSet, p);
//
//            // For each weight ij
//            for (int i = 0; i < size; i++)
//            {
//                for (int j = 0; j < size; j++)
//                {
//                    // i =/= j
//                    if (i != j)
//                    {
//                        // add this patterns i*j to the total weight ij
//                        addWeight(i, j, currentNodes[i] * currentNodes[j]);
//                    }
//                }
//            }
//        }
//    }
//
//    /**
//     * Testing the HNN, with currently loaded testing patterns.
//     */
//    public void test()
//    {
//        for (int p = 0; p < testingSet.getNumPatterns(); p++)
//        {
//            // Loading and initialization for testing
//            loadPattern(testingSet, p);
//            copyCurrentNode(); // Set input/current and prev nodes to be equal
//
//            System.out.print("Pattern \"");
//            printCurrentNodes();
//
//            boolean finished = false;
//            int epoch = 0;
//            int count;
//
//            // Repeat until stable
//            while(!finished)
//            {
//                count = 0;
//                epoch++;
//
////                System.out.println("Pattern " + p + " epoch " + epoch + ".");
////                printCurrentNodes();
//
//                // Eq 16 Calculate node value at (t+1) for all nodes
//                for (int i = 0; i < size; i++)
//                {
//                    // Eq 16 Weighted sum of all other nodes
//                    for (int j = 0; j < size; j++)
//                    {
//                        // Eq 16 all OTHER nodes
//                        if (i != j)
//                        {
//                            // Eq 16 Add to sum: (weight between node i and node j) * (node j at t)
//                            // WEIGHTED SUM = (weight ij) * (node j at t)
//                            currentNodes[i] += (connectionWeights[i][j] * previousNodes[j]);
//                        }
//                    }
//
//                    // Apply step function to the WEIGHTED SUM and then apply value
//                    if (currentNodes[i] < 0)
//                        currentNodes[i] = -1;
//                    else if (currentNodes[i] > 0)
//                        currentNodes[i] = 1;
//
//                    // increment count if this node is not stable
//                    if (currentNodes[i] != previousNodes[i])
//                        count++;
//                }
//
//                // Save the values of this iteration so they are available in the next.
//                copyCurrentNode();
//
//                // Count only equals 0 when the network is stable e.g. P = P+1 / this and the
//                //      last iteration match
//                if (count == 0)
//                    finished = true;
//                else if (epoch > MAX_EPOCH)
//                    break;
//            } // while !finished
//
//            if (epoch > MAX_EPOCH)
//            {
//                System.out.println(" didn't not resolve in " + MAX_EPOCH + " epochs.");
//            }
//            else
//            {
//                System.out.print("\"resolved into pattern \"");
//                printCurrentNodes();
//                System.out.println("\" in " + epoch + " epochs.");
//            }
//        }
//    }
//
//    /**
//     * Print the weight values of the network
//     */
//    public void printWeights()
//    {
//        for(int j = 0; j < size; j++)
//        {
//            for(int i = 0; i < size; i++)
//            {
//                System.out.printf("%2.2f  ", connectionWeights[i][j]);
//            }
//            System.out.println();
//        }
//    }
//
//    // =================== Private internal methods ================
//
//    private void printCurrentNodes()
//    {
//        for (int i = 0; i < size; i++)
//        {
//            System.out.print(currentNodes[i] + " ");
//        }
//    }
//
//    /** Adds the weight to the total weight stored at ij.
//     *
//     * Ensures weights[i][j] == weights[j][i]
//     *
//     * @param weight the weight to add to the total.
//     */
//    private void addWeight(int i, int j, float weight)
//    {
//        connectionWeights[i][j] += weight;
//        connectionWeights[j][i] += weight;
//    }
//
//    private void loadPattern(PatternLoader patternSet, int index)
//    {
//        Vector<Integer> set;
//
//        if(index < patternSet.getNumPatterns())
//        {
//            set = patternSet.getPattern(index);
//            if (set.size() == size)
//            {
//                for (int i = 0; i < size; i++)
//                {
//                    currentNodes[i] = set.elementAt(i);
//                }
//            }
//            else
//                System.err.println("Set size mismatch!");
//        }
//    }
//
//    private void copyCurrentNode()
//    {
//        for (int i = 0; i < size; i++)
//        {
//            previousNodes[i] = currentNodes[i];
//        }
//    }
//
//
//
//}
