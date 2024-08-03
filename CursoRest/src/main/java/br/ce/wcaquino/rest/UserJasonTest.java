package br.ce.wcaquino.rest;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.arrayWithSize;
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

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserJasonTest {

	@Test
	public void test_a_deveVeficarEmPrimeiroNivel() {
		RestAssured.given()// Pré-condições
				.when()// Ação
				.get("https://restapi.wcaquino.me/users/1").then()// Asserts
				.statusCode(200).body("id", Matchers.is(1)).body("name", Matchers.containsStringIgnoringCase("silva"))
				.body("age", Matchers.greaterThan(18));
	}

	@Test
	public void test_b_deveVerificarPrimeiroNivelDeOutrasFormas() {
		Response response = RestAssured.request(Method.GET, "https://restapi.wcaquino.me/users/1");

		// path
		Assert.assertThat("1", is(response.path("id").toString()));
		Assert.assertThat("1", is(response.path("%s", "id").toString()));

		// jsonpath
		JsonPath jpath = new JsonPath(response.asString());
		Assert.assertEquals(1, jpath.getInt("id"));

		// from
		int id = JsonPath.from(response.asString()).getInt("id");
		Assert.assertEquals(1, id);

	}

	@Test
	public void test_c_deveVerificarSegundoNivel() {

		RestAssured.given().when().get("https://restapi.wcaquino.me/users/2").then().statusCode(200)
				.body("name", containsString("Joaquina")).body("age", greaterThan(18))
				.body("endereco.rua", is("Rua dos bobos"));
	}

	@Test
	public void test_d_deveVerificarLista() {
		RestAssured.given() // Pré-condições
				.when() // Ação
				.get("https://restapi.wcaquino.me/users/3").then() // Asserts
				.statusCode(200).body("name", containsString("Ana")).body("filhos", hasSize(2))
				.body("filhos[0].name", is("Zezinho")).body("filhos[1].name", is("Luizinho"))
				.body("filhos.name", hasItem("Zezinho")).body("filhos.name", hasItems("Zezinho", "Luizinho"));
	}

	@Test
	public void test_e_deveVerificarMensagemDeErro() {
		RestAssured.given() // Pré-condições
				.when()// Ação
				.get("https://restapi.wcaquino.me/users/4").then().statusCode(404)
				.body("error", is("Usuário inexistente"));
	}

	@Test
	public void test_f_deveVerificarListaNaRaiz() {
		RestAssured.given() // Pré-condições
				.when() // Ação
				.get("https://restapi.wcaquino.me/users").then() // Asserts
				.statusCode(200).body("$", hasSize(3)) // $ >> indica procura na raiz
				.body("name", hasItems("João da Silva", "Maria Joaquina", "Ana Júlia")).body("age[1]", is(25))
				.body("filhos.name", hasItem(Arrays.asList("Zezinho", "Luizinho"))).body("salary", hasItem(2500))
				.body("salary", contains(1234.5678f, 2500, null));
	}

	@Test
	public void test_g_deveFazerVerificacaoAvancada() {
		RestAssured.given() // Pré-condições
				.when() // ação
					.get("https://restapi.wcaquino.me/users")
				.then() // Asserts
					.statusCode(200)
					.body("$", hasSize(3))
					.body("age.findAll{it <= 25}.size()", is(2))
					.body("age.findAll{it <= 25 && it > 20}.size()", is(1))
					.body("findAll{it.age <= 25 && it.age > 20}.name", hasItem("Maria Joaquina"))
					.body("findAll{it.age <= 25}[0].name", is("Maria Joaquina"))
					.body("find{it.age <= 25}.name", is("Maria Joaquina")) // retorna o primeiro registro
					.body("findAll{it.name.contains('n')}.name", hasItems("Maria Joaquina", "Ana Júlia"))
					.body("findAll{it.name.length() > 10}.name", hasItems("João da Silva", "Maria Joaquina"))
					.body("name.collect{it.toUpperCase()}", hasItem("Maria Joaquina".toUpperCase()))
					.body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}",	hasItem("Maria Joaquina".toUpperCase()))
					.body("age.collect{it * 2}", hasItems(60,50,40))
					.body("id.max()", is(3))
					.body("salary.min()", is(1234.5678f))
					.body("salary.findAll{it != null}.sum()", is(Matchers.closeTo(3734.5678f, 0.001) ))
					.body("salary.findAll{it != null}.sum()", Matchers.allOf(greaterThan(3000d), lessThan(4000d)))
				;
	}

	@Test
	public void test_h_deveFazerVerificacaoAvancada() {
		ArrayList<String> names = 
				RestAssured
					.given() // Pré-condições
					.when() // ação
						.get("https://restapi.wcaquino.me/users")
					.then() // Asserts
						.statusCode(200)
						.extract().path("name.findAll{it.startsWith('Maria')}")
					;
		Assert.assertEquals(1, names.size());
		Assert.assertTrue(names.get(0).equalsIgnoreCase("mAria JoaQuina"));
		Assert.assertEquals(names.get(0).toUpperCase(),"mAria JoaQuina".toUpperCase());
	}
}
