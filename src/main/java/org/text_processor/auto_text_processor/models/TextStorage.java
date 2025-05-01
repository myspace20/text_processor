package org.text_processor.auto_text_processor.models;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TextStorage<T> {

    private final Map<T, Text<T>> textMap  = new HashMap<>();

    public void addText(T id, Text<T> text){
        textMap.put(id,text);
    }

    public void removeText(T id)   {
        Text<T> text =  getText(id);
        textMap.remove(id);
    }

    public ArrayList<Text<T>> getAllTexts(){

        return  new ArrayList<>(textMap.values());
    }

    public void updateText(T id, String field, Object newValue) throws Exception {
        System.out.println( " id" + id);
        Text<T> currentText = getText(id);
//        Text<T> currentText1 = textMap.getText(id);
        if (currentText == null) {
            throw new Exception("Text does not exist");
        }
        Text<T> updatedText = getText(field, newValue, currentText);

        boolean replaced = textMap.replace(id, currentText, updatedText);
        if (!replaced) {
            throw new Exception("Text update failed");
        }
    }

    private Text<T> getText(String field, Object newValue, Text<T> currentText) throws Exception {
        Text<T> updatedText = new Text<T>(
                currentText.getPath(),
                currentText.getBody(),
                currentText.getTextId()

        );

        switch (field.trim().toLowerCase()) {
            case "body" -> updatedText.setBody((String) newValue);
            default -> {
                throw new Exception("Invalid employee data field");
            }
        }
        return updatedText;
    }

    public Text<T> getText(T id) {
        return  textMap.get(id);
    }
}
