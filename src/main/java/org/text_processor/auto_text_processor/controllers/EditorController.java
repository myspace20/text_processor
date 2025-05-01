package org.text_processor.auto_text_processor.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.text_processor.auto_text_processor.models.Text;
import org.text_processor.auto_text_processor.models.TextID;
import org.text_processor.auto_text_processor.models.TextProcessor;
import org.text_processor.auto_text_processor.models.TextStorage;
import org.text_processor.auto_text_processor.validations.UserInput;


import java.util.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

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

    private final TextProcessor textProcessor = new TextProcessor();
    private final TextStorage<TextID> textStorage = new TextStorage<>();
    private final TreeItem<String> rootItem = new TreeItem<>("Opened Files");

    private String mainBody;
    private String changedText;
    private Text<TextID> text;



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

    @FXML
    private void onBackToMain() {
        comparisonSplitPane.setVisible(false);
        mainTextArea.setVisible(true);
        backButton.setVisible(false);
    }

    @FXML
    private void redo(){
        mainTextArea.redo();
    }

    @FXML
    private void undo(){
        mainTextArea.undo();
    }

    @FXML
    private void onSaveToFile() throws Exception {
        String regex = regexField.getText();
        String replacementText = replacementField.getText();
        UserInput.validateInputField(regex, "Regex");
        UserInput.validateInputField(replacementText, "Replacement text");
        textProcessor.replacePatternsInFile("\\b\\w{2}\\b", replacementText,text.getPath());
        showAlert("Done", "File written to successfully");
    }

    @FXML
    public void onUpdateText(Text<TextID> textForUpdate) throws Exception {
        LOGGER.log(Level.INFO, "Updating text for id: {0}, field: {1}", new Object[]{textForUpdate.getTextId(), "body"});
        try {
            System.out.println("object for update" + textForUpdate);
            textStorage.updateText(textForUpdate.getTextId(), "body", mainBody); // Ensure mainBody is defined or passed
        } catch (Exception e) {
            throw new Exception("Text update failed", e);
        }
    }


    @FXML
    private void onAcceptChanges() throws Exception {
        String updatedText = changedTextArea.getText();
        UserInput.validateInputField(updatedText, "Updated text");
        mainTextArea.setText(updatedText);
        mainBody = updatedText;
        ArrayList<Text<TextID>> texts = textStorage.getAllTexts();
        for(Text<TextID> text:texts){
            System.out.println(text.getTextId());
        }
        onUpdateText(text);
        Text<TextID> updatedTextMap = textStorage.getText(text.id);
        System.out.println("updated text" + updatedTextMap);
        comparisonSplitPane.setVisible(false);
        mainTextArea.setVisible(true);
    }

    @FXML
    public void onOpenFile() {
        File file = chooseFile();
        if (file != null) {
            try {
                Text<TextID> newText = createTextFromFile(file);
                if (!isTextAlreadyStored(newText)) {
                    storeAndDisplayText(newText);
                    text = newText;
                }
                loadTextToEditor(newText);
            } catch (IOException e) {
                showAlert("Error", "Failed to open the file.");
            }
        }
    }

    private File chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        return fileChooser.showOpenDialog(mainTextArea.getScene().getWindow());
    }

    private Text<TextID> createTextFromFile(File file) throws IOException {
        String body = textProcessor.retrieveTextContentFromFile(file.getAbsolutePath(), 5);
        TextID id = new TextID();
        return new Text<>(file.getAbsolutePath(), body, id);
    }

    private boolean isTextAlreadyStored(Text<TextID> text) {
        return textStorage.getAllTexts()
                .stream()
                .anyMatch(e -> e.equals(text));
    }

    private void storeAndDisplayText(Text<TextID> text) {
        textStorage.addText(text.id, text);
        addTextToTreeView(text);
    }

    private void loadTextToEditor(Text<TextID> text) {
        this.text = text;
        this.mainBody = text.getBody();
        mainTextArea.setText(text.getBody());
    }


    private void addTextToTreeView(Text<TextID> text) {
        String displayName = new File(text.getPath()).getName();

        boolean exists = rootItem.getChildren().stream()
                .anyMatch(item -> item.getValue().equals(displayName));

        if (!exists) {
            TreeItem<String> fileItem = new TreeItem<>(displayName);
            rootItem.getChildren().add(fileItem);
        }
    }

    @FXML
    public void onMatchPattern() {
        try {
            String regex = regexField.getText();
            UserInput.validateInputField(regex, "regex");
            UserInput.validateInputField(mainBody, "main body");
            List<String> matches = textProcessor.findMatchesUsingRegex("\\b\\w{2}\\b", mainBody);
            int count = textProcessor.countPatterOccurences(matches);
            matchedList.getItems().setAll(matches);
            totalMatches.setText(String.valueOf(count) + " occurences");
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }

    @FXML
    public void onFrequencyAnalysis() {
        try {
            String word = wordFrequency.getText();
            UserInput.validateInputField(word, "word");
            UserInput.validateInputField(mainBody, "main body");
            long frequency = textProcessor.wordFrequency(mainBody, word);
            showAlert("Word Frequency", "The word appears " + frequency + " times.");
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }

    @FXML
    public void onReplaceMatches() {
        try {
            String regex = regexField.getText();
            String replacement = replacementField.getText();
            UserInput.validateInputField(replacement, "replacement");
            UserInput.validateInputField(regex, "regex");
            UserInput.validateInputField(mainBody, "main body");
            changedText = textProcessor.replacePatternsInText("\\b\\w{2}\\b", replacement, mainBody);
            text.setBody(changedText);
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }

    @FXML
    private void onCompareAndReplace() {
        String originalText = mainTextArea.getText();
        if (originalText == null || originalText.isEmpty()) {
            showAlert("Empty text to compare with", "Please load a file and try again");
            return;
        }

        originalTextArea.setText(originalText);
        changedTextArea.setText(changedText);

        mainTextArea.setVisible(false);
        comparisonSplitPane.setVisible(true);
        backButton.setVisible(true);
    }

    @FXML
    public void onSummarize() {
        mainBody = textProcessor.summarizeText(mainBody);
        mainTextArea.setText(mainBody);
    }

    public void onDelete() throws Exception {
        try{
            if(text == null){
                throw new Exception("Text is empty, please add a text file");
            }
            textStorage.removeText(text.id);
            String displayName = new File(text.getPath()).getName();
            rootItem.getChildren().removeIf(item -> item.getValue().equals(displayName));

            mainTextArea.clear();
            originalTextArea.clear();
            changedTextArea.clear();

            matchedList.getItems().clear();
            totalMatches.setText("0 occurrences");

            comparisonSplitPane.setVisible(false);
            mainTextArea.setVisible(true);
            backButton.setVisible(false);

            showAlert("Success", "Text deleted successfully.");
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }
}
