package br.ce.wcaquino.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.Test;

import io.restassured.http.ContentType;
import io.restassured.path.xml.XmlPath;

public class AuthTest {

	@Test
	public void deveAcessarSWAPI() {
		given()
		.when()
			.get("https://swapi.dev/api/people/1")
		.then()
			.log().all()
			.statusCode(200)
			.body("name", is("Luke Skywalker"))
		;
	}
	
	// b65eb96d301bee4e3f0d6ed10e45c963
	
	@Test
	public void deveObterClima() {
		given()
			.log().all()
			.queryParam("q", "Fortaleza, BR")
			.queryParam("appid", "b65eb96d301bee4e3f0d6ed10e45c963")
			.queryParam("units", "metrics")
		.when()
			.get("https://api.openweathermap.org/data/2.5/weather")
		.then()
			.log().all()
			.statusCode(200)
			.body("name", Matchers.is("Fortaleza"))
			.body("coord.lon", is(-38.5247f))
			.body("main.temp", greaterThan(25f))
		;
	}
	
	@Test
	public void naoDeveAcessarSemSenha() {
		given()
			.log().all()
		.when()
			.get("https://restapi.wcaquino.me/basicauth")
		.then()
			.log().all()
			.statusCode(401)
		;
	}

	@Test
	public void deveFazerAutenticacaoBasica_1() {
		given()
			.log().all()
		.when()
			.get("https://admin:senha@restapi.wcaquino.me/basicauth")
		.then()
			.log().all()
			.statusCode(200)
			.body("status", is("logado"))
		;
	}

	@Test
	public void deveFazerAutenticacaoBasica_2() {
		given()
			.log().all()
			.auth().basic("admin", "senha")
		.when()
			.get("https://restapi.wcaquino.me/basicauth")
		.then()
			.log().all()
			.statusCode(200)
			.body("status", is("logado"))
		;
	}

	@Test
	public void deveFazerAutenticacaoBasicaChallenge() {
		given()
			.log().all()
			.auth().preemptive().basic("admin", "senha")
		.when()
			.get("https://restapi.wcaquino.me/basicauth2")
		.then()
			.log().all()
			.statusCode(200)
			.body("status", is("logado"))
		;
	}
	
	@Test
	public void deveFazerAutenticacaoComTokenJWT() {
		
		Map<String, String> login = new HashMap<String, String>();
		login.put("email", "ttt@t.com");
		login.put("senha", "123456");
		
		
		String token = 
				given()
					.log().all()
					.body(login) //Login na api
					.contentType(ContentType.JSON)
				.when()
					.post("https://barrigarest.wcaquino.me/signin")
				.then()
					.log().all()
					.statusCode(200)
					.extract().path("token"); 		//Receber o token

				;
		
		//Obter as contas
				given()
					.log().all()
					.header("Authorization", "JWT " + token)
				.when()
					.get("https://barrigarest.wcaquino.me/contas")
				.then()
					.log().all()
					.statusCode(200)
					.body("nome", hasItem("Conta de Teste"))
				;
	}
	
	@Test
	public void deveAcessarAplicacaoWeb() {
		String cookie = 
				given()
					.log().all()
					.formParam("email", "ttt@t.com") //Login 
					.formParam("senha", "123456") //senha 
					.contentType(ContentType.URLENC.withCharset("UTF-8"))
				.when()
					.post("https://seubarriga.wcaquino.me/logar")
				.then()
					.log().all()
					.statusCode(200)
					//.contentType(ContentType.HTML)
					.extract().header("set-cookie")
				;
		
		cookie = cookie.split("=")[1].split(";")[0];
		System.out.println(cookie);

		//Obter Conta
		String path = 
				given()
					.log().all()
					.cookie("connect.sid", cookie)
				.when()
					.get("https://seubarriga.wcaquino.me/contas")
				.then()
					.log().all()
					.statusCode(200)
					.body("html.body.table.tbody.tr[0].td[0]", is("Conta de Teste"))
					.extract().asString()
				;
		
		System.out.println("--------------");
		System.out.println(path);
		
	}
}
