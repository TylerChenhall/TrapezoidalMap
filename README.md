# TrapezoidalMap
A demonstration of the Randomized Trapezoidal Map for Point Search

Originally created during Spring 2014 for Com S 418: Computational Geometry at Iowa State University

## Project Description
This repository demonstrates the Randomized Trapezoidal Map, as described in de Berg's Computational Geometry textbook.  Segments are provided either via a GUI interface or a simple text file.  The project shuffles the segment list and builds a trapezoidal map (in order to achieve expected construction and query times of O(nlog(n)) and O(log(n)), respectively, where n is the number of segments.

The segments are displayed in a simple GUI. As the user moves the mouse pointer over the image, the data structure is queried to identify which region contains the pointer.  This information is then used to highlight the geometric shape containing the pointer.

## Demo Instructions
The project was originally developed with the NetBeans IDE and run using the automatically-generated .jar file.  If desired, it is also possible to compile via command line, although a build script is not provided.  The "main" method is contained in pointsearch/PointSearch.java.  Running without any command line arguments will produce the following instructions for use:

To create a trapezoidal map from an input file,
restart using a file path argument.

To create segments graphically for a trapezoidal map,
enter "b", followed by a valid file path for 
storing the new segment data file.
Note that for safety purposes, this program will not
override old data files.

To use the segment array builder, there are three commands:
* Left click: Add a segment from the previous point to the click point
* Left + hold Shift: Start a new point, but do not create a segment
* Right click: Close the current figure

Crossing segments are automatically prevented (red cursor)

Close when finished
