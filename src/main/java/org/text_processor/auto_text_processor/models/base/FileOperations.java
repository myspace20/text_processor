package org.text_processor.auto_text_processor.models.base;


import java.io.BufferedReader;
import java.io.IOException;

public interface FileOperations {
    BufferedReader readFile(String file) throws IOException;
    void replacePatternsInFile(String pattern, String replacement, String filePath) throws IOException;

}

