package server;

import java.util.*;

public class WhackAMoleGame implements Runnable{

    private int rows;
    private int columns;
    private int time;
    private int rTime;
    private WhackAMolePlayer[] players;
    private WhackAMole game;
    private boolean[] moleStats;

    /**
     * Initializes the game.
     *
     * @param players array of players
     * @param rows number of rows
     * @param columns number of columns
     * @param time total time allotted
     */
    public WhackAMoleGame(WhackAMolePlayer[] players, int rows, int columns, int time) {
        this.players = players;
        this.rows = rows;
        this.columns = columns;
        this.time = time;
        this.moleStats = new boolean[rows * columns];
        for(int i = 0; i < moleStats.length; i++){
            moleStats[i] = false;
        }
        game = new WhackAMole(rows, columns, players.length, time);
    }

    /**
     * Run method for game
     * Will run until WAM runs out of time
     */
    @Override
    public void run(){
        boolean running = true;
        while (running){
            Thread randomTimer = new Thread(); // will be used to randomize the timing
            try {
                Random random = new Random();
                int t = random.nextInt(250) + 750; // picks random value between 1 and 2 seconds
                randomTimer.sleep(t);
                this.rTime = t;
            } catch (InterruptedException IE){
                IE.printStackTrace();
            }
            Random upMole = new Random();
            int up = upMole.nextInt(rows*columns); // picks a random mole to pop up
            Random downMole = new Random();
            int down = downMole.nextInt(rows*columns); // picks a random mole to go down
            moleStats[down] = false;
            for (WhackAMolePlayer p: players
            ) {
                p.moleUp(up);
                p.moleDown(down);
            }
            if(gameRunning()){
                running = false;
            }
        }

        for(int i = 0; i < players.length; i++){
            players[i].close();
        }
    }

    /**
     * determines if the game is running
     * also determines winner and sends messages
     * @return boolean
     */
    public boolean gameRunning(){
        if(game.timeUp(this.rTime)){
            int highest = -1;
            ArrayList<WhackAMolePlayer> winners = new ArrayList<>();
            ArrayList<WhackAMolePlayer> losers = new ArrayList<>();
            for(int i = 0; i < players.length; i++) {
                if (players[i].getScore() > highest) {
                    highest = players[i].getScore();
                    winners.clear();
                    winners.add(players[i]);
                }
                else if (players[i].getScore() == highest) {
                    winners.add(players[i]);
                }
                else {
                    losers.add(players[i]);
                }
            }

            if(winners.size() > 1){
                for(int x = 0; x < winners.size(); x++){
                    winners.get(x).gameTied();
                }
                for(int y = 0; y < losers.size(); y++){
                    losers.get(y).gameLost();
                }
            }
            else{
                winners.get(0).gameWon();
            }
            for(int y = 0; y < losers.size(); y++){
                losers.get(y).gameLost();
            }
            return true;

        }
        else{
            return false;
        }
    }
}
