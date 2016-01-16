package data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * This class represents the Trapezoidal Map for a given set of segments in the plane.
 * The constructor accepts a list of segments which follow the following criteria:
 *      1) Segments are non-crossing
 *      2) Segment interiors are disjoint, but segments may meet at endpoints (to allow closed figures)
 * Both the physical map and search structure are represented
 *
 *
 * @author Tyler Chenhall
 */
public class SearchStructure {

    private Node root;

    /**
     * Builds the trapezoidal map search structure from the segment array. The
     * initial boundary is provided in terms of 4 ints.  This constructor builds both
     * the physical map structure (creating the neighbor links for each trapezoid)
     * and also incrementally builds the map search structure (using nodes in a pseudo-tree)
     * 
     * Details of the algorithm are included as comments throughout the constructor
     *
     * @param segs The list of segments to build a search structure for
     * @param lx initial left bound
     * @param rx initial right bound
     * @param ly initial lower bound
     * @param ry initial upper bound
     */
    public SearchStructure(Segment[] segs, int lx, int rx, int ly, int ry) {
        long t1 = System.nanoTime();
        //construct the search structure and map? is the map even needed for anything?

        // 1. determine a bounding box for the segments
        int minx = lx;
        int maxx = rx;
        int miny = ly;
        int maxy = ry;
        for (int i = 0; i < segs.length; i++) {
            if (segs[i] != null) {
                minx = Math.min(minx, segs[i].getMinX());
                maxx = Math.max(maxx, segs[i].getMaxX());
                miny = Math.min(miny, segs[i].getMinY());
                maxy = Math.max(maxy, segs[i].getMaxY());
            }
        }
        //create a trapezoid using the bounding box
        Point left = new Point(minx, miny);
        Point right = new Point(maxx, maxy);
        Trapezoid t = new Trapezoid(left, right, new Segment(new Point(minx, maxy), new Point(maxx, maxy)),
                new Segment(new Point(minx, miny), new Point(maxx, miny)));
        Leaf f = new Leaf(t);
        t.setLeaf(f);
        root = f;
        //System.out.println("Bounding box created");

        // 2. shuffle the segments
        // the array is first duplicated in case the ordering is important in the original array
        Segment[] arr = Arrays.copyOf(segs, segs.length);
        Random r = new Random();
        int rnd;
        Segment temp;
        //random shuffling
        for (int i = arr.length - 1; i >= 1; i--) {
            //determine a random index to swap with (index < i) and swap
            //based on Chapter 4 of deBerg (p77)
            rnd = r.nextInt(i);
            temp = arr[i];
            arr[i] = arr[rnd];
            arr[rnd] = temp;
        }

        //original arrangement
        /*for (int i = 0; i < arr.length; i++) {
         //System.out.println(arr[i]);
         arr[i] = segs[i];
         }*/

        //System.out.println("Segment array shuffled");

        // 3. incrementally make the trapezoidal map
        //System.out.println("Ready to construct trapezoidal map");
        for (int i = 0; i < arr.length && arr[i] != null; i++) {
            //find the trapezoids intersected by arr[i]
            //System.out.println("in loop");
            Leaf[] list = followSegment(arr[i]);

            //System.out.println(list.length);
            if (list.length == 1) {//the segment is entirely within a single trapezoid

                //System.out.println("Case I");
                //split into 4 sections
                Trapezoid old = list[0].getData();
                Trapezoid lefty = new Trapezoid(old.getLeftBound(), arr[i].getLeftEndPoint(), old.getUpperBound(), old.getLowerBound());
                Trapezoid righty = new Trapezoid(arr[i].getRightEndPoint(), old.getRightBound(), old.getUpperBound(), old.getLowerBound());
                Trapezoid top = new Trapezoid(arr[i].getLeftEndPoint(), arr[i].getRightEndPoint(), old.getUpperBound(), arr[i]);
                Trapezoid bottom = new Trapezoid(arr[i].getLeftEndPoint(), arr[i].getRightEndPoint(), arr[i], old.getLowerBound());
                XNode ll = new XNode(arr[i].getLeftEndPoint());
                XNode rr = new XNode(arr[i].getRightEndPoint());
                YNode ss = new YNode(arr[i]);

                Leaf leftyN = new Leaf(lefty);
                lefty.setLeaf(leftyN);
                Leaf rightyN = new Leaf(righty);
                righty.setLeaf(rightyN);
                Leaf topN = new Leaf(top);
                top.setLeaf(topN);
                Leaf bottomN = new Leaf(bottom);
                bottom.setLeaf(bottomN);
                if (!(lefty.hasZeroWidth() || righty.hasZeroWidth())) {

                    //link all the nodes for the trapezoids
                    ll.setLeftChildNode(leftyN);
                    ll.setRightChildNode(rr);
                    rr.setRightChildNode(rightyN);
                    rr.setLeftChildNode(ss);
                    ss.setLeftChildNode(topN);
                    ss.setRightChildNode(bottomN);

                    //connect the nodes to the old structure
                    if (list[0].getParentNode() == null) {
                        root = ll;
                    } else {
                        //the previous node might have more than one parent node
                        ArrayList<Node> parents = list[0].getParentNodes();
                        for (int j = 0; j < parents.size(); j++) {
                            Node tempParent = parents.get(j);
                            if (tempParent.getLeftChildNode() == list[0]) {
                                tempParent.setLeftChildNode(ll);
                            } else {
                                tempParent.setRightChildNode(ll);
                            }
                        }
                    }

                    //link the trapezoids together
                    lowerLink(lefty, bottom);
                    lowerLink(old.getLowerLeftNeighbor(), lefty);
                    upperLink(lefty, top);
                    upperLink(old.getUpperLeftNeighbor(), lefty);

                    lowerLink(righty, old.getLowerRightNeighbor());
                    lowerLink(bottom, righty);
                    upperLink(righty, old.getUpperRightNeighbor());
                    upperLink(top, righty);
                } else if (lefty.hasZeroWidth() && !righty.hasZeroWidth()) {//only left has zero width
                    //link all the nodes for the trapezoids
                    rr.setLeftChildNode(ss);
                    rr.setRightChildNode(rightyN);
                    ss.setLeftChildNode(topN);
                    ss.setRightChildNode(bottomN);

                    //connect the nodes to the old structure
                    if (list[0].getParentNode() == null) {
                        root = rr;
                    } else {
                        //the previous node might have more than one parent node
                        ArrayList<Node> parents = list[0].getParentNodes();
                        for (int j = 0; j < parents.size(); j++) {
                            Node tempParent = parents.get(j);
                            if (tempParent.getLeftChildNode() == list[0]) {
                                tempParent.setLeftChildNode(rr);
                            } else {
                                tempParent.setRightChildNode(rr);
                            }
                        }
                    }

                    //link the trapezoids together
                    lowerLink(old.getLowerLeftNeighbor(), bottom);
                    upperLink(old.getUpperLeftNeighbor(), top);

                    lowerLink(righty, old.getLowerRightNeighbor());
                    lowerLink(bottom, righty);
                    upperLink(righty, old.getUpperRightNeighbor());
                    upperLink(top, righty);
                } else if (righty.hasZeroWidth() && !lefty.hasZeroWidth()) {//only right has zero width
                    //link all the nodes for the trapezoids
                    ll.setLeftChildNode(leftyN);
                    ll.setRightChildNode(ss);
                    ss.setLeftChildNode(topN);
                    ss.setRightChildNode(bottomN);

                    //connect the nodes to the old structure
                    if (list[0].getParentNode() == null) {
                        root = ll;
                    } else {
                        //the previous node might have more than one parent node
                        ArrayList<Node> parents = list[0].getParentNodes();
                        for (int j = 0; j < parents.size(); j++) {
                            Node tempParent = parents.get(j);
                            if (tempParent.getLeftChildNode() == list[0]) {
                                tempParent.setLeftChildNode(ll);
                            } else {
                                tempParent.setRightChildNode(ll);
                            }
                        }
                    }

                    //link the trapezoids together
                    lowerLink(lefty, bottom);
                    lowerLink(old.getLowerLeftNeighbor(), lefty);
                    upperLink(lefty, top);
                    upperLink(old.getUpperLeftNeighbor(), lefty);

                    lowerLink(bottom, old.getLowerRightNeighbor());
                    upperLink(top, old.getUpperRightNeighbor());
                } else {
                    //both have zero width

                    //build the search structure
                    ss.setLeftChildNode(topN);
                    ss.setRightChildNode(bottomN);

                    //connect the nodes to the old structure
                    if (list[0].getParentNode() == null) {
                        root = ss;
                    } else {
                        //the previous node might have more than one parent node
                        ArrayList<Node> parents = list[0].getParentNodes();
                        for (int j = 0; j < parents.size(); j++) {
                            Node tempParent = parents.get(j);
                            if (tempParent.getLeftChildNode() == list[0]) {
                                tempParent.setLeftChildNode(ss);
                            } else {
                                tempParent.setRightChildNode(ss);
                            }
                        }
                    }

                    //link the trapezoids together (this is nontrivial in degenerates cases)
                    lowerLink(old.getLowerLeftNeighbor(), bottom);
                    lowerLink(bottom, old.getLowerRightNeighbor());
                    upperLink(old.getUpperLeftNeighbor(), top);
                    upperLink(top, old.getUpperRightNeighbor());
                }

            } else {//(3 divisions for the first and last trapezoids, 2 for the middle ones)
                //System.out.println("Case II");
                //the first and last cases get broken into 3 parts
                //the middle ones are different

                //if the left segment endpoint is not leftp of list[0].getData(), then
                //there is an extra trapezoid at the left end.  Likewise for rightp of list[n-1].getData()

                //for everything in the middle, we start with a single top and bottom trap for both
                //then we merge trapezoids together as needed
                //note that before merging, some trapezoids may have an endpoint which is null
                Trapezoid[] topArr = new Trapezoid[list.length];
                Trapezoid[] botArr = new Trapezoid[list.length];
                for (int j = 0; j < list.length; j++) {
                    //top is defined by the original upper segment, the new segment & two endpoints
                    //left endpoint:
                        /*
                     * if j==0, is segment's left endpoint
                     * else is old trap's left endpoint if it is above the segment
                     */
                    //right endpoint is similar
                    if (j == 0) {
                        Point rtP = null;
                        if (isPointAboveLine(list[j].getData().getRightBound(), arr[i])) {
                            rtP = list[j].getData().getRightBound();
                        }
                        topArr[j] = new Trapezoid(arr[i].getLeftEndPoint(), rtP, list[j].getData().getUpperBound(), arr[i]);
                    } else if (j == list.length - 1) {
                        Point ltP = null;
                        if (isPointAboveLine(list[j].getData().getLeftBound(), arr[i])) {
                            ltP = list[j].getData().getLeftBound();
                        }
                        topArr[j] = new Trapezoid(ltP, arr[i].getRightEndPoint(), list[j].getData().getUpperBound(), arr[i]);
                    } else {
                        Point rtP = null;
                        if (isPointAboveLine(list[j].getData().getRightBound(), arr[i])) {
                            rtP = list[j].getData().getRightBound();
                        }
                        Point ltP = null;
                        if (isPointAboveLine(list[j].getData().getLeftBound(), arr[i])) {
                            ltP = list[j].getData().getLeftBound();
                        }
                        topArr[j] = new Trapezoid(ltP, rtP, list[j].getData().getUpperBound(), arr[i]);
                    }

                    //the bottom array is constructed using a similar strategy
                    if (j == 0) {
                        Point rtP = null;
                        if (!isPointAboveLine(list[j].getData().getRightBound(), arr[i])) {
                            rtP = list[j].getData().getRightBound();
                        }
                        botArr[j] = new Trapezoid(arr[i].getLeftEndPoint(), rtP, arr[i], list[j].getData().getLowerBound());
                    } else if (j == list.length - 1) {
                        Point ltP = null;
                        if (!isPointAboveLine(list[j].getData().getLeftBound(), arr[i])) {
                            ltP = list[j].getData().getLeftBound();
                        }
                        botArr[j] = new Trapezoid(ltP, arr[i].getRightEndPoint(), arr[i], list[j].getData().getLowerBound());
                    } else {
                        Point rtP = null;
                        if (!isPointAboveLine(list[j].getData().getRightBound(), arr[i])) {
                            rtP = list[j].getData().getRightBound();
                        }
                        Point ltP = null;
                        if (!isPointAboveLine(list[j].getData().getLeftBound(), arr[i])) {
                            ltP = list[j].getData().getLeftBound();
                        }
                        botArr[j] = new Trapezoid(ltP, rtP, arr[i], list[j].getData().getLowerBound());
                    }
                }

                //then merge degenerate trapezoids together (those with a null bounding point)
                int aTop = 0;
                int bTop;
                int aBot = 0;
                int bBot;
                boolean topHasRightP = false;
                boolean botHasRightP = false;
                for (int j = 0; j < list.length; j++) {
                    if (topArr[j].getRightBound() != null) {
                        bTop = j;
                        //merge trapezoids aTop through bTop
                        //we only want one trapezoid, so we just have bTop-aTop+1 pointers to it for now
                        Trapezoid tempMerge = new Trapezoid(topArr[aTop].getLeftBound(), topArr[bTop].getRightBound(), topArr[aTop].getUpperBound(), arr[i]);
                        for (int k = aTop; k <= bTop; k++) {
                            //now there are duplicates of the same trapezoid unfortunately, but I think if we link them together left to right
                            //this shouldn't cause problems later...it just means a bit more storage use
                            topArr[k] = tempMerge;
                        }
                        aTop = j + 1;
                    }

                    if (botArr[j].getRightBound() != null) {
                        bBot = j;
                        //merge trapezoids aBot through bBot
                        Trapezoid tempMerge = new Trapezoid(botArr[aBot].getLeftBound(), botArr[bBot].getRightBound(), arr[i], botArr[aBot].getLowerBound());
                        for (int k = aBot; k <= bBot; k++) {
                            botArr[k] = tempMerge;
                        }
                        aBot = j + 1;
                    }
                }

                //do trapezoid links...this should unlink the original trapezoids from the physical structure except at the ends
                //do all left links before doing right links in order to avoid linking errors
                for (int j = 0; j < list.length; j++) {
                    if (j != 0) {
                        //update left links
                        //link right to left
                        //only recycle old links if they are not in the list to be removed

                        //only when the trapezoids do not repeat
                        if (topArr[j] != topArr[j - 1]) {
                            lowerLink(topArr[j - 1], topArr[j]);
                        }

                        //leave the upper left neighbor null unless we have something to set it to
                        Trapezoid temp2 = list[j].getData().getUpperLeftNeighbor();
                        if (!list[j - 1].getData().equals(temp2)) {
                            upperLink(temp2, topArr[j]);
                        }

                        //only do this for non-repeating trapezoids
                        if (botArr[j] != botArr[j - 1]) {
                            upperLink(botArr[j - 1], botArr[j]);
                        }

                        temp2 = list[j].getData().getLowerLeftNeighbor();
                        if (!list[j - 1].getData().equals(temp2)) {
                            lowerLink(temp2, botArr[j]);
                        }

                    }

                }
                for (int j = 0; j < list.length; j++) {
                    if (j != topArr.length - 1) {
                        //update right links

                        //only for non-repeats
                        if (topArr[j] != topArr[j + 1]) {
                            lowerLink(topArr[j], topArr[j + 1]);
                        }

                        Trapezoid temp2 = list[j].getData().getUpperRightNeighbor();
                        if (!list[j + 1].getData().equals(temp2)) {
                            upperLink(topArr[j], temp2);
                        }

                        //only for non-repeats
                        if (botArr[j] != botArr[j + 1]) {
                            upperLink(botArr[j], botArr[j + 1]);
                        }

                        temp2 = list[j].getData().getLowerRightNeighbor();
                        if (!list[j + 1].getData().equals(temp2)) {
                            lowerLink(botArr[j], temp2);
                        }
                    }
                }

                //deal with the possible extra end trapezoids
                Trapezoid leftmost = null;
                Trapezoid rightmost = null;
                Trapezoid oldLeft = list[0].getData();
                Trapezoid oldRight = list[list.length - 1].getData();
                if (!arr[i].getLeftEndPoint().equals(oldLeft.getLeftBound())) {
                    //there is a leftmost trapezoid
                    leftmost = new Trapezoid(oldLeft.getLeftBound(), arr[i].getLeftEndPoint(),
                            oldLeft.getUpperBound(), oldLeft.getLowerBound());
                }
                if (!arr[i].getRightEndPoint().equals(list[list.length - 1].getData().getRightBound())) {
                    //there is a rightmost trapezoid
                    rightmost = new Trapezoid(arr[i].getRightEndPoint(), oldRight.getRightBound(),
                            oldRight.getUpperBound(), oldRight.getLowerBound());
                }

                //add remaining trapezoid links at the end
                if (leftmost != null) {
                    lowerLink(oldLeft.getLowerLeftNeighbor(), leftmost);
                    upperLink(oldLeft.getUpperLeftNeighbor(), leftmost);

                    lowerLink(leftmost, botArr[0]);
                    upperLink(leftmost, topArr[0]);
                } else {
                    //link top & bot arr with appropriate left links of oldLeft
                    if (oldLeft.getUpperBound().getLeftEndPoint().equals(oldLeft.getLowerBound().getLeftEndPoint())) {
                        //triangles, so no neighbors to worry about
                    } else if (oldLeft.getUpperBound().getLeftEndPoint().equals(oldLeft.getLeftBound())) {
                        //upper half degenerates to a triangle
                        lowerLink(oldLeft.getLowerLeftNeighbor(), botArr[0]);
                    } else if (oldLeft.getLowerBound().getLeftEndPoint().equals(oldLeft.getLeftBound())) {
                        //lower half degenerates to a triangle
                        upperLink(oldLeft.getUpperLeftNeighbor(), topArr[0]);
                    } else {
                        //neither degenerates to a triangle
                        lowerLink(oldLeft.getLowerLeftNeighbor(), botArr[0]);
                        upperLink(oldLeft.getUpperLeftNeighbor(), topArr[0]);
                    }
                }
                if (rightmost != null) {
                    lowerLink(rightmost, oldRight.getLowerRightNeighbor());
                    upperLink(rightmost, oldRight.getUpperRightNeighbor());

                    lowerLink(botArr[botArr.length - 1], rightmost);
                    upperLink(topArr[topArr.length - 1], rightmost);
                } else {
                    //link the top & bot arr with the appropriate right links of oldRight
                    if (oldRight.getUpperBound().getRightEndPoint().equals(oldRight.getLowerBound().getRightEndPoint())) {
                        //triangles, hence no right neighbors
                    } else if (oldRight.getUpperBound().getRightEndPoint().equals(oldRight.getRightBound())) {
                        //upper half degenerates to a triangle
                        lowerLink(botArr[botArr.length - 1], oldRight.getLowerRightNeighbor());
                    } else if (oldRight.getLowerBound().getRightEndPoint().equals(oldRight.getRightBound())) {
                        //lower half degenerates to a triangle
                        upperLink(topArr[topArr.length - 1], oldRight.getUpperRightNeighbor());
                    } else {
                        //neither degenerates to a triangle
                        lowerLink(botArr[botArr.length - 1], oldRight.getLowerRightNeighbor());
                        upperLink(topArr[topArr.length - 1], oldRight.getUpperRightNeighbor());
                    }
                }

                //create leaf structures ahead of time to deal with the duplication problem
                Leaf[] topLeaf = new Leaf[topArr.length];
                Leaf[] botLeaf = new Leaf[botArr.length];
                Leaf aa;
                for (int j = 0; j < topLeaf.length; j++) {
                    if (j == 0 || topArr[j] != topArr[j - 1]) {
                        //create a new topLeaf
                        aa = new Leaf(topArr[j]);
                        topArr[j].setLeaf(aa);
                        topLeaf[j] = aa;
                    } else {
                        //reuse the old Leaf
                        topLeaf[j] = topLeaf[j - 1];
                    }

                    if (j == 0 || botArr[j] != botArr[j - 1]) {
                        //create a new botLeaf
                        aa = new Leaf(botArr[j]);
                        botArr[j].setLeaf(aa);
                        botLeaf[j] = aa;
                    } else {
                        //reuse the old Leaf
                        botLeaf[j] = botLeaf[j - 1];
                    }
                }

                //then add nodes and node links...this should unlink the original trapezoids from the physical structure
                Node[] newStructures = new Node[list.length];
                for (int j = 0; j < list.length; j++) {
                    Node yy = new YNode(arr[i]);
                    if (j == 0 && leftmost != null) {
                        XNode xx = new XNode(arr[i].getLeftEndPoint());
                        aa = new Leaf(leftmost);
                        leftmost.setLeaf(aa);
                        xx.setLeftChildNode(aa);
                        xx.setRightChildNode(yy);

                        newStructures[j] = xx;
                    } else if (j == newStructures.length - 1 && rightmost != null) {
                        XNode xx = new XNode(arr[i].getRightEndPoint());
                        aa = new Leaf(rightmost);
                        rightmost.setLeaf(aa);
                        xx.setRightChildNode(aa);
                        xx.setLeftChildNode(yy);

                        newStructures[j] = xx;
                    } else {
                        newStructures[j] = yy;
                    }

                    yy.setLeftChildNode(topLeaf[j]);

                    yy.setRightChildNode(botLeaf[j]);

                    //insert the new structure in place of the old one
                    //Node parent = list[j].getParentNode();

                    //now there may be many parents...
                    ArrayList<Node> parents = list[j].getParentNodes();
                    for (int k = 0; k < parents.size(); k++) {
                        Node parent = parents.get(k);
                        if (parent.getLeftChildNode() == list[j]) {
                            //replace left child
                            parent.setLeftChildNode(newStructures[j]);
                        } else {
                            parent.setRightChildNode(newStructures[j]);
                        }
                    }
                }
            }
        }
        long t2 = System.nanoTime();
        System.out.println((t2 - t1));
    }

