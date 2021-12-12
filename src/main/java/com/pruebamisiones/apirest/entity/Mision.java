package com.pruebamisiones.apirest.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public class Mision {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="identificador")
    private int idMision;

    @Column(name="feciniciomision")
    private Date fecInicioMision;

    @Column(name="fecfinmision")
    private Date fecFinMision;

    @Column(name="tripulacion")
    private int tripulacion;

    @Column(name="capitanes")
    private String capitanes;
    
    @Column(name="planetas")
    private String planetas;
    
    @Column(name="nave")
    private int idNave;
    
    public int getIdNave() {
		return idNave;
	}

	public void setIdNave(int idNave) {
		this.idNave = idNave;
	}

	public int getId() {
		return idMision;
	}

	public void setId(int idMision) {
		this.idMision = idMision;
	}

	public Date getFecInicioMision() {
		return fecInicioMision;
	}

	public void setFecInicioMision(Date fecInicioMision) {
		this.fecInicioMision = fecInicioMision;
	}

	public Date getFecFinMision() {
		return fecFinMision;
	}

	public void setFecFinMision(Date fecFinMision) {
		this.fecFinMision = fecFinMision;
	}

	public int getTripulacion() {
		return tripulacion;
	}

	public void setTripulacion(int tripulacion) {
		this.tripulacion = tripulacion;
	}

	public String getCapitanes() {
		return capitanes;
	}

	public void setCapitanes(String capitanes) {
		this.capitanes = capitanes;
	}

	public String getPlanetas() {
		return planetas;
	}

	public void setPlanetas(String planetas) {
		this.planetas = planetas;
	}

	public Mision() {}

    public Mision(int idMision, Date fecInicioMision, Date fecFinMision, int tripulacion, String capitanes, String planetas, int idNave) {
        this.idMision = idMision;
        this.fecInicioMision = fecInicioMision;
        this.fecInicioMision = fecFinMision;
        this.tripulacion = tripulacion;
        this.capitanes = capitanes;
        this.planetas = planetas;
        this.idNave = idNave;
    }
}
