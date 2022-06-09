package dad;

import io.vertx.sqlclient.Row;

public class Buzzer {

	private int frecuencia;
	private int duracion;
	
	
	public Buzzer(Row user) {
		super();
		this.frecuencia = user.getInteger("frecuencia");
		this.duracion = user.getInteger("duracion");
	}

	public Buzzer(int frecuencia, int duracion) {
		super();
		this.frecuencia = frecuencia;
		this.duracion = duracion;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + duracion;
		result = prime * result + frecuencia;
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
		Buzzer other = (Buzzer) obj;
		if (duracion != other.duracion)
			return false;
		if (frecuencia != other.frecuencia)
			return false;
		return true;
	}

	public int getFrecuencia() {
		return frecuencia;
	}

	public void setFrecuencia(int frecuencia) {
		this.frecuencia = frecuencia;
	}

	public int getDuracion() {
		return duracion;
	}

	public void setDuracion(int duracion) {
		this.duracion = duracion;
	}

	@Override
	public String toString() {
		return "Buzzer [frecuencia=" + frecuencia + ", duracion=" + duracion + "]";
	}

}
