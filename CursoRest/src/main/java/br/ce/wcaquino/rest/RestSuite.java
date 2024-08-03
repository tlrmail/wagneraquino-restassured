package br.ce.wcaquino.rest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@SuiteClasses({ OlaMundoTest.class, UserJsonTest.class, UserAtributosEstaticosJsonTest.class, VerbosTest.class,
		UserXMLTest.class, UserRequestResponseXMLTest.class, SerializacaoTest.class })
@RunWith(Suite.class)
public class RestSuite {

}
