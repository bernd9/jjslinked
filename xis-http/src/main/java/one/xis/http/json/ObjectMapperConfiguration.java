package one.xis.http.json;

import one.xis.Bean;
import one.xis.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
class ObjectMapperConfiguration {

    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}
