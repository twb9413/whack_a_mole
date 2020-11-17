package server;

import common.WAMProtocol;

import java.sql.Time;
import java.util.Scanner;
import java.net.Socket;
import java.io.IOException;
import java.net.ServerSocket;
import java.io.PrintStream;
import java.util.Timer;
import java.util.TimerTask;

/**
 * File: WhackAMole
 * @author Stanley Liang, Tyler Bradley
 */

public class WhackAMole implements WAMProtocol {

    /**
     * Game fields
     */
    private int rows;
    private int columns;
    private int numPlayers;
    private int playerNum;
    private int totalTime = 0;
    private int currentTime = 0;
    private int time;

    /**
     * Game constructor
     */
    public WhackAMole(int rows, int columns, int numPlayers, int time) {
        this.rows = rows;
        this.columns = columns;
        this.numPlayers = numPlayers;
        this.time = time*1000;
    }


    /**
     * is the game running
     */
    public boolean timeUp(int rTime) {
        currentTime += totalTime + rTime;
        if(currentTime >= this.time){
            return true;
        }else {
            return false;
        }
    }
}
