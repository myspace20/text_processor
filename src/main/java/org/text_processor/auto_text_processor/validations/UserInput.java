package org.text_processor.auto_text_processor.validations;

import org.text_processor.auto_text_processor.exceptions.InvalidInputException;

public class UserInput {

    public static void validateInputField(String value, String field) throws Exception {
        if(isEmptyOrNullString(value)){
            throw new InvalidInputException(field + " must not be empty");
        }

    }

    static  boolean isEmptyOrNullString(String value){
        return value == null || value.isEmpty();
    }

}