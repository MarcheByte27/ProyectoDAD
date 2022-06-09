package dad;

import java.time.LocalDateTime;

import io.vertx.sqlclient.Row;

public class GPS {
	private String posicion;
	private LocalDateTime Marca_Temporal_GPS;
	private Integer IDVeh;
	
	public GPS(String posicion, LocalDateTime marca_Temporal_GPS, Integer iDVeh) {
		super();
		this.posicion = posicion;
		Marca_Temporal_GPS = marca_Temporal_GPS;
		IDVeh = iDVeh;
	}
	public String getPosicion() {
		return posicion;
	}
	public void setPosicion(String posicion) {
		this.posicion = posicion;
	}
	public LocalDateTime getMarca_Temporal_GPS() {
		return Marca_Temporal_GPS;
	}
	public void setMarca_Temporal_GPS(LocalDateTime marca_Temporal_GPS) {
		Marca_Temporal_GPS = marca_Temporal_GPS;
	}
	public Integer getIDVeh() {
		return IDVeh;
	}
	public void setIDVeh(Integer iDVeh) {
		IDVeh = iDVeh;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((IDVeh == null) ? 0 : IDVeh.hashCode());
		result = prime * result + ((Marca_Temporal_GPS == null) ? 0 : Marca_Temporal_GPS.hashCode());
		result = prime * result + ((posicion == null) ? 0 : posicion.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GPS other = (GPS) obj;
		if (IDVeh == null) {
			if (other.IDVeh != null)
				return false;
		} else if (!IDVeh.equals(other.IDVeh))
			return false;
		if (Marca_Temporal_GPS == null) {
			if (other.Marca_Temporal_GPS != null)
				return false;
		} else if (!Marca_Temporal_GPS.equals(other.Marca_Temporal_GPS))
			return false;
		if (posicion == null) {
			if (other.posicion != null)
				return false;
		} else if (!posicion.equals(other.posicion))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "GPS [posicion=" + posicion + ", Marca_Temporal_GPS=" + Marca_Temporal_GPS
				+ ", IDVeh=" + IDVeh + "]";
	}
	public GPS(Row gps) {
		super();
		this.posicion = gps.getString("Posicion");
		this.Marca_Temporal_GPS = gps.getLocalDateTime("Marca_Temporal_GPS");
		this.IDVeh = gps.getInteger("IDVeh");
		
	}
	

}
