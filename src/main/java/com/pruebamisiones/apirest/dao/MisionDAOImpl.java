package com.pruebamisiones.apirest.dao;

import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.pruebamisiones.apirest.entity.Mision;

@Repository
public class MisionDAOImpl implements MisionDAO{

	@Autowired
    private EntityManager entityManager;

    @Override
    public List<Mision> findAll() {
        Session currentSession = entityManager.unwrap(Session.class);
        Query<Mision> theQuery = currentSession.createQuery("From Mision");
        List<Mision> Misiones = theQuery.getResultList();
        return Misiones;

    }

    @Override
    public List<Mision> findByCpt(int capitan, String fecInicio) {
        Session currentSession = entityManager.unwrap(Session.class);
        Query<Mision> theQuery;
      	theQuery = currentSession.createQuery("From Mision where instr(capitanes,"+capitan+")>0 and feciniciomision<'"+fecInicio+"' and fecfinmision>'"+fecInicio+"'");
        List<Mision> Misiones = theQuery.getResultList();
        return Misiones;
    }
    
    @Override
    public List<Mision> findByCpts(String capitanes) {
        Session currentSession = entityManager.unwrap(Session.class);
        String[] capitanesArray = capitanes.split(",");
        String condicion = "";
        Query<Mision> theQuery;
        
        for (int i=0; i<capitanesArray.length; i++) {
        	if (i==capitanesArray.length-1) {
        		condicion = condicion + "instr(capitanes,"+capitanesArray[i]+")>0";
        	}else {
        		condicion = condicion + "instr(capitanes,"+capitanesArray[i]+")>0 and ";
        	}
        }
        
        theQuery = currentSession.createQuery("From Mision where "+condicion);
        List<Mision> Misiones = theQuery.getResultList();
        return Misiones;
    }

    @Override
    public void save(Mision Mision) {
        Session currentSession = entityManager.unwrap(Session.class);
        currentSession.saveOrUpdate(Mision);  

    }

}
