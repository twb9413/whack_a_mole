package server;

import common.WAMProtocol;
import java.net.Socket;
import java.io.IOException;
import java.net.ServerSocket;


/**
 * File: GameSever
 * @author Stanley Liang, Tyler Bradley
 */


public class WAMServer implements Runnable, WAMProtocol {

    /**
     * Fields of a WAMServer
     */

    private ServerSocket server;
    private String rows;
    private String columns;
    private int players;
    private int time;


    /**
     * Game server constructor
     */
    public WAMServer(int port, String rows, String columns, int players, int time) throws IOException{
        try{
            this.rows = rows;
            this.columns = columns;
            this.players = players;
            this.time = time;
            server = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Main method of the server
     * @param args Arguments for server, game-port#  #rows  #columns  #players  game-duration-seconds
     */
    public static void main(String[] args) throws IOException{
        if(args.length != 5){
            System.out.println("Usage Error: WAMServer");
        }
        if(Integer.parseInt(args[3]) < 1){
            System.out.println("Usage Error: 1 or more players needed");
        }
        int port = Integer.parseInt(args[0]);
        String rows = args[1];
        String columns = args[2];
        int players = Integer.parseInt(args[3]);
        int time = Integer.parseInt(args[4]);
        WAMServer server = new WAMServer(port, rows, columns, players, time);
        server.run();
    }


    /**
     * Run method for server, will have game
     */
    @Override
    public void run(){
        try {
            Socket[] players = new Socket[this.players+1];
            WhackAMolePlayer[] WAMplayers = new WhackAMolePlayer[this.players];
            for(int i = 1; i <= this.players; i++){
                System.out.println("Waiting for player " + i + " to connect");
                players[i] = server.accept();
                System.out.println("player " + i + " connected!");
                WAMplayers[i-1] = new WhackAMolePlayer(players[i], 0, i);
                WAMplayers[i-1].connect(rows, columns, i);
                new Thread(WAMplayers[i-1]).start();
            }

            WhackAMoleGame game = new WhackAMoleGame(WAMplayers, Integer.parseInt(rows), Integer.parseInt(columns), time);
            new Thread(game).run();
            server.close();
        } catch (IOException e) {
            System.err.println("Something has gone horribly wrong!");
            e.printStackTrace();
        }

    }

}
