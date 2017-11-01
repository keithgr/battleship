
/*========================================================================*\

 File: Ship.java
 Date: 4/18/2016
 Name: Keith Grable

 \*========================================================================*/

package ai;

import java.awt.Point;

class Ship {

///////////////////////
// V A R I A B L E S //
///////////////////////
//
    //the lower-left-most point of the ship
    private Point startPoint;

    //the number of spaces occupied by the ship
    private int size;

    //true if the ship is oriented horizontally
    private boolean isHorizontal;

///////////////////////////////////////////   
// C O N S T R U C T O R   M E T H O D S //
///////////////////////////////////////////
//
    //def cons
    Ship(int x, int y) {
        startPoint = new Point(x, y);
        size = 1;
        isHorizontal = true;
    }

    //variable x, y, size, and orientaion cons
    Ship(int x, int y, int sz, boolean isHor) {
        startPoint = new Point(x, y);
        size = sz;
        isHorizontal = isHor;
    }//end cons

/////////////////////////////////////  
// F U N C T I O N   M E T H O D S //
/////////////////////////////////////
//
    //returns ship size
    int getSize() {
        return size;
    }//end method

    //returns the point of a given segment of the ship
    Point getSeg(int index) {
        //thrown if the index is not on the ship
        if (index < 0 || index >= size) {
            throw new IllegalArgumentException("Ship does not contain index " + index);
        }

        if (isHorizontal) {
            return new Point(startPoint.x + index, startPoint.y);
        } else {
            return new Point(startPoint.x, startPoint.y + index);
        }
    }//end method
}//end class