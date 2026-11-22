package com.trackorjargh.e2e.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.transaction.Transactional;

import static java.lang.invoke.MethodHandles.lookup;
import static org.junit.Assert.assertEquals;
import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trackorjargh.grafics.Grafics;
import com.trackorjargh.javaclass.Gender;
import com.trackorjargh.javarepository.GenderRepository;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class TestGraphics {

	final static Logger log = getLogger(lookup().lookupClass());
	
	@Autowired
	private GenderRepository genderRepository;
	
	@Autowired
	protected WebApplicationContext webAppCtx;
	
	private MockMvc mvc; 

	@BeforeEach
	public void setUp() {
		mvc = MockMvcBuilders.webAppContextSetup(webAppCtx)
				.apply(springSecurity()).build();
	}

	@Test
	@Transactional
	public void checkGenreGraph() throws Exception {
		HashMap<String, Integer> points = new HashMap<>();
		
		int sumGenre;
		for (Gender genre : genderRepository.findAll()) {
			sumGenre = 0;
			sumGenre += genre.getFilms().size();
			sumGenre += genre.getBooks().size();
			sumGenre += genre.getShows().size();

			points.put(genre.getName(), sumGenre);
		}
		
		
		MvcResult result = mvc.perform(get("/api/generos/grafico")).andReturn();
		List<Grafics> listGenres = json2graph(result.getResponse().getContentAsString());
		
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
}
