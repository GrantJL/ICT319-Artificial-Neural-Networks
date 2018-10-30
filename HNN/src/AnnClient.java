import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/**
 * A Client to test and demonstrate the Artificial Neural Network (ANN) classes MLP and HNN.
 *
 * Tests and demonstrates the ANN classes created for the ICT319 Assignments.
 * @author Jayden Grant
 * @date 30-Aug-18
 */
public class AnnClient
{
    //static MLP mlp = new MLP(45, 15, 5);

    public static void main(String[] args) throws InterruptedException
    {
        MLP tets = new MLP(2, 2, 1);

        float[][] testIn = load("patterns/MLP/XORtrain.txt", 48, 2);
        float[][] testTarget = load("patterns/MLP/result/XORtrain.txt", 48, 1);
        float[][] testTest = load("patterns/MLP/XORtest.txt", 48, 2);
        float[][] testTestTarget = load("patterns/MLP/result/XORtest.txt", 48, 2);

        tets.train(testIn, testTarget);
        float[][] results = tets.test(testTest);

        boolean success = true;
        for(int i = 0; i < results.length; i++)
        {
            if ( Math.abs(testTestTarget[i][0] - results[i][0]) > 0.01 )
            {
                success = false;
                break;
            }
            System.out.println(testTestTarget[i][0] - results[i][0]);
        }

        if (success)
        {
            System.out.println("Test was successful!");
        }
        else
        {
            System.out.println("Test FAILED!");
        }

        printStudentDetails();
        Thread.sleep(300);
    }

    /**
     * Loads the data from the file into a 2D float array.
     * Each set of data is expected to be on a single line
     * with a space separating each individual data point.
     *
     * If the data contained within the file does not match the
     * parameters, this method may not function as expected.
     *
     * @param file The file to load formatted data from.
     * @param noSets The number of sets to load (the number of lines of data).
     * @param setSize The size of each set, how many data points on each line.
     * @return A 2D array containing the loaded data. e.g. dataArray[dataSet][dataPoint]
     */
    private static float[][] load(String file, int noSets, int setSize)
    {
        float[][] data = new float[noSets][setSize];

        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String line;

            for (int set = 0; set < noSets && (line = in.readLine()) != null; set++)
            {
                float[] floats = new float[setSize];
                try{
                    Scanner sc = new Scanner(line);
                    for (int f = 0; f < setSize && sc.hasNextFloat(); f++)
                        floats[f] = sc.nextFloat();
                    data[set] = floats;
                    sc.close();
                }
                catch (Exception ex)
                {
                    System.out.println(ex.toString());
                }
            }
            in.close();
        }
        catch (FileNotFoundException ex)
        {
            System.err.println("File not found!");
        }
        catch (IOException ex)
        {
            System.err.println("IO Error~");
        }

        return data;
    }

    private static void demoMLP()
    {
        //System.out.println("Testing MLP");
        //mlp.test(null);
    }

    private static void testMLP()
    {
        //System.out.println("Testing MLP");
        //mlp.test("patterns/MLP/8bitPermsR.txt");
    }

    private static void printStudentDetails()
    {
        System.out.println(" - Student Details - ");
        System.out.println("Name:    Jayden Grant");
        System.out.println("Number:  32670206");
    }

}
