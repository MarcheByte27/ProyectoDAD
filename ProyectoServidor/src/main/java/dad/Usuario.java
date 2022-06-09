package dad;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import io.vertx.sqlclient.Row;

public class Usuario {

	private Integer IDUsuario;
	private String DNI;
	private Integer Telefono;
	private String Nombre;
	private String Direccion;
	private LocalDate Fecha_Nacimiento;
	private String username;
	private String password;
	
	
	public Integer getIDUsuarios() {
		return IDUsuario;
	}
	public void setIDUsuarios(Integer iDUsuarios) {
		this.IDUsuario = iDUsuarios;
	}
	public String getDNI() {
		return DNI;
	}
	public void setDNI(String DNI) {
		this.DNI = DNI;
	}
	public Integer getTelefono() {
		return Telefono;
	}
	public void setTelefono(Integer telefono) {
		this.Telefono = telefono;
	}
	public String getNombre() {
		return Nombre;
	}
	public void setNombre(String nombre) {
		this.Nombre = nombre;
	}
	public String getDireccion() {
		return Direccion;
	}
	public void setDireccion(String direccion) {
		this.Direccion = direccion;
	}
	public LocalDate getFecha_Nacimiento() {
		return Fecha_Nacimiento;
	}
	public void setFecha_Nacimiento(LocalDate fecha_Nacimiento) {
		this.Fecha_Nacimiento = fecha_Nacimiento;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
		
	}
	
	public Usuario(Row user) {
		super();
		this.IDUsuario = user.getInteger("IDUsuario");
		this.DNI = user.getString("DNI");
		this.Telefono = user.getInteger("Telefono");
		this.Nombre = user.getString("Nombre");
		this.Direccion = user.getString("Direccion");
		
		this.Fecha_Nacimiento = user.getLocalDate("Fecha_Nacimiento");
		this.username = user.getString("username");
		this.password = user.getString("password");
		
	}
	
	public static Date LocalDateToDate(LocalDate fecha) {
		return Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}
	
	public Usuario(String DNI, Integer telefono, String nombre, String direccion,
			LocalDate fecha_Nacimiento, String username, String password) {
		super();
		this.IDUsuario = null;
		this.DNI = DNI;
		this.Telefono = telefono;
		this.Nombre = nombre;
		this.Direccion = direccion;
		this.Fecha_Nacimiento = fecha_Nacimiento;
		this.username = username;
		this.password = password;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((DNI == null) ? 0 : DNI.hashCode());
		result = prime * result + ((Direccion == null) ? 0 : Direccion.hashCode());
		result = prime * result + ((Fecha_Nacimiento == null) ? 0 : Fecha_Nacimiento.hashCode());
		result = prime * result + ((IDUsuario == null) ? 0 : IDUsuario.hashCode());
		result = prime * result + ((Nombre == null) ? 0 : Nombre.hashCode());
		result = prime * result + ((Telefono == null) ? 0 : Telefono.hashCode());
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
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
		Usuario other = (Usuario) obj;
		if (DNI == null) {
			if (other.DNI != null)
				return false;
		} else if (!DNI.equals(other.DNI))
			return false;
		if (Direccion == null) {
			if (other.Direccion != null)
				return false;
		} else if (!Direccion.equals(other.Direccion))
			return false;
		if (Fecha_Nacimiento == null) {
			if (other.Fecha_Nacimiento != null)
				return false;
		} else if (!Fecha_Nacimiento.equals(other.Fecha_Nacimiento))
			return false;
		if (IDUsuario == null) {
			if (other.IDUsuario != null)
				return false;
		} else if (!IDUsuario.equals(other.IDUsuario))
			return false;
		if (Nombre == null) {
			if (other.Nombre != null)
				return false;
		} else if (!Nombre.equals(other.Nombre))
			return false;
		if (Telefono == null) {
			if (other.Telefono != null)
				return false;
		} else if (!Telefono.equals(other.Telefono))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "UsuarioImpl [IDUsuario=" + IDUsuario + ", DNI=" + DNI + ", Telefono=" + Telefono + ", Nombre="
				+ Nombre + ", Direccion=" + Direccion + ", Fecha_Nacimiento=" + Fecha_Nacimiento + ", username="
				+ username + ", password=" + password + "]";
	}
	
	
	
	
	
	
	
	

}
