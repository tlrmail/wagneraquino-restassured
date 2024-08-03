package br.ce.wcaquino.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.is;

import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserRequestResponseXMLTest {

	public static RequestSpecification reqSpecification; 
	public static ResponseSpecification resSpecification;
	
	@BeforeClass
	public static void setup() {
		RestAssured.baseURI = "https://restapi.wcaquino.me/usersXML"; 

		RequestSpecBuilder reqBuilder = new RequestSpecBuilder();
		reqBuilder.log(LogDetail.ALL);
		reqSpecification = reqBuilder.build();

		ResponseSpecBuilder resbBuilder = new ResponseSpecBuilder();
		resbBuilder.expectStatusCode(200);
		resbBuilder.log(LogDetail.ALL);
		resSpecification = resbBuilder.build();
	}
	
	@Test
	public void test_a_devoTrabalharComXML() {
		given()
			.spec(reqSpecification)
		.when()
			.get("/3")
		.then()
			.spec(resSpecification)
			.body("user.name", is("Ana Julia"))
			.body("user.@id", is("3"))
			.body("user.filhos.name.size()", is(2))
			.body("user.filhos.name[0]", is("Zezinho"))
			.body("user.filhos.name[1]", is("Luizinho"))
			.body("user.filhos.name", hasItem("Zezinho"))
			.body("user.filhos.name", hasItems("Zezinho", "Luizinho"))

		;
	}

	@Test
	public void test_b_devoTrabalharComXML() {
		given()
			.spec(reqSpecification)
		.when()
			.get("https://restapi.wcaquino.me/usersXML/3")
		.then()
			.spec(resSpecification)
			.rootPath("user")
			.body("name", is("Ana Julia"))
			.body("@id", is("3"))
			.body("filhos.name.size()", is(2))
			.body("filhos.name[0]", is("Zezinho"))
			.body("filhos.name[1]", is("Luizinho"))
			.body("filhos.name", hasItem("Zezinho"))
			.body("filhos.name", hasItems("Zezinho", "Luizinho"))

		;
	}

	@Test
	public void test_c_devoTrabalharComXML() {
		given()
			.spec(reqSpecification)
		.when()
			.get("https://restapi.wcaquino.me/usersXML/3")
		.then()
			.spec(resSpecification)
			.rootPath("user")
			.body("name", is("Ana Julia"))
			.body("@id", is("3"))
			.rootPath("user.filhos")
			.body("name.size()", is(2))
			.body("name[0]", is("Zezinho"))
			.body("name[1]", is("Luizinho"))
			.body("name", hasItem("Zezinho"))
			.body("name", hasItems("Zezinho", "Luizinho"))

		;
	}

	@Test
	public void test_d_devoTrabalharComXML() {
		given()
			.spec(reqSpecification)
		.when()
			.get("https://restapi.wcaquino.me/usersXML/3")
		.then()
			.spec(resSpecification)
			.rootPath("user")
			.body("name", is("Ana Julia"))
			.body("@id", is("3"))
			.rootPath("user.filhos")
			.body("name.size()", is(2))
			.detachRootPath("filhos")
			.body("filhos.name[0]", is("Zezinho"))
			.body("filhos.name[1]", is("Luizinho"))
			.appendRootPath("filhos")
			.body("name", hasItem("Zezinho"))
			.body("name", hasItems("Zezinho", "Luizinho"))
		;
	}
	
	@Test
	public void test_e_devoFazerPesquisasAvancadasComXML() {
		given()
			.spec(reqSpecification)
		.when()
			.get("https://restapi.wcaquino.me/usersXML")
		.then()
			.spec(resSpecification)
			.body("users.user.size()", is(3))
			.body("users.user.findAll{it.age.toInteger() <= 25}.size()", is(2))
			.body("users.user.@id", hasItems("1", "2", "3"))
			.body("users.user.find{it.age == 25}.name", is("Maria Joaquina"))
			.body("users.user.findAll{it.name.toString().contains('n')}.name", hasItems("Maria Joaquina", "Ana Julia"))
			.body("users.user.salary.find{it != null}", is("1234.5678"))
			.body("users.user.salary.find{it != null}.toDouble()", is(1234.5678d))
			.body("users.user.age.collect{it.toInteger() * 2}", hasItems(40,50,60))
			.body("users.user.name.findAll{it.toString().startsWith('Maria')}.collect{it.toString().toUpperCase()}", is("MARIA JOAQUINA"))
		;
	}
	
	@Test
	public void test_f_devoFazerPesquisasAvancadasComXMLEJava() {
		String nome = 
			given()
				.spec(reqSpecification)
			.when()
				.get("https://restapi.wcaquino.me/usersXML")
			.then()
				.spec(resSpecification)
				.extract().path("users.user.name.findAll{it.toString().startsWith('Maria')}")
		;
		System.out.println(nome.toString());
		Assert.assertEquals("Maria Joaquina".toUpperCase(), nome.toUpperCase());

	}
	
	@Test
	public void test_g_devoFazerPesquisasAvancadasComXMLEJava() {
		List<Object> nomes = 
				given()
					.spec(reqSpecification)
				.when()
					.get("https://restapi.wcaquino.me/usersXML")
				.then()
					.spec(resSpecification)
					.extract().path("users.user.name.findAll{it.toString().contains('n')}")
				;
			System.out.println(nomes.toString());
			Assert.assertEquals(2, nomes.size());
			Assert.assertEquals("Maria Joaquina".toUpperCase(), nomes.get(0).toString().toUpperCase());
			Assert.assertTrue("Ana JulIA".equalsIgnoreCase(nomes.get(1).toString()));
	}

	/**
	 * Procurar no google: roseta stone xpath
	 */
	@Test
	public void test_h_devoFazerPesquisasAvancadasComXMLComXPath() {
				given()
					.spec(reqSpecification)
				.when()
					.get("https://restapi.wcaquino.me/usersXML")
				.then()
					.spec(resSpecification)
					.body(hasXPath("/users/user[@id='1']"))
					.body(hasXPath("//user[@id='2']"))
					.body(hasXPath("//name[text()='Luizinho']"))
					.body(hasXPath("//name[text()='Ana Julia']/following-sibling::filhos"), allOf(containsString("Zezinho"), containsString("Luizinho")))
					.body(hasXPath("//name[text()='Ana Julia']/following-sibling::filhos"), allOf(containsString("Zezinho"), containsString("Luizinho")))
//					.body(hasXPath("/users/user[2]/name"), is("Maria Joaquina"))
//					.body(hasXPath("/users/user[last()]/name"), is("Ana Julia"))
//					.body(hasXPath("//user[age < 24]/name"), is("Ana Julia"))
					
				;
	}
	
}
