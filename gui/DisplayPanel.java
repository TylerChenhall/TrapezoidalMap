package gui;

import data.Point;
import data.SearchStructure;
import data.Segment;
import data.Trapezoid;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 * This class describes the display panel for the project The GUI displays all
 * segments, the mouse location, and highlighted regions
 *
 * @author Tyler Chenhall
 */
public class DisplayPanel extends JPanel implements Runnable {

    private static final int DELAY = 25;//(25) pause between repaints
    private int xval;
    private int yval;
    private Thread t;
    private Segment[] segments = null;
    private Trapezoid highlighted;
    private SearchStructure search;

    /**
     * Initialize the DisplayPanel with a list of segments to be used for this
     * run. Also sets up a basic mouse listener which responds to mouse press &
     * move.
     * @param segs The segment array to use
     * @param s The search structure corresponding to the given segment array
     */
    public DisplayPanel(Segment[] segs, SearchStructure s) {
        segments = segs;
        search = s;
        highlighted = null;
        setBackground(Color.gray);
        setDoubleBuffered(true);
        MouseAdapter m = new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                xval = e.getX();
                yval = e.getY();
                //System.out.println("mouse moved");
            }

            @Override
            public void mousePressed(MouseEvent e) {
                xval = e.getX();
                yval = e.getY();
                //System.out.println("mouse pressed");
            }
        };
        addMouseListener(m);
        addMouseMotionListener(m);
    }

    /**
     * Set the currently highlighted trapezoid. (never used)
     * @param t The trapezoid to highlight
     */
    public void setTrapezoid(Trapezoid t) {
        highlighted = t;
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

    public void cycle() {
        //do any necessary update calculations between display stuff
        //there may not be anything here
        highlighted = search.findPointTrap(new Point(xval, yval));
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = (Graphics2D) g;


        //display all the lines & the point of interest
        g2d.setColor(Color.black);
        g2d.setStroke(new BasicStroke((float) 5.0));
        if (segments != null) {
            for (int i = 0; i < segments.length; i++) {
                if (segments[i] != null) {
                    g2d.draw(segments[i].getline());
                }
            }
        }

        //display the highlighted trapezoid

        //g2d.draw(p);
        //g2d.setPaint(Color.blue);
        //g2d.fill(p);
        if (highlighted != null) {
            g2d.setStroke(new BasicStroke((float) 4.0));
            //Segment[] trap;
            //g2d.setColor(Color.white);
            //Polygon p = highlighted.getBoundaryPolygon();
            //g2d.draw(p);
            g2d.setPaint(Color.blue);
            //g2d.fill(p);
            //displayLeft(g2d, highlighted);
            //displayRight(g2d, highlighted);
            ArrayList<Polygon> pp = new ArrayList<Polygon>();

            displayAll(g2d, highlighted, pp, 0);

            /*
             * Neighbor Display Code below
             trap = highlighted.getBoundary();
             g2d.draw(trap[0].getline());
             g2d.draw(trap[1].getline());
             g2d.draw(trap[2].getline());
             g2d.draw(trap[3].getline());
             //System.out.println(trap[0] + " to " + trap[2]);
             g2d.setStroke(new BasicStroke((float) 2.0));
             g2d.setColor(Color.red);
             if (highlighted.getLowerLeftNeighbor() != null) {
             trap = highlighted.getLowerLeftNeighbor().getBoundary();
             g2d.draw(trap[0].getline());
             g2d.draw(trap[1].getline());
             g2d.draw(trap[2].getline());
             g2d.draw(trap[3].getline());
             }
             if (highlighted.getLowerRightNeighbor() != null) {
             trap = highlighted.getLowerRightNeighbor().getBoundary();
             g2d.draw(trap[0].getline());
             g2d.draw(trap[1].getline());
             g2d.draw(trap[2].getline());
             g2d.draw(trap[3].getline());
             }
             if (highlighted.getUpperLeftNeighbor() != null) {
             trap = highlighted.getUpperLeftNeighbor().getBoundary();
             g2d.draw(trap[0].getline());
             g2d.draw(trap[1].getline());
             g2d.draw(trap[2].getline());
             g2d.draw(trap[3].getline());
             }
             if (highlighted.getUpperRightNeighbor() != null) {
             trap = highlighted.getUpperRightNeighbor().getBoundary();
             g2d.draw(trap[0].getline());
             g2d.draw(trap[1].getline());
             g2d.draw(trap[2].getline());
             g2d.draw(trap[3].getline());
             }*/


        }

        //display the mouse pointer
        g2d.setColor(Color.green);
        g2d.setStroke(new BasicStroke((float) 5.0));
        g2d.draw(new Ellipse2D.Double(xval, yval, 4, 4));

        Toolkit.getDefaultToolkit().sync();
        g.dispose();
    }

    /**
     * Should display all trapezoids recursively.
     *
     * @param g2d
     * @param t
     * @param pp
     */
    public void displayAll(Graphics2D g2d, Trapezoid t, ArrayList<Polygon> pp, int i) {
        if (i >= 10 || t == null) {
            return;
        }
        //i++; //was necessary before I dealt with the fact that Polygon does not have a proper .equals method
        Polygon p = t.getBoundaryPolygon();
        if (!pp.contains(p)) {
            pp.add(p);
            g2d.fill(p);
            displayAll(g2d, t.getLowerLeftNeighbor(), pp, i);
            displayAll(g2d, t.getLowerRightNeighbor(), pp, i);
            displayAll(g2d, t.getUpperLeftNeighbor(), pp, i);
            displayAll(g2d, t.getUpperRightNeighbor(), pp, i);
        }
    }

    /**
     * Recursively fills the left neighbors of the given trapezoid in the
     * polygon. (old)
     *
     * @param g2d
     * @param t
     */
    public void displayLeft(Graphics2D g2d, Trapezoid t) {
        Trapezoid ll = t.getLowerLeftNeighbor();
        Trapezoid ul = t.getUpperLeftNeighbor();
        if (ll != null) {
            g2d.fill(ll.getBoundaryPolygon());
            displayLeft(g2d, ll);
        }
        if (ul != null) {
            g2d.fill(ul.getBoundaryPolygon());
            displayLeft(g2d, ul);
        }
    }

    /**
     * Recursively fills the right neighbors of the given trapezoid in the
     * polygon. (old)
     *
     * @param g2d
     * @param t
     */
    public void displayRight(Graphics2D g2d, Trapezoid t) {
        Trapezoid lr = t.getLowerRightNeighbor();
        Trapezoid ur = t.getUpperRightNeighbor();
        if (lr != null) {
            g2d.fill(lr.getBoundaryPolygon());
            displayRight(g2d, lr);
        }
        if (ur != null) {
            g2d.fill(ur.getBoundaryPolygon());
            displayRight(g2d, ur);
        }
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
