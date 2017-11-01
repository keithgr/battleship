
/*========================================================================*\

 File: GameSim.java
 Date: 5/11/2016
 Name: Keith Grable

 \*========================================================================*/
package ai;

import java.awt.Point;

//http://www.calculatorsoup.com/calculators/statistics/statistics.php

public class GameSim {

    static int tw = 10;
    static int th = 10;
    static int sampSize = 5000; //sample size

///////////////////////////
// M A I N   M E T H O D //
///////////////////////////
//  
    //runs numerous trials
    //generates a frequency histogram
    public static void main(String[] args) {

        //makes histogram
        int[] scoreFreqs = new int[2 * tw * th + 1];
        int[] data = new int[sampSize];

        int p2Wins = 0, p3Wins = 0;
        for (int i = 0; i < sampSize; i++) {
            TwoSpacedPlayer p2 = new TwoSpacedPlayer(tw, th); //new independent players
            ThreeSpacedPlayer p3 = new ThreeSpacedPlayer(tw, th);
            Player e = new Player(tw, th); //shared enemy

            int scoreDiff = simulateGame(p2, e) - simulateGame(p3, e);

            if (scoreDiff < 0) {
                p2Wins++;
            } else if (scoreDiff > 0) {
                p3Wins++;
            }

            System.out.print(scoreDiff + " ");
            if (i % 500 == 499) {
                System.out.println("");
            }

            scoreFreqs[scoreDiff + tw * th]++;
        }

        double p2Prop = (double) p2Wins / sampSize, p3Prop = (double) p3Wins / sampSize;
        System.out.println("");
        System.out.println(p2Wins + " : " + p2Prop);
        System.out.println(p3Wins + " : " + p3Prop);
        System.out.println("");

        //displays histogram
        for (int i = 0; i <= 2 * tw * th; i++) {
            System.out.println(getHistBar(scoreFreqs, i));
        }

        //gets and displays mean
        int sum = 0, freqCount = 0;
        for (int i = 0; i <= 2 * tw * th; i++) {
            sum += (i - tw * th) * scoreFreqs[i];
            freqCount += scoreFreqs[i];
        }
        System.out.println("");
        System.out.println("Mean: " + ((double) sum / freqCount));
    }//end method

    //simulates one single-player game
    //returns the attempts taken
    private static int simulateGame(Player p, Player e) {
        //sum of the ship sizes
        int hitsRequired = 0;
        for (int i = 0; i < e.getShipSizes().size(); i++) {
            hitsRequired += e.getShipSizes().get(i);
        }

        //counters
        int attemptCount = 0, hitCount = 0;

        //while hitcount is less than the necessary hits to win
        while (hitCount < hitsRequired) {
            Point atkPt = p.attack(); //executes player's attack and gets the coordinates
            boolean isHit = !e.isVacant(new Ship(atkPt.x, atkPt.y, 1, true)); //true if point is occupied by an enemy ship
            p.setLastAttackResult(isHit); //sends feedback to the player
            if (isHit) {
                hitCount++;
            }
            attemptCount++;
        }
        return attemptCount;
    }//end method

    //returns a bar for a histogram
    private static String getHistBar(int[] freqs, int x) {
        String bar = Integer.toString(x - tw * th) + ": ";
        while (bar.length() < "-100: ".length()) {
            bar = " " + bar;
        }
        for (int i = 0; i < freqs[x]; i++) {
            bar += "#";
        }
        return bar;
    }//end method
}//end class
