package com.pruebamisiones.apirest.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.pruebamisiones.apirest.dao.MisionDAO;
import com.pruebamisiones.apirest.entity.Mision;
import com.pruebamisiones.swapi.API;
import com.pruebamisiones.swapi.GetRequestRepository;

@Service
public class MisionServiceImpl implements MisionService {

    @Autowired
    private MisionDAO MisionDAO;

    @Override
    public List<Mision> findAll() {
        List<Mision> listMisiones= MisionDAO.findAll();
        return listMisiones;
    }

    @Override
    public List<Mision> findByCpts(String capitanes){
    	List<Mision> listMisions= MisionDAO.findByCpts(capitanes);
        return listMisions;
    }

    @Override
    public ResponseEntity<Object> save(String fechaInicio, int idNave, String capitanes,int numTripulacion,String planetas) {
    	String validacion = validate(fechaInicio,idNave,capitanes,numTripulacion,planetas);
    	Map<String, Object> map = new HashMap<String, Object>();
    	
    	if(validacion.equals("")) {

    		Mision mision = new Mision();
    		
    		mision.setIdNave(idNave);
    		mision.setTripulacion(numTripulacion);
    		mision.setCapitanes(capitanes);
    		mision.setPlanetas(planetas);
    		
    		DateFormat dateFormat = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
    		Date fecInicio = null;
			try {
				fecInicio = dateFormat.parse(fechaInicio);
			} catch (ParseException e) {
				e.printStackTrace();
			}
    		mision.setFecInicioMision(fecInicio);

    		//Recuperamos el tiempo de misi??n y calculamos la fecha de fin.
    		Calendar cal = Calendar.getInstance();
    		cal.setTime(fecInicio);             
    		cal.add(Calendar.HOUR_OF_DAY, calculoTiempoMision(idNave, capitanes, numTripulacion, planetas));     
    		Date fecFin = cal.getTime();         
    		
    		mision.setFecFinMision(fecFin);
    		
        	MisionDAO.save(mision);
        	
        	map.put("message", "Se ha insertado la misi??n correctamente.");
            return new ResponseEntity<Object>(map,HttpStatus.OK);
        	
    	}else {
        	
        	map.put("message", validacion);
            return new ResponseEntity<Object>(map,HttpStatus.BAD_REQUEST);
    	}

    }
    
