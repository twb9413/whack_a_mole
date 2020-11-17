package client.gui;

import java.util.LinkedList;
import java.util.List;

/**
 * Author: Tyler Bradley, Stanley Liang
 */

public class WhackAMoleBoard {

    /**
     * Possible statuses of the mole
     */
    public enum moleStatus{
        UP, DOWN
    }

    /**
     * Status of game
     */
    public enum gameStatus{
        ONGOING, I_WON, I_LOST, TIE, ERROR;

        private String message = null;

        public void setMsg( String msg ) {
            this.message = msg;
        }

        @Override
        public String toString() {
            return super.toString() +
                    this.message == null ? "" : ( '(' + this.message + ')' );
        }
    }

    /**
     * Which player hit the mole first
     */
    public enum hitOrder{
        FIRST, NOTFIRST, NONE;

        private String message = null;

        public void setMsg( String msg ) {
            this.message = msg;
        }

        @Override
        public String toString() {
            return super.toString() +
                    this.message == null ? "" : ( '(' + this.message + ')' );
        }
    }

    /**
     * Number of Rows and Columns for this game
     */
    private int rows;
    private int columns;

    /**
     * Observer for the board
     */
    private List<Observer<WhackAMoleBoard>> observers;

    /**
     * Enum for the game status and status for each mole
     */
    private gameStatus gameStatus;
    private moleStatus[][] moleStatuses;
    private hitOrder hitOrder;
    private int score = 0;

    /**
     * Constructor for Board
     * Sets the status of each mole to DOWN to start
     */
    public WhackAMoleBoard(int rows, int columns){
        this.observers = new LinkedList<>();
        this.gameStatus = gameStatus.ONGOING;
        this.hitOrder = hitOrder.NONE;
        this.moleStatuses = new moleStatus[rows + 1][columns + 1];
        for(int col = 0; col < columns + 1 ; col++){
            for(int row = 0; row < rows + 1; row++){
                moleStatuses[row][col] = moleStatus.DOWN;
            }
        }
    }


    /**
     * Set rows and columns
     */
    public void setRows(int rows){ this.rows = rows; }
    public void setColumns(int columns){ this.columns = columns; }

    /**
     * getRows and getColumns
     */
    public int getRows(){
        return this.rows;
    }
    public int getColumns(){
        return this.columns;
    }

    /**
     * Gets the game status
     * @return status
     */
    public gameStatus getStatus(){ return this.gameStatus; }

    /**
     * Gets the status of the mole at coordinate specified
     * @param row row 0 -> number of rows - 1
     * @param column same as rows but for columns
     * @return the status of the mole at that position
     */
    public moleStatus getMoleStatus(int row, int column){ return this.moleStatuses[row][column]; }

    /**
     * Gets the hit order of the players
     * @return hit order
     */
    public hitOrder getHitOrder(){
        return this.hitOrder;
    }

    /**
     * Gets the score for the player
     * @return score
     */
    public int getScore(){
        return this.score;
    }


    /**
     * adds an Observer with WAM board
     * @param observer
     */
    public void addObserver(Observer<WhackAMoleBoard> observer) {
        this.observers.add(observer);
    }

    /**
     * Alerts the observer
     * Will be used in other methods
     */
    private void alertObservers() {
        for (Observer<WhackAMoleBoard> obs: this.observers ) {
            obs.update(this);
        }
    }


    /**
     * takes a position and converts it to a coordinate
     * then sets the mole at that coordinate to the UP status
     * @param position number received from the client when they receive MOLE_UP message
     */
    public void moleUp(int position){
        int row = position / columns;
        int col = position % columns;
        this.moleStatuses[row][col] = moleStatus.UP;
        alertObservers();
    }

    /**
     * Take the position of a mole and turns it into a coordinate
     * Sets MOLE DOWN status at that coordinate
     * @param position number received from the client when they receive MOLE_DOWN message
     */
    public void moleDown(int position){
        int row = position / columns;
        int col = position % columns;
        this.moleStatuses[row][col] = moleStatus.DOWN;
        alertObservers();
    }


    /**
     * Board sets status to game won
     */
    public void gameWon(){
        this.gameStatus = gameStatus.I_WON;
        alertObservers();
    }

    /**
     * Sets game status Lost
     */
    public void gameLost(){
        this.gameStatus = gameStatus.I_LOST;
        alertObservers();
    }

    public void gameTied(){
        this.gameStatus = gameStatus.TIE;
        alertObservers();
    }

    /**
     * Who hit the mole first
     * @param position position of the mole
     */
    public void whacked(int position){
        this.hitOrder = hitOrder.FIRST;
        alertObservers();
    }

    /**
     * Who did not hit the mole first
     * @param position position of the mole
     */
    public void whackF(int position){
        this.hitOrder = hitOrder.NOTFIRST;
        alertObservers();
    }

    /**
     * Error occurred, sets status
     * @param arguments error msg
     */
    public void error(String arguments) {
        this.gameStatus = gameStatus.ERROR;
        this.gameStatus.setMsg(arguments);
        alertObservers();
    }

    public void updateScore(int updatedScore){
        this.score += updatedScore;
        alertObservers();
    }

    /**
     * close
     */
    public void close(){
        alertObservers();
    }
}
