package net.cloudcentrik.autolink.testservice.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.cloudcentrik.autolink.testservice.model.TestUrl;
import net.cloudcentrik.autolink.testservice.model.WikiPageViews;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class WikiHttpClient {
    private final ObjectMapper objectMapper = new ObjectMapper();
    //private final String url = TestUrl.wikipedia;
    private final WebClient webClient;

    public WikiHttpClient(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public CompletableFuture<List<WikiPageViews>> getPageViews(String subject, String startDate, String endDate) {
        final String url = TestUrl.createWikiPageViewsUrl(subject, startDate, endDate);
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(s -> s.path("items"))
                .map(s -> {
                    try {
                        return objectMapper.readValue(s.traverse(), new TypeReference<List<WikiPageViews>>() {
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                        return new ArrayList<WikiPageViews>();
                    }
                })
                .toFuture();
    }

    public InputStream urlResource() throws IOException {
        URL url = new URL("https://path.to.your.resource");
        URLConnection urlConnection = url.openConnection();
        // urlConnection.setRequestProperty("", ""); // set auth headers if necessary
        return urlConnection.getInputStream();
    }

    /*@Bean
    public JsonItemReader<WikiPageViews> itemReader() throws IOException {
        return new JsonItemReaderBuilder<WikiPageViews>()
                .name("restReader")
                .resource(new InputStreamResource(urlResource()))
                .strict(true)
                .jsonObjectReader(new JacksonJsonObjectReader<>(WikiPageViews.class))
                .build();
    }*/

}
