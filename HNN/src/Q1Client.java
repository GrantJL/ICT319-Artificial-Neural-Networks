import javax.swing.*;

/**
 * @author Jayden Grant
 * @date 30-Aug-18
 */
public class Q1Client
{
    static HNN hnn = new HNN();
    public static void main(String[] args)
    {
        hnn.train();
        hnn.printIdent();
        JFileChooser jfile = new JFileChooser("patterns/");
        int a = jfile.showOpenDialog(null);
        if(a == JFileChooser.APPROVE_OPTION)
        {
            System.out.println(jfile.getSelectedFile().toString());
        }
    }


}
