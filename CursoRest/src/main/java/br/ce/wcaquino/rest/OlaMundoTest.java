package br.ce.wcaquino.rest;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.request;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

public class OlaMundoTest {

	@Test
	public void testOlaMundo() {

		Response response = request(Method.GET, "https://restapi.wcaquino.me/ola");
		System.out.println(response.getBody().asString().equals("Ola Mundo!"));
		System.out.println(response.statusCode() == 200);
		System.out.println(response.getBody().asString());
		System.out.println("Status code: " + response.statusCode());

		ValidatableResponse validacao = response.then();
		validacao.statusCode(200);

		/*
		 * validacao.statusCode(201); // Se der erro irá mostar na aba JUnit: 'Failures: 1'
		 * 
		 * throw new RuntimeException(); // Se lançar uma exceção irá mostar na aba JUnit: 'Error: 1'
		 */

		Assert.assertTrue(response.getBody().asString().equals("Ola Mundo!"));
		Assert.assertTrue(response.statusCode() == 200);
		Assert.assertTrue("Status code errado! Deveria ser 200", response.statusCode() == 200);
		Assert.assertEquals(200, response.statusCode());
	}
	
	@Test
	public void devoConhecerOutrasFormasRestAssured() {
		//Primeira Forma
		Response response = RestAssured.request(Method.GET, "https://restapi.wcaquino.me/ola");
		ValidatableResponse validacao = response.then();
		validacao.statusCode(200);
		
		//Segunda Forma
		RestAssured.get("https://restapi.wcaquino.me/ola").then().statusCode(200);
		
		//Terceira Forma
		RestAssured
			.given()
				//Pré-condições
			.when()
				//Ação
				.get("https://restapi.wcaquino.me/ola")
			.then()
				//Assertivas
				.assertThat()
				.statusCode(200)
		;
		
	}
	
	@Test
	public void devoConhecerMatchersHamcrest() {
		
		assertThat("Maria", Matchers.is("Maria"));
		assertThat("Maria", Matchers.not("João"));
		assertThat("Maria", Matchers.anyOf(Matchers.is("Maria"), Matchers.is("Joaquina")));
		assertThat("Maria", Matchers.allOf(Matchers.startsWith("Ma"), Matchers.containsString("ari"),Matchers.endsWith("ria")));

		assertThat(128, Matchers.is(128));
		assertThat(128, Matchers.isA(Integer.class));
		assertThat(128d, Matchers.isA(Double.class));
		assertThat(128d, Matchers.greaterThan(110d));
		assertThat(128d, Matchers.lessThan(130d));
		
		List<Integer> impares = Arrays.asList(1,3,5,7,9);
		assertThat(impares, Matchers.hasSize(5));
		assertThat(impares, Matchers.contains(1,3,5,7,9));
		assertThat(impares, Matchers.containsInAnyOrder(1,7,5,3,9));
		assertThat(impares, Matchers.hasItem(1));
		assertThat(impares, Matchers.hasItems(1,5,9));
		
	}

	@Test
	public void devoValidarBody() {
		given()
		.when()
			.get("https://restapi.wcaquino.me/ola")
		.then()
			.statusCode(200)
			.body(Matchers.containsString("Mundo"))
			.body(Matchers.is("Ola Mundo!"))
			.body(Matchers.is(Matchers.not(Matchers.nullValue())))
			;
			
	}
}
