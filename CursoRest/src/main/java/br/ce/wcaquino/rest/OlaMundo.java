package br.ce.wcaquino.rest;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.response.Validatable;
import io.restassured.response.ValidatableResponse;

public class OlaMundo {

	public static void main(String[] args) {
		Response response = RestAssured.request(Method.GET, "https://restapi.wcaquino.me/ola");
		System.out.println(response.getBody().asString().equals("Ola Mundo!"));
		System.out.println(response.getBody().asPrettyString());
		
		ValidatableResponse validacao = response.then();
		validacao.statusCode(200);
		validacao.statusCode(201); // dará em exceção.
		
		response = RestAssured.request(Method.GET, "https://api.nasa.gov");
		System.out.println(response.getStatusCode() == 200);
		System.err.println(response.getBody().asPrettyString());

	}
	
}
