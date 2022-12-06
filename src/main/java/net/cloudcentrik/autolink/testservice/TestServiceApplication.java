package net.cloudcentrik.autolink.testservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import net.cloudcentrik.autolink.testservice.model.Greeting;
import net.cloudcentrik.autolink.testservice.model.WikiPageViews;
import net.cloudcentrik.autolink.testservice.utils.WikiHttpClient;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.time.Clock;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Log4j2
@SpringBootApplication
@EnableAsync
//@EnableBatchProcessing
//@EnableDiscoveryClient
@ComponentScan("net.cloudcentrik.autolink.testservice")
public class TestServiceApplication {
	private final ObjectMapper objectMapper = new ObjectMapper();
	@Autowired
	private WikiHttpClient wikiHttpClient;

	@Value("${server.port}")
	private Integer port;

	@Value("${spring.application.name}")
	private String name;

	private final Function<ServerRequest, ServerRequest> requestProcessor = (request) -> {
		log.info("=====> uri: {}",request.uri().toString());
		return request;
	};

	@Bean
	RouterFunction<ServerResponse> routes() {
		return route()
				.before(requestProcessor)
				.GET("/greetings", r -> ok().bodyValue(Map.of("greetings", "Hello, world!")))
				.GET("/start", r -> {
					CompletableFuture<Void> cf = CompletableFuture.runAsync(() -> {
						startProcessing();
					});
					var now = Clock.systemDefaultZone().instant();
					return ok().bodyValue(new Greeting("status: processing started on :http://"+name+":"+port+"/ at:"+now));
				})
				.build();
	}

	@Bean
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(2);
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("TestTask-");
		executor.initialize();
		return executor;
	}

	@Async
	public void startProcessing() {
		//TestTask.startTestTask();
		wikiHttpClient.getPageViews("sweden","20221101","20221201")
				.thenAccept(response ->response.stream()
						.forEach(wikiPageViews ->logViews(wikiPageViews)));
	}

	public static void main(String[] args) {
		SpringApplication.run(TestServiceApplication.class, args);
	}

	@SneakyThrows
	private void logViews(WikiPageViews wikiPageViews){
		log.info(objectMapper.writeValueAsString(wikiPageViews));
		Thread.sleep(100);
	}

}
