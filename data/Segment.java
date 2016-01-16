package data;

import java.awt.geom.Line2D;

/**
 * Represents a segment by its endpoints. Endpoints are stored in order as given
 * by the compareTo function of the Point class.
 *
 * @author Tyler Chenhall
 */
public class Segment {

    private Point lpoint;
    private Point rpoint;
    private Line2D.Double l;//for display purposes

    public Segment(Point one, Point two) {
        //we store the left, lower point as lpoint
        //the other point is stored as rpoint
        if (one.compareTo(two) <= 0) {
            lpoint = one;
            rpoint = two;
        } else {
            lpoint = two;
            rpoint = one;
        }
        l = new Line2D.Double(lpoint.getX(), lpoint.getY(), rpoint.getX(), rpoint.getY());
    }

    /**
     * Get the left segment endpoint (as ordered by the compareTo function of
     * the Point class).
     *
     * @return The left segment endpoint
     */
    public Point getLeftEndPoint() {
        return lpoint;
    }

    /**
     * Get the right segment endpoint (as ordered by the compareTo function of
     * the Point class).
     *
     * @return The right segment endpoint
     */
    public Point getRightEndPoint() {
        return rpoint;
    }

    /**
     * Get the minimum x value for a point on the segment. Since the endpoints
     * are ordered horizontally, this is easy
     *
     * @return The minimum x value
     */
    public int getMinX() {
        return lpoint.getX();
    }

    /**
     * Get the maximum x value for a point on the segment.
     *
     * @return The maximum x value
     */
    public int getMaxX() {
        return rpoint.getX();
    }

    /**
     * Get the minimum y value for a point on the segment.
     *
     * @return The minimum y value
     */
    public int getMinY() {
        return Math.min(lpoint.getY(), rpoint.getY());
    }

    /**
     * Get the maximum y value for a point on the segment.
     *
     * @return The maximum y value
     */
    public int getMaxY() {
        return Math.max(lpoint.getY(), rpoint.getY());
    }

    /**
     * Get the geometric object corresponding to the line segment. Doing this
     * allows for easy display (built in!)
     *
     * @return The Line2D object representing this segment
     */
    public Line2D.Double getline() {
        return l;
    }

    @Override
    public boolean equals(Object s) {
        if (!(s instanceof Segment) || s == null) {
            return false;
        }
        Segment ss = (Segment) s;
        return ss.lpoint.equals(this.lpoint) && ss.rpoint.equals(this.rpoint);
    }

    /**
     * Returns the point on the segment at the given x value or the lower
     * endpoint if the segment is vertical. The behavior for vertical segments
     * may change later
     *
     * @param x The x-value to intersect the line at
     * @return The point on the line (segment) at the given x-value
     */
    public Point intersect(int x) {
        if (lpoint.getX() != rpoint.getX()) {

            long ysum = ((long) (x - lpoint.getX())) * ((long) rpoint.getY()) + ((long) (rpoint.getX() - x)) * ((long) lpoint.getY());
            double yval = (ysum * 1.0) / (rpoint.getX() - lpoint.getX());
            return new Point(x, (int) yval);
        } else {
            return new Point(lpoint.getX(), lpoint.getY());
        }
    }

    /**
     * Calculates the slope of a non vertical segment. If the segment might be
     * vertical, isVertical should be checked first
     *
     * @return the slope (if not vertical) or 0 if it is vertical
     */
    private double getSlope() {
        if (isVertical()) {
            return 0;
        }
        return (rpoint.getY() - lpoint.getY()) / ((double) (rpoint.getX() - lpoint.getX()));
    }

    /**
     * Checks if this segment is vertical
     *
     * @return True if the segment is vertical
     */
    private boolean isVertical() {
        return (rpoint.getX() == lpoint.getX());
    }

    /**
     * Checks to see if this segment object crosses another properly (not a
     * shared endpoint)
     *
     * @param other The other segment to check against
     * @return True if the segments intersect at a point which is not a common
     * vertex
     */
    public boolean crosses(Segment other) {
        //check if x-ranges overlap
        if (other.lpoint.getX() > this.rpoint.getX()) {
            return false;
        }
        if (other.rpoint.getX() < this.lpoint.getX()) {
            return false;
        }

        //at this point, the x-ranges overlap
        if (this.isVertical() && other.isVertical()) {//they must lie vertically aligned
            if (this.getMaxY() <= other.getMinY() || this.getMinY() >= other.getMaxY()) {
                return false;
            }
            return true;
        } else if (this.isVertical()) {
            Point p = other.intersect(this.lpoint.getX());
            return (p.getY() > this.getMinY()) && (p.getY() < this.getMaxY());
        } else {//neither is a vertical line
            //we use a bounding box technique instead of directly computing the intersection
            //it is quite possible we aren't saving any time with this strategy

            //must find the intersection points
            double slope1 = this.getSlope();
            double slope2 = other.getSlope();
            //use slope1 to calculate 3 b's, same for slope2
            double b00 = this.lpoint.getY() - this.lpoint.getX() * slope1;
            double b01 = other.lpoint.getY() - other.lpoint.getX() * slope1;
            double b02 = other.rpoint.getY() - other.rpoint.getX() * slope1;

            double b10 = other.lpoint.getY() - other.lpoint.getX() * slope2;
            double b11 = this.lpoint.getY() - this.lpoint.getX() * slope2;
            double b12 = this.rpoint.getY() - this.rpoint.getX() * slope2;
            if (((b01 <= b00 && b00 <= b02) || (b01 >= b00 && b00 >= b02)) && ((b11 <= b10 && b10 <= b12) || b11 >= b10 && b10 >= b12)) {
                return this.equals(other) || !(this.lpoint.equals(other.lpoint) || this.lpoint.equals(other.rpoint) || this.rpoint.equals(other.lpoint) || this.rpoint.equals(other.rpoint));

            }
        }

        return false;
    }

    @Override
    public String toString() {
        return lpoint + "     " + rpoint;
    }
}