    private String validate(String fechaInicio, int idNave, String capitanes,int numTripulacion,String planetas) {
    	String[] capitanesArray = capitanes.split(",");
    	String[] planetasArray = planetas.split(",");

    	//Validaci??n: La fecha de inicio y el identificador de la nave vienen informados.
    	if (fechaInicio.isEmpty()) {
    		return "La fecha de inicio no viene informada.";
    	}
    	
    	if (idNave == 0) {
    		return "El identificador de la nave no viene informado.";
    	}
    	
    	//Validaci??n: Hay por lo menos un capitan y un planeta
    	if(capitanesArray.length == 0) { 
    		return "Debe haber por lo menos un capitan informado";
    	}
    	
    	if (planetasArray.length == 0) {
    		return "Debe haber por lo menos un planeta informado";
    	}
    	//Validaci??n: La tripulaci??n puede ser 0 o mayor.
    	if (numTripulacion < 0) {
    		return "La tripulaci??n debe ser 0 o mayor.";
    	}
    	
    	//Recuperamos la informaci??n de la nave
    	API api = new API();
        GetRequestRepository repository = new GetRequestRepository(api);
        
        JsonObject nave = repository.innerRequest("https://swapi.dev/api/starships/"+idNave+"/");
    	
    	//Si la nave ha tenido pilotos, estos deben estar. 
       	JsonArray pilotos = nave.get("pilots").getAsJsonArray();
       	if (pilotos.size() > 0) {
       		boolean pilotNotPresent = true;
       		for (int i=0; i<pilotos.size(); i++) {
       			String idPilot = pilotos.get(i).getAsString().replace("https://swapi.dev/api/people/","").replace("/","");
       			if (capitanes.contains(idPilot)) {
       				pilotNotPresent = false;
       			}
       		}
           	if (pilotNotPresent) {
           		return "No hay ning??n piloto asociado a la nave asignado a la misi??n";
           	}
       	}
        
    	/*Validaci??n: El n??mero de capitanes + tripulaci??n debe ser mayor o igual a la tripulaci??n (crew) que requiere la nave
    	 * 
    	 * Se recoge el valor crew de la nave teniendo en cuenta el separador de miles y que puede venir informado como rango.
    	 * Si viene informado como rango se toma el valor menor.
    	 */
    	String crewSwapi = nave.get("crew").getAsString().replace(",", ".");
        int crew;
    	if (crewSwapi.indexOf("-") == -1) {
        	crew = Integer.parseInt(crewSwapi);
        }else {
        	crew = Integer.parseInt(crewSwapi.substring(0,crewSwapi.indexOf("-")));
        }
    	if ((capitanesArray.length + numTripulacion) < crew) {
    		return "La cantidad de capitanes + tripulaci??n es menor que la tripulaci??n de la nave";
    	}
    	
    	/* Validacion: c + d debe ser menor o igual a la tripulaci??n (crew) + los pasajeros (passengers) de la nave.
    	 *
    	 * Se recoge el valor de pasajeros de la nave teniendo en cuenta los casos en que puede venir como n/a
    	 */
    	String passengersSwapi = nave.get("passengers").getAsString();
    	int passengers;
    	if (passengersSwapi.equals("n/a")) {
    		passengers=0;
        }else {
        	passengers=Integer.parseInt(passengersSwapi);
        }
    	
    	if ((capitanesArray.length + numTripulacion) > (crew + passengers)) {
    		return "La cantidad de capitanes + tripulaci??n es mayor que la tripulaci??n de la nave + los pasajeros.";
    	}
    	
    	/* Validaci??n: Los capitanes no puede estar asignados a m??s de una misi??n.
    	 * 
    	 * Se incluye la fecha de inicio pasada por par??metro ya que se tiene en cuenta si las misiones
    	 * en que participa est??n activas o finalizadas. Si son finalizadas no suponen un error.
    	 */
    	for (String idCapitan : capitanesArray) {
    		List<Mision> listMisions= MisionDAO.findByCpt(Integer.parseInt(idCapitan),fechaInicio);
    		if (listMisions.size() > 0) {
    			return "Hay alg??n capit??n asignado ya a otra misi??n en el mismo periodo.";
    		}
		}
    	
    	return "";
    }
    
    
    private int calculoTiempoMision(int idNave, String capitanes,int numTripulacion,String planetas) {
    	String[] planetasArray = planetas.split(",");
    	String[] capitanesArray = capitanes.split(",");
    	int distanciaTotal = 0;
    	int tiempoMision = 0;
    	
    	int valorTripulacion = numTripulacion*10;
    	int valorCapitanes = capitanesArray.length*100;
    	
    	//Recuperamos informaci??n de los planetas.
    	API api = new API();
        GetRequestRepository repository = new GetRequestRepository(api);
        
    	for (String idPlaneta : planetasArray) {
            JsonObject planeta = repository.innerRequest("https://swapi.dev/api/planets/"+Integer.parseInt(idPlaneta)+"/");
            distanciaTotal = distanciaTotal + planeta.get("diameter").getAsInt();
    	}
    	
    	float tiempo = (float) distanciaTotal / (valorTripulacion+valorCapitanes);
    	if (tiempo < 1) {
    		tiempoMision= 1;
    	}else {
    		tiempoMision = (int) Math.ceil(tiempo);
    	}
    	
    	return  tiempoMision;
    }

    
    @SuppressWarnings("unchecked")
	public ResponseEntity<Object> salidaMisiones(List<Mision> ListMisiones)
    {
    	/*Se prepara un objeto misiones para devolver con los datos solicitados en el enunciado de la prueba.*/
        List<JSONObject> misiones = new ArrayList<JSONObject>();
        for (Mision n : ListMisiones) {
      	  	API api = new API();
      	  	GetRequestRepository repository = new GetRequestRepository(api);
      	  	JSONObject mision = new JSONObject();
            mision.put("id",n.getId());
      	  	mision.put("FechaInicio", n.getFecInicioMision().toString());
            mision.put("FechaFin", n.getFecFinMision().toString());
            
            //Nave
            JSONObject nave = new JSONObject();
            JsonObject naveSwapi = repository.innerRequest("https://swapi.dev/api/starships/"+n.getIdNave()+"/");
            nave.put("id", n.getIdNave());
            nave.put("nombre", naveSwapi.get("name").getAsString());
            nave.put("tripulacion", naveSwapi.get("crew").getAsString());
          	nave.put("pasajeros", naveSwapi.get("passengers").getAsString());
            mision.put("nave", nave);
            
            //Capitanes
            String[] capts = n.getCapitanes().split(",");
            JSONArray capitanes = new JSONArray();
            for(String c:capts) {
            	JSONObject capitan = new JSONObject();
            	capitan.put("id", c);
            	JsonObject capSwapi = repository.innerRequest("https://swapi.dev/api/people/"+c+"/");
            	capitan.put("nombre", capSwapi.get("name").getAsString());
            	capitanes.add(capitan);
            }
            
            mision.put("capitanes", capitanes);
            
            //Planetas
            String[] planet = n.getPlanetas().split(",");
            JSONArray planetas = new JSONArray();
            for(String p:planet) {
                JSONObject planeta = new JSONObject();
            	planeta.put("id", p);
            	JsonObject capSwapi = repository.innerRequest("https://swapi.dev/api/planets/"+p+"/");
            	planeta.put("nombre", capSwapi.get("name").getAsString());
            	planeta.put("diametro", capSwapi.get("diameter").getAsString());
            	planetas.add(planeta);
            }
            
            mision.put("planetas", planetas);
            
            mision.put("tripulacion", n.getTripulacion());
            
            misiones.add(mision);
        }

        return new ResponseEntity<Object>(misiones, HttpStatus.OK);
    } 
}
