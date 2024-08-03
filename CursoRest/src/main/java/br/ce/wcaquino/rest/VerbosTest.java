package br.ce.wcaquino.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import io.restassured.http.ContentType;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class VerbosTest {

	@Test
	public void test_a_deveSalvarUsuario() {
		given()
			.log().all()
			.contentType(ContentType.JSON)
			.body("{ \"name\" : \"Josefo\", \"age\": 50}")
		.when()
			.post("https://restapi.wcaquino.me/users")
		.then()
			.statusCode(201)
			.log().all()
			.body("id", notNullValue())
			.body("name", is("Josefo"))
			.body("age", is(50))
		;

	}
	
	@Test
	public void test_b_naoDeveSalvarUsuarioSemNome() {
		given()
			.log().all()
			.contentType(ContentType.JSON)
			.body("{\"age\": 50}")
		.when()
			.post("https://restapi.wcaquino.me/users")
		.then()
			.statusCode(400)
			.log().all()
			.body("id", is(nullValue()))
			.body("error", is("Name é um atributo obrigatório"))
		;
		
	}
	
	@Test
	public void test_c_naoDeveSalvarUsuarioSemNomeViaXML() {
		given()
			.log().all()
			.contentType(ContentType.XML)
			.body("<user><name>Josefo</name><age>50</age></user>")
		.when()
			.post("https://restapi.wcaquino.me/usersXML")
		.then()
			.statusCode(201)
			.log().all()
			.body("user.@id", is(notNullValue()))
			.body("user.name", is("Josefo"))
			.body("user.age", is("50"))
		;
	}
	
	@Test
	public void test_d_deveAlterarUsuario() {
		given()
			.log().all()
			.contentType(ContentType.JSON)
			.body("{ \"name\" : \"Josefo\", \"age\": 80}")
		.when()
			.put("https://restapi.wcaquino.me/users/2")
		.then()
			.statusCode(200)
			.log().all()
			.body("id", is(2))
			.body("name", is("Josefo"))
			.body("age", is(80))
			.body("salary", is(2500))
		;

	}

	@Test
	public void test_e_deveCustomizarURL() {
		given()
			.log().all()
			.contentType(ContentType.JSON)
			.body("{ \"name\" : \"Josefo\", \"age\": 80}")
		.when()
			.put("https://restapi.wcaquino.me/{entidade}/{userId}", "users", 2)
		.then()
			.statusCode(200)
			.log().all()
			.body("id", is(2))
			.body("name", is("Josefo"))
			.body("age", is(80))
			.body("salary", is(2500))
		;

	}

	@Test
	public void test_f_deveCustomizarURL_Parte2() {
		given()
			.log().all()
			.contentType(ContentType.JSON)
			.body("{ \"name\" : \"Josefo\", \"age\": 80}")
			.pathParam("entidade", "users")
			.pathParam("userId", 2)
		.when()
			.put("https://restapi.wcaquino.me/{entidade}/{userId}")
		.then()
			.statusCode(200)
			.log().all()
			.body("id", is(2))
			.body("name", is("Josefo"))
			.body("age", is(80))
			.body("salary", is(2500))
		;

	}

	@Test
	public void test_g_deveRemoverUsuario() {
		given()
			.log().all()
		.when()
			.delete("https://restapi.wcaquino.me/users/1")
		.then()
			.statusCode(204)
			.log().all()
		;
	}

	@Test
	public void test_h_naoDeveRemoverUsuario() {
		given()
			.log().all()
		.when()
			.delete("https://restapi.wcaquino.me/users/1000")
		.then()
			.statusCode(400)
			.log().all()
			.body("error", is("Registro inexistente"))
			;
	}
}