    /**
     * Link two neighboring trapezoids that are lower neighbors
     *
     * @param left The left trapezoid to link
     * @param right The right trapezoid to link
     */
    private void lowerLink(Trapezoid left, Trapezoid right) {
        if (left != null) {
            left.setLowerRightNeighbor(right);
        }
        if (right != null) {
            right.setLowerLeftNeighbor(left);
        }
    }

    /**
     * Link two neighboring trapezoids that are upper neighbors
     *
     * @param left The left trapezoid to link
     * @param right The right trapezoid to link
     */
    private void upperLink(Trapezoid left, Trapezoid right) {
        if (left != null) {
            left.setUpperRightNeighbor(right);
        }
        if (right != null) {
            right.setUpperLeftNeighbor(left);
        }
    }

    /**
     * Get the list of trapezoids in the current structure intersected by the
     * segment.
     *
     * @param s The query segment
     * @return An array of trapezoids (Leaf array) intersected by the segment
     */
    private Leaf[] followSegment(Segment s) {
        //System.err.println("Follow segment not yet implemented");
        ArrayList<Leaf> list = new ArrayList<Leaf>();
        Leaf previous = findPoint(s.getLeftEndPoint(), s);
        //shift over leftward to make sure we have the first of any repeated trapezoids

        list.add(previous);
        while (/*previous != null &&*/s.getRightEndPoint().compareTo(previous.getData().getRightBound()) > 0) {
            //choose the next trapezoid in the sequence
            if (this.isPointAboveLine(previous.getData().getRightBound(), s)) {
                previous = previous.getData().getLowerRightNeighbor().getLeaf();
            } else {
                previous = previous.getData().getUpperRightNeighbor().getLeaf();
            }
            list.add(previous);
        }

        Leaf[] arr = new Leaf[list.size()];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }

