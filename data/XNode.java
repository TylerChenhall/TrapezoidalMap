package data;

/**
 * An X node stores a segment end point
 * @author Tyler Chenhall
 */
public class XNode extends Node {
    private Point data;
    
    public XNode(Point p) {
        super();
        data = p;
    }
    
    /**
     * Return the Point contained in the Node
     * @return The Point data
     */
    public Point getData() {
        return data;
    }
}