package com.nasva.crptapi;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.*;

class CrptApiTest {

    @Test
    void createDocument() throws InterruptedException {

        WireMockServer wireMockServer = new WireMockServer(options().port(8089));
        wireMockServer.start();
        wireMockServer.stubFor(post("/v3/lk/documents/create").willReturn(ok()));
        CrptApi crptApi = new CrptApi( new CrptApi.InMemoryRequestLimiter(3, TimeUnit.MINUTES),
                wireMockServer.baseUrl());
        CrptApi.Document.Description description = new CrptApi.Document.Description();

        List<CrptApi.Document.Product> products = new ArrayList<>();
        CrptApi.Document.Product product = new CrptApi.Document.Product();
        products.add(product);
        CrptApi.Document document = new CrptApi.Document();
        document.setProducts(products);
        document.setDescription(description);

        crptApi.createDocument(document);
        wireMockServer.stop();
    }

    @Test
    void testRequestLimiter(){
        List<LocalDateTime> times = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            times.add(LocalDateTime.of(2024,5,5,10,10,i));
        }
        int requestLimit = 3;
        CrptApi.RequestLimiter requestLimiter = new CrptApi.InMemoryRequestLimiter(requestLimit, TimeUnit.MINUTES);
        List<LocalDateTime> list = new ArrayList<>();
        List<LocalDateTime> expected = List.of(
                LocalDateTime.parse("2024-05-05T10:10:00"),
                LocalDateTime.parse("2024-05-05T10:10:01"),
                LocalDateTime.parse("2024-05-05T10:10:02"),
                LocalDateTime.parse("2024-05-05T10:11:00"),
                LocalDateTime.parse("2024-05-05T10:11:01"),
                LocalDateTime.parse("2024-05-05T10:11:02"),
                LocalDateTime.parse("2024-05-05T10:12:00"),
                LocalDateTime.parse("2024-05-05T10:12:01"),
                LocalDateTime.parse("2024-05-05T10:12:02"),
                LocalDateTime.parse("2024-05-05T10:13:00")
        );
        for (int i = 0; i < 10; i++) {
            list.add(requestLimiter.nextTime(times.get(i)));
        }

        Assertions.assertEquals(expected, list);
    }

}