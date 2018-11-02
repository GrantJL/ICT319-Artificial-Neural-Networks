import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
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
    public static void main(String[] args) throws InterruptedException
    {
        // 45 input, 10 hidden, 5 output nodes
        MLP mlp = new MLP(45, 15, 5);

        // Load the data sets
        long loadTime = System.currentTimeMillis();
        float[][] train =       loadCharMatrix("patterns/trainAlpha.txt", 46, 45, 5, 9, true);
        float[][] trainTarget = load("patterns/trainAlphaTargets.txt", 46, 5);
        float[][] test =        loadCharMatrix("patterns/testAlpha.txt", 130, 45, 5, 9, true);
        float[][] testTarget =  load("patterns/testAlphaTargets.txt", 130, 5);
        loadTime = System.currentTimeMillis() - loadTime;

        // train the mlp
        long trainTime = System.currentTimeMillis();
        mlp.train(train, trainTarget);
        trainTime = System.currentTimeMillis() - trainTime;

        // test the mlp
        // We do not pass the MLP the targets/expected results for the test set
        float[][] results = mlp.test(test);

        // check the results of the test
        int errors = 0;
        ArrayList<Integer> errorIndices = new ArrayList<>();
        for(int i = 0; i < results.length; i++)
        {
            char target = decodeVowel(testTarget[i]);
            char result = decodeVowel(results[i]);
            if ( target != result )
            {
                errors++;
                errorIndices.add(new Integer(i));
            }
        }

        if (errors == 0)
        {
            System.out.println("All tests successful!");
        }
        else
        {
            System.out.println(errors + " out of " + results.length + " tests failed");
            createErrorReport(errorIndices, test, testTarget, results);
        }

        System.out.println();
        System.out.println("Load time: " + loadTime + "ms");
        System.out.println("Train time: " + trainTime + "ms");
        System.out.println();

        printStudentDetails();
    }

    /**
     * Simplify the output of the MLP to a single char. (Vowel problem)
     * @param vowelRep Array represent a vowel
     * @return char representing a vowel (or 'C' for consonants).
     */
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
     *
     * @param indices A list of indices which failed the test.
     * @param data The original data passed into the MLP for testing.
     * @param results The results produced by the MLP.
     * @param targets The expected results.
     */
    private static void createErrorReport(ArrayList<Integer> indices, float[][] data, float[][] targets, float[][] results)
    {
        JFileChooser fileDialog = new JFileChooser(System.getProperty("user.dir"));
        fileDialog.setDialogTitle("Save Error Report");
        fileDialog.setSelectedFile(new File(System.getProperty("user.dir") + "\\Error Report.txt"));
        int status = fileDialog.showSaveDialog(null);

        if (status == JFileChooser.APPROVE_OPTION)
        {
            File file = fileDialog.getSelectedFile();
            BufferedWriter out = null;

            try
            {
                out = new BufferedWriter(new FileWriter(file));
                out.write("Errors: " + indices.size() + " out of " + data.length
                        + " (" + Math.round(indices.size() * 100 / (float)data.length) + "%)"
                        + System.lineSeparator());

                for (Integer index : indices)
                {
                    String line = "";
                    float[] input = data[index];
                    char target = decodeVowel(targets[index]);
                    char result = decodeVowel(results[index]);

                    line += "Target: " + target + System.lineSeparator();
                    line += "Result: " + result + System.lineSeparator();
                    for (int r = 0; r < 5; r++)
                    {
                        for (int c = 0; c < 9; c++)
                        {
                            line += (input[c+(r*9)] < 0.5f ? "0 " : "1 ");
                        }
                        line += System.lineSeparator();
                    }
                    line += System.lineSeparator();
                    out.write(line);
                }
                System.out.println("Finished saving error data.");
            }
            catch (FileNotFoundException e)
            {
                System.err.println("File not saved, file not found!");
            }
            catch (IOException e)
            {
                System.err.println("File not saved!");
            }

            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
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

    /**
     * Loads the data from the file into a 2D float array.
     * Each set of data may be on multiple lines with a
     * space separating each individual data point.
     *
     * If the data contained within the file does not match the
     * parameters, this method may not function as expected.
     *
     * @param file The file to load formatted data from.
     * @param noSets The number of sets to load (the number of lines of data).
     * @param setSize The size of each set, how many data points on each line.
     * @param rows The number of lines each set of data is on.
     * @param cols The number of individual data points on each line.
     * @param gap Whether there is a blank new line between each data set.
     * @return A 2D array containing the loaded data. e.g. dataArray[dataSet][dataPoint]
     */
    private static float[][] loadCharMatrix(String file, int noSets, int setSize, int rows, int cols, boolean gap)
    {
        float[][] data = new float[noSets][setSize];

        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String line;

            for (int set = 0; set < noSets; set++)
            {
                float[] floats = new float[setSize];
                // For each row in a set
                for (int row = 0; row < rows; row++)
                {
                    if ( (line = in.readLine()) == null)
                        return data;

                    try{
                        Scanner sc = new Scanner(line);
                        // For each column add the value to the array
                        for (int f = 0; f < cols && (f+(row*cols) < setSize) && sc.hasNextFloat(); f++)
                        {
                            floats[f+(row*cols)] = sc.nextFloat();
                            //System.out.print((floats[f+(row*cols)] < 0.5f ? " " : "#"));
                        }

                        sc.close();
                    }
                    catch (Exception ex)
                    {
                        System.out.println(ex.toString());
                    }
                    System.out.println();
                }

                data[set] = floats;

                if (true) //
                    line = in.readLine();
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

    /**
     * Print the author's student details.
     */
    private static void printStudentDetails()
    {
        System.out.println(" - Student Details - ");
        System.out.println("Name:    Jayden Grant");
        System.out.println("Number:  32670206");
    }

}
