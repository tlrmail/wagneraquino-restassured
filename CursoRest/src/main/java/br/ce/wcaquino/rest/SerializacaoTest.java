package br.ce.wcaquino.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import br.ce.wcaquino.entities.User;
import io.restassured.http.ContentType;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SerializacaoTest {
	
	@Test
	public void test_a_deveSalvarUsuarioUsandoMap() {
		
		Map<String, Object> params = new HashMap<>();
		params.put("name", "Usuário via map.");
		params.put("age", 25);
		
		given()
			.log().all()
			.contentType(ContentType.JSON)
			.body(params)
		.when()
			.post("https://restapi.wcaquino.me/users")
		.then()
			.statusCode(201)
			.log().all()
			.body("id", is(notNullValue()))
			.body("name", is("Usuário via map."))
			.body("age", is(25))
		;

	}

	@Test
	public void test_b_deveSalvarUsuarioUsandoObjeto() {
		
		User user = new User("Usuário via objeto.", 35, 1234.5678d);
		
		given()
			.log().all()
			.contentType(ContentType.JSON)
			.body(user)
		.when()
			.post("https://restapi.wcaquino.me/users")
		.then()
			.statusCode(201)
			.log().all()
			.body("id", is(notNullValue()))
			.body("name", is("Usuário via objeto."))
			.body("age", is(35))
			.body("salary", is(1234.5678f))
		;

	}
	
	@Test
	public void test_c_deveDeserializarObjetoSalvarUsuarioUsandoObjeto() {
		
		User userRequest = new User("Usuário deserializado.", 35, 1234.5678d);
		
		User userResponse =		
							given()
								.log().all()
								.contentType(ContentType.JSON)
								.body(userRequest)
							.when()
								.post("https://restapi.wcaquino.me/users")
							.then()
								.statusCode(201)
								.log().all()
								.extract().body().as(User.class);
							;

		System.out.println(userResponse);
		Assert.assertThat(userResponse.getId(), notNullValue());
		Assert.assertEquals("Usuário deserializado.", userResponse.getName());
		Assert.assertThat(userRequest.getAge(), is(35));
	}

	@Test
	public void test_d_deveSalvarUsuarioViaXMLUsandoObjeto() {
		
		User user = new User("Usuario XML.", 40, 1234.5678d);
		
		given()
			.log().all()
			.contentType(ContentType.XML)
			.body(user)
		.when()
			.post("https://restapi.wcaquino.me/usersXML")
		.then()
			.statusCode(201)
			.log().all()
			.body("user.@id", is(notNullValue()))
			.body("user.name", is("Usuario XML."))
			.body("user.age", is("40"))
			.body("user.salary", is("1234.5678"))
		;

	}
	
	@Test
	public void test_e_deveDeserializarObjetoViaXMLSalvarUsuarioUsandoObjeto() {
		
		User userRequest = new User("Usuario XML.", 40, 1234.5678d);
		
		User userResponse = 		
							given()
								.log().all()
								.contentType(ContentType.XML)
								.body(userRequest)
							.when()
								.post("https://restapi.wcaquino.me/usersXML")
							.then()
								.statusCode(201)
								.log().all()
								.extract().body().as(User.class)
							;
		
		System.out.println(userResponse);
		Assert.assertThat(userResponse.getId(), is(notNullValue()));
		Assert.assertThat(userResponse.getName(), is("Usuario XML."));
		Assert.assertThat(userResponse.getAge(), is(40));
		Assert.assertThat(userResponse.getSalary(), is(1234.5678d));
	}

}
