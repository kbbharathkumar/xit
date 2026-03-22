package com.kbquants.marketdata.broker.upstox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

public class UpstoxHistoricalClient {

    private static final Logger log = LoggerFactory.getLogger(UpstoxHistoricalClient.class);

    private final HttpClient client = HttpClient.newHttpClient();
    private final String accessToken;

    public UpstoxHistoricalClient(String accessToken) {
        this.accessToken = accessToken;
    }

    public String fetch(String instrumentKey, String intervalPath, LocalDate from, LocalDate to) throws Exception {
        URI historicalUri = buildHistoricalUri(instrumentKey, intervalPath, from, to);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(historicalUri)
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            log.error("Upstox API error for instrumentKey={} statusCode={}", instrumentKey, response.statusCode());
            throw new RuntimeException("Upstox API error: " + response.body());
        }

        return response.body();
    }

    URI buildHistoricalUri(String instrumentKey, String intervalPath, LocalDate from, LocalDate to) {
        String encodedInstrumentKey = URLEncoder.encode(instrumentKey, StandardCharsets.UTF_8)
                .replace("+", "%20");

        String url = String.format(
                "https://api.upstox.com/v3/historical-candle/%s/%s/%s/%s",
                encodedInstrumentKey,
                intervalPath,
                to,
                from);

        return URI.create(url);
    }
}
