package client.gui;

import common.WAMProtocol;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static common.WAMProtocol.*;

/**
 * Author: Tyler Bradley, Stanley Liang
 */

public class WhackAMoleClient {

    /**
     * Server client fields
     */
    private Socket clientSoc;

    private Scanner netIn;

    private PrintStream netOut;

    private WhackAMoleBoard board;

    private int playerNum;

    /**
     * WhackAMole client constructor
     * @param host host string
     * @param port port number
     * @param board game board
     * @throws IOException exception
     */
    public WhackAMoleClient(String host, int port, WhackAMoleBoard board) throws IOException{
        try{
            this.clientSoc = new Socket(host, port);
            this.netIn = new Scanner(clientSoc.getInputStream());
            this.netOut = new PrintStream(clientSoc.getOutputStream());

            String request = this.netIn.next();
            String args = this.netIn.nextLine();
            String[] gameParameters = args.split(" ");
            if(request.equals(WAMProtocol.WELCOME)){
                System.out.println("Connected to server " + this.clientSoc);
                this.board = new WhackAMoleBoard(Integer.parseInt(gameParameters[1]), Integer.parseInt(gameParameters[2]));
                this.board.setRows(Integer.parseInt(gameParameters[1]));
                System.out.println(Integer.parseInt(gameParameters[1])); // getting right rows and columns, not setting them in the GUI
                this.board.setColumns(Integer.parseInt(gameParameters[2]));
                System.out.println(Integer.parseInt(gameParameters[2]));
                this.playerNum = Integer.parseInt(gameParameters[3]);
                System.out.println("player number: " + this.playerNum);
            }
            else{
                throw new IOException("Expected WELCOME from server.");
            }
        }
        catch (IOException e){
            throw new IOException(e);
        }
    }

    /**
     * returns the board
     * @return WAM board
     */
    public WhackAMoleBoard getBoard(){ return this.board; }


    public int getPlayerNum(){
        return playerNum;
    }
    /**
     * Notifies the board that a mole up was received
     * @param position number proceeding MOLE_UP message
     * @return number that will be turned into coordinates
     */
    public int moleUp(int position){
        this.board.moleUp(position);
        return position;
    }

    /**
     * Notifies the board that a mole down was received
     * @param position number proceeding MOLE_DOWN message
     * @return number that will be turned into coordinates
     */
    public int moleDown(int position){
        this.board.moleDown(position);
        return position;
    }


    /**
     * Notifies the board that the client won
     */
    public void gameWon(){
        System.out.println("You win!");
        this.board.gameWon();
    }

    /**
     * Notifies the board that client lost
     */
    public void gameLost(){
        System.out.println("You lose!");
        this.board.gameLost();
    }

    /**
     * Game tied on board side
     */
    public void gameTied(){
        System.out.println("Tie!");
        this.board.gameTied();
    }

    public void whacked(int position){
        System.out.println("Mole whacked!");
        this.board.whacked(position);
    }

    public void updateScore(int updatedScore){
        System.out.println("Score updated!" + updatedScore);
        this.board.updateScore(updatedScore);
    }


    /**
     * Starts the client thread
     */
    public void startListener(){
        new Thread(() -> this.run()).start();
    }

    /**
     * closes the client
     */
    public void close(){
        try {
            this.clientSoc.close();
        }
        catch( IOException ioe ) {
            ioe.printStackTrace();
        }
        this.board.close();
    }


    /**
     * Will send the WHACK message to the server from this client
     */
    public void sendWhack(int player, int number){

        this.netOut.println(WHACK);
        netOut.flush();
    }

    /**
     * Run method for client thread
     */
    public void run(){
        boolean go = true;
        while(go){
            try{
                String nextLine = this.netIn.nextLine();
                String request[] = nextLine.split(" ");
                System.out.println( "Net message in = \"" + request[0] + '"' );

                switch (request[0]){
                    case MOLE_UP:
                        moleUp(Integer.parseInt(request[1]));
                        String pos = request[1];
                        System.out.println("A mole should go up at postition " + pos);
                        break;
                    case MOLE_DOWN:
                        moleDown(Integer.parseInt(request[1]));
                        String posd = request[1];
                        System.out.println("A mole should go down at postition " + posd);
                        break;
                    case GAME_WON:
                        gameWon();
                        go = false;
                        break;
                    case GAME_LOST:
                        gameLost();
                        go = false;
                        break;
                    case GAME_TIED:
                        gameTied();
                        go = false;
                        break;
                    case WHACK:
                        whacked(Integer.parseInt(request[1]));
                        String posf = request[1];
                        System.out.println("A mole has been whacked at postition " + posf);
                        break;
                    case SCORE:
                        updateScore(2);
                        break;
                    case ERROR:
                        go = false;
                        break;
                    default:
                        System.err.println("Unrecognized request: " + request[0]);
                        break;
                }
            }
            catch (NoSuchElementException nse){
                nse.printStackTrace();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        this.close();
    }
}
