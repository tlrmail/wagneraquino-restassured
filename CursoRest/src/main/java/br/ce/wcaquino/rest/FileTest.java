package br.ce.wcaquino.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FileTest {

	@Test
	public void a_deveObrigarEnvioArquivo() {
		given()
			.log().all()
		.when()
			.post("https://restapi.wcaquino.me/upload")
		.then()
			.log().all()
			.statusCode(404)  // Deveria ser 400
			.body("error", is("Arquivo não enviado"))
		;
	}

	@Test
	public void b_deveFazerUploadArquivo() {
		given()
			.log().all()
			.multiPart("arquivo", new File("C:\\Users\\tlrma\\Desktop\\FAP.txt"))
		.when()
			.post("https://restapi.wcaquino.me/upload")
		.then()
			.log().all()
			.statusCode(200)  // Deveria ser 400
			.body("name", is("FAP.txt"))
		;
	}

	@Test
	public void c_naoDeveFazerUploadDeArquivoGrande() {
		
		given()
			.log().all()
			.multiPart("arquivo", new File("src/main/resources/chromedriver-win64.zip"))
		.when()
			.post("https://restapi.wcaquino.me/upload")
		.then()
			.log().all()
			.time(Matchers.lessThan(5000L))
			.statusCode(413)
		;
	}
	
	@Test
	public void d_deveBaixarArquivo_1() {
		given()
			.log().all()
		.when()
			.get("https://restapi.wcaquino.me/download")
		.then()
			.log().all()
			.statusCode(200)
		;
	}

	@Test
	public void e_deveBaixarArquivo_2() throws IOException {
		
		byte[] image = 		
			given()
				.log().all()
			.when()
				.get("https://restapi.wcaquino.me/download")
			.then()
				.log().all()
				.statusCode(200)
				.extract().asByteArray()
			;
		File img = new File("src/main/resources/file.jpg");
		OutputStream output = new FileOutputStream(img);
		output.write(image);
		output.close();
		
		System.out.println(img.length());
		Assert.assertThat(img.length(), Matchers.lessThan(100000L));
		
	}
}