    /**
     * Find the trapezoid in the trapezoidal map which contains the search
     * point.
     *
     * @param p The point to search for
     * @return The trapezoid containing the query point
     */
    public Leaf findPoint(Point p, Segment s) {
        Node current = root;
        while (!(current instanceof data.Leaf)) {
            if (current instanceof data.XNode) {
                int val = p.compareTo(((XNode) current).getData());

                if (val < 0) {
                    current = current.getLeftChildNode();
                } else {
                    current = current.getRightChildNode();
                }
            } else {//YNode
                //we are searching for a point, without segment information
                if (s == null) {
                    if (isPointAboveLine(p, ((YNode) current).getData())) {
                        current = current.getLeftChildNode();
                    } else {
                        current = current.getRightChildNode();
                    }
                } else {
                    //we are searching for a point on one of the segments
                    if (isPointAboveLine2(p, ((YNode) current).getData(), s)) {
                        current = current.getLeftChildNode();
                    } else {
                        current = current.getRightChildNode();
                    }
                }
            }
        }
        //System.out.println("Found Trapezoid Region ");// + (current instanceof Trapezoid));
        return ((Leaf) current);
    }

    /**
     * Leverages the findPont method which finds a leaf, and returns the corresponding trapezoid.
     * @param p The point to query
     * @return The trapezoid containing the point
     */
    public Trapezoid findPointTrap(Point p) {
        return findPoint(p, null).getData();
    }

