package data;

import java.awt.Polygon;

/**
 * Represents a trapezoid object in the trapezoidal map or search structure.
 *
 * Trapezoid is now a proud independent node.
 *
 * @author Tyler Chenhall
 */
public final class Trapezoid {
    //neighbors of this trapezoid

    private Trapezoid uleft_neighbor;
    private Trapezoid lleft_neighbor;
    private Trapezoid uright_neighbor;
    private Trapezoid lright_neighbor;
    private Leaf owner;
    //variables describing the trapezoid shape
    private Point leftP;
    private Point rightP;
    private Segment topSeg;
    private Segment botSeg;
    private Polygon poly;

    /**
     * Constructs a trapezoid object based on the x boundaries and bounding
     * segments. Sets the neighbor trapezoids to null currently
     *
     * @param left Left x boundary
     * @param right Right x boundary
     * @param top Segment determining the upper boundary
     * @param bottom Segment determining the lower boundary
     */
    /* Old implementation
     * public Trapezoid(int left, int right, Segment top, Segment bottom) {
     //implement instantiation method
     //leftX = left;
     //rightX = right;
     topSeg = top;
     botSeg = bottom;
        
     uleft_neighbor = null;
     lleft_neighbor = null;
     uright_neighbor = null;
     lright_neighbor = null;
     }*/
    public Trapezoid(Point left, Point right, Segment top, Segment bottom) {
        leftP = left;
        rightP = right;
        topSeg = top;
        botSeg = bottom;

        uleft_neighbor = null;
        lleft_neighbor = null;
        uright_neighbor = null;
        lright_neighbor = null;
        owner = null;
        if (left != null && right != null) {//if one of the points is null, this is only a temporary trapezoid
            poly = this.getPrivateBoundaryPolygon(left, right, top, bottom);
        }
    }

    /**
     * Get the left bounding point
     * @return The left vertex
     */
    public Point getLeftBound() {
        return leftP;
    }

    /**
     * Get the right bounding point
     * @return The right bounding vertex
     */
    public Point getRightBound() {
        return rightP;
    }

    /**
     * Get the lower bounding segment
     * @return The lower segment
     */
    public Segment getLowerBound() {
        return botSeg;
    }

    /**
     * Get the upper bounding segment for the trapezoid
     * @return The upper segment 
     */
    public Segment getUpperBound() {
        return topSeg;
    }

    /**
     * Get the trapezoid which lies to the left of this trapezoid below the left boundary vertex
     * @return The lower left neighbor
     */
    public Trapezoid getLowerLeftNeighbor() {
        return lleft_neighbor;
    }

    /**
     * Get the trapezoid which lies to the left of this one, above the left boundary vertex
     * @return the upper left neighbor trapezoid
     */
    public Trapezoid getUpperLeftNeighbor() {
        return uleft_neighbor;
    }

    public Trapezoid getLowerRightNeighbor() {
        return lright_neighbor;
    }

    public Trapezoid getUpperRightNeighbor() {
        return uright_neighbor;
    }

    public void setLowerLeftNeighbor(Trapezoid t) {
        lleft_neighbor = t;
    }

    public void setUpperLeftNeighbor(Trapezoid t) {
        uleft_neighbor = t;
    }

    public void setLowerRightNeighbor(Trapezoid t) {
        lright_neighbor = t;
    }

    public void setUpperRightNeighbor(Trapezoid t) {
        uright_neighbor = t;
    }

    /**
     * Set the leaf which contains this trapezoid
     * @param l The leaf containing this trapezoid
     */
    public void setLeaf(Leaf l) {
        owner = l;
    }

    /**
     * Get the leaf containing this trapezoid
     * @return The leaf pointing to this trapezoid
     */
    public Leaf getLeaf() {
        return owner;
    }

    @Override
    /**
     * Two trapezoids are equal iff they have the same bounding segments
     */
    public boolean equals(Object t) {
        if (t == null || !(t instanceof Trapezoid)) {
            return false;
        }
        Trapezoid tt = (Trapezoid) t;
        return (this.topSeg == tt.topSeg) && (this.botSeg == tt.botSeg);
    }

    /**
     * Returns the boundary of this trapezoid as an array of 4 segments. (Old)
     *
     * @return The array of segments representing the boundary
     */
    public Segment[] getBoundary() {
        Point tl = topSeg.intersect(leftP.getX());
        Point tr = topSeg.intersect(rightP.getX());
        Point bl = botSeg.intersect(leftP.getX());
        Point br = botSeg.intersect(rightP.getX());
        Segment[] segs = {new Segment(tl, tr), new Segment(tr, br), new Segment(br, bl), new Segment(bl, tl)};

        //Line2D.Double[] arr = {topSeg.getline(), botSeg.getline()};
        return segs;
    }

    /**
     * Returns the boundary of the trapezoid as a Polygon object for easy
     * display.
     *
     * @return The polygon object representing the boundary of the Trapezoid
     */
    private Polygon getPrivateBoundaryPolygon(Point left, Point right, Segment top, Segment bottom) {
        Point tl = top.intersect(left.getX());
        Point tr = top.intersect(right.getX());
        Point bl = bottom.intersect(left.getX());
        Point br = bottom.intersect(right.getX());
        int[] xx = {tl.getX(), tr.getX(), br.getX(), bl.getX()};
        int[] yy = {tl.getY(), tr.getY(), br.getY(), bl.getY()};
        return new Polygon(xx, yy, 4);
    }

    /**
     * Return the boundary polygon for this trapezoid
     * @return The boundary Polygon
     */
    public Polygon getBoundaryPolygon() {
        return poly;
    }

    /**
     * Return true if this trapezoid has zero width
     * @return True if the trapezoid is a sliver with zero width
     */
    public boolean hasZeroWidth() {
        return leftP.equals(rightP);
        //return (leftP.getX()-rightP.getX() == 0);
    }
}
