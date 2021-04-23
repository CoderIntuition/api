package com.coderintuition.CoderIntuition.exceptions;

import com.coderintuition.CoderIntuition.pojos.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.awaitility.core.ConditionTimeoutException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"unchecked", "rawtypes"})
@ControllerAdvice
@Slf4j
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex) {
        log.error("Unhandled exception, class={}, message={}, stackTrace={}", ex.getClass().toString(), ex.getMessage(), ex.getStackTrace());
        ErrorResponse error = new ErrorResponse("Error", List.of(ex.getLocalizedMessage()));
        return new ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public final ResponseEntity<Object> handleBadCredentialsException(BadCredentialsException ex) {
        log.info("Handled BadCredentialsException, message={}", ex.getLocalizedMessage());
        ErrorResponse error = new ErrorResponse("Authentication Error", List.of("Invalid email or password"));
        return new ResponseEntity(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RecordNotFoundException.class)
    public final ResponseEntity<Object> handleRecordNotFoundException(RecordNotFoundException ex) {
        log.info("Handled RecordNotFoundException, message={}", ex.getLocalizedMessage());
        ErrorResponse error = new ErrorResponse("Record Not Found", List.of(ex.getLocalizedMessage()));
        return new ResponseEntity(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public final ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        log.info("Handled ConstraintViolationException, message={}", ex.getLocalizedMessage());
        List<String> details = new ArrayList<>();
        for (ConstraintViolation violation : ex.getConstraintViolations()) {
            details.add(violation.getPropertyPath() + " " + violation.getMessage());
        }
        ErrorResponse error = new ErrorResponse("Validation Error", details);
        return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadRequestException.class)
    public final ResponseEntity<Object> handleBadRequestException(BadRequestException ex) {
        log.info("Handled BadRequestException, message={}", ex.getLocalizedMessage());
        ErrorResponse error = new ErrorResponse("Error", List.of(ex.getLocalizedMessage()));
        return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InternalServerException.class)
    public final ResponseEntity<Object> handleInternalServerException(InternalServerException ex) {
        log.error("Handled InternalServerException, message={}, stackTrace={}", ex.getMessage(), ex.getStackTrace());
        ErrorResponse error = new ErrorResponse("Error", List.of(ex.getLocalizedMessage()));
        return new ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public final ResponseEntity<Object> handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException ex) {
        log.info("Handled SQLIntegrityConstraintViolationException, message={}", ex.getLocalizedMessage());
        ErrorResponse error = new ErrorResponse("Error", List.of(ex.getLocalizedMessage()));
        return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public final ResponseEntity<Object> handleTransactionSystemException(TransactionSystemException ex) {
        log.info("Handled TransactionSystemException, message={}", ex.getLocalizedMessage());
        if (ex.getRootCause() instanceof ConstraintViolationException) {
            return handleConstraintViolationException((ConstraintViolationException) ex.getRootCause());
        }

        ErrorResponse error = new ErrorResponse("Error", List.of(ex.getLocalizedMessage()));
        return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public final ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.info("Handled DataIntegrityViolation, message={}", ex.getLocalizedMessage());
        if (ex.getRootCause() instanceof SQLIntegrityConstraintViolationException) {
            return handleSQLIntegrityConstraintViolationException((SQLIntegrityConstraintViolationException) ex.getRootCause());
        }

        ErrorResponse error = new ErrorResponse("Error", List.of(ex.getLocalizedMessage()));
        return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConditionTimeoutException.class)
    public final ResponseEntity<Object> handleConditionTimeoutException(ConditionTimeoutException ex) {
        log.warn("Handled ConditionTimeoutException, message={}, stackTrace={}", ex.getMessage(), ex.getStackTrace());
        ErrorResponse error = new ErrorResponse("Timeout", List.of("Please try again or report this issue."));
        return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.warn("Handled MethodArgumentNotValidException, message={}", ex.getLocalizedMessage());
        List<String> details = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            details.add(error.getField() + " " + error.getDefaultMessage());
        }
        ErrorResponse error = new ErrorResponse("Validation Error", details);
        return new ResponseEntity(error, HttpStatus.BAD_REQUEST);
    }
}