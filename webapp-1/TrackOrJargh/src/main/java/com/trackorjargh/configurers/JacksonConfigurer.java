package com.trackorjargh.configurers;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

@Configuration
public class JacksonConfigurer extends WebMvcConfigurerAdapter {

	@Bean
	public Jackson2ObjectMapperBuilder jacksonBuilder() {
		return new Jackson2ObjectMapperBuilder().failOnUnknownProperties(false)
				.serializationInclusion(Include.NON_EMPTY).serializerByType(Page.class, new JsonPageSerializer());

	}

	@SuppressWarnings("rawtypes")
	public class JsonPageSerializer extends JsonSerializer {

		@Override
		public void serialize(Object object, JsonGenerator jsonGen, SerializerProvider serializerProvider)
				throws IOException, JsonProcessingException {
			Page page = (Page) object;
			ObjectMapper om = new ObjectMapper().disable(MapperFeature.DEFAULT_VIEW_INCLUSION)
					.setSerializationInclusion(Include.NON_EMPTY);
			jsonGen.writeStartObject();
			jsonGen.writeFieldName("content");
			jsonGen.writeRawValue(
					om.writerWithView(serializerProvider.getActiveView()).writeValueAsString(page.getContent()));
			jsonGen.writeFieldName("last");
			jsonGen.writeBoolean(page.isLast());
			jsonGen.writeFieldName("totalElements");
			jsonGen.writeNumber(page.getTotalElements());
			jsonGen.writeFieldName("totalPages");
			jsonGen.writeNumber(page.getTotalPages());
			jsonGen.writeFieldName("size");
			jsonGen.writeNumber(page.getSize());
			jsonGen.writeFieldName("number");
			jsonGen.writeNumber(page.getNumber());
			jsonGen.writeObjectField("sort", page.getSort());
			jsonGen.writeFieldName("first");
			jsonGen.writeBoolean(page.isFirst());
			jsonGen.writeFieldName("numberOfElements");
			jsonGen.writeNumber(page.getNumberOfElements());
			jsonGen.writeEndObject();
		}

	}
}