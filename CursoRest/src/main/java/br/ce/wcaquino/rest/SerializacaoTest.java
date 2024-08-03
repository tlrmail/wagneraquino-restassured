package br.ce.wcaquino.rest;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import br.ce.wcaquino.entidades.User;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

public class SerializacaoTest {

	@BeforeClass
	public static void setup() {
		RestAssured.baseURI = "https://restapi.wcaquino.me";
	}
	
	@Test
	public void test_a_deveSalvarUmUsuarioUsandoUmMap() {

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", "Usuario via map");
		params.put("age", 25);
		
		RestAssured
			.given() //Pré-condições
				.log().all()
				.contentType(ContentType.JSON)
				.body(params)
			.when() //Ação
				.post("/users")
			.then() //Assert
				.log().all()
				.statusCode(201)
				.body("id", is(notNullValue()))
				.body("name", is("Usuario via map"))
				.body("age", is(25))
			;
	}

	@Test
	public void test_b_deveSalvarUmUsuarioUsandoUmObjeto() {

		User user = new User("Usuario via objeto", 35, 34.45);
		
		RestAssured
			.given() //Pré-condições
				.log().all()
				.contentType(ContentType.JSON)
				.body(user)
			.when() //Ação
				.post("/users")
			.then() //Assert
				.log().all()
				.statusCode(201)
				.body("id", is(notNullValue()))
				.body("name", is("Usuario via objeto"))
				.body("age", is(35))
				.body("salary", is(34.45f))
			;
	}

}
