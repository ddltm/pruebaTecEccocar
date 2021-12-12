package com.pruebamisiones.apirest.utils;

import java.util.HashMap;

public class Constantes {

	private static final HashMap<Integer, String> planetaNombre = new HashMap<>();
	private static final HashMap<Integer, Integer> planetaDiametro = new HashMap<>();
	private static final HashMap<Integer, String> capitanNombre = new HashMap<>();
	private static final HashMap<Integer, String> naveNombre = new HashMap<>();
	private static final HashMap<Integer, String> naveTripulacion = new HashMap<>();
	private static final HashMap<Integer, String> navePasajeros = new HashMap<>();
	private static final HashMap<Integer, String> navePilotos = new HashMap<>();
	
	//capt 1,3,4,6,7 planet 	2,5,6,7,8
    static {
        planetaNombre.put(1, "Planeta1");
        planetaNombre.put(2, "Planeta2");
        planetaNombre.put(3, "Planeta3");
        planetaNombre.put(4, "Planeta4");
        planetaNombre.put(5, "Planeta5");
        planetaNombre.put(6, "Planeta6");
        planetaNombre.put(7, "Planeta7");
        planetaNombre.put(8, "Planeta8");
        
        planetaDiametro.put(1, 10465);
        planetaDiametro.put(2, 6784);
        planetaDiametro.put(3, 8656);
        planetaDiametro.put(4, 9547);
        planetaDiametro.put(5, 12147);
        planetaDiametro.put(6, 7784);
        planetaDiametro.put(7, 8842);
        planetaDiametro.put(8, 15742);
        
        capitanNombre.put(1, "Luke");
        capitanNombre.put(2, "Han");
        capitanNombre.put(3, "Leia");
        capitanNombre.put(4, "Chewbaca");
        capitanNombre.put(5, "Lando");
        capitanNombre.put(6, "Darth Vader");
        capitanNombre.put(7, "Darth Sidius");
        
        naveNombre.put(1,"Crucero Estelar");
        naveNombre.put(2,"Halcon Milenario");
        naveNombre.put(3,"Destructor Imperial");
        
        naveTripulacion.put(1,"850");
        naveTripulacion.put(2,"10");
        naveTripulacion.put(3,"1000");
        
        navePasajeros.put(1,"1100");
        navePasajeros.put(2,"20");
        navePasajeros.put(3,"1300");
        
        navePilotos.put(1,"");
        navePilotos.put(2,"2,4");
        navePilotos.put(3,"6");
    }

	public static String getPlanetanombre(int id) {
		return planetaNombre.get(id);
	}

	public static Integer getPlanetadiametro(int id) {
		return planetaDiametro.get(id);
	}

	public static String getCapitannombre(int id) {
		return capitanNombre.get(id);
	}

	public static String getNavenombre(int id) {
		return naveNombre.get(id);
	}

	public static String getNavetripulacion(int id) {
		return naveTripulacion.get(id);
	}

	public static String getNavepasajeros(int id) {
		return navePasajeros.get(id);
	}

	public static String getNavepilotos(int id) {
		return navePilotos.get(id);
	}
	
    
}
