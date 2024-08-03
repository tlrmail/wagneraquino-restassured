package br.ce.wcaquino.rest;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.internal.path.xml.NodeBase;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserXMLTest {

	private static RequestSpecification requestSpecification; 
	private static ResponseSpecification responseSpecification; 
	
	@BeforeClass
	public static void setup() {
		RestAssured.baseURI = "https://restapi.wcaquino.me";
		RestAssured.port = 443;
		RestAssured.basePath = "/usersXML";

		RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder();
		requestSpecBuilder.log(LogDetail.ALL);
		requestSpecification = requestSpecBuilder.build();
		
		ResponseSpecBuilder responseSpecBuilder = new ResponseSpecBuilder();
		responseSpecBuilder.expectStatusCode(200);
		responseSpecification = responseSpecBuilder.build();
	}
	
	@Test
	public void test_a_deveTrabalharComXML() {
		
		
		RestAssured
			.given()//Pré-condições
				.log().all()
			.when() //Ação
				.get("/3")
			.then() // Asserts
				.statusCode(200)
				.body("user.name", is("Ana Julia"))
				.body("user.@id", is("3"))
				.body("user.filhos.name.size()", is(2))
				.body("user.filhos.name[1]", is("Luizinho"))
				.body("user.filhos.name", Matchers.hasItem("Luizinho"))
				.body("user.filhos.name", Matchers.hasItems("Luizinho", "Zezinho"))
			;
	}
	
	@Test
	public void test_b_deveTrabalharComRootPath_1() {
		RestAssured
			.given()
				.spec(requestSpecification)
			.when()
				.get("/3")
			.then()
				.spec(responseSpecification)
				.rootPath("User")
				.body("name", is("Ana Julia"))
				.body("@id", is("3"))
				.body("filhos.name.size()", is(2))
				.body("filhos.name[1]", is("Luizinho"))
				.body("filhos.name", Matchers.hasItem("Luizinho"))
				.body("filhos.name", Matchers.hasItems("Luizinho", "Zezinho"))
			;
	}

	@Test
	public void test_c_deveTrabalharComRootPath_2() {
		RestAssured
			.given()
				.spec(requestSpecification)
			.when()
				.get("/3")
			.then()
				.spec(responseSpecification)
				.rootPath("user")
				.body("name", is("Ana Julia"))
				.body("@id", is("3"))
				.rootPath("user.filhos")
				.body("name.size()", is(2))
				.body("name[1]", is("Luizinho"))
				.body("name", Matchers.hasItem("Luizinho"))
				.body("name", Matchers.hasItems("Luizinho", "Zezinho"))
			;
	}

	@Test
	public void test_d_deveTrabalharComRootPath_3() {
		RestAssured
			.given()
				.spec(requestSpecification)
			.when()
				.get("/3")
			.then()
				.spec(responseSpecification)
				.rootPath("user")
				.body("name", is("Ana Julia"))
				.body("@id", is("3"))
				.rootPath("user.filhos")
				.body("name.size()", is(2))
				.detachRootPath("filhos") // tira o filhos de user.filhos
				.body("filhos.name[1]", is("Luizinho"))
				.body("filhos.name", Matchers.hasItem("Luizinho"))
				.appendRootPath("filhos") // coloca filhos de volta
				.body("name", Matchers.hasItems("Luizinho", "Zezinho"))
			;
	}

	/**
	 * A linguagem usada dentro do body é groovy
	 */
	@Test
	public void test_e_deveFazerPesquisasAvancadasXML() {
		RestAssured
			.given()
				.spec(requestSpecification)
			.when()
				.get()
			.then()
				.spec(responseSpecification)
				.body("users.user.size()", is(3))
				.body("users.user.findAll{it.age.toInteger() <= 25}.size()", is(2))
				.body("users.user.@id", hasItems("1","2","3"))
				.body("users.user.find{it.age == 25}.name", is("Maria Joaquina"))
				.body("users.user.findAll{it.name.toString().contains('n')}.name", hasItems("Maria Joaquina","Ana Julia"))
				.body("users.user.salary.find{it != null}", is("1234.5678"))
				.body("users.user.salary.find{it != null}.toDouble()", is(1234.5678))
				.body("users.user.age.collect{it.toInteger() * 2}", hasItems(40,50,60))
				.body("users.user.name.findAll{it.toString().startsWith('Maria')}.collect{it.toString().toUpperCase()}", is("MARIA JOAQUINA"))
			;
	}

	/**
	 * A linguagem usada dentro do path é groovy
	 */
	@Test
	public void test_f_deveFazerPesquisasAvancadasXMLeJava_1() {
		Object nome = 
			RestAssured
				.given()
					.spec(requestSpecification)
				.when()
					.get()
				.then()
					.spec(responseSpecification)
					.extract().path("users.user.name.findAll{it.toString().startsWith('Maria')}")
				;
		Assert.assertEquals("Maria Joaquina", nome.toString());
	}

	/**
	 * A linguagem usada dentro do path é groovy
	 */
	@Test
	public void test_g_deveFazerPesquisasAvancadasXMLeJava_2() {
		ArrayList<NodeBase> nomes =     // deve ser Usado o NodeImpl, mas ele não estava aparecendo no pacote.... 
			RestAssured
				.given()
					.spec(requestSpecification)
				.when()
					.get()
				.then()
					.spec(responseSpecification)
					.extract().path("users.user.name.findAll{it.toString().contains('n')}")
				;
		System.out.println(nomes.get(0).toString().toUpperCase());
		Assert.assertEquals(2, nomes.size());
		Assert.assertEquals("MARIA JOAQUINA", nomes.get(0).toString().toUpperCase());
		Assert.assertEquals("ANA JULIA", nomes.get(1).toString().toUpperCase());

		NodeBase nb = nomes.get(0);
		System.out.println(nb.toString());
	}
	
	/**
	 * O rosseta stone and cookhood tem várias especificações do xpath
	 */
	@Test
	public void test_h_deveFazerPesquisasAvancadasComXMLeXPath() {
			RestAssured
				.given()
					.spec(requestSpecification)
				.when()
					.get()
				.then()
					.spec(responseSpecification)
//					.body(hasXPath("count(//user/name)"), is("3"))
					.body(hasXPath("/users/user[@id='1']"))
					.body(hasXPath("//user[@id='2']"))
					.body(hasXPath("//name[text()='Luizinho']"))
					.body(hasXPath("//name[text()='Luizinho']/../../name[text()='Ana Julia']")) // o /../../ desce dois níveis no xml
					.body(hasXPath("//name[text()='Ana Julia']/following-sibling::filhos"), allOf(containsString("Zezinho"), containsString("Luizinho")))    
					.body(hasXPath("/users/user[@id='1']/name"))
					.body(hasXPath("/users/user[last()]/name"))
					.body(hasXPath("/users/user/name[contains(.,'n')]"))
					.body(hasXPath("//user[age < 24]/name"))
					.body(hasXPath("//user[age > 20 and age < 30]/name"))
					.body(hasXPath("//user[age > 20][age < 30]/name"))
			;

	}
	
}
