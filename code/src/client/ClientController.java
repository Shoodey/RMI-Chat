package client;

import helpers.Helpers;
import helpers.PrimaryStageAware;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.Optional;

public class ClientController implements PrimaryStageAware {

    private Stage stage;
    private ClientInterface client;
    private ClientInterface whisperee;

    @FXML
    SplitPane splitPane;

    @FXML
    Pane leftPane;

    @FXML
    VBox chatVbox;

    @FXML
    Pane rightPane;

    @FXML
    Button sendButton;

    @FXML
    TextField composeTextField;

    @FXML
    TableView<ClientInterface> clientsTableView;

    @FXML
    TableColumn<ClientInterface, String> clientsTableColumn;

    @FXML
    Button whisperButton;

    @FXML
    public void initialize() {
        getUsername();
    }

    private void getUsername() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("RMI Chat Client");
        dialog.setHeaderText("Let us know how you are");
        dialog.setContentText("Please enter your name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresentOrElse(
                username -> {
                    if (username.isEmpty()) {
                        getUsername();
                    } else {
                        ClientImplementation.controller = this;
                        client = ClientImplementation.connect(username.trim());
                        setTableProperties();

                        try {
                            stage.setTitle("RMI Chat Client - " + client.getUsername());
                        } catch (RemoteException e) {
                            Helpers.alertException(e);
                        }
                    }
                },
                () -> System.exit(0)
        );
    }

    @FXML
    public void onEnter(ActionEvent actionEvent) throws RemoteException {
        sendMessage();
    }

    @FXML
    public void sendMessage() throws RemoteException {
        String message = composeTextField.getText();
        if (!message.isEmpty()) {
            if (message.startsWith("(Whisper to")) {
                whisper(client, message);
            } else {
                shout(client, message);
            }
            composeTextField.setText("");
        }
    }

    private void shout(ClientInterface client, String message) throws RemoteException {
        ClientImplementation.sendMessage(client, message);
    }

    void setClientsList() {
        clientsTableView.getItems().clear();

        try {
            for (ClientInterface currentClient : ClientImplementation.getClientsList()) {
                clientsTableView.getItems().add(currentClient);
            }

            clientsTableColumn.setSortType(TableColumn.SortType.ASCENDING);
            clientsTableColumn.setSortable(false);
        } catch (RemoteException e) {
            Helpers.alertException(e);
        }
    }

    void appendMessage(Label timestamp, Label username, Text message) {
        System.out.println(message.getText());
        Platform.runLater(
                () -> {
                    HBox newMessage = new HBox(5);
                    HBox.setHgrow(timestamp, Priority.ALWAYS);
//                    HBox.setHgrow(message, Priority.ALWAYS);

                    if (username != null) {
                        HBox.setHgrow(username, Priority.ALWAYS);
                        newMessage.getChildren().addAll(timestamp, username, message);

                    } else {
                        newMessage.getChildren().addAll(timestamp, message);
                    }

                    newMessage.setPrefHeight(20);
                    chatVbox.getChildren().add(newMessage);
                }
        );
    }

    private void setTableProperties() {
        clientsTableColumn.setCellValueFactory(
                new PropertyValueFactory<>("username")
        );
    }

    @FXML
    public void getWhisperee() throws RemoteException {
        whisperee = clientsTableView.getSelectionModel().getSelectedItem();
        if (!client.equals(whisperee)) {
            composeTextField.setText("(Whisper to " + whisperee.getUsername() + ") ");
            composeTextField.requestFocus();
            composeTextField.positionCaret(composeTextField.getText().length());
        }
    }

    @FXML
    public void whisper(ClientInterface whisperer, String message) throws RemoteException {
        message = message.replace("(Whisper to " + whisperee.getUsername() + ")", "");
        ClientImplementation.whisperMessage(whisperer, whisperee, message);
    }

    @Override
    public void setPrimaryStage(Stage primaryStage) {
        this.stage = primaryStage;
    }
}