package br.ce.wcaquino.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserAtributosEstaticosJsonTest {

	@BeforeClass
	public static void setup() {
		RestAssured.baseURI = "https://restapi.wcaquino.me";
//		RestAssured.port = 80;
//		RestAssured.basePath = "/v2";
	}
	
	@Test
	public void test_a_deveVerificarPrimeiroNivel() {
		given()
			.contentType(ContentType.JSON)
		.when()
			.get("users/1")
		.then()
			.statusCode(200)
			.body("id", is(1))
			.body("name", containsString("Silva"))
			.body("age", greaterThan(18))
		;
	}
	
	@Test
	public void test_b_deveVerificarPrimeiroNivelOutraFormas() {
		
		Response response = RestAssured.request(Method.GET, "/users/1");
		System.out.println(response.asPrettyString());
		System.out.println(response.jsonPath().getString("id"));
		
		//JsonPath
		JsonPath jpath = new JsonPath(response.asString());
		Assert.assertEquals(1, jpath.getInt("id"));
		Assert.assertEquals("1", response.jsonPath().getString("id"));
		Assert.assertEquals(1, response.jsonPath().getInt("id"));
		
		//From
		int id = JsonPath.from(response.asString()).getInt("id");
		Assert.assertEquals(1, id);
		
		//Path
		Assert.assertEquals(Integer.valueOf(1), response.path("%s", "id"));
		
	}

	@Test
	public void test_c_deveVerificarSegundoNivel() {
		String t = 
				given()
					.contentType(ContentType.JSON)
				.when()
					.get("/users/2")
				.then()
					.statusCode(200)
					.body("name", containsString("Joaquina"))
					.body("endereco.rua", is("Rua dos bobos"))
					.extract().asPrettyString()
				;
		
		System.out.println(t);

	}

	@Test
	public void test_d_deveVerificarLista() {
		String t = 
				given()
					.contentType(ContentType.JSON)
				.when()
					.get("/users/3")
				.then()
					.statusCode(200)
					.body("name", containsString("Ana"))
					.body("filhos", hasSize(2))
					.body("filhos[0].name", is("Zezinho"))
					.body("filhos[1].name", is("Luizinho"))
					.body("filhos.name", hasItem("Zezinho"))
					.body("filhos.name", hasItems("Zezinho", "Luizinho"))
					.extract().asPrettyString()
				;
		
		System.out.println(t);

	}

	@Test
	public void test_e_deveRetornarErroUsuarioInexistente() {
		String t = 
			given()
			
			.when()
				.get("/users/4")
			.then()
				.statusCode(404)
				.body("error",is("Usuário inexistente"))
				.extract().asPrettyString()
			;
		
		System.out.println(t);
	}
	
	@Test
	public void test_f_deveVerificarListaRaiz() {
		String t = 
			given()
			.when()
				.get("/users")
			.then()
				.statusCode(200)
				.body("", hasSize(3))
				.body("name", hasItems("João da Silva", "Maria Joaquina", "Ana Júlia"))
				.body("age[1]", is(25))
				.body("filhos.name", hasItem(Arrays.asList("Zezinho", "Luizinho")))
				.body("salary", contains(1234.5677f, 2500, null))
				.extract().asPrettyString()
			;
		System.out.println(t);
	}
	
	/**
	 * A linguagem usada dentro do body é o groovy
	 */
	@Test
	public void test_g_deveVerificacoesAvancadas() {
		String t = 
			given()
			.when()
				.get("/users")
			.then()
				.statusCode(200)
				.body("$", hasSize(3))
				.body("age.findAll{it <= 25}.size()", is(2))
				.body("age.findAll{it > 20 && it <= 25}.size()", is(1))
				.body("findAll{it.age > 20 && it.age <= 25}.name", hasItem("Maria Joaquina"))
				.body("findAll{it.age <= 25}[0].name", is("Maria Joaquina"))
				.body("findAll{it.age <= 25}[-1].name", is("Ana Júlia"))
				.body("find{it.age <= 25}.name", is("Maria Joaquina"))
				.body("findAll{it.name.contains('n')}.name", hasItems("Ana Júlia", "Maria Joaquina"))
				.body("findAll{it.name.length() > 10}.name", hasItems("João da Silva", "Maria Joaquina"))
				.body("name.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA"))
//				.body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}", allOf(Matchers.hasItemInArray("MARIA JOAQUINA"), Matchers.arrayWithSize(1)))
				.body("age.collect{it * 2}", hasItems(60, 50, 40))
				.body("id.max()", is(3))
				.body("salary.min()", is(1234.5678f))
				.body("salary.findAll{it != null}.sum()", is(closeTo(3734.5678f, 0.001))) // o closeTo é uma valor próximo ao pedido
				.body("salary.findAll{it > null}.sum()", allOf(greaterThan(3000d), lessThan(5000d))) 
				.extract().asPrettyString()
				
			;
		System.out.println(t);
	}
	
	@Test
	public void test_h_devoUnirJsonPathComJAVA() {
		ArrayList<String> names = 
				given()
				.when()
					.get("/users")
				.then()
					.statusCode(200)
					.extract().path("name.findAll{it.startsWith('Maria')}")
				;
		System.out.println(names);
		Assert.assertEquals(1, names.size());
		Assert.assertTrue(names.get(0).equalsIgnoreCase("Maria JOAQUINA"));
		Assert.assertEquals(names.get(0).toLowerCase(), "maria joaquina");
	}
	
	@Test
	public void test_i_atributosEstaticos() {
		given()
			.log().all()
		.when()
			.get("/users")
		.then()
			.statusCode(200)
		;
	}
}