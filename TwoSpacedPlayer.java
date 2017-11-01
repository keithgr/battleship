
/*========================================================================*\

 File: TwoSpacedPlayer.java
 Date: 5/11/2016
 Name: Keith Grable

 \*========================================================================*/

package ai;

import java.util.ArrayList;

public class TwoSpacedPlayer extends Player {

///////////////////////   
// C O N S T A N T S //
///////////////////////
//
    //the attack phases for this class
    private static final String MAIN_PHASE = "TWO_SPACED, MAIN_PHASE", //default search
            SURROUNDING = "TWO_SPACED, SURROUNDING", //searches laterally surrounding spaces of a successful main phase attack
            FOLLOW_UP = "TWO_SPACED, FOLLOW_UP"; //attacks spaces that follow up successful surrounding attacks

///////////////////////
// V A R I A B L E S //
///////////////////////
//
    
///////////////////////////////////////////   
// C O N S T R U C T O R   M E T H O D S //
///////////////////////////////////////////
//    
    //all constructors are based on parent class
    public TwoSpacedPlayer() {
        super();
    }//end method

    public TwoSpacedPlayer(int width, int height) {
        super(width, height);
    }//end method

    public TwoSpacedPlayer(int width, int height, boolean noAdj) {
        super(width, height, noAdj);
    }//end method

    public TwoSpacedPlayer(int width, int height, boolean noAdj, ArrayList<Integer> shipSzs) {
        super(width, height, noAdj, shipSzs);
    }//end method

///////////////////////////////////////////   
// I N I T I A L I Z E R   M E T H O D S //
///////////////////////////////////////////
//  
    @Override
    protected void initAttackQueue() {
        //there is only one main phase
        ArrayList<Space> mainPhase = new ArrayList<Space>();

        //diagonal lines are two spaces apart
        boolean isEven = rand.nextBoolean();

        for (int x = 0; x < super.enemySpaces.length; x++) {
            for (int y = 0; y < super.enemySpaces[0].length; y++) {
                if ((x + y) % 2 == 0 == isEven) {
                    mainPhase.add(enemySpaces[x][y]);
                }
            }//1 1 1 1 1 1 
        }//0 0 0 0 0 0 
        attackQueue.add(mainPhase);

        //the ID for the attack group
        attackGroupIds.add(MAIN_PHASE);

        //adds an attack group of all spaces at the end of the queue
        super.initAttackQueue();
    }//end method

/////////////////////////////////////  
// F U N C T I O N   M E T H O D S //
/////////////////////////////////////
//    
    @Override
    protected void updateQueue() {
        //attack queue only changes if prev attack is a hit
        if (resLastAtk) {

            //update depends on the phase of the last attack
            switch (phaseLastAtk) {
                case MAIN_PHASE:
                case FOLLOW_UP: {
                    //establishes a reference point
                    ptRef = ptLastAtk;
                    ArrayList<Space> surrSpaces = new ArrayList<Space>(), //attack group for surrounding spaces
                            followUps = new ArrayList<Space>();//attack group for potential follow up spaces
                    //(e.g. a surrounding space is already hit)
                    
                    //surrounding points
                    //and follow up points
                    int[] xSurr = {ptRef.x + 1, ptRef.x, ptRef.x - 1, ptRef.x},
                            ySurr = {ptRef.y, ptRef.y + 1, ptRef.y, ptRef.y - 1},
                            xFoll = {ptRef.x + 2, ptRef.x, ptRef.x - 2, ptRef.x},
                            yFoll = {ptRef.y, ptRef.y + 2, ptRef.y, ptRef.y - 2};
                    
                    //iterates over each direction
                    for (int i = 0; i < 4; i++) {        
                        //if surrounding point exists
                        if (isInBounds(xSurr[i], ySurr[i])) {
                            //adds surrounding spaces
                            surrSpaces.add(enemySpaces[xSurr[i]][ySurr[i]]);

                            //if follow up point exists and surrounding point is already hit successfully
                            if (isInBounds(xFoll[i], yFoll[i]) && enemySpaces[xSurr[i]][ySurr[i]].getStatus() == Space.HIT) {
                                followUps.add(enemySpaces[xFoll[i]][yFoll[i]]);
                            }
                        }
                    }//0 0 0 0 0 0 
                    
                    //adds the attack groups to the attack queue in the proper order
                    attackQueue.add(0, surrSpaces);
                    attackGroupIds.add(0, SURROUNDING);
                    //adds follow up group after surrounding space groups
                    for (int i = 0; i < attackQueue.size(); i++) {
                        if (!attackGroupIds.get(i).equals(SURROUNDING)) {
                            attackQueue.add(i, followUps);
                            attackGroupIds.add(i, FOLLOW_UP);
                            break;
                        }
                    }
                    break; //end case
                }
                case SURROUNDING: {
                    int xFoll = 2 * ptLastAtk.x - ptRef.x,
                            yFoll = 2 * ptLastAtk.y - ptRef.y;
                    //adds the follow up point to the atttack queue
                    if (isInBounds(xFoll, yFoll)) {
                        ArrayList<Space> followUp = new ArrayList<Space>();
                        followUp.add(enemySpaces[xFoll][yFoll]);

                        //adds follow up group after surrounding space groups
                        for (int i = 0; i < attackQueue.size(); i++) {
                            if (!attackGroupIds.get(i).equals(SURROUNDING)) {
                                attackQueue.add(i, followUp);
                                attackGroupIds.add(i, FOLLOW_UP);
                                break;
                            }
                        }//0 0 0 0 0 0
                    }
                    break; //end case
                }
            }//end switch
        }//if resLastAtk
    }//end method

}//end class
