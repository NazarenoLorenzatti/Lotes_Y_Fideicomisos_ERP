package com.ar.base.responses;

import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class BuildResponsesServicesImpl {

    public ResponseEntity<?> buildErrorResponse(String informacion, String codigo, String mensaje) {
        ResponseRest responseRest = new ResponseRest();
        responseRest.setMetadata(informacion, codigo, mensaje);
        return new ResponseEntity<>(responseRest, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<?> buildResponse(String informacion, String codigo, String mensaje, Object object, HttpStatus httpStatus) {
        ResponseRest responseRest = new ResponseRest();
        Response objectResponse = new Response();
        List<Object> listObject = new ArrayList();
        listObject.add(object);
        objectResponse.setResponse(listObject);
        responseRest.setResponse(objectResponse);
        responseRest.setMetadata(informacion, codigo, mensaje);
        return new ResponseEntity<>(responseRest, httpStatus);

    }

}
