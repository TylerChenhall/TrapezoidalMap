package pointsearch;

import data.Point;
import data.SearchStructure;
import data.Segment;
import gui.DisplayPanel;
import gui.DrawSegments;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import javax.swing.JFrame;

/**
 * @author Tyler Chenhall
 */
public class PointSearch {

    /**
     * @param args the command line arguments
     */
    @SuppressWarnings("empty-statement")
    public static void main(String[] args) {
        boolean timerMode = false;
        if (timerMode) {
            //do some timing calculation stuff instead of a GUI mode
        } else if (args.length == 0) {
            System.out.println("Welcome to the Trapezoidal Map Demo");
            System.out.println("-----------------------------------------------");
            System.out.println("To create a trapezoidal map from an input file,");
            System.out.println("restart using a file path argument.");
            System.out.println("");
            System.out.println("To create segments graphically for a trapezoidal map,");
            System.out.println("enter \"b\", followed by a valid file path for ");
            System.out.println("storing the new segment data file.");
            System.out.println("Note that for safety purposes, this program will not");
            System.out.println("override old data files.");
            System.out.println("");
            System.out.println("To use the segment array builder, there are three commands:");
            System.out.println("Left click: Add a segment from the previous point to the click point");
            System.out.println("Left + hold Shift: Start a new point, but do not create a segment");
            System.out.println("Right click: Close the current figure");
            System.out.println("Crossing segments are automatically prevented (red cursor)");
            System.out.println("Close when finished");
                    
        } else if (args.length == 2 && args[0].equalsIgnoreCase("b")) {//draw segments as an input
            final JFrame f = new JFrame();
            DrawSegments ds = new DrawSegments();
            f.add(ds);

            //mimicked the code from:
            //http://stackoverflow.com/questions/9093448/do-something-when-the-close-button-is-clicked-on-a-jframe
            //except I am only making the frame invisible
            f.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    f.setVisible(false);
                }
            });
            //f.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

            f.setSize(1200, 820);
            f.setLocationRelativeTo(null);
            f.setTitle("Computation Geometry - Segment Array Builder");
            f.setVisible(true);
            f.setResizable(false);

            //java keeps ignoring this loop if it is empty so we'll just sleep for a bit
            while (f.isVisible()) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    System.err.println("Sleep error in PointSearch Class");
                }
            }
            //get the segments once the panel has completed
            Segment[] arr = ds.getSegments();
            System.out.println("got the segments! " + arr.length);
            storeLines(args[1], arr);
            SearchStructure ss = new SearchStructure(arr, 0, 1200, 0, 800);

            JFrame f2 = new JFrame();
            //send the list to the DisplayPanel
            DisplayPanel dp = new DisplayPanel(arr, ss);
            f2.add(dp);
            f2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            //set the size of the window in pixels
            //set the window location
            //set visible
            //set resizable

            //maybe do something else later, but for now:
            f2.setSize(1200, 820);
            f2.setLocationRelativeTo(null);
            f2.setTitle("Computation Geometry - Point Location");
            f2.setVisible(true);
            f2.setResizable(false);
        } else {//use the data file at args[0]
            //String filename = "/home/tyler/Documents/demo.txt";
            //get the list of segments
            Segment[] arr = getLines(args[0]);
            //construct the trapezoidal map search structure
            SearchStructure ss = new SearchStructure(arr, 0, 1200, 0, 800);

            //This is the GUI version of the code with segment input file
            JFrame f = new JFrame();
            //send the list to the DisplayPanel
            DisplayPanel dp = new DisplayPanel(arr, ss);
            f.add(dp);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            //set the size of the window in pixels
            //set the window location
            //set visible
            //set resizable

            //maybe do something else later, but for now:
            f.setSize(1200, 820);
            f.setLocationRelativeTo(null);
            f.setTitle("Computation Geometry - Point Location");
            f.setVisible(true);
            f.setResizable(false);
        }
    }

    /**
     * Reads in a series of lines from a basic file Right now there is not much
     * input validation going on, and this would probably be a good thing to
     * add. File format: #segments x1 y1 x2 y2 ...
     *
     * @param s The file path for the list of segments
     * @return
     */
    private static Segment[] getLines(String s) {
        Segment[] arr = null;
        File f = new File(s);
        try {
            Scanner scan = new Scanner(f);
            if (scan.hasNextInt()) {
                int len = scan.nextInt();//find out how many lines are in the file
                arr = new Segment[len];
                for (int i = 0; i < len && scan.hasNextInt(); i++) {//read in each new line
                    arr[i] = new Segment(new Point(scan.nextInt(), scan.nextInt()), new Point(scan.nextInt(), scan.nextInt()));
                }
            }
        } catch (FileNotFoundException ex) {
            System.err.println("Unable to load segment file");
        }
        return arr;
    }
    
    /**
     * Store the line segments as an output file at the given file path
     * @param s The file path to use
     * @param arr The segment array to store
     * @return True if the file is successfully written
     */
    private static boolean storeLines(String s, Segment[] arr) {
        File f = new File(s);
        if (f.exists()) {
            System.out.println("File already exists.  Could not create new output file");
        } else {
            try {
                PrintWriter out = new PrintWriter(f);
                out.println("" + arr.length);
                for(int i = 0; i < arr.length; i++) {
                    out.println(arr[i].toString());
                }
                out.close();
                return true;
            } catch (FileNotFoundException ex) {
                System.out.println("Error writing to file.  File not written");
            }
        }
        return false;
    }
}
