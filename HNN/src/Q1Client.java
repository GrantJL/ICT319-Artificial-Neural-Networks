import java.util.Scanner;

/**
 * @author Jayden Grant
 * @date 30-Aug-18
 */
public class Q1Client
{
//    static HNN hnn = new HNN();
    static MLP mlp = new MLP("patterns/MLP/XORtrain.txt",
        "patterns/MLP/result/XORtrain.txt",
        "patterns/MLP/result/XORtest.txt");

    public static void main(String[] args) throws InterruptedException
    {
        Scanner in = new Scanner(System.in);
        mlp.train();

        System.out.println("\n\nSelect Option:");
        System.out.println("  A: Demo Hopfield Neural Network");
        System.out.println("  B: Additional HNN testing\n");
        System.out.println("  C: Demo Multi-Layer Perceptron");
        System.out.println("  D: Additional MLP testing (All permutations with repeats)\n");
        System.out.println("  R: Retrain MLP (with new weights)");
        System.out.println("  Q: QUIT");
        String opt = in.nextLine().toLowerCase();

        while(opt.compareTo("q") != 0)
        {
            switch(opt)
            {
                case "a":
                    demoHNN();
                    break;
                case "b":
                    testHNN();
                    break;
                case "c":
                    demoMLP();
                    break;
                case "d":
                    testMLP();
                    break;
                case "r": // Reinitialize/train with random weights
                    mlp.train();
                    break;
            }

            System.out.println("\n\nSelect Option:");
            System.out.println("  A: Demo Hopfield Neural Network");
            System.out.println("  B: Demo Multi-Layer Perceptron\n");
            System.out.println("  C: Demo Multi-Layer Perceptron");
            System.out.println("  D: Additional MLP testing (All permutations with repeats)\n");
            System.out.println("  R: Retrain MLP (with new weights)");
            System.out.println("  Q: QUIT");
            opt = in.nextLine().toLowerCase();
        }
        printStudentDetails();
        Thread.sleep(300);
    }

    private static void demoHNN()
    {
        HNN hnn = new HNN("patterns/HNN/testing.txt", "patterns/HNN/training.txt");

        System.out.println("Training HNN");
        hnn.train();
        System.out.println("Testing HNN");
        hnn.test();
    }

    private static void testHNN()
    {
        HNN hnn = new HNN("patterns/HNN/test/testing.txt", "patterns/HNN/test/training.txt");

        System.out.println("Training HNN");
        hnn.train();
        System.out.println("Testing HNN");
        hnn.test();
    }

    private static void demoMLP()
    {
        System.out.println("Testing MLP");
        mlp.test(null);
    }

    private static void testMLP()
    {
        System.out.println("Testing MLP");
        mlp.test("patterns/MLP/8bitPermsR.txt");
    }

    private static void printStudentDetails()
    {
        System.out.println(" - Student Details - ");
        System.out.println("Name:    Jayden Grant");
        System.out.println("Number:  32670206");
    }

}
