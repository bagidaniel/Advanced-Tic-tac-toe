package game.gui;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import game.model.Data;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import game.model.Direction;
import game.model.Position;
import game.model.GameModel;
import javafx.stage.Stage;

public class GameController {

    private String bluePlayerName;

    private String redPlayerName;

    public void setBluePlayerName(String bluePlayerName) {
        this.bluePlayerName = bluePlayerName;
    }

    public void setRedPlayerName(String redPlayerName) {
        this.redPlayerName = redPlayerName;
    }

    private boolean selectionPhaseOne = true;

    private List<Position> selectablePositions = new ArrayList<>();

    private Position selectedPosition;

    private GameModel model = new GameModel();

    private boolean isGameOver = false;

    @FXML
    private GridPane board;

    @FXML
    private Label label;

    @FXML
    public void handleResetButton(ActionEvent event) throws IOException {
        resetGame();
    }

    @FXML
    private void initialize() {
        createBoard();
        createCircles();
        setSelectablePositions();
        showSelectableCells();
        Platform.runLater(this::setUpNextTurn);
    }

    private void resetGame(){
        clearBoard();
        hideSelectableCells();
        isGameOver = false;
        selectionPhaseOne = true;
        model = new GameModel();
        createBoard();
        createCircles();
        setSelectablePositions();
        showSelectableCells();
    }

    private void createBoard() {
        for (int i = 0; i < board.getRowCount(); i++) {
            for (int j = 0; j < board.getColumnCount(); j++) {
                var cell = createCell();
                board.add(cell, j, i);
            }
        }
    }

    private StackPane createCell() {
        var cell = new StackPane();
        cell.getStyleClass().add("cell");
        cell.setOnMouseClicked(this::handleMouseClick);
        return cell;
    }

    private void clearBoard(){
        for (int i = 0; i < model.getCircleCount(); i++) {
            getCell(model.getCirclePosition(i)).getChildren().clear();
            getCell(model.getCirclePosition(i)).getStyleClass().remove("winningCell");
        }
    }

    private void createCircles() {
        for (int i = 0; i < model.getCircleCount(); i++) {
            model.positionProperty(i).addListener(this::circlePositionChange);
            var circle = createCircle(Color.valueOf(model.getCircleType(i).name()));
            getCell(model.getCirclePosition(i)).getChildren().add(circle);
        }
    }

    private Circle createCircle(Color color) {
        var circle = new Circle(50);
        circle.setFill(color);
        return circle;
    }

    @FXML
    private void handleMouseClick(MouseEvent event) {
        var cell = (StackPane) event.getSource();
        var row = GridPane.getRowIndex(cell);
        var col = GridPane.getColumnIndex(cell);
        var position = new Position(row, col);
        if (!isGameOver){
            handleClickOnCell(position);
        }
    }

    private void handleClickOnCell(Position position) {
        if (selectionPhaseOne){
            if (selectablePositions.contains(position)) {
                selectedPosition = position;
                switchSelectionPhase();
            }
        }
        else {
            if (selectablePositions.contains(position)) {
                var circleID = model.getCircleID(selectedPosition).getAsInt();
                var direction = Direction.of(position.row() - selectedPosition.row(), position.col() - selectedPosition.col());
                model.move(circleID, direction);
                handleNextTurn();
            }
        }
    }

    private void handleNextTurn(){
        if (model.isWin(model.getCirclePositions())){
            hideSelectableCells();
            setWinLabel();
            isGameOver = true;
            showWinningCells();
            try {
                exportData();
            }catch (Exception e){
                System.out.println("Hiba");
            }
            //showGameOverUI();
        }
        else {
            switchSelectionPhase();
        }
    }

    private void showGameOverUI(){
        var alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText("Game Over");
        alert.setContentText("You Win!");
        alert.showAndWait();
    }

    private void exportData() throws Exception{
        var objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        var data = new Data();
        if (model.getPlayerTurn() % 2 == 0){
            data.setWinner(bluePlayerName);
            data.setLooser(redPlayerName);
        }
        else {
            data.setWinner(redPlayerName);
            data.setLooser(bluePlayerName);
        }
        data.setDate(String.valueOf(LocalDate.now()));

        System.out.println(objectMapper.writeValueAsString(data));

        try(var writer = new FileWriter("data.json")){
            objectMapper.writeValue(writer, data);
        }
        System.out.println(objectMapper.readValue(new FileReader("data.json"), Data.class));
    }

    private void showWinningCells(){
        for (var winningPositions : model.winningPositions) {
            var cell = getCell(winningPositions);
            cell.getStyleClass().add("winningCell");
        }
    }

    private void switchSelectionPhase() {
        selectionPhaseOne = !selectionPhaseOne;
        hideSelectableCells();
        setSelectablePositions();
        showSelectableCells();
    }

    public void increasePlayerTurn(){
        model.increasePlayerTurn();
    }

    public void setUpNextTurn(){
        if (model.getPlayerTurn() % 2 == 0) {
            label.setText(bluePlayerName + " turns");
            label.setStyle("-fx-background-color: black;" + "-fx-text-fill: blue");
        }
        else{
            label.setText(redPlayerName + " turns");
            label.setStyle("-fx-background-color: black;" + "-fx-text-fill: red");
        }
    }

    public void setWinLabel(){
        if (model.getPlayerTurn() % 2 == 0){
            label.setText(bluePlayerName + " wins");
        }
        else{
            label.setText(redPlayerName + " wins");
        }
        label.setStyle("-fx-background-color: black;" + "-fx-text-fill: green");
    }

    private void setSelectablePositions() {
        selectablePositions.clear();
        if (selectionPhaseOne){
            increasePlayerTurn();
            setUpNextTurn();
            selectablePositions.addAll(model.getCirclePositions());
        }
        else {
            var circleID = model.getCircleID(selectedPosition).getAsInt();
            for (var direction : model.getValidMoves(circleID)) {
                selectablePositions.add(selectedPosition.getPositionAt(direction));
            }
        }
    }

    private void showSelectableCells() {
        for (var selectablePosition : selectablePositions) {
            var cell = getCell(selectablePosition);
            cell.getStyleClass().add("selectable");
        }
    }

    private void hideSelectableCells() {
        for (var selectablePosition : selectablePositions) {
            var cell = getCell(selectablePosition);
            cell.getStyleClass().remove("selectable");
        }
    }

    private StackPane getCell(Position position) {
        for (var child : board.getChildren()) {
            if (GridPane.getRowIndex(child) != null && GridPane.getRowIndex(child) == position.row() &&
                GridPane.getColumnIndex(child) != null && GridPane.getColumnIndex(child) == position.col()) {
                return (StackPane) child;
            }
        }
        throw new AssertionError();
    }

    private void circlePositionChange(ObservableValue<? extends Position> observable, Position oldPosition, Position newPosition) {
        StackPane oldCell = getCell(oldPosition);
        StackPane newCell = getCell(newPosition);
        newCell.getChildren().addAll(oldCell.getChildren());
        oldCell.getChildren().clear();
    }


}