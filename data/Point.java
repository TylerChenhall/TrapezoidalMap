package data;

/**
 * Class representing a point object with integer coordinates in 2D. Once
 * created, the coordinates may be accessed but not modified.
 *
 * @author Tyler Chenhall
 */
public class Point implements Comparable<Point> {

    private int x;
    private int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Return the x-coordinate of this point.
     * @return The x-coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Return the y-coordinate of the point.
     * @return The y-coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Allows comparisons of points based only on the x-coordinates
     * @param p The point to compare to
     * @return 1 if this point lies to the right, 0 if they are at the same x, -1 for to the left
     */
    public int compareToX(Point p) {
        if (p == null) {
            return 1;
        }
        if (this.x < p.x) {
            return -1;
        }
        if (this.x == p.x) {
            return 0;
        }
        return 1;
    }

    @Override
    /**
     * Compare points first on x-coordinate, and then on y-coordinate
     */
    public int compareTo(Point p) {
        if (p == null) {
            return 1;
        }
        if (this.x < p.x || (this.x == p.x && this.y < p.y)) {
            return -1;
        } else if ((this.x == p.x) && (this.y == p.y)) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    /**
     * Returns true if both x and y of the points match
     */
    public boolean equals(Object p) {
        if (p == null || !(p instanceof Point)) {
            return false;
        }
        Point pp = (Point) p;
        return (this.x == pp.x) && (this.y == pp.y);
    }

    @Override
    public String toString() {
        return "" + x + "   " + y;
    }
}
