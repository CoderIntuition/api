package com.coderintuition.CoderIntuition.exceptions;

import com.coderintuition.CoderIntuition.pojos.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.RollbackException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"unchecked", "rawtypes"})
@ControllerAdvice
@Slf4j
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex) {
        log.warn("Unhandled exception, class={}, message={}, stackTrace={}", ex.getClass().toString(), ex.getMessage(), ex.getStackTrace());
        ex.printStackTrace();
        ErrorResponse error = new ErrorResponse("Error", List.of(ex.getLocalizedMessage()));
        return new ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public final ResponseEntity<Object> handleBadCredentials() {
        log.warn("Handled BadCredentialsException");
        ErrorResponse error = new ErrorResponse("Authentication Error", List.of("Invalid email or password"));
        return new ResponseEntity(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RecordNotFoundException.class)
    public final ResponseEntity<Object> handleRecordNotFound(RecordNotFoundException ex) {
        log.warn("Handled RecordNotFoundException");
        ErrorResponse error = new ErrorResponse("Record Not Found", List.of(ex.getLocalizedMessage()));
        return new ResponseEntity(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public final ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        log.warn("Handled ConstraintViolationException");
        List<String> details = new ArrayList<>();
        for (ConstraintViolation violation : ex.getConstraintViolations()) {
            details.add(violation.getPropertyPath() + " " + violation.getMessage());
        }
        ErrorResponse error = new ErrorResponse("Validation Error", details);
        return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public final ResponseEntity<Object> handleBadRequest(BadRequestException ex) {
        log.warn("Handled BadRequestException");
        ErrorResponse error = new ErrorResponse("Error", List.of(ex.getLocalizedMessage()));
        return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InternalServerException.class)
    public final ResponseEntity<Object> handleInternalServerException(InternalServerException ex) {
        log.warn("Handled InternalServerException");
        ErrorResponse error = new ErrorResponse("Error", List.of(ex.getLocalizedMessage()));
        return new ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public final ResponseEntity<Object> handleSQLIntegrityConstraintViolation(SQLIntegrityConstraintViolationException ex) {
        log.warn("Handled SQLIntegrityConstraintViolationException");
        ErrorResponse error = new ErrorResponse("Error", List.of(ex.getLocalizedMessage()));
        return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public final ResponseEntity<Object> handleTransactionSystem(TransactionSystemException ex) {
        if (ex.getRootCause() instanceof ConstraintViolationException) {
            return handleConstraintViolation((ConstraintViolationException) ex.getRootCause());
        }

        ErrorResponse error = new ErrorResponse("Error", List.of(ex.getLocalizedMessage()));
        return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public final ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        if (ex.getRootCause() instanceof SQLIntegrityConstraintViolationException) {
            return handleSQLIntegrityConstraintViolation((SQLIntegrityConstraintViolationException) ex.getRootCause());
        }

        ErrorResponse error = new ErrorResponse("Error", List.of(ex.getLocalizedMessage()));
        return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.warn("Handled MethodArgumentNotValidException");
        List<String> details = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            details.add(error.getField() + " " + error.getDefaultMessage());
        }
        ErrorResponse error = new ErrorResponse("Validation Error", details);
        return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
    }
}