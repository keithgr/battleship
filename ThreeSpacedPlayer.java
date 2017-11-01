
/*========================================================================*\

 File: ThreeSpacedPlayer.java
 Date: 5/11/2016
 Name: Keith Grable

 \*========================================================================*/

package ai;

import java.util.ArrayList;

public class ThreeSpacedPlayer extends Player {

///////////////////////   
// C O N S T A N T S //
///////////////////////
//
    private static final String PHASE_ONE = "THREE_SPACED, PHASE_ONE", //initial diagonal lines (fills 1/3 of board)
            PHASE_TWO = "THREE_SPACED, PHASE_TWO", //extra diagonal lines (fills 2/3 of board cumulative)
            SURROUNDING_ONE = "THREE_SPACED, SURROUNDING_ONE", //searches the laterally surrounding spaces of a successful phase one/two attack
            SURROUNDING_TWO = "THREE_SPACED, SURROUNDING_TWO", //follows up surrounding one if the corresponding direction is a hit
            FOLLOW_UP = "THREE_SPACED, FOLLOW_UP"; //follows up successful surrounding two attacks

///////////////////////////////////////////   
// C O N S T R U C T O R   M E T H O D S //
///////////////////////////////////////////
//
   //all constructors are based on parent class
    public ThreeSpacedPlayer() {
        super();
    }//end method

    public ThreeSpacedPlayer(int width, int height) {
        super(width, height);
    }//end method

    public ThreeSpacedPlayer(int width, int height, boolean noAdj) {
        super(width, height, noAdj);
    }//end method

