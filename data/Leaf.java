package data;

/**
 * A leaf stores a trapezoid as its data.
 * a leaf should not have child nodes but the capability is still present
 * 
 * @author Tyler Chenhall
 */
public class Leaf extends Node{
    private Trapezoid data;
    
    public Leaf(Trapezoid t) {
        super();
        data = t;
    }
    
    /**
     * Return the trapezoid stored by this Leaf
     * @return The trapezoid 
     */
    public Trapezoid getData() {
        return data;
    }
}
