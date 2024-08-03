package br.ce.wcaquino.rest;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class OlaMundoTest {

	@Test
	public void test_a_OlaMundo() throws Exception {
		Response response = RestAssured.request(Method.GET, "https://restapi.wcaquino.me/ola");
		Assert.assertTrue(response.getBody().asString().equals("Ola Mundo!"));
		Assert.assertTrue("O status code deveria ser 200", response.statusCode() == 200);
		Assert.assertEquals(200, response.statusCode());
		
		ValidatableResponse validacao = response.then();
		validacao.statusCode(200);
	}
	
	@Test
	public void test_b_devoConhecerOutrasFormasRestAssured() {
		Response response = RestAssured.request(Method.GET, "https://restapi.wcaquino.me/ola");
		ValidatableResponse validacao = response.then();
		validacao.statusCode(200);
		
		RestAssured.get("https://restapi.wcaquino.me/ola").then().statusCode(200); // equivalente as três linhas acima
		RestAssured.given()//Pré-condições
					.when() //Ações >> Métodos(GET, POST)
						.get("https://restapi.wcaquino.me/ola")
					.then() //Assertivas
						.statusCode(200);
	}
	
	@Test
	public void test_c_devoConhecerMatchersHamcrest() {
		Assert.assertThat("Maria", Matchers.is("Maria"));
		Assert.assertThat(128, Matchers.is(128));
		Assert.assertThat(128, Matchers.isA(Integer.class));
		Assert.assertThat(128d, Matchers.isA(Double.class));
		Assert.assertThat(128d, Matchers.greaterThan(120d));
		Assert.assertThat(128d, Matchers.lessThan(130d));
		
		List<Integer> impares = Arrays.asList(1,3,5,7,9);
		Assert.assertThat(impares, Matchers.hasSize(5));
		Assert.assertThat(impares, Matchers.contains(1,3,5,7,9));// para não ter problemas tem que estar ordenada a lista
		Assert.assertThat(impares, Matchers.containsInAnyOrder(1,5,3,9,7)); // é necessário ter todos os elementos da lista
		Assert.assertThat(impares, Matchers.hasItem(1));

		Assert.assertThat("Maria", Matchers.is(Matchers.not("João")));
		Assert.assertThat("Maria", Matchers.not("João"));
		Assert.assertThat("Maria", Matchers.anyOf(Matchers.is("Maria"), Matchers.is("Lucas")));
		Assert.assertThat("Joaquina", Matchers.allOf(Matchers.startsWith("Joa"), Matchers.endsWith("quina"), Matchers.containsString("aq")));

	}

	@Test
	public void test_d_deveValidarBody() {
		
		RestAssured
			.given()//Pré-condições
			.when() // ação
				.get("https://restapi.wcaquino.me/ola")
			.then() //Assertivas
				.statusCode(200)
				.body(Matchers.is("Ola Mundo!"))
				.body(Matchers.containsString("Mundo"))
				.body(Matchers.is(Matchers.notNullValue()));
		
	}
}
