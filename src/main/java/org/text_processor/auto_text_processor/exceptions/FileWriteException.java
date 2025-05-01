package org.text_processor.auto_text_processor.exceptions;

import java.io.IOException;

public class FileWriteException extends IOException {
    public FileWriteException(String message){
        super(message);
    }
}