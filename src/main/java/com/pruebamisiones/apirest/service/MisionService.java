package com.pruebamisiones.apirest.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.pruebamisiones.apirest.entity.Mision;

public interface MisionService {
	
	public List<Mision> findAll();

    public List<Mision> findByCpts(String capitanes);

    public void save(String fechaInicio, int idNave, String capitanes,int numTripulacion,String planetas);
    
    public ResponseEntity<Object> salidaMisiones(List<Mision> ListMisiones);

}
