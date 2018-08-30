import java.util.Vector;

/**
 * @author Jayden Grant
 * @date 30-Aug-18
 */
public class HNN
{
    private int size;

    private int[] currentNodes;
    private int[] previousNodes;
    private float[][] connectionWeights;

    private PatternLoader trainingSet;
    private PatternLoader testingSet;

    public HNN()
    {
        this("patterns/testing.txt", "patterns/training.txt");
    }

    public HNN(String testSetFile, String trainSetFile)
    {
        trainingSet = new PatternLoader(trainSetFile);
        testingSet = new PatternLoader(testSetFile);

        size = trainingSet.getPatternSize();

        currentNodes = new int[size];
        previousNodes = new int[size];
        connectionWeights = new float[size][size];
    }

    private void initializeWeights()
    {
        for(int i = 0; i < size; i++)
        {
            for(int j = 0; j < size; j++)
            {
                    connectionWeights[i][j] = 0;
            }
        }
    }

    public void printWeights()
    {
        for(int j = 0; j < size; j++)
        {
            for(int i = 0; i < size; i++)
            {
                System.out.printf("%2.2f  ", connectionWeights[i][j]);
            }
            System.out.println();
        }
    }

    public void printIdent()
    {
        for(int j = 0; j < size; j++)
        {
                System.out.printf("%2.2f  ", connectionWeights[j][j]);
        }
        System.out.println();

    }

    private void addWeight(int i, int j, float weight)
    {
        connectionWeights[i][j] += weight;
        connectionWeights[j][i] += weight;
    }

    public void train()
    {
        for (int p = 0; p < trainingSet.getNumPatterns(); p++)
        {
            loadPattern(trainingSet, p);

            for (int i = 0; i < size; i++)
            {
                for (int j = 0; j < size; j++)
                {
                    if (i != j)
                    {
                        addWeight(i, j, currentNodes[i] * currentNodes[j]);
                    }
                }
            }
        }
    }

    public void test()
    {
        int finished = 1;
        int count = 0;
        int epoch = 0;

        for (int p = 0; p < testingSet.getNumPatterns(); p++)
        {
            loadPattern(testingSet, p);
            copyCurrentNode();

            while(finished > 0)
            {
                count = 0;
                epoch++;

                for (int i = 0; i < size; i++)
                {
                    for (int j = 0; j < size; j++)
                    {
                        if (i != j)
                        {

                        }
                    }
                }
            }
        }
    }

    void loadPattern(PatternLoader patternSet, int index)
    {
        Vector<Integer> set;

        if(index < patternSet.getNumPatterns())
        {
            set = patternSet.getPattern(index);
            if (set.size() == size)
            {
                for (int i = 0; i < size; i++)
                {
                    currentNodes[i] = set.elementAt(i);
                }
            }
            else
                System.err.println("Set size mismatch!");
        }
    }

    private void copyCurrentNode()
    {
        for (int i = 0; i < size; i++)
        {
            previousNodes[i] = currentNodes[i];
        }
    }



}
