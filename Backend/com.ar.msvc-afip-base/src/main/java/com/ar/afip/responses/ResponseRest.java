package com.ar.afip.responses;

import java.util.ArrayList;
import java.util.HashMap;
import lombok.Data;

@Data
public class ResponseRest {

    private final ArrayList<HashMap<String, String>> metadata = new ArrayList<>();
    private Response response = new Response();
   
    public void setMetadata(String respuesta, String codigo, String info) {
		
		HashMap<String, String> mapeo = new HashMap<>();
		
		mapeo.put("respuesta", respuesta);
		mapeo.put("codigo", codigo);
		mapeo.put("informacion", info);
		
		metadata.add(mapeo);
	}
}