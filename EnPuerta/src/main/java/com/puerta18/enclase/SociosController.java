package com.puerta18.enclase;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.puerta18.model.Socio;

@Controller
public class SociosController {

	@Autowired
	private Environment env;

	// landing page, muestra un formulario de busqueda
	// y tambien muestra los resultados con un parametro no requerido
	@GetMapping("/socios/")
	public String index(@RequestParam(required = false) String palabraClave, Model template) throws SQLException {

		if (palabraClave == null) {
			palabraClave = "+";
		}
		// palabraClave = palabraClave.toLowerCase();

		Connection connection;
		connection = DriverManager.getConnection(env.getProperty("spring.datasource.url"),
				env.getProperty("spring.datasource.username"), env.getProperty("spring.datasource.password"));

		PreparedStatement consulta = connection
				.prepareStatement("SELECT * FROM socios WHERE nombre LIKE ? OR apellido LIKE ? OR dni LIKE ?");
		// no agregue en el 3 porque debe ser exacto.
		consulta.setString(1, "%" + palabraClave + "%");
		consulta.setString(2, "%" + palabraClave + "%");
		consulta.setString(3, palabraClave);
		ResultSet resultados = consulta.executeQuery();

		ArrayList<Socio> lossocios = new ArrayList<Socio>();

		while (resultados.next()) { // ciclo
			int id = resultados.getInt("id");
			String nombre = resultados.getString("nombre");
			String apellido = resultados.getString("apellido");
			String mail = resultados.getString("email");
			String dni = resultados.getString("dni");
			// boolean presente=resultados.getBoolean("presente");
			Socio elsocio = new Socio(id, nombre, apellido, mail, dni);
			lossocios.add(elsocio);

		}

		template.addAttribute("socios", lossocios);
		connection.close();

		return "inicio";
	}

	@GetMapping("/socios/nuevo") // formulario de alta vacio

	public String nuevo(Model template) {
		
			
		return "login";

	}

	@GetMapping("/socios/nuevo/procesar") // inserta nuevos socios

	public String procesarnuevoCurso(@RequestParam String nombre, @RequestParam String apellido,
			@RequestParam String email, @RequestParam String dni) throws SQLException {
		Connection connection; // Usar el import de java.sql
		connection = DriverManager.getConnection(env.getProperty("spring.datasource.url"),
				env.getProperty("spring.datasource.username"), env.getProperty("spring.datasource.password"));

		PreparedStatement consulta = connection
				.prepareStatement("INSERT INTO  socios ( nombre ,  apellido, email ,dni) VALUES (?,?,?,?);");

		consulta.setString(1, nombre);
		consulta.setString(2, apellido);
		consulta.setString(3, email);
		consulta.setString(4, dni);

		consulta.execute();
		connection.close();

		return "redirect:/socios/";

	}

	@GetMapping("/socios/checkin/{id}") //
	public String checkIn(@PathVariable int id, Model template) throws SQLException {
		Connection connection;
		connection = DriverManager.getConnection(env.getProperty("spring.datasource.url"),
				env.getProperty("spring.datasource.username"), env.getProperty("spring.datasource.password"));
		String checkin = "checkin";
		PreparedStatement consulta = connection
				.prepareStatement("INSERT INTO  checks(id_socio, momento,tipo)VALUES (?, NOW(), 'in')");
		consulta.setInt(1, id);
		consulta.execute();
		connection.close();
		return "redirect:/socios/";
	}

	@GetMapping("/socios/checkout/{id}") //
	public String checkOut(@PathVariable int id, Model template) throws SQLException {
		Connection connection;
		connection = DriverManager.getConnection(env.getProperty("spring.datasource.url"),
				env.getProperty("spring.datasource.username"), env.getProperty("spring.datasource.password"));
		String checkin = "checkin";
		PreparedStatement consulta = connection
				.prepareStatement("INSERT INTO  checks(id_socio, momento,tipo)VALUES (?, NOW(), 'out')");
		consulta.setInt(1, id);
		consulta.execute();
		connection.close();
		return "redirect:/socios/";
	}

	// estas rutas mas adelante vamos a protegerlas con usuario y contraseña
	// @GetMapping("/socios/mostrar/{id}") // muestra el detalle completo de los
	// socios buscador
	@GetMapping("/socios/activos")
	public String mostrarSocio(@RequestParam String palabraClave, Model template) throws SQLException {
		Connection connection;
		connection = DriverManager.getConnection(env.getProperty("spring.datasource.url"),
				env.getProperty("spring.datasource.username"), env.getProperty("spring.datasource.password"));

		PreparedStatement consulta = connection.prepareStatement(
				"SELECT * FROM checks.momento, socios.nombre, socios.apellido, checks.tipo FROM checks INNER JOIN socios ON socios.id=checks.id_socio");

		ResultSet resultados = consulta.executeQuery();

		ArrayList<Socio> lossocios = new ArrayList<Socio>();

		while (resultados.next()) { // ciclo
			int id = resultados.getInt("id");
			String nombre = resultados.getString("nombre");
			String apellido = resultados.getString("apellido");
			String mail = resultados.getString("mail");
			String dni = resultados.getString("dni");
			// boolean presente=resultados.getBoolean("presente");
			Socio elsocio = new Socio(id, nombre, apellido, mail, dni);
			lossocios.add(elsocio);

		}

		template.addAttribute("socios", lossocios);
		connection.close();
		return "inicio";

	}

	// socios buscador
	
	
	// @GetMapping("/socios/listado") // muestra el listado completo sin paginacion,
	// por ahora

	@GetMapping("/socios/listado")
	public String listadosocios(Model template) throws SQLException {
		
		Connection connection;
		connection = DriverManager.getConnection(
				env.getProperty("spring.datasource.url"),
				env.getProperty("spring.datasource.username"),
				env.getProperty("spring.datasource.password"));
		
		PreparedStatement consulta = 
				connection.prepareStatement("SELECT * FROM socios");

		ResultSet resultados = consulta.executeQuery();
		
		ArrayList<Socio> losSocios = new ArrayList<Socio>();
		
		while ( resultados.next() ) { // ciclo
			int id = resultados.getInt("id");
			String nombre = resultados.getString("nombre");
			String apellido = resultados.getString("apellido");
			String email = resultados.getString("email");
			String dni = resultados.getString("dni");
			
			Socio ElSocio = new Socio(id, nombre, apellido, email, dni);
			
			losSocios.add(ElSocio);
			
		}

		template.addAttribute("socios", losSocios);
		connection.close();
		
		return "listadoSocios";
	}
	
	
	// @GetMapping("/socios/modificar/{id}")
	// @GetMapping("/socios/modificar/procesar/{id}")
}
