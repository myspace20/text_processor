package org.text_processor.auto_text_processor.exceptions;

import java.io.IOException;

public class FileReadException extends IOException {
    public FileReadException(String message){
        super(message);
    }
}
