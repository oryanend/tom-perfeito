package com.oryanend.tom_perfeito_api.controllers.handlers;

import java.util.ArrayList;
import java.util.List;

public class ValidationError extends StandardError {

    private List<FieldMessage> errors = new ArrayList<FieldMessage>();

    public List<FieldMessage> getErrors() {
        return errors;
    }

    public void addErrors(String fieldName, String message) {
        errors.add(new FieldMessage(fieldName, message));
    }
}
