package dad;

import io.vertx.sqlclient.Row;

public class Vehiculo {
	
	private String Matricula;
	private String Modelo;
	private Integer IDUsuario;
	
	public String getMatricula() {
		return Matricula;
	}
	public void setMatricula(String matricula) {
		Matricula = matricula;
	}
	public String getModelo() {
		return Modelo;
	}
	public void setModelo(String modelo) {
		Modelo = modelo;
	}
	public Integer getIDUsuario() {
		return IDUsuario;
	}
	public void setIDUsuario(Integer iDUsuario) {
		IDUsuario = iDUsuario;
	}
	
	
//	public Usuario(Row user) {
//		super();
//		this.IDUsuario = user.getInteger("IDUsuario");
//		this.DNI = user.getString("DNI");
//		this.Telefono = user.getInteger("Telefono");
//		this.Nombre = user.getString("Nombre");
//		this.Direccion = user.getString("Direccion");
//		
//		this.Fecha_Nacimiento = user.getLocalDate("Fecha_Nacimiento");
//		this.username = user.getString("username");
//		this.password = user.getString("password");
//		
//	}
	
//	private String Matricula;
//	private String Modelo;
//	private Integer IDUsuario;
	
	public Vehiculo(Row veh) {
		super();
		this.Matricula = veh.getString("Matricula");
		this.Modelo = veh.getString("Modelo");
		this.IDUsuario = veh.getInteger("IDUsuario");
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((IDUsuario == null) ? 0 : IDUsuario.hashCode());
		result = prime * result + ((Matricula == null) ? 0 : Matricula.hashCode());
		result = prime * result + ((Modelo == null) ? 0 : Modelo.hashCode());
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
		Vehiculo other = (Vehiculo) obj;
		if (IDUsuario == null) {
			if (other.IDUsuario != null)
				return false;
		} else if (!IDUsuario.equals(other.IDUsuario))
			return false;
		if (Matricula == null) {
			if (other.Matricula != null)
				return false;
		} else if (!Matricula.equals(other.Matricula))
			return false;
		if (Modelo == null) {
			if (other.Modelo != null)
				return false;
		} else if (!Modelo.equals(other.Modelo))
			return false;
		return true;
	}
	public Vehiculo(String matricula, String modelo, Integer iDUsuario) {
		super();
		Matricula = matricula;
		Modelo = modelo;
		IDUsuario = iDUsuario;
	}
	@Override
	public String toString() {
		return "Vehiculo [Matricula=" + Matricula + ", Modelo=" + Modelo + ", IDUsuario=" + IDUsuario + "]";
	}
	
	
}
