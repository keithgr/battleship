/*========================================================================*\

 File: Player.java
 Date: 4/18/2016
 Name: Keith Grable

 \*========================================================================*/
package ai;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

public class Player {

    static int tw = 10;
    static int th = 10;

///////////////////////////
// M A I N   M E T H O D //
///////////////////////////
//
    public static void main(String[] args) {

        TwoSpacedPlayer p2 = new TwoSpacedPlayer(tw, th);
        ThreeSpacedPlayer p3 = new ThreeSpacedPlayer(tw, th);

        Player e = new Player(tw, th);

        for (int i = 0; i < tw * th; i++) {
            Point ap2 = p2.attack(),
                    ap3 = p3.attack();

            boolean isHit2 = !e.isVacant(new Ship(ap2.x, ap2.y, 1, true)),
                    isHit3 = !e.isVacant(new Ship(ap3.x, ap3.y, 1, true));
            p2.setLastAttackResult(isHit2);
            p3.setLastAttackResult(isHit3);

            //Turn num
            System.out.println("Turn " + (i + 1));

            dispBoard(p2, p3, e);
        }
    }

    private static void dispBoard(Player p2, Player p3, Player e) {
        for (int r = 0; r < p2.enemySpaces[0].length; r++) {

            for (int c2 = 0; c2 < p2.enemySpaces.length; c2++) {
                String s = ". ";
                if (!e.isVacant(new Ship(c2, r, 1, true))) {
                    s = "# ";
                }

                if (p2.enemySpaces[c2][r].getStatus() == Space.UNKNOWN) {
                    s = "\u001B[0m" + s;
                } else if (p2.enemySpaces[c2][r].getStatus() == Space.HIT) {
                    if (p2.ptLastAtk.x != c2 || p2.ptLastAtk.y != r) {
                        s = "\u001B[31m" + "X "; //red
                    } else {
                        s = "\u001B[35m" + "X "; //purple
                    }
                } else if (p2.enemySpaces[c2][r].getStatus() == Space.MISS) {
                    if (p2.ptLastAtk.x != c2 || p2.ptLastAtk.y != r) {
                        s = "\u001B[37m" + "X "; //white
                    } else {
                        s = "\u001B[34m" + "X "; //blue
                    }
                }
                System.out.print(s);
            }
            System.out.print("\t");
            for (int c3 = 0; c3 < p3.enemySpaces.length; c3++) {
                String s = ". ";
                if (!e.isVacant(new Ship(c3, r, 1, true))) {
                    s = "# ";
                }
                if (p3.enemySpaces[c3][r].getStatus() == Space.UNKNOWN) {
                    s = "\u001B[0m" + s;
                } else if (p3.enemySpaces[c3][r].getStatus() == Space.HIT) {
                    if (p3.ptLastAtk.x != c3 || p3.ptLastAtk.y != r) {
                        s = "\u001B[31m" + "X "; //red
                    } else {
                        s = "\u001B[35m" + "X "; //purple
                    }
                } else if (p3.enemySpaces[c3][r].getStatus() == Space.MISS) {
                    if (p3.ptLastAtk.x != c3 || p3.ptLastAtk.y != r) {
                        s = "\u001B[37m" + "X "; //white
                    } else {
                        s = "\u001B[34m" + "X "; //blue
                    }
                }
                System.out.print(s);
            }
            System.out.println("");
        }
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
                + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
                + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
                + "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        //"~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
    }//end method

///////////////////////   
// C O N S T A N T S //
///////////////////////
//
    //value for randomness
    protected final Random rand = new Random();

    //default board length/width
    private static final int DEF_BRD_SIZE = 10;

    //standard ship sizes
    private static final ArrayList<Integer> DEF_SHIP_SIZES() {
        ArrayList<Integer> temp = new ArrayList<Integer>();
        temp.add(2);
        temp.add(3);
        temp.add(3);
        temp.add(4);
        temp.add(5);
        return temp;
    }//end method

    //the attack phases for this class
    private static final String ALL_SPACES = "PLAYER, ALL_SPACES";

///////////////////////
// V A R I A B L E S //
///////////////////////
//
    //space arrays for player and enemy
    protected Space[][] playerSpaces = new Space[DEF_BRD_SIZE][DEF_BRD_SIZE],
            enemySpaces = new Space[DEF_BRD_SIZE][DEF_BRD_SIZE];

    //the actual ship size array for the player or enemy
    private ArrayList<Integer> shipSizes = DEF_SHIP_SIZES();

    //the player's ships
    private ArrayList<Ship> playerShips = new ArrayList<Ship>();

    //major array list contains array lists of potential attack spaces with 
    //____higher priority to array lists of lower indices
    //minor array list contains potential attack spaces of equal priority
    protected ArrayList<ArrayList<Space>> attackQueue = new ArrayList<ArrayList<Space>>();

    //String values that define attack groups within attack queue
    protected ArrayList<String> attackGroupIds = new ArrayList<String>();

