package lt.fxportal.API.Exceptions;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import lt.fxportal.API.DTOs.ErrorResponseDTO;
import lt.fxportal.ApplicationServices.Exceptions.CurrencyNotFoundException;
import lt.fxportal.ApplicationServices.Exceptions.ExchangeRateNotFoundException;
import lt.fxportal.ApplicationServices.Exceptions.InvalidDateRangeException;

@ControllerAdvice
public class GlobalExceptionHandler {

   @ExceptionHandler(CurrencyNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleCurrencyNotFound(CurrencyNotFoundException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(ex.getMessage(), 404);
        return ResponseEntity.status(404).body(error);
    }

    @ExceptionHandler(ExchangeRateNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleRateNotFound(ExchangeRateNotFoundException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(ex.getMessage(), 404);
        return ResponseEntity.status(404).body(error);
    }

    @ExceptionHandler(InvalidDateRangeException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidDateRange(InvalidDateRangeException ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(ex.getMessage(), 400);
        return ResponseEntity.status(400).body(error);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception ex) {
        ErrorResponseDTO error = new ErrorResponseDTO(ex.getMessage(), 500);
        return ResponseEntity.status(500).body(error);
    }

}
