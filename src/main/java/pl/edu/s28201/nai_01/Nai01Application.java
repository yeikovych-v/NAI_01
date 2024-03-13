package pl.edu.s28201.nai_01;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import pl.edu.s28201.nai_01.controller.ProgramController;

@SpringBootApplication
public class Nai01Application {

	private final ProgramController controller;
	private final ApplicationContext context;

	@Autowired
	public Nai01Application(ProgramController controller, ApplicationContext context) {
		this.controller = controller;
        this.context = context;
    }

	public static void main(String[] args) {
		SpringApplication.run(Nai01Application.class, args);
	}


	@Bean
	public CommandLineRunner commandLineRunner() {
		return args -> {
			controller.startProgram();
			finish();
		};
	}

	public void finish() {
		SpringApplication.exit(context);
	}
}
