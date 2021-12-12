package com.pruebamisiones.apirest.dao;

import java.util.List;

import com.pruebamisiones.apirest.entity.Mision;

public interface MisionDAO {
	
	public List<Mision> findAll();

    public List<Mision> findByCpt(int capitan, String fecInicio);
    
    public List<Mision> findByCpts(String capitanes);

    public void save(Mision Mision);

}
