package pl.edu.s28201.nai_01.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Configuration
public class ConsoleConfig {

    @Bean
    public BufferedReader getConsoleReader() {
        return new BufferedReader(new InputStreamReader(System.in));
    }
}
