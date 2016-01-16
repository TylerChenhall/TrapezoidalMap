package gui;

import data.Point;
import data.Segment;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * This class describes the display panel for the project The GUI displays all
 * segments, the mouse location, and highlighted regions
 *
 * @author Tyler Chenhall
 */
public class DrawSegments extends JPanel implements Runnable {

    private static final int DELAY = 25;//(25) pause between repaints
    private Point lastOrigin;
    private Point lastPoint;
    //coordinates of the last mouse Press event
    private int xval;
    private int yval;
    //records type of mouse event
    private boolean shiftPressed;
    private boolean leftClick;
    private boolean newClick;
    //click point color
    private Color cc = Color.green;
    private Thread t;
    private ArrayList<Segment> segments;

    /**
     * Initialize the DisplayPanel with a list of segments to be used for this
     * run. Also sets up a basic mouse listener which responds to mouse press &
     * move.
     *
     * @param segs The segment array to use
     * @param s The search structure corresponding to the given segment array
     */
    public DrawSegments() {
        lastOrigin = null;
        lastPoint = null;

        this.newClick = false;
        segments = new ArrayList<Segment>();
        setBackground(Color.gray);
        setDoubleBuffered(true);
        MouseAdapter m = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                /*
                 * 1) Record coordinates
                 * 2) Store left or right click & any modifiers
                 * 3) Turn the click into a new segment
                 * Since we want to keep mousePress responses fast, we leave the actual segment creation until later
                 * 
                 * Right click: merge to the last origin point
                 * Left click: continue from the last point
                 */

                // (1)
                xval = e.getX();
                yval = e.getY();
                // (2)
                //Use this technique to avoid any potential ambiguities in the click type
                if (SwingUtilities.isLeftMouseButton(e)) {
                    leftClick = true;
                } else {
                    leftClick = false;
                }
                shiftPressed = e.isShiftDown();
                newClick = true;
            }
        };

        addMouseListener(m);
        addMouseMotionListener(m);
    }

    /**
     * Get the array of segments from this panel.
     *
     * @return The segment array
     */
    public Segment[] getSegments() {
        Segment[] arr = new Segment[segments.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = segments.get(i);
        }
        return arr;
    }

    /*
     * Methods for painting, etc. are contained below
     */
    @Override
    public void addNotify() {
        super.addNotify();
        t = new Thread(this);
        t.start();
    }

    /**
     * Creates the new segments / points if there is a new click
     */
    public void cycle() {
        if (newClick) {
            //left click = add a new point and connect to the last point with a segment if it exists
            if (leftClick && !shiftPressed) {
                //left
                if (lastOrigin == null) {
                    //if we have no segments, make a new origin point and last point but no segment
                    lastOrigin = new Point(xval, yval);
                    lastPoint = new Point(xval, yval);
                } else {
                    //else create a segment using the last point
                    Segment ns = new Segment(lastPoint, new Point(xval, yval));
                    //check for intersections
                    if (intersects(ns)) {
                        cc = Color.red;
                    } else {
                        cc = Color.green;
                        segments.add(ns);
                        lastPoint = new Point(xval, yval);
                    }
                }

            } else if (leftClick && shiftPressed) {
                //left + shift
                //creates a new origin point to start a new figure but does not close the old one
                //no new segment is created
                lastOrigin = new Point(xval, yval);
                lastPoint = new Point(xval, yval);
                cc = Color.green;
            } else if (!leftClick && !shiftPressed) {
                //right
                //close the current shape
                Segment ns = new Segment(lastPoint, lastOrigin);
                //check for intersections
                if (intersects(ns)) {
                    cc = Color.red;
                } else {
                    cc = Color.green;
                    segments.add(ns);
                    //set the last point to the same as the last origin
                    lastPoint = lastOrigin;
                }
            }
        }
        newClick = false;
    }

    /**
     * Checks to see if the new segment intersects properly with any of the
     * current segments in the ArrayList.
     *
     * @TODO
     * @param ns The segment to check
     * @return True if the new segment intersects with something in the
     * ArrayList of current segments
     */
    private boolean intersects(Segment ns) {
        int j = segments.size();
        for (int i = 0; i < j; i++) {
            if (segments.get(i).crosses(ns)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;

        //draw the segment list so far
        g2d.setColor(Color.black);
        g2d.setStroke(new BasicStroke((float) 3.0));
        int j = segments.size();
        for (int i = 0; i < j; i++) {
            g2d.draw(segments.get(i).getline());
        }


        //display the mouse pointer
        g2d.setColor(cc);
        g2d.setStroke(new BasicStroke((float) 5.0));
        g2d.draw(new Ellipse2D.Double(xval, yval, 4, 4));

        Toolkit.getDefaultToolkit().sync();
        g.dispose();
    }

    /**
     * Calls repaint to redraw the images on the panel, and then sleeps the
     * thread again
     */
    @Override
    public void run() {
        long before, diff, sleep;

        while (true) {//do until the program is done
            before = System.currentTimeMillis();
            cycle();
            repaint();
            diff = System.currentTimeMillis() - before;
            sleep = DELAY - diff;
            if (sleep < 0) {
                sleep = 2;
            }
            try {
                Thread.sleep(sleep);
            } catch (InterruptedException ex) {
                System.err.println("Interrupted repaint thread");
            }

        }
    }
}
