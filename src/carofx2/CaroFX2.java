package carofx2;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;

public class CaroFX2 extends Application {

    private boolean playable = true;
    private int playerFlag = 1;
    private Tile[][] board = new Tile[20][20];
    public static int[][] arrBoard = new int[20][20];
    private List<Tile> comboWin1 = new ArrayList<>();
    private List<Tile> comboWin2 = new ArrayList<>();
    public List<Tile> listUndo = new ArrayList<>();

    public Pane root = new Pane();
    public Pane chessBoard = new Pane();
    public Button btnNewGame = new Button("New game");
    public Button btnUndo = new Button("Undo");
    public static Label lbTime = new Label();
    public static Label lbTurn = new Label();
    public Integer seconds = 0;
    public Integer minutes = 0;
    public Integer hours = 0;
    public Timeline clock = null;

    
    // khoi tao cac thanh phan de dua vao scene
    public Parent createContent() {
        clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            updateTimer();            
        }), new KeyFrame(Duration.seconds(1)));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
        root.setPrefSize(800, 600);
        chessBoard.setPrefSize(600, 600);

        btnNewGame.setLayoutX(650);
        btnNewGame.setLayoutY(10);

        btnUndo.setLayoutX(650);
        btnUndo.setLayoutY(50);
        
        lbTurn.setFont(new Font("TimeNewRoman", 20));
        lbTurn.setLayoutX(650);
        lbTurn.setLayoutY(90);
        lbTurn.setText("Turn X");
        
        lbTime.setFont(new Font("TimeNewRoman", 20));
        lbTime.setLayoutX(650);
        lbTime.setLayoutY(130);
                
        btnNewGame.setOnAction(event -> {
            resetBoard();
            seconds = 0;
            minutes = 0;
            hours = 0;
            clock.play();
        });
        btnUndo.setOnAction(event -> {
            undoMove();
            clock.play();
        });

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                Tile tile = new Tile();
                tile.setTranslateX(j * 30); // dich chuyen vi tri o vuong ban co de ve
                tile.setTranslateY(i * 30);

                chessBoard.getChildren().add(tile);

                board[j][i] = tile;
            }
        }
        root.getChildren().add(chessBoard);
        root.getChildren().add(btnNewGame);
        root.getChildren().add(btnUndo);
        root.getChildren().add(lbTime);
        root.getChildren().add(lbTurn);

        return root;
    }
    // ham tang thoi gian
    void updateTimer() {
        String _seconds;
        String _minutes;
        String _hours;
        if (seconds == 60) {
            seconds = 00;
            minutes++;
        }
        if (minutes == 60) {
            minutes = 00;
            hours++;
        }
                
        if (seconds < 10) {
            _seconds = "0" + seconds;            
        }else _seconds = seconds.toString();
        
        if (minutes < 10) {
            _minutes = "0" + minutes;            
        }
        else _minutes = minutes.toString();
        if (hours < 10) {
            _hours = "0" + hours;         
        }else _hours = hours.toString();
        lbTime.setText(String.valueOf("Time : " + _hours + ":" + _minutes + ":" + _seconds));
       
        seconds++;
    }

    @Override
    public void start(Stage primaryStage) {

        primaryStage.setTitle("CARO by JAVAFX");
        primaryStage.setResizable(false);

        primaryStage.setScene(new Scene(createContent()));

        primaryStage.show();
    }

    private class Tile extends StackPane {

        private Text text = new Text();
        public Rectangle border;

        public Tile() {
            // tap cac o ban co
            border = new Rectangle(30, 30);
            border.setFill(Color.YELLOW);
            border.setStroke(Color.BLACK); // chon may cho vien hinh vuong

            text.setFont(Font.font(30));

            setAlignment(Pos.CENTER);
            getChildren().addAll(border, text);
            //set su kien khi nhan vao o ban co
            setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (!playable) {
                        return;
                    }
                    int col = (int) ((getCenterX() - board[0][0].getCenterX()) / 30);
                    int row = (int) ((getCenterY() - board[0][0].getCenterY()) / 30);

                    if (arrBoard[row][col] != 0) {
                        return;
                    }
                    if (event.getButton() == MouseButton.PRIMARY && arrBoard[row][col] == 0 && playerFlag == 1) {
                        arrBoard[row][col] = 1;
                        drawX();
                        listUndo.add(board[col][row]);
                        listUndo.get(listUndo.size() - 1).border.setFill(Color.WHITE);
                        if(listUndo.size() > 1) listUndo.get(listUndo.size() - 2).border.setFill(Color.YELLOW);
                        if (checkWin(row, col) == 1) {
                            System.out.println("Nguoi choi 1 chien thang");
                            playable = false;
                            clock.pause();
                            playWinAnimation(comboWin1);
                            showAlertWithHeaderText(playerFlag);
                        }
                        playerFlag = 2;
                        lbTurn.setText("Turn O");
                    } else if (event.getButton() == MouseButton.PRIMARY && arrBoard[row][col] == 0 && playerFlag == 2) {
                        arrBoard[row][col] = 2;
                        drawO();
                        listUndo.add(board[col][row]);
                        listUndo.get(listUndo.size() - 1).border.setFill(Color.WHITE);
                        if(listUndo.size() > 1) listUndo.get(listUndo.size() - 2).border.setFill(Color.YELLOW);
                        if (checkWin(row, col) == 2) {
                            System.out.println("Nguoi choi 2 chien thang");
                            playable = false;
                            clock.pause();
                            playWinAnimation(comboWin2);
                            showAlertWithHeaderText(playerFlag);
                        }
                        playerFlag = 1;
                        lbTurn.setText("Turn X");
                    }
                }
            });
        }

        public double getCenterX() {
            return getTranslateX() + 15;
        }

        public double getCenterY() {
            return getTranslateY() + 15;
        }

        public String getValue() {
            return text.getText();
        }

        private void drawX() {
            text.setText("X");
        }

        private void drawO() {
            text.setText("O");
        }
    }

    private int checkWin(int cl, int rw) {
        int r = 0, c = 0;
        int i;
        boolean player1, player2;
        //Check hàng ngang
        while (c < 20 - 5) {
            player1 = true;
            player2 = true;
            for (i = 0; i < 5; i++) {
                if (arrBoard[cl][c + i] != 1) {
                    player1 = false;
                    comboWin1.clear();
                } else {
                    comboWin1.add(board[c + i][cl]);
                }
                if (arrBoard[cl][c + i] != 2) {
                    player2 = false;
                    comboWin2.clear();
                } else {
                    comboWin2.add(board[c + i][cl]);
                }
            }
            if (player1) {
                return 1;
            }
            if (player2) {
                return 2;
            }
            c++;
        }
        //Check hàng dọc
        while (r < 20 - 5) {
            player1 = true;
            player2 = true;
            for (i = 0; i < 5; i++) {
                if (arrBoard[r + i][rw] != 1) {
                    player1 = false;
                    comboWin1.clear();
                } else {
                    comboWin1.add(board[rw][r + i]);
                }
                if (arrBoard[r + i][rw] != 2) {
                    player2 = false;
                    comboWin2.clear();
                } else {
                    comboWin2.add(board[rw][r + i]);
                }
            }
            if (player1) {
                return 1;
            }
            if (player2) {
                return 2;
            }
            r++;
        }
        //Check duong cheo xuoi
        r = rw;
        c = cl;
        while (r > 0 && c > 0) {
            r--;
            c--;
        }
        while (r <= 20 - 5 && c <= 20 - 5) {
            player1 = true;
            player2 = true;
            for (i = 0; i < 5; i++) {
                if (arrBoard[c + i][r + i] != 1) {
                    player1 = false;
                    comboWin1.clear();
                } else {
                    comboWin1.add(board[r + i][c + i]);
                }
                if (arrBoard[c + i][r + i] != 2) {
                    player2 = false;
                    comboWin2.clear();
                } else {
                    comboWin2.add(board[r + i][c + i]);
                }
            }
            if (player1) {
                return 1;
            }
            if (player2) {
                return 2;
            }
            r++;
            c++;
        }
        //Check duong cheo nguoc
        r = rw;
        c = cl;
        while (r < 20 - 1 && c > 0) {
            r++;
            c--;
        }
        while (r >= 4 && c <= 20 - 5) {
            player1 = true;
            player2 = true;
            for (i = 0; i < 5; i++) {
                if (arrBoard[r - i][c + i] != 1) {
                    player1 = false;
                    comboWin1.clear();
                } else {
                    comboWin1.add(board[c + i][r - i]);
                }
                if (arrBoard[r - i][c + i] != 2) {
                    player2 = false;
                    comboWin2.clear();
                } else {
                    comboWin2.add(board[c + i][r - i]);
                }
            }
            if (player1) {
                return 1;
            }
            if (player2) {
                return 2;
            }
            r--;
            c++;
        }
        return 0;
    }
    // Noi cac o tao dan den chien thang
    private void playWinAnimation(List<Tile> combo) {
        for (int i = 0; i < combo.size(); i++) {
            combo.get(i).border.setFill(Color.LIGHTBLUE);
        }
    }
    // Reset ban co ve ban dau
    public void resetBoard() {
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                arrBoard[i][j] = 0;
                board[j][i].text.setText("");
                board[j][i].border.setFill(Color.YELLOW);
                playable = true;
            }
        }
    }
    // Undo nuoc di
    public void undoMove() {
        listUndo.remove(listUndo.size() - 1);
        if (playerFlag == 1) {
            playerFlag = 2;
            lbTurn.setText("Turn O");
        } else if (playerFlag == 2) {
            playerFlag = 1;
            lbTurn.setText("Turn X");
        }
        playable = true;
        repaintChessBoard();
        
    }
    //Ve lai ban co
    public void repaintChessBoard() {
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                if (!listUndo.contains(board[i][j])) {
                    board[i][j].text.setText("");
                    board[i][j].border.setFill(Color.YELLOW);
                    arrBoard[j][i] = 0;
                }
                board[i][j].border.setFill(Color.YELLOW);
            }
            listUndo.get(listUndo.size() - 1).border.setFill(Color.WHITE);
        }
    }
    
    private void showAlertWithHeaderText(int flag) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("GAME OVER");
        alert.setHeaderText("CONGRATULATIONS !!!");
        if (flag == 1) alert.setContentText("Player with X - chess is WINNER !");
        else if (flag == 2) alert.setContentText("Player with O - chess is WINNER !");
        alert.showAndWait();
    }

    public static void main(String[] args) {
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 20; j++) {
                arrBoard[i][j] = 0;
            }
        }

        launch(args);
    }
}
