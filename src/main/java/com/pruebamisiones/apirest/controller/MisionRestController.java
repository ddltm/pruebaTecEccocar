package com.pruebamisiones.apirest.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pruebamisiones.apirest.entity.Mision;
import com.pruebamisiones.apirest.service.MisionService;

//Indiciamos que es un controlador rest
@RestController
@RequestMapping("/api") //esta sera la raiz de la url, es decir http://127.0.0.1:8080/api/

public class MisionRestController {

  //Inyectamos el servicio para poder hacer uso de el
  @Autowired
  private MisionService MisionService;

  /*Este método se hará cuando por una petición GET (como indica la anotación) se llame a la url 
  http://127.0.0.1:8080/api/ListaMisiones*/
  @GetMapping("/ListaMisiones")
  public ResponseEntity<Object> findAll(){
      //retornará todos los usuarios
      return salidaMisiones(MisionService.findAll());
  }

  /*Este método se hará cuando por una petición GET (como indica la anotación) se llame a la url + el id de un usuario
  http://127.0.0.1:8080/api/ListaMisiones/1*/
  @GetMapping("/ListaMisiones/{capitanes}")
  public ResponseEntity<Object> getMision(@PathVariable String capitanes){
      List<Mision> listMisiones = MisionService.findByCpts(capitanes);

      if(listMisiones == null) {
          throw new RuntimeException("No se encontraron misiones para los capitanes indicados por parámetro");
      }
      
      return salidaMisiones(listMisiones);
  }

  /*Este método se hará cuando por una petición POST (como indica la anotación) se llame a la url
  http://127.0.0.1:8080/api/NuevaMision  */
  @PostMapping("/NuevaMision/{fechaInicio}/{idNave}/{capitanes}/{numTripulacion}/{planetas}")
  public void addMision(@PathVariable Map<String, String> pathVarsMap) {
	  String fechaInicio = pathVarsMap.get("fechaInicio");
	  int idNave = Integer.parseInt(pathVarsMap.get("idNave"));
	  String capitanes = pathVarsMap.get("capitanes");
	  int numTripulacion = Integer.parseInt(pathVarsMap.get("numTripulacion"));
	  String planetas = pathVarsMap.get("planetas");
	  
	  MisionService.save(fechaInicio, idNave, capitanes, numTripulacion, planetas);
	  
  }
  
  private ResponseEntity<Object> salidaMisiones(List<Mision> ListMisiones)
  {
      return MisionService.salidaMisiones(ListMisiones);
  } 
  
}
