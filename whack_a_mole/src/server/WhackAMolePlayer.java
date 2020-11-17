package server;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static common.WAMProtocol.*;

/**
 * Class to represent a player
 */
public class WhackAMolePlayer implements Closeable, Runnable {
    /** The {@link Socket} used to communicate with the client. */
    private Socket socket;
    /** The Scanner used to read responses */
    private Scanner scanner;
    /** The PrintStream send requests to the client */
    private PrintStream printStream;
    /** The score that the player has */
    private int score;
    /** The number of the player */
    private int playerNum;


    /**
     * Constructor for WhackAMole Player
     * @param socket Socket used to communicate to Client
     * @throws IOException
     */
    public WhackAMolePlayer(Socket socket, int score, int playerNum) throws IOException {
        this.socket = socket;
        this.score = score;
        this.playerNum = playerNum;
        try {
            scanner = new Scanner(socket.getInputStream());
            printStream = new PrintStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    /**
     * Sends Connect (WELCOME) message to client
     */
    public void connect(String rows, String columns, int playerNum) {
        printStream.println(WELCOME + " " + rows + " " + columns + " " + playerNum);
    }


    /**
     * called to send game won message to player
     */
    public void gameWon(){
        printStream.println(GAME_WON);
    }

    /**
     * called to send the game lost message to player
     */
    public void gameLost(){
        printStream.println(GAME_LOST);
    }

    /**
     * called to send the game tied message to player
     */
    public void gameTied(){
        printStream.println(GAME_TIED);
    }

    /**
     * Called to send the Error message to a client
     * @param message error message
     */
    public void error(String message){
        printStream.println(ERROR + " " + message);
    }

    /**
     * Called to send the score message to a client
     * return player score
     */
    public int getScore(){
        return score;
    }

    /**
     * Will send the moleUp message to player
     * @param position where the mole should appear
     */
    public void moleUp(int position){
        printStream.println(MOLE_UP + " " + position);
    }

    /**
     * Will send moleDown message to player
     * @param position where the mole should go down
     */
    public void moleDown(int position){
        printStream.println(MOLE_DOWN + " " + position);
    }

    /**
     * Listens for the whack messages from client
     */
    public void run(){
        try{

            System.out.println("PLAYER RUNNING");
            while(true){
                String str = scanner.nextLine();
                if(str.equals(WHACK)) {
                    System.out.println("got WHACK");
                    this.score += 2;
                    printStream.println(SCORE + " " + 2);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * Called to close the client connection once all the moles have been whacked.
     */
    @Override
    public void close() {
        try {
            socket.close();
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
