package org.example.skillwheel.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Configuration
public class JacksonConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();

        // Register JavaTimeModule for JSR310 types (Java 8 date/time)
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        // Register custom deserializer for LocalTime
        SimpleModule customTimeModule = new SimpleModule();
        customTimeModule.addDeserializer(LocalTime.class, new FlexibleLocalTimeDeserializer());

        objectMapper.registerModules(javaTimeModule, customTimeModule);

        return objectMapper;
    }

    /**
     * Custom deserializer for LocalTime that supports both HH:mm and HH:mm:ss
     * formats
     */
    public static class FlexibleLocalTimeDeserializer extends JsonDeserializer<LocalTime> {
        private static final DateTimeFormatter FORMATTER_WITH_SECONDS = DateTimeFormatter.ofPattern("HH:mm:ss");
        private static final DateTimeFormatter FORMATTER_WITHOUT_SECONDS = DateTimeFormatter.ofPattern("HH:mm");

        @Override
        public LocalTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String timeString = p.getValueAsString();

            try {
                // First try with seconds
                return LocalTime.parse(timeString, FORMATTER_WITH_SECONDS);
            } catch (DateTimeParseException e) {
                try {
                    // Then try without seconds
                    return LocalTime.parse(timeString, FORMATTER_WITHOUT_SECONDS);
                } catch (DateTimeParseException ex) {
                    throw new IOException("Could not parse time: " + timeString, ex);
                }
            }
        }
    }
}