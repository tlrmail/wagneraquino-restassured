package br.ce.wcaquino.rest;

import static io.restassured.RestAssured.given;

import org.hamcrest.Matchers;
import org.junit.Test;

import io.restassured.http.ContentType;

public class HTMLTest {

	@Test
	public void deveFazerBuscasComHTML_1() {
	 	given()
	 		.log().all()
	 	.when()
	 		.get("https://restapi.wcaquino.me/v2/users")
	 	.then()
	 		.log().all()
	 		.statusCode(200)
	 		.contentType(ContentType.HTML)
	 		.body("html.body.div.table.tbody.tr.size()", Matchers.is(3))
 			.body("html.body.div.table.tbody.tr[1].td[2]", Matchers.is("25"));
	 	;
		
	}

	@Test
	public void deveFazerBuscasComHTML_2() {
	 	given()
	 		.log().all()
	 	.when()
	 		.get("https://restapi.wcaquino.me/v2/users")
	 	.then()
	 		.log().all()
	 		.statusCode(200)
	 		.contentType(ContentType.HTML)
	 		.appendRootPath("html.body.div.table.tbody.")
	 		.body("tr.size()", Matchers.is(3))
 			.body("tr[1].td[2]", Matchers.is("25"))
	 		.body("tr.find{it.toString().startsWith('2')}.td[1]", Matchers.is("Maria Joaquina"))
	 	;
		
	}

}
