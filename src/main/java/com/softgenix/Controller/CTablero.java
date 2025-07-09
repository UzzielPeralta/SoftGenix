package com.softgenix.Controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class CTablero implements Initializable {

    // Anotaciones para vincular con los fx:id del FXML
    @FXML
    private HBox header;

    @FXML
    private Button createButton;

    @FXML
    private SplitPane splitPane;

    @FXML
    private VBox inboxPanel;

    @FXML
    private ScrollPane listsScrollPane;

    @FXML
    private HBox listsContainer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Aquí puedes añadir lógica que se ejecuta al iniciar la pantalla.
        // Por ejemplo, cargar datos, configurar listeners, etc.
        System.out.println("La interfaz de Zendo se ha inicializado.");
    }
}