package com.oryanend.tom_perfeito_api.controllers.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.oryanend.tom_perfeito_api.db.migration.exceptions.PathNotFoundException;
import com.oryanend.tom_perfeito_api.services.exceptions.DatabaseException;
import com.oryanend.tom_perfeito_api.services.exceptions.ResourceAlreadyExistsException;
import com.oryanend.tom_perfeito_api.services.exceptions.ResourceNotFoundException;
import com.oryanend.tom_perfeito_api.services.exceptions.UnauthorizedActionException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.time.Instant;

@ControllerAdvice
public class ControllerExceptionHandler implements AuthenticationFailureHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> validation(MethodArgumentNotValidException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
        ValidationError err = new ValidationError();
        err.setTimestamp(Instant.now());
        err.setStatus(status.value());
        err.setError("Validation Exception");
        err.setMessage(e.getMessage());
        err.setPath(request.getRequestURI());

        for (FieldError f : e.getBindingResult().getFieldErrors()) {
            err.addErrors(f.getField(), f.getDefaultMessage());
        }

        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<StandardError> resourceAlredyExists(ResourceAlreadyExistsException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError();
        err.setTimestamp(Instant.now());
        err.setStatus(status.value());
        err.setError("Resource already exists");
        err.setMessage(e.getMessage());
        err.setPath(request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<StandardError> resourceNotFound(ResourceNotFoundException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError err = new StandardError();
        err.setTimestamp(Instant.now());
        err.setStatus(status.value());
        err.setError("Resource not found");
        err.setMessage(e.getMessage());
        err.setPath(request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(PathNotFoundException.class)
    public ResponseEntity<StandardError> pathNotFound(PathNotFoundException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        StandardError err = new StandardError();
        err.setTimestamp(Instant.now());
        err.setStatus(status.value());
        err.setError("Path not found");
        err.setMessage(e.getMessage());
        err.setPath(request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<StandardError> unauthorizedAction(UnauthorizedActionException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        StandardError err = new StandardError();
        err.setTimestamp(Instant.now());
        err.setStatus(status.value());
        err.setError("Unauthorized action");
        err.setMessage(e.getMessage());
        err.setPath(request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<StandardError> database(DatabaseException e, HttpServletRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        StandardError err = new StandardError();
        err.setTimestamp(Instant.now());
        err.setStatus(status.value());
        err.setError("Data Integrity Violation Exception");
        err.setMessage(e.getMessage());
        err.setPath(request.getRequestURI());
        return ResponseEntity.status(status).body(err);
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        HttpStatus status = HttpStatus.UNAUTHORIZED;

        StandardError err = new StandardError();
        err.setTimestamp(Instant.now());
        err.setStatus(status.value());
        err.setError("Invalid Credentials");
        err.setMessage("Email or Password invalid");
        err.setPath(request.getRequestURI());

        response.setStatus(status.value());
        response.setContentType("application/json;charset=UTF-8");

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        response.getWriter().write(mapper.writeValueAsString(err));
    }
}