    public ThreeSpacedPlayer(int width, int height, boolean noAdj, ArrayList<Integer> shipSzs) {
        super(width, height, noAdj, shipSzs);
    }//end method

///////////////////////////////////////////   
// I N I T I A L I Z E R   M E T H O D S //
///////////////////////////////////////////
//     
    protected void initAttackQueue() {
        //attack group of phase one
        ArrayList<Space> phaseOne = new ArrayList<Space>(),
                phaseTwo = new ArrayList<Space>();

        //diagonal attack lines are three spaces apart
        int y = rand.nextInt(3), //the modulus-3 value of y coordinates on x = -1
                yDelta = rand.nextInt(2) * 2 - 1; //the change of the y coordinate (+-1) as x increases by 1

        for (int x = 0; x < enemySpaces.length; x++) {
            //loop sets initial y value to 0, 1, or 2
            //y increments by 3
            for (y = (y + yDelta + 3) % 3; y < enemySpaces[0].length; y += 3) {
                phaseOne.add(enemySpaces[x][y]);
            }//1 1 1 1 1 1 
        }//0 0 0 0 0 0 
        attackQueue.add(phaseOne);

        //the ID for the attack group
        attackGroupIds.add(PHASE_ONE);
        
            //creates phase two
            for (int x = 0; x < enemySpaces.length; x++) {
                //checks vertical pairs of spaces
                for (y = 0; y < enemySpaces[0].length - 1; y++) {
                    //if empty pair is found
                    if (!phaseOne.contains(enemySpaces[x][y]) && !phaseOne.contains(enemySpaces[x][y + 1])) {
                        if (yDelta == -1 && x + y < enemySpaces[0].length - 1
                                || yDelta == 1 && x + y > enemySpaces[0].length - 1) {
                            phaseTwo.add(enemySpaces[x][y]);
                        } else {
                            phaseTwo.add(enemySpaces[x][y + 1]);
                        }
                    }
                }
            }

            //adds phase two to attack queue
            attackQueue.add(phaseTwo);
            attackGroupIds.add(PHASE_TWO);
            
        //adds an attack group of all spaces at the end of the queue
        super.initAttackQueue();
    }//end method

/////////////////////////////////////  
// F U N C T I O N   M E T H O D S //
/////////////////////////////////////
//
    protected void updateQueue() {
        //attack queue only changes if prev attack is a hit
        if (resLastAtk) {

            //update depends on the phase of the last attack
            switch (phaseLastAtk) {
                case PHASE_ONE:
                case PHASE_TWO:
                case FOLLOW_UP: {
                    //establishes a reference point
                    ptRef = ptLastAtk;

                    //attack groups to be added
                    ArrayList<Space> surrSpaces1 = new ArrayList<Space>(), //attack group for immediate surrounding spaces
                            surrSpaces2 = new ArrayList<Space>(), //attack group for next surrounding spaces
                            followUps = new ArrayList<Space>(); //attack group for follow up attacks

                    //surrounding points
                    //and follow up points
                    int[] xSurr1 = {ptRef.x + 1, ptRef.x, ptRef.x - 1, ptRef.x},
                            ySurr1 = {ptRef.y, ptRef.y + 1, ptRef.y, ptRef.y - 1},
                            xSurr2 = {ptRef.x + 2, ptRef.x, ptRef.x - 2, ptRef.x},
                            ySurr2 = {ptRef.y, ptRef.y + 2, ptRef.y, ptRef.y - 2},
                            xFoll = {ptRef.x + 3, ptRef.x, ptRef.x - 3, ptRef.x},
                            yFoll = {ptRef.y, ptRef.y + 3, ptRef.y, ptRef.y - 3};

                    //iterates over each direction
                    for (int i = 0; i < 4; i++) {
                        if (isInBounds(xSurr1[i], ySurr1[i])) {
                            surrSpaces1.add(enemySpaces[xSurr1[i]][ySurr1[i]]);
                            if (isInBounds(xSurr2[i], ySurr2[i]) && enemySpaces[xSurr1[i]][ySurr1[i]].getStatus() == Space.HIT) {
                                surrSpaces2.add(enemySpaces[xSurr2[i]][ySurr2[i]]);
                                if (isInBounds(xFoll[i], yFoll[i]) && enemySpaces[xSurr2[i]][ySurr2[i]].getStatus() == Space.HIT) {
                                    followUps.add(enemySpaces[xFoll[i]][yFoll[i]]);
                                }
                            }
                        }
                    }//0 0 0 0 0 0 

                    //adds the attack groups to the attack queue in the proper order
                    attackQueue.add(0, surrSpaces1);
                    attackGroupIds.add(0, SURROUNDING_ONE);

                    //adds surrounding two group after surrounding one group
                    for (int i = 0; i < attackQueue.size(); i++) {
                        if (!attackGroupIds.get(i).equals(SURROUNDING_ONE)) {
                            attackQueue.add(i, surrSpaces2);
                            attackGroupIds.add(i, SURROUNDING_TWO);
                            break;
                        }
                    }

                    //adds follow up group after surrounding one and surrounding two groups
                    for (int i = 0; i < attackQueue.size(); i++) {
                        if (!attackGroupIds.get(i).equals(SURROUNDING_ONE)
                                && !attackGroupIds.get(i).equals(SURROUNDING_TWO)) {
                            attackQueue.add(i, followUps);
                            attackGroupIds.add(i, FOLLOW_UP);
                            break;
                        }
                    }
                    break; //end case
                }
                case SURROUNDING_ONE: {
                    //surrounding two and follow points for a specific direction from the reference point
                    int xSurr2 = 2 * ptLastAtk.x - ptRef.x,
                            ySurr2 = 2 * ptLastAtk.y - ptRef.y,
                            xFoll = 3 * ptLastAtk.x - 2 * ptRef.x,
                            yFoll = 3 * ptLastAtk.y - 2 * ptRef.y;

                    //checks for potential surrounding two point
                    if (isInBounds(xSurr2, ySurr2)) {
                        ArrayList<Space> surrSpace2 = new ArrayList<Space>();
                        surrSpace2.add(enemySpaces[xSurr2][ySurr2]);

                        //checks for potential follow up spaces
                        if (isInBounds(xFoll, yFoll) && enemySpaces[xSurr2][ySurr2].getStatus() == Space.HIT) {
                            ArrayList<Space> followUp = new ArrayList<Space>();
                            followUp.add(enemySpaces[xFoll][yFoll]);

                            //adds follow up group after surrounding one and surrounding two groups
                            for (int i = 0; i < attackQueue.size(); i++) {
                                if (!attackGroupIds.get(i).equals(SURROUNDING_ONE)
                                        && !attackGroupIds.get(i).equals(SURROUNDING_TWO)) {
                                    attackQueue.add(i, followUp);
                                    attackGroupIds.add(i, FOLLOW_UP);
                                    break;
                                }
                            }//0 0 0 0 0 0
                        }
                        //adds surrounding two group after surrounding one groups
                        for (int i = 0; i < attackQueue.size(); i++) {
                            if (!attackGroupIds.get(i).equals(SURROUNDING_ONE)) {
                                attackQueue.add(i, surrSpace2);
                                attackGroupIds.add(i, SURROUNDING_TWO);
                                break;
                            }
                        }//0 0 0 0 0 0
                    }
                    break; //end case
                }
                case SURROUNDING_TWO: {
                    //the follow up points for a specific direction from the reference point
                    int xFoll = ptLastAtk.x + (ptLastAtk.x - ptRef.x) / 2,
                            yFoll = ptLastAtk.y + (ptLastAtk.y - ptRef.y) / 2;

                    //checks for a potential follow up group
                    if (isInBounds(xFoll, yFoll)) {
                        ArrayList<Space> followUp = new ArrayList<Space>();
                        followUp.add(enemySpaces[xFoll][yFoll]);

                        //adds follow up group after surrounding one and surrounding two groups
                        for (int i = 0; i < attackQueue.size(); i++) {
                            if (!attackGroupIds.get(i).equals(SURROUNDING_ONE)
                                    && !attackGroupIds.get(i).equals(SURROUNDING_TWO)) {
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
    }

}//end class
