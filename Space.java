/*========================================================================*\

 File: Space.java
 Date: 4/18/2016
 Name: Keith Grable

 \*========================================================================*/

package ai;

import java.awt.Point;

class Space {

///////////////////////   
// C O N S T A N T S //
///////////////////////
//
    //possible statuses for the space
    static final int UNKNOWN = 0, MISS = 1, HIT = 2;

///////////////////////
// V A R I A B L E S //
///////////////////////
//
    //the location of the space
    private final Point location;

    //the attack status of the space
    private int status = UNKNOWN; //defaults to unknown

///////////////////////////////////////////   
// C O N S T R U C T O R   M E T H O D S //
///////////////////////////////////////////
//
    //variable location cons
    Space(int x, int y) {
        location = new Point(x, y);
    }//end cons

/////////////////////////////////////  
// F U N C T I O N   M E T H O D S //
/////////////////////////////////////
// 
    //returns the location of the space
    Point getLocation() {
        return location;
    }//end method

    //returns the status of the space
    int getStatus() {
        return status;
    }//end method

    //changes the status of the space
    void setStatus(int s) {
        status = s;
    }//end method
}//end class
