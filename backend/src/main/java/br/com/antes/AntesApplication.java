package br.com.antes;

import java.time.Clock;
import java.time.ZoneId;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AntesApplication {
    public static void main(String[] args) { SpringApplication.run(AntesApplication.class, args); }

    @Bean
    Clock clock(@Value("${antes.fuso-horario}") String fuso) { return Clock.system(ZoneId.of(fuso)); }
}
