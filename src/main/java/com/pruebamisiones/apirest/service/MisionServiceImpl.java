package com.pruebamisiones.apirest.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import com.pruebamisiones.apirest.entity.Mision;
import com.pruebamisiones.apirest.utils.Constantes;
import com.pruebamisiones.swapi.API;
import com.pruebamisiones.swapi.GetRequestRepository;
import com.google.gson.JsonObject;
import com.pruebamisiones.apirest.dao.MisionDAO;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

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
    public void save(String fechaInicio, int idNave, String capitanes,int numTripulacion,String planetas) {
    	if(validate(fechaInicio,idNave,capitanes,numTripulacion,planetas)) {

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

    		//Recuperamos el tiempo de misión y calculamos la fecha de fin.
    		Calendar cal = Calendar.getInstance();
    		cal.setTime(fecInicio);             
    		cal.add(Calendar.HOUR_OF_DAY, calculoTiempoMision(idNave, capitanes, numTripulacion, planetas));     
    		Date fecFin = cal.getTime();         
    		
    		mision.setFecFinMision(fecFin);
    		
        	MisionDAO.save(mision);
    	}else {
    		System.out.println("No Valido");
    	}


    }
    
    private boolean validate(String fechaInicio, int idNave, String capitanes,int numTripulacion,String planetas) {
    	String[] capitanesArray = capitanes.split(",");
    	String[] planetasArray = planetas.split(",");
        
    	//Validación: La fecha de inicio y el identificador de la nave vienen informados.
    	if (fechaInicio.isEmpty() || idNave == 0) {
    		return false;
    	}
    	
    	//Validación: Hay por lo menos un capitan y un planeta
    	if(capitanesArray.length == 0 || planetasArray.length == 0) {
    		return false;
    	}
    	
    	//Validación: La tripulación puede ser 0 o mayor.
    	if (numTripulacion < 0) {
    		return false;
    	}
    	
    	//Recuperamos la información de la nave
    	API api = new API();
        GetRequestRepository repository = new GetRequestRepository(api);
        
        JsonObject nave = repository.innerRequest("https://swapi.dev/api/starships/"+idNave+"/");
    	
    	//Si la nave ha tenido pilotos, estos deben estar.
        
        //String pilotos = nave.get("pilots").getAsString();
    	String pilotos = Constantes.getNavepilotos(idNave);        
        
    	
    	/*Validación: El número de capitanes + tripulación debe ser mayor o igual a la tripulación (crew) que requiere la nave
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
    	//int crew = Integer.parseInt(Constantes.getNavetripulacion(idNave));
    	if ((capitanesArray.length + numTripulacion) < crew) {
    		return false;
    	}
    	
    	/* Validacion: c + d debe ser menor o igual a la tripulación (crew) + los pasajeros (passengers) de la nave.
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
    	
    	//int passengers = Integer.parseInt(Constantes.getNavepasajeros(idNave));
    	if ((capitanesArray.length + numTripulacion) > (crew + passengers)) {
    		return false;
    	}
    	
    	/* Validación: Los capitanes no puede estar asignados a más de una misión.
    	 * 
    	 * Se incluye la fecha de inicio pasada por parámetro ya que se tiene en cuenta si las misiones
    	 * en que participa están activas o finalizadas. Si son finalizadas no suponen un error.
    	 */
    	for (String idCapitan : capitanesArray) {
    		List<Mision> listMisions= MisionDAO.findByCpt(Integer.parseInt(idCapitan),fechaInicio);
    		if (listMisions.size() > 0) {
    			return false;
    		}
		}
    	
    	return true;
    }
    
    private int calculoTiempoMision(int idNave, String capitanes,int numTripulacion,String planetas) {
    	String[] planetasArray = planetas.split(",");
    	String[] capitanesArray = capitanes.split(",");
    	int distanciaTotal = 0;
    	int tiempoMision = 0;
    	
    	int valorTripulacion = numTripulacion*10;
    	int valorCapitanes = capitanesArray.length*100;
    	
    	//Recuperamos información de los planetas.
    	API api = new API();
        GetRequestRepository repository = new GetRequestRepository(api);
        
    	for (String idPlaneta : planetasArray) {
            JsonObject planeta = repository.innerRequest("https://swapi.dev/api/planets/"+Integer.parseInt(idPlaneta)+"/");
            distanciaTotal = distanciaTotal + planeta.get("diameter").getAsInt();
    		//distanciaTotal = distanciaTotal + Constantes.getPlanetadiametro(Integer.parseInt(idPlaneta));
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
        //Get data from service layer into entityList.
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
            
            /*nave.put("nombre", Constantes.getNavenombre(n.getIdNave()));
            nave.put("tripulacion", Constantes.getNavetripulacion(n.getIdNave()));
            nave.put("pasajeros", Constantes.getNavepasajeros(n.getIdNave()));*/
            mision.put("nave", nave);
            
            //Capitanes
            String[] capts = n.getCapitanes().split(",");
            JSONArray capitanes = new JSONArray();
            for(String c:capts) {
            	JSONObject capitan = new JSONObject();
            	capitan.put("id", c);
            	JsonObject capSwapi = repository.innerRequest("https://swapi.dev/api/people/"+c+"/");
            	capitan.put("nombre", capSwapi.get("name").getAsString());
            	//capitan.put("nombre", Constantes.getCapitannombre(Integer.parseInt(c)));
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
            	/*planeta.put("nombre", Constantes.getPlanetanombre(Integer.parseInt(p)));
            	planeta.put("diametro", Constantes.getPlanetadiametro(Integer.parseInt(p)));*/
            	planetas.add(planeta);
            }
            
            mision.put("planetas", planetas);
            
            mision.put("tripulacion", n.getTripulacion());
            
            misiones.add(mision);
        }
        return new ResponseEntity<Object>(misiones, HttpStatus.OK);
    } 
}
