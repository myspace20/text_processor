package org.text_processor.auto_text_processor.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import org.text_processor.auto_text_processor.exceptions.InvalidInputException;
import org.text_processor.auto_text_processor.exceptions.TextNotFoundException;
import org.text_processor.auto_text_processor.models.Text;
import org.text_processor.auto_text_processor.models.TextID;
import org.text_processor.auto_text_processor.models.TextProcessor;
import org.text_processor.auto_text_processor.models.TextStorage;
import org.text_processor.auto_text_processor.validations.UserInput;


import java.util.logging.Logger;

import java.io.File;
import java.io.IOException;
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
        LOGGER.log(Level.INFO,"Started writing to file");
        try{
            String regex = regexField.getText();
            String replacementText = replacementField.getText();
            UserInput.validateInputField(regex, "Regex");
            UserInput.validateInputField(replacementText, "Replacement text");
            textProcessor.replacePatternsInFile(regex, replacementText,text.getPath());
            showAlert("Success","File written to successfully");
            LOGGER.log(Level.INFO,"File written to successfully");
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
            LOGGER.log(Level.SEVERE,e.getMessage());
        }

    }

    @FXML
    public void onUpdateText(Text<TextID> textForUpdate) throws Exception {
        LOGGER.log(Level.INFO, "Updating text for id: {0}, field: {1}", new Object[]{textForUpdate.getTextId(),"Body"});
        try {
            textStorage.updateText(textForUpdate.getTextId(), "body", mainBody);
            LOGGER.log(Level.INFO, "Updated text for id: {0}, field: {1}", new Object[]{textForUpdate.getTextId(),"Body"});
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
            LOGGER.log(Level.SEVERE, "Error occured while updating text body for id: {0}, field: {1}", new Object[]{textForUpdate.getTextId(),"Body"});
        }
    }


    @FXML
    private void onAcceptChanges(){
        LOGGER.log(Level.INFO,"Started accepting changes to text in storage");
        try{
            String updatedText = changedTextArea.getText();
            UserInput.validateInputField(updatedText, "Updated text");
            mainTextArea.setText(updatedText);
            mainBody = updatedText;
            onUpdateText(text);
            comparisonSplitPane.setVisible(false);
            mainTextArea.setVisible(true);
            LOGGER.log(Level.INFO,"Changes accepted to text in storage successfully");
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
            LOGGER.log(Level.SEVERE,e.getMessage());
        }
    }

    @FXML
    public void onOpenFile() {
        File file = chooseFile();
        if (file != null) {
            LOGGER.log(Level.INFO,"Started reading from file: {0}", file.getAbsolutePath());
            try {
                Text<TextID> newText = createTextFromFile(file);
                if (!isTextAlreadyStored(newText)) {
                    storeAndDisplayText(newText);
                    text = newText;
                }
                loadTextToEditor(newText);
                LOGGER.log(Level.INFO,"File read successfully: {0}", file.getAbsolutePath());
            } catch (IOException e) {
                showAlert("Error", "Failed to open the file.");
                LOGGER.log(Level.SEVERE,e.getMessage());
            }catch (Exception e){
                showAlert("Error", e.getMessage());
                LOGGER.log(Level.SEVERE,e.getMessage());
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
            LOGGER.log(Level.INFO,"Started matching regex pattern");
            String regex = regexField.getText();
            UserInput.validateInputField(regex, "Regex");
            UserInput.validateInputField(mainBody, "Main body");
            List<String> matches = textProcessor.findMatchesUsingRegex(regex, mainBody);
            int count = textProcessor.countPatterOccurences(matches);
            matchedList.getItems().setAll(matches);
            totalMatches.setText(count + " occurrences");
            showAlert("Pattern Frequency", "The word appears " + count + " times.");
            LOGGER.log(Level.INFO,"Regex pattern matching operation ended");
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
            LOGGER.log(Level.SEVERE,e.getMessage());
        }
    }

    @FXML
    public void onFrequencyAnalysis() {
        try {
            String word = wordFrequency.getText();
            LOGGER.log(Level.INFO,"Word frequency analysis matching operation started for: {0}",word);
            UserInput.validateInputField(word, "Word");
            UserInput.validateInputField(mainBody, "Main body");
            long frequency = textProcessor.wordFrequency(mainBody, word);
            showAlert("Word Frequency", "The word appears " + frequency + " times.");
            LOGGER.log(Level.INFO,"Word frequency analysis matching operation ended for: {0}",word);
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
            LOGGER.log(Level.SEVERE,e.getMessage());
        }
    }

    @FXML
    public void onReplaceMatches() {
        try {
            String regex = regexField.getText();
            String replacement = replacementField.getText();
            LOGGER.log(Level.INFO,"Pattern match replacement operation started for: {0}", regex);
            UserInput.validateInputField(replacement, "Replacement");
            UserInput.validateInputField(regex, "Regex");
            UserInput.validateInputField(mainBody, "Main body");
            changedText = textProcessor.replacePatternsInText(regex, replacement, mainBody);
            text.setBody(changedText);
            showAlert("Pattern Replacement", "Pattern match replacement completed");
            LOGGER.log(Level.INFO,"Pattern match replacement operation ended for: {0}", regex);
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
            LOGGER.log(Level.SEVERE,e.getMessage());

        }
    }

    @FXML
    private void onCompareAndReplace() throws InvalidInputException {
        try{
            LOGGER.log(Level.INFO,"Compare and replace in progress");
            String originalText = mainTextArea.getText();
            UserInput.validateInputField(originalText, "Original text");
            originalTextArea.setText(originalText);
            changedTextArea.setText(changedText);
            mainTextArea.setVisible(false);
            comparisonSplitPane.setVisible(true);
            backButton.setVisible(true);
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
            LOGGER.log(Level.SEVERE,e.getMessage());
        }
    }

    @FXML
    public void onSummarize() {
        UserInput.validateInputField(mainBody, "Original text");
        mainBody = textProcessor.summarizeText(mainBody);
        mainTextArea.setText(mainBody);
    }

    public void onDelete() {
        try {
            validateTextExists();
            deleteTextFromStorage();
            updateUIAfterDeletion();
            showAlert("Success", "Text deleted successfully.");
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }

    private void validateTextExists() throws TextNotFoundException {
        if (text == null) {
            throw new TextNotFoundException("Text is empty, please add a text file");
        }
    }

    private void deleteTextFromStorage() throws TextNotFoundException {
        textStorage.removeText(text.id);
        String displayName = new File(text.getPath()).getName();
        rootItem.getChildren().removeIf(item -> item.getValue().equals(displayName));
    }

    private void updateUIAfterDeletion() {
        mainTextArea.clear();
        originalTextArea.clear();
        changedTextArea.clear();

        matchedList.getItems().clear();
        totalMatches.setText("0 occurrences");

        comparisonSplitPane.setVisible(false);
        mainTextArea.setVisible(true);
        backButton.setVisible(false);
    }

}
