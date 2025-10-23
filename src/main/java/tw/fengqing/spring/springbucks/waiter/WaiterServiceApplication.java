package tw.fengqing.spring.springbucks.waiter;

import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
//import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
//import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import tw.fengqing.spring.springbucks.waiter.model.Coffee;
//import tw.fengqing.spring.springbucks.waiter.model.CoffeeOrder;

import java.util.TimeZone;

@SpringBootApplication
@EnableCaching
public class WaiterServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(WaiterServiceApplication.class, args);
	}
	
	@Bean
	public Hibernate6Module hibernate6Module() {
		return new Hibernate6Module();
	}

	@Bean
	public Jackson2ObjectMapperBuilderCustomizer jacksonBuilderCustomizer() {
		return builder -> {
			builder.indentOutput(true);
			builder.timeZone(TimeZone.getTimeZone("Asia/Taipei"));
		};
	}
	
	// 註解此方法，如需要設定base-path，可以啟用此方法 http://localhost:8080/api/coffee
	// @Bean
	// public RepositoryRestConfigurer repositoryRestConfigurer() {
	// 	return new RepositoryRestConfigurer() {
	// 		@Override
	// 		public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
	// 			config.setBasePath("/api");
	// 			config.exposeIdsFor(Coffee.class, CoffeeOrder.class);
	// 		}
	// 	};
	// }
}
