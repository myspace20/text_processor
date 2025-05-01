package org.text_processor.auto_text_processor.exceptions;

public class InvalidInputException extends IllegalArgumentException{
    public InvalidInputException(String message){
        super(message);
    }
}
