package net.cloudcentrik.autolink.testservice.batch;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import net.cloudcentrik.autolink.testservice.model.TestUrl;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

@Log4j2
public class DataBufferToInputStream {

    private static final String REQUEST_ENDPOINT = TestUrl.wikipedia;

    private static WebClient getWebClient() {
        WebClient.Builder webClientBuilder = WebClient.builder();
        return webClientBuilder.build();
    }

    public static InputStream getResponseAsInputStream(WebClient client, String url) throws IOException, InterruptedException {

        PipedOutputStream pipedOutputStream = new PipedOutputStream();
        PipedInputStream pipedInputStream = new PipedInputStream(1024 * 10);
        pipedInputStream.connect(pipedOutputStream);

        Flux<DataBuffer> body = client.get()
                .uri(url)
                .exchangeToFlux(clientResponse -> {
                    return clientResponse.body(BodyExtractors.toDataBuffers());
                })
                .doOnError(error -> {
                    log.error("error occurred while reading body", error);
                })
                .doFinally(s -> {
                    try {
                        pipedOutputStream.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                })
                .doOnCancel(() -> {
                    log.error("Get request is cancelled");
                });

        DataBufferUtils.write(body, pipedOutputStream)
                .log("Writing to output buffer")
                .subscribe();
        return pipedInputStream;
    }

    private static String readContentFromPipedInputStream(PipedInputStream stream) throws IOException {
        StringBuffer contentStringBuffer = new StringBuffer();
        try {
            Thread pipeReader = new Thread(() -> {
                try {
                    contentStringBuffer.append(readContent(stream));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            pipeReader.start();
            pipeReader.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            stream.close();
        }

        return String.valueOf(contentStringBuffer);
    }

    private static String readContent(InputStream stream) throws IOException {
        StringBuffer contentStringBuffer = new StringBuffer();
        byte[] tmp = new byte[stream.available()];
        int byteCount = stream.read(tmp, 0, tmp.length);
        log.info(String.format("read %d bytes from the stream\n", byteCount));
        contentStringBuffer.append(new String(tmp));
        return String.valueOf(contentStringBuffer);
    }

    public static void testApiCall() throws IOException, InterruptedException {
        WebClient webClient = getWebClient();
        /*InputStream inputStream = getResponseAsInputStream(webClient, REQUEST_ENDPOINT);
        Thread.sleep(10000);
        String content = readContentFromPipedInputStream((PipedInputStream) inputStream);
        log.info("response content: \n{}", content.replace("}", "}\n"));*/

        CompletableFuture<InputStream> futureInputStream = CompletableFuture.supplyAsync(()->{
            try {
                getResponseAsInputStream(webClient, REQUEST_ENDPOINT);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        });

        futureInputStream.thenAccept(inputStream -> {
            String content = null;
            try {
                content = readContentFromPipedInputStream((PipedInputStream) inputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
            log.info("response content: \n{}", content.replace("}", "}\n"));
        });
    }
}
