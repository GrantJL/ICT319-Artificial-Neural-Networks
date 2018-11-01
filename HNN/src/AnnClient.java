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
        MLP mlp = new MLP(45, 15, 5);

        long loadTime = System.currentTimeMillis();
        float[][] train =       load("patterns/MLP/characters/trainAlpha.txt", 46, 45);
        float[][] trainTarget = load("patterns/MLP/characters/trainAlphaTargets.txt", 46, 5);
        float[][] test =        load("patterns/MLP/characters/testAlpha.txt", 130, 45);
        float[][] testTarget =  load("patterns/MLP/characters/testAlphaTargets.txt", 130, 5);
        loadTime = System.currentTimeMillis() - loadTime;

        // train the mlp
        long trainTime = System.currentTimeMillis();
        mlp.train(train, trainTarget);
        trainTime = System.currentTimeMillis() - trainTime;

        // test the mlp
        float[][] results = mlp.test(test);

        // check the results of the test
        int errors = 0;
        System.out.println("       T R");
        for(int i = 0; i < results.length; i++)
        {
            char target = decodeVowel(testTarget[i]);
            char result = decodeVowel(results[i]);
            if ( target != result )
            {
                errors++;
                System.out.println("error: " + target + " " + result);
            }
        }

        if (errors == 0)
        {
            System.out.println("All tests successful!");
        }
        else
        {
            System.out.println(errors + " out of " + results.length + " tests failed");
        }

        System.out.println();
        System.out.println("Load time: " + loadTime + "ms");
        System.out.println("Train time: " + trainTime + "ms");
        System.out.println();

        printStudentDetails();
        Thread.sleep(300);
    }

    private static char decodeVowel(float[] vowelRep)
    {
        if (vowelRep.length == 5)
        {
            if (vowelRep[0] == 1.0f)
                return 'A';
            else if (vowelRep[1] == 1.0f)
                return 'E';
            else if (vowelRep[2] == 1.0f)
                return 'I';
            else if (vowelRep[3] == 1.0f)
                return 'O';
            else if (vowelRep[4] == 1.0f)
                return 'U';
            else
                return 'C';
        }
        else
        {
            return '.';
        }
    }

    /**
     * Provide formatted output of the test inputs which did not match their target.
     */
    private static void createErrorReport()
    {/*Maybe pass in array of inputs and their targets (maybe not, should be able to distinguish the inputs*/
        // Format input (9x5)
        // Provide target (A E I O U C)
        // save to a file  (save dialog?)
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

    private static void printStudentDetails()
    {
        System.out.println(" - Student Details - ");
        System.out.println("Name:    Jayden Grant");
        System.out.println("Number:  32670206");
    }

}
