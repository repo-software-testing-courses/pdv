package com.trackorjargh.e2e.api;

import static java.lang.invoke.MethodHandles.lookup;
import static org.junit.Assert.assertEquals;
import static org.slf4j.LoggerFactory.getLogger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trackorjargh.e2e.api.SSLClientFactory.HttpClientType;
import com.trackorjargh.grafics.Grafics;
import com.trackorjargh.javaclass.Gender;

public class TestAPIRestTemplate extends ElasTestBase {

	final static Logger log = getLogger(lookup().lookupClass());

	protected static String sutUrl;

	@BeforeAll
	public static void setupClass() {
		String sutHost = System.getenv("ET_SUT_HOST");
		String sutPort = System.getenv("ET_SUT_PORT");
		String sutProtocol = System.getenv("ET_SUT_PROTOCOL");

		if (sutHost == null) {
			sutUrl = "http://localhost:8000/";
		} else {
			sutPort = sutPort != null ? sutPort : "8080";
			sutProtocol = sutProtocol != null ? sutProtocol : "http";

			sutUrl = sutProtocol + "://" + sutHost + ":" + sutPort + "/";
		}

		logger.info("Webapp URL: " + sutUrl);
	}

	@Test
	public void checkGenreGraph() throws Exception {
		String urlGenres = sutUrl + "api/generos";
		String urlGraph = sutUrl + "api/generos/grafico";
		
		RestTemplate restTemplate = new RestTemplate(
				SSLClientFactory.getClientHttpRequestFactory(HttpClientType.HttpClient));
		
		String responseGenre = restTemplate.getForEntity(urlGenres, String.class).getBody();
		List<Gender> genres = json2genre(responseGenre);
				
		HashMap<String, Integer> points = new HashMap<>();
		int sumGenre;
		for (Gender genre : genres) {
			sumGenre = 0;
			sumGenre += genre.getFilms().size();
			sumGenre += genre.getBooks().size();
			sumGenre += genre.getShows().size();

			points.put(genre.getName(), sumGenre);
		}

		String responseGraph = restTemplate.getForEntity(urlGraph, String.class).getBody();
		List<Grafics> listGenres = json2graph(responseGraph);
		
		for(Grafics g: listGenres) {
			int num1 = (int) g.getPoints();
			int num2 = points.get(g.getName());
					
			assertEquals("The genre \"" + g.getName() + "\" not match", num1, num2);
		}
		
		log.info("The graph is correct");
	}

	public static List<Grafics> json2graph(String json) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		json = json.replaceAll("numItems", "points");
		return mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, Grafics.class));
	}
	
	public static List<Gender> json2genre(String json) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, Gender.class));
	}

}