    /**
     * Checks to see if a point is above the segment. Works by calculating y of
     * the segment at x of the point
     *
     * @param p The point of interest
     * @param s The segment of interest
     * @return True if on or above the segment; false otherwise
     */
    public static boolean isPointAboveLine(Point p, Segment s) {
        int x = p.getX();
        int y = p.getY();
        return (x - s.getLeftEndPoint().getX()) * s.getRightEndPoint().getY()
                + (s.getRightEndPoint().getX() - x) * s.getLeftEndPoint().getY()
                < y * (s.getRightEndPoint().getX() - s.getLeftEndPoint().getX());
    }

    /**
     * Checks if the input point on the given old segment lies above or below the new segment.
     * If the input point lies on the new segment, we determine above/below by which
     * segment has the higher slope.
     * 
     * @TODO Check this for nondegenerate case?
     * @param p The point under consideration
     * @param old The segment which the point lies on
     * @param pseg The segment to compare the point to
     * @return True if the point lies above segment pseg, or the point lies on pseg, on a segment of higher slope
     */
    public static boolean isPointAboveLine2(Point p, Segment old, Segment pseg) {
        //check if p is on segment old
        /*long x1 = p.getX();
         long x2 = old.getLeftEndPoint().getX();
         long x3 = old.getRightEndPoint().getX();
         long y1 = p.getY();
         long y2 = old.getLeftEndPoint().getY();
         long y3 = old.getRightEndPoint().getY();
         long result = (x2-x1)*(y3-y1) - (x3-x1)*(y2-y1);*/
        //according to the textbook, p can only lie on segment old if it is the left endpoint
        if (p.equals(old.getLeftEndPoint())) {
            //compare slopes
            long x1 = p.getX();
            long x2 = old.getRightEndPoint().getX();
            long x3 = pseg.getRightEndPoint().getX();
            long y1 = p.getY();
            long y2 = old.getRightEndPoint().getY();
            long y3 = pseg.getRightEndPoint().getY();
            long result = (x2 - x1) * (y3 - y1) - (x3 - x1) * (y2 - y1);
            return result > 0;
        }
        //if not, call isPointAboveLine
        return isPointAboveLine(p, old);
    }
}