package org.text_processor.auto_text_processor.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;


import java.util.logging.Logger;

public class EditorController {

    private static final Logger LOGGER = Logger.getLogger(EditorController.class.getName());

    @FXML
    private SplitPane comparisonSplitPane;

    @FXML
    private TextArea originalTextArea;

    @FXML
    private TextArea changedTextArea;

    @FXML
    private TextArea mainTextArea;

    @FXML
    private Button backButton;


    @FXML
    private TextField regexField;

    @FXML
    private TextField replacementField;


    @FXML
    private Label totalMatches;

    @FXML
    private ListView<String> matchedList;

    @FXML
    private TextField wordFrequency;

    @FXML
    private TreeView<String> treeView;


    private final TreeItem<String> rootItem = new TreeItem<>("Opened Files");

    private String mainBody;
    private String changedText;

    @FXML
    public void initialize() {
        rootItem.setExpanded(true);
        treeView.setRoot(rootItem);
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


}