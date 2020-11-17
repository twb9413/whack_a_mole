package client.gui;

import java.util.List;

// javafx imports
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


/**
 * Author: Tyler Bradley, Stanley Liang
 */

public class WAMGUI extends Application implements Observer<WhackAMoleBoard>{
    /** WAM client */
    private WhackAMoleClient WAMClient;
    /** WAM board */
    private WhackAMoleBoard WAMBoard;

    /** number of columns */
    private int col; // will use WAM some WAMBoard.get methods
    /** number of rows */
    private int rows; //
    /** Image imports */
    private Image m = new Image(getClass().getResourceAsStream("mole.png"));
    private Image h = new Image(getClass().getResourceAsStream("hole.png"));
    /** field for GridPane which will be added to the center of a BorderPane */
    private GridPane holeGrid = new GridPane();
    /** Label for Score */
    private Label score = new Label("Score: # \n");
    private Label status = new Label("status of game will be here");

    private Boolean[][] moleWhacked;




    /**
     * Initializes the game
     * Sets up client and gets parameters to make board
     * adds the board to the observer
     */
    @Override
    public void init() {
        try{
            List<String> args = getParameters().getRaw();

            String host = args.get(0);
            int port = Integer.parseInt(args.get(1));

            // create the network connection
            this.WAMClient = new WhackAMoleClient(host, port, WAMBoard);

            // sets the boards columns and rows to be used in the GUI
            this.WAMBoard = this.WAMClient.getBoard();
            this.rows = WAMBoard.getRows();
            this.col = WAMBoard.getColumns();

            this.WAMBoard.addObserver(this);

            this.moleWhacked = new Boolean[rows][col];

        } catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * Start method for the GUI, will create the playing field
     * @param stage what everything is added to
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Whack-a-Mole!");

        BorderPane borderPane = new BorderPane();

        // adds empty holes to a gridPane
        for(int r = 0; r < rows; r++) {
            for (int c = 0; c < col; c++) {
                ImageView hole = new ImageView(h);
                hole.minHeight(64);
                hole.maxHeight(64);
                holeGrid.add(hole, c, r);
                moleWhacked[r][c] = false;
            }
        }

        // VBox at the top will show score and status of the game
        VBox vBox = new VBox(status, score);

        // adding gridPane to center of border pane
        borderPane.setCenter(holeGrid);
        borderPane.setTop(vBox);

        Scene scene = new Scene(borderPane);
        stage.setScene(scene);
        stage.show();

        // start the client
        this.WAMClient.startListener();
        this.refresh(WAMBoard);

    }

    /**
     * makes a button
     * @return button
     */
    public Button makeButton(){
        Button moleButton =  new Button();
        ImageView mole = new ImageView(m);
        moleButton.setGraphic(mole);
        // same dimensions as the grid hole it is covering
        moleButton.setMaxWidth(64);
        moleButton.setMinWidth(64);
        moleButton.setMaxHeight(64);
        moleButton.setMinHeight(64);
        return moleButton;
    }


    /**
     * called at the end of the game
     * no moles will be on the board
     */
    public void getDown(){
        for(int r = 0; r < rows; r++) {
            for (int c = 0; c < col; c++) {
                ImageView hole = new ImageView(h);
                hole.minHeight(128);
                hole.maxHeight(128);
                holeGrid.add(hole, c, r);
            }
        }
    }


    /**
     * Refreshes the GUI
     */
    private void refresh(WhackAMoleBoard WAMBoard) {
        // checking each position and updating whether or not a mole should be shown or not
        for(int r = 0; r<rows; r++){
            for(int c=0; c<col; c++)
                if (WAMBoard.getMoleStatus(r, c).equals(WhackAMoleBoard.moleStatus.DOWN)) {
                    moleWhacked[r][c] = false;
                    ImageView hole = new ImageView(h);
                    hole.minHeight(64);
                    hole.maxHeight(64);
                    holeGrid.add(hole, c, r);
                } else if(!moleWhacked(r, c)){
                    showButton(r, c);
                }
        }

        // gets the game status from this clients board and displays the appropriate text in a VBox
        WhackAMoleBoard.gameStatus gameStatus = WAMBoard.getStatus();
        switch (gameStatus) {
            case ERROR:
                status.setText("An error has occurred!");
                break;
            case I_WON:
                status.setText("You won!");
                score.setText(Integer.toString(WAMBoard.getScore()));
                getDown();
                break;
            case I_LOST:
                status.setText("You lost!");
                score.setText(Integer.toString(WAMBoard.getScore()));
                getDown();
                break;
            case TIE:
                status.setText("Tie!");
                score.setText(Integer.toString(WAMBoard.getScore()));
                getDown();
                break;
            default:
                status.setText("Ongoing");
                score.setText(Integer.toString(WAMBoard.getScore()));
        }
    }

    /**
     * Refreshes the WAMBoard
     * @param WAMBoard this clients board
     */
    public void update(WhackAMoleBoard WAMBoard) {
        if ( Platform.isFxApplicationThread() ) {
            this.refresh(this.WAMBoard);
        }
        else {
            Platform.runLater( () -> this.refresh(WAMBoard) );
        }
    }

    public void showButton(int row, int col){
        ImageView hole = new ImageView(h);
        int position = (row*this.col)+col;
        Button moleButton = makeButton();
        holeGrid.add(moleButton, col, row);
        moleButton.setOnAction(actionEvent -> {
            moleWhacked[row][col] = true;
            sendWhack(WAMClient.getPlayerNum(), position);
            holeGrid.add(hole, col, row);
        });
    }

    /**
     * returns if the mole was whacked in that spot
     * @param row row pos
     * @param col col pos
     * @return
     */
    public Boolean moleWhacked(int row, int col){
        return moleWhacked[row][col];
    }

    /**
     * action of a button, will remove itself and send a score message to board
     */
    public void sendWhack(int player, int number){
        System.out.println("Whack sent by player: " + player + " mole: " + number);
        WAMClient.sendWhack(player, number);
    }

    /**
     * The main method expects the host and port.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java ClientGUI host port");
            System.exit(-1);
        } else {
            Application.launch(args);
        }
    }
}
