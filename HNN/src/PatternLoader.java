import com.sun.org.apache.xpath.internal.SourceTree;

import java.io.*;
import java.util.Vector;
import java.util.Scanner;
import java.io.*;

/**
 * @author Jayden Grant
 * @date 30-Aug-18
 */
public class PatternLoader
{
    private Vector<Vector<Integer>> patterns = new Vector<>();
    private BufferedReader in;
    private int size = 0;
    private int pats = 0;

    PatternLoader(String file)
    {
        try {
            in = new BufferedReader(new FileReader(file));
            String line;

            while ((line = in.readLine()) != null)
            {
                Vector<Integer> p = new Vector<>();
                try{
                    Scanner sc = new Scanner(line);
                    while(sc.hasNextInt())
                        p.add(sc.nextInt());
                    patterns.add(p);
                    size = p.size();
                    sc.close();
                }
                catch (Exception ex)
                {
                    System.out.println(ex.toString());
                }
                pats = patterns.size();
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
    }

    public int getNumPatterns()
    {
        return pats;
    }

    public int getPatternSize()
    {
        return size;
    }

    public Vector<Integer> getPattern(int index)
    {
        if (index < pats)
            return patterns.elementAt(index);
        else
            return null;
    }
}
