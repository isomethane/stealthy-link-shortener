package site.isolink.stealthylinkshortener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * Main application class.
 */
@SpringBootApplication
@PropertySource("classpath:public.properties")
public class StealthyLinkShortenerApplication {
	/**
	 * Application main function.
	 * @param args string arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(StealthyLinkShortenerApplication.class, args);
	}
}
