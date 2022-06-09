package dad;

import java.time.LocalDateTime;

import io.vertx.sqlclient.Row;


public class Temperatura {
	
	private Float valor;
	private LocalDateTime Marca_temporal_temp;
	private Integer IDVeh;
	
	public Temperatura(Float valor, LocalDateTime marca_temporal, Integer iDVeh) {
		super();
		this.valor = valor;
		this.Marca_temporal_temp = marca_temporal;
		this.IDVeh = iDVeh;
	}

	public Float getValor() {
		return valor;
	}
	public void setValor(Float valor) {
		this.valor = valor;
	}
	public LocalDateTime getMarca_temporal_temp() {
		return Marca_temporal_temp;
	}
	public void setMarca_temporal_temp(LocalDateTime marca_temporal) {
		this.Marca_temporal_temp = marca_temporal;
	}
	public Integer getIDVeh() {
		return IDVeh;
	}
	public void setIDVeh(Integer iDVeh) {
		this.IDVeh = iDVeh;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((IDVeh == null) ? 0 : IDVeh.hashCode());
		result = prime * result + ((Marca_temporal_temp == null) ? 0 : Marca_temporal_temp.hashCode());
		result = prime * result + ((valor == null) ? 0 : valor.hashCode());
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
		Temperatura other = (Temperatura) obj;
		if (IDVeh == null) {
			if (other.IDVeh != null)
				return false;
		} else if (!IDVeh.equals(other.IDVeh))
			return false;
		if (Marca_temporal_temp == null) {
			if (other.Marca_temporal_temp != null)
				return false;
		} else if (!Marca_temporal_temp.equals(other.Marca_temporal_temp))
			return false;
		if (valor == null) {
			if (other.valor != null)
				return false;
		} else if (!valor.equals(other.valor))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "Temperatura [valor=" + valor + ", Marca_temporal=" + Marca_temporal_temp + ", IDVeh=" + IDVeh + "]";
	}
	public Temperatura(Row temp) {
		super();
		this.valor = temp.getFloat("Valor");
		this.Marca_temporal_temp = temp.getLocalDateTime("Marca_Temporal_Temp");
		this.IDVeh = temp.getInteger("IDVeh");
		
	}
	

}
