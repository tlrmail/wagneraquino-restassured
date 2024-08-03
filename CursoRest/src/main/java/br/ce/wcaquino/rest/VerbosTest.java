package br.ce.wcaquino.rest;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class VerbosTest {

	@BeforeClass
	public static void setup() {
		RestAssured.baseURI = "https://restapi.wcaquino.me";
	}
	
	@Test
	public void test_a_deveSalvarUmUsuarioVerboPOSTViaJSON() {
		RestAssured
			.given() //Pré-condições
				.log().all()
				.contentType(ContentType.JSON)
				.body("{\"name\": \"João Calvino\", \"age\": 36, \"salary\": 1234.5678 }")
			.when() //Ação
				.post("/users")
			.then() //Assert
				.log().all()
				.statusCode(201)
				.body("id", is(notNullValue()))
				.body("name", is("João Calvino"))
				.body("age", is(36))
			;
	}
	
	@Test
	public void test_b_naoDeveSalvarUsuarioSemNomeVerboPOSTViaJSON() {
		RestAssured
			.given() //Pré-condições
				.log().all()
				.contentType(ContentType.JSON)
				.body("\"age\": 36}")
			.when() //Ação
				.post("/users")
			.then() //Assert
				.log().all()
				.statusCode(400)
				.body("id", is(nullValue()))
				.body("error", is("Houve algum problema no tratamento do seu XML"))
			;
	}
	
	@Test
	public void test_c_naoDeveSalvarUsuarioVerboPOSTViaXML() {
		RestAssured
			.given() //Pré-condições
				.log().all()
				.contentType(ContentType.XML)
				.body("<user><name>Joao Calvino</name><age>30</age></user>")
			.when() //Ação
				.post("/usersXML")
			.then() //Assert
				.log().all()
				.statusCode(201)
				.body("user.@id", is(notNullValue()))
				.body("user.name", is("Joao Calvino"))
				.body("user.age", is("30"))
			;
	}

	@Test
	public void test_d_deveSalvarUmUsuarioVerboPUTViaJSON() {
		RestAssured
			.given() //Pré-condições
				.log().all()
				.contentType(ContentType.JSON)
				.body("{\"name\": \"Usuario Alterado\", \"age\": 86}")
			.when() //Ação
				.put("/users/1")
			.then() //Assert
				.log().all()
				.statusCode(200)
				.body("id", is(1))
				.body("name", is("Usuario Alterado"))
				.body("age", is(86))
				.body("salary", is(1234.5678f))
			;
	}

	@Test
	public void test_e_deveCustomizarURLVerboPUTViaJSON_1() {
		RestAssured
			.given() //Pré-condições
				.log().all()
				.contentType(ContentType.JSON)
				.body("{\"name\": \"Usuario Alterado\", \"age\": 86}")
			.when() //Ação
				.put("/{entidade}/{userId}", "users", "1")
			.then() //Assert
				.log().all()
				.statusCode(200)
				.body("id", is(1))
				.body("name", is("Usuario Alterado"))
				.body("age", is(86))
				.body("salary", is(1234.5678f))
			;
	}

	@Test
	public void test_f_deveCustomizarURLVerboPUTViaJSON_2() {
		RestAssured
			.given() //Pré-condições
				.log().all()
				.contentType(ContentType.JSON)
				.body("{\"name\": \"Usuario Alterado\", \"age\": 86}")
				.pathParam("entidade", "users")
				.pathParam("userId", "1")
			.when() //Ação
				.put("/{entidade}/{userId}")
			.then() //Assert
				.log().all()
				.statusCode(200)
				.body("id", is(1))
				.body("name", is("Usuario Alterado"))
				.body("age", is(86))
				.body("salary", is(1234.5678f))
			;
	}

	@Test
	public void test_g_deveRemoverUsuarioVerboDELETEViaJSON() {
		RestAssured
			.given() //Pré-condições
				.log().all()
			.when() //Ação
				.delete("/users/1")
			.then() //Assert
				.log().all()
				.statusCode(204)
			;
	}

	@Test
	public void test_h_naoDeveRemoverUsuarioInexistenteVerboDELETEViaJSON() {
		RestAssured
			.given() //Pré-condições
				.log().all()
			.when() //Ação
				.delete("/users/10")
			.then() //Assert
				.log().all()
				.statusCode(400)
				.body("error", is("Registro inexistente"))
			;
	}
}
