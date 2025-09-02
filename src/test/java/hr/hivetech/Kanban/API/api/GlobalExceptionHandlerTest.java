package hr.hivetech.Kanban.API.api;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    @Test
    void handleValidationException_returnsBadRequest() {
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError error1 = new FieldError("object", "field1", "must not be null");
        FieldError error2 = new FieldError("object", "field2", "must be valid");
        when(bindingResult.getFieldErrors()).thenReturn(List.of(error1, error2));

        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);

        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        ResponseEntity<ApiError> response = handler.handleValidationException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Bad Request", response.getBody().getError());
        assertTrue(response.getBody().getMessage().contains("field1: must not be null"));
        assertTrue(response.getBody().getMessage().contains("field2: must be valid"));
    }

    @Test
    void handleUnexpectedException_returnsInternalServerError() {
        Exception ex = new Exception("fail");
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        ResponseEntity<ApiError> response = handler.handleUnexpectedException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal Server Error", response.getBody().getError());
        assertTrue(response.getBody().getMessage().contains("fail"));
    }
}