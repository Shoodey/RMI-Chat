package server;

import helpers.Config;
import helpers.Helpers;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ServerController {

    @FXML
    Label statusLabel;

    @FXML
    Button toggleStatusButton;

    @FXML
    Label listeningLabel;

    @FXML
    public void initialize() {
        setStatusLabelText();
        setToggleStatusButtonText();
    }

    private void setStatusLabelText() {
        statusLabel.setText(ServerImplementation.getStatus());
        if (ServerImplementation.online) {
            statusLabel.setTextFill(Color.rgb(28, 184, 65));
        } else {
            statusLabel.setTextFill(Color.rgb(202, 60, 60));
        }
    }

    private void setToggleStatusButtonText() {
        if (ServerImplementation.online) {
            toggleStatusButton.setText("Power off");
            Helpers.setClass(toggleStatusButton, "button-danger");
        } else {
            toggleStatusButton.setText("Power on");
            Helpers.setClass(toggleStatusButton, "button-success");
        }
    }

    private void setListeningLabelText() throws UnknownHostException {
        if(ServerImplementation.online){
            InetAddress inetAddress = InetAddress.getLocalHost();
            listeningLabel.setText("Listening on: " + inetAddress.getHostAddress() + ":" + Config.PORT);
        }else {
            listeningLabel.setText("");
        }
    }

    @FXML
    void toggleStatus() {
        try {
            ServerImplementation.toggleStatus();
            setListeningLabelText();
        } catch (Exception e) {
            Helpers.alertException(e);
            e.printStackTrace();
        }
        setStatusLabelText();
        setToggleStatusButtonText();
    }


}
