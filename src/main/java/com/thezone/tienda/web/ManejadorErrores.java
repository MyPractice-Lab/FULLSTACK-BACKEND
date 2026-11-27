package com.thezone.tienda.web;

import com.thezone.tienda.servicio.CredencialesInvalidasExcepcion;
import com.thezone.tienda.servicio.RecursoNoEncontradoExcepcion;
import com.thezone.tienda.servicio.ReglaNegocioExcepcion;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(RecursoNoEncontradoExcepcion.class)
    public ResponseEntity<Map<String, Object>> noEncontrado(RecursoNoEncontradoExcepcion error) {
        return respuesta(HttpStatus.NOT_FOUND, error.getMessage());
    }

    @ExceptionHandler(ReglaNegocioExcepcion.class)
    public ResponseEntity<Map<String, Object>> reglaNegocio(ReglaNegocioExcepcion error) {
        return respuesta(HttpStatus.CONFLICT, error.getMessage());
    }

    @ExceptionHandler(CredencialesInvalidasExcepcion.class)
    public ResponseEntity<Map<String, Object>> credenciales(CredencialesInvalidasExcepcion error) {
        return respuesta(HttpStatus.UNAUTHORIZED, error.getMessage());
    }

    @ExceptionHandler(AccesoDenegadoExcepcion.class)
    public ResponseEntity<Map<String, Object>> accesoDenegado(AccesoDenegadoExcepcion error) {
        return respuesta(HttpStatus.FORBIDDEN, error.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validacion(MethodArgumentNotValidException error) {
        Map<String, String> campos = new LinkedHashMap<>();

        error.getBindingResult().getFieldErrors()
                .forEach(fallo -> campos.putIfAbsent(fallo.getField(), fallo.getDefaultMessage()));

        Map<String, Object> cuerpo = cuerpoBase(HttpStatus.BAD_REQUEST, "Hay datos inválidos en la petición.");
        cuerpo.put("campos", campos);

        return ResponseEntity.badRequest().body(cuerpo);
    }

    private ResponseEntity<Map<String, Object>> respuesta(HttpStatus estado, String mensaje) {
        return ResponseEntity.status(estado).body(cuerpoBase(estado, mensaje));
    }

    private Map<String, Object> cuerpoBase(HttpStatus estado, String mensaje) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("momento", LocalDateTime.now().toString());
        cuerpo.put("estado", estado.value());
        cuerpo.put("error", estado.getReasonPhrase());
        cuerpo.put("mensaje", mensaje);
        return cuerpo;
    }
}