    //coordinates of the player's most recent attack
    //to be sent to game interface
    protected Point ptLastAtk = new Point(-1, -1);

    //point of reference for non-main-phase attacks
    protected Point ptRef = new Point(-1, -1);

    //the string for the last attack phase
    protected String phaseLastAtk = "";

    //result (true if "HIT"; false if "MISS") of the player's most recent attack
    //to be recieved from game interface
    protected boolean resLastAtk = false;

///////////////////////////////////////////   
// C O N S T R U C T O R   M E T H O D S //
///////////////////////////////////////////
//
    //def cons
    public Player() {
        initAll(false);
    }//end cons

    //variable ship placement, board size cons
    public Player(int width, int height) {
        playerSpaces = enemySpaces = new Space[width][height];
        initAll(false);
    }//end cons

    //variable ship placement, board size cons
    public Player(int width, int height, boolean noAdj) {
        playerSpaces = enemySpaces = new Space[width][height];
        initAll(noAdj);
    }//end cons

    //variable ship placement, board size, ship sizes cons
    public Player(int width, int height, boolean noAdj, ArrayList<Integer> shipSzs) {
        playerSpaces = enemySpaces = new Space[width][height];
        shipSizes = shipSzs;
        initAll(noAdj);
    }//end cons

///////////////////////////////////////////   
// I N I T I A L I Z E R   M E T H O D S //
///////////////////////////////////////////
//      
    //calls all initializer methods
    private void initAll(boolean noAdj) {
        initSpaces();
        placeShips(noAdj);
        initAttackQueue();
    }//end method

    //initializes all spaces in player and enemy space arrays
    private void initSpaces() {
        for (int x = 0; x < playerSpaces.length; x++) {
            for (int y = 0; y < playerSpaces[0].length; y++) {
                playerSpaces[x][y] = new Space(x, y);
                enemySpaces[x][y] = new Space(x, y);
            }
        }
    }//end method

    //finds locations for ships of appropriate sizes
    private void placeShips(boolean noAdj) {
        //for loop iterates over each ship size
        for (int s = 0; s < shipSizes.size(); s++) {

            //randomized selection values
            boolean isHor = rand.nextBoolean();
            int xPl, yPl; //x,y coordinates of ship placement

            //ensures that all ship segments are in bounds
            if (isHor) {
                xPl = rand.nextInt(playerSpaces.length - (shipSizes.get(s) - 1));
                yPl = rand.nextInt(playerSpaces[0].length);
            } else {
                xPl = rand.nextInt(playerSpaces.length);
                yPl = rand.nextInt(playerSpaces[0].length - (shipSizes.get(s) - 1));
            }

            //potential ship is placed if it is, non adjacent, or, vacant and adjacency is allowed
            Ship potShip = new Ship(xPl, yPl, shipSizes.get(s), isHor);
            if (playerShips.isEmpty() || isNonAdj(potShip) || !noAdj && isVacant(potShip)) {
                playerShips.add(potShip);
            } else {
                s--; //iteration has failed
            }
        } //0 0 0 0 0 0 
    }//end method

    //establishes phase attack locations for the player
    protected void initAttackQueue() {
        //an attack group of all spaces
        ArrayList<Space> allSpaces = new ArrayList<Space>();

        //adds an array list of all attack spaces at the end of the queue
        for (int x = 0; x < enemySpaces.length; x++) {
            for (int y = 0; y < enemySpaces[0].length; y++) {
                allSpaces.add(enemySpaces[x][y]);
            }
        }
        attackQueue.add(allSpaces);

        //the ID for the attack group
        attackGroupIds.add(ALL_SPACES);
    }//end method 

/////////////////////////////////////  
// F U N C T I O N   M E T H O D S //
/////////////////////////////////////
// 
    //determines if a given point exists within player/enemy spaces array
    protected boolean isInBounds(int x, int y) {
        return x >= 0 && x < playerSpaces.length
                && y >= 0 && y < playerSpaces[0].length;
    }//end method

    //returns true if a given ship is non-adjacent (lateral) to a given array list of ships
    private boolean isNonAdj(Ship testShip) {
        //for loop iterates over each segment of the test ship
        for (int seg = 0; seg < testShip.getSize(); seg++) {
            Point testSeg = testShip.getSeg(seg);
            int xTest = testSeg.x, yTest = testSeg.y;

            //for loop iterates over each ship in the array list of other ships
            for (Ship otherShip : playerShips) {

                //for loop iterates over each segment of the current other ship
                for (int oSeg = 0; oSeg < otherShip.getSize(); oSeg++) {
                    Point otherShipSeg = otherShip.getSeg(oSeg);
                    int xOther = otherShipSeg.x, yOther = otherShipSeg.y;

                    //looks for cases where the test segment and other segment are adjacent
                    if (Math.abs(xTest - xOther) <= 1 && Math.abs(yTest - yOther) <= 1
                            && (Math.abs(xTest - xOther) != 1 || Math.abs(yTest - yOther) != 1)) {
                        return false;
                    }
                }//2 2 2 2 2 2
            }//1 1 1 1 1 1 
        }//0 0 0 0 0 0 
        return true; //true by default
    }//end method

    //returns true if a given ship can be placed
    boolean isVacant(Ship testShip) {
        //for loop iterates over each segment of the test ship
        for (int seg = 0; seg < testShip.getSize(); seg++) {
            Point testSeg = testShip.getSeg(seg);
            int xTest = testSeg.x, yTest = testSeg.y;

            //for loop iterates over each ship in the array list of other ships
            for (Ship otherShip : playerShips) {

                //for loop iterates over each segment of the current other ship
                for (int oSeg = 0; oSeg < otherShip.getSize(); oSeg++) {
                    Point otherShipSeg = otherShip.getSeg(oSeg);
                    int xOther = otherShipSeg.x, yOther = otherShipSeg.y;

                    //looks for cases where the test segment and other segment overlapping
                    if (xTest == xOther && yTest == yOther) {
                        return false;
                    }
                }//2 2 2 2 2 2
            }//1 1 1 1 1 1 
        }//0 0 0 0 0 0 
        return true; //true by default
    }//end method

    /*
     I assume that the interface will not allow the player to update information
     after the attack coordinates have been sent; therefore, player attack data must
     be updated at the beginning of the attack.
    
     EXAMPLE OF IMPLEMENTATION FROM THE INTERFACE CLASS:
    
     this.thisPlayer.thisAttack(myPlayer.attack()); //get attack coordinates from myPlayer for this turn
     Point attackPoint = myPlayer.getLastAttackPoint(); //get the point on the enemy board to be checked
     boolean isHit = this.thisEnemy.getBoard()[attackPoint.x][attackPoint.y]; //check point on enemy board
     this.thisPlayer.myPlayer.setLastAttackResult(isHit); //send feedback to myPlayer
     */
    //
    //returns coordinates for an attack from the player
    //removes a space from attack queue
    public Point attack() {
        //adds new spaces to the queue if necessary
        updateQueue();

        //prepares the queue immediately before an attack space is selected
        cleanQueue();

        //gets the highest priority attack group
        ArrayList<Space> attackGroup = attackQueue.get(0);

        //gets the phase of the attack
        phaseLastAtk = attackGroupIds.get(0);

        //gets the location of a random space in the attack group
        //assigns it to the last attack point
        ptLastAtk = attackGroup.get(rand.nextInt(attackGroup.size())).getLocation();

        return ptLastAtk;
    }//end method

    //returns the point of the last attack  
    public Point getLastAttackPoint() {
        return ptLastAtk;
    }//end method

    //updates the result of the last player attack
    //updates enemy spaces
    public void setLastAttackResult(boolean isHit) {
        resLastAtk = isHit;

        //if this is not the first attack
        //then the last attacked space is updated by status
        if (ptLastAtk.x != -1 && ptLastAtk.y != -1) {
            if (resLastAtk) {
                enemySpaces[ptLastAtk.x][ptLastAtk.y].setStatus(Space.HIT);
            } else {
                enemySpaces[ptLastAtk.x][ptLastAtk.y].setStatus(Space.MISS);
            }
        }
    }//end method

    //responds to game feedback by adding highest priority points to attack queue if necessary
    protected void updateQueue() {
        //EMPTY FOR THIS CLASS
    }//end method

    //removes empty array lists and known spaces from attack queue
    private void cleanQueue() {
        //iterates over each attack group
        for (int i = 0; i < attackQueue.size(); i++) {
            ArrayList<Space> attackGroup = attackQueue.get(i);

            //iterates over each space within the current attack group
            for (int j = 0; j < attackGroup.size(); j++) {
                if (attackGroup.get(j).getStatus() != Space.UNKNOWN) {
                    attackGroup.remove(j);
                    j--; //j shifts left to accomodate removal
                }
            }//1 1 1 1 1 1 

            //removes the attack group and ID if it is empty
            if (attackGroup.isEmpty()) {
                attackQueue.remove(i);
                attackGroupIds.remove(i);
                i--;//i shifts left to accomodate removal
            }

        }//0 0 0 0 0 0 
    }//end method

    //returns the array list of ship sizes for the player
    ArrayList<Integer> getShipSizes() {
        ArrayList<Integer> shipSzs = new ArrayList<Integer>();
        for (Integer shipSize : shipSizes) {
            shipSzs.add(shipSize);
        }
        return shipSzs;
    }//end method

///////////////////////////////////////////
// L A S T   R E S O R T   M E T H O D S //
///////////////////////////////////////////
//
    //foreits the game for when loss is inevitable 
    private void forfeitGame(boolean isSalty) {
        if (isSalty) {
            System.out.println("NO CONTEST");
            stallGame(); //ENSURES THAT THERE IS NO WINNER
        } else {
            System.out.println("JUST FINISH THE BLOODY GAME!");
        }
    }//end method

    //executes a never-ending loop, thus stopping progression of the game
    private void stallGame() {
        while (true) {
        }
    }//end method 
}//end class
