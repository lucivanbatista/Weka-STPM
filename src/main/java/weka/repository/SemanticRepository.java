package weka.repository;

import org.springframework.stereotype.Component;

@Component
public class SemanticRepository {
	
	public static final String STR_QUERY_POINTS_STOPS = "select c.tid, c.latitude, c.longitude, c.time, c.edge_id, c.gid, c.gid_stop, s.stop_gid, "
			+ " s.start_time, s.end_time, s.rf ";
	
	// Objetos
	private static final String FILTRO_OBJETO = STR_QUERY_POINTS_STOPS + " where c.gid_stop = s.gid AND c.tid = ";
	
	// Horário
	private static final String FILTRO_HORARIO = STR_QUERY_POINTS_STOPS + " where date_part('hour', c.time) = ";
	
	// Tipo de POI
	private static final String FILTRO_POI = STR_QUERY_POINTS_STOPS + " where s.rf = ";

	public String getStopsFiltro(String tableName, String value, int limit, String filtro) {
		String limitQuery = "";
    	if(limit > 0) {
    		limitQuery = " limit " + limit;
    	}
    	String strQuery = STR_QUERY_POINTS_STOPS + " from complete_" + tableName + " as c, " + tableName + " as s ";
    	if(filtro.equals("fObjeto") && value != null) {
    		strQuery = strQuery + FILTRO_OBJETO + value;
    	}else if(filtro.equals("fHora") && value != null) {
    		strQuery = strQuery + FILTRO_HORARIO + value;
    		System.out.println(strQuery);
    	}else if(filtro.equals("fPOI") && value != null) {
    		strQuery = strQuery + FILTRO_POI + value;
    	}
		return strQuery + limitQuery;
    }
}