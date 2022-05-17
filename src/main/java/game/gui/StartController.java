package game.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class StartController {

    @FXML
    private TextField blueNameTextField;

    @FXML
    private TextField redNameTextField;

    @FXML
    private void initialize(){
        blueNameTextField.setText("Blue");
        redNameTextField.setText("Red");
    }

    @FXML
    private void handleStartButton(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/game.fxml"));
        Parent root = fxmlLoader.load();
        GameController controller = fxmlLoader.getController();
        controller.setBluePlayerName(blueNameTextField.getText());
        controller.setRedPlayerName(redNameTextField.getText());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}
