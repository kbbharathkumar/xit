package com.kbquants.marketdata.broker.upstox;

import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UpstoxHistoricalClientTest {

    @Test
    void shouldEncodeInstrumentKeyAndPlaceToDateBeforeFromDate() {
        UpstoxHistoricalClient client = new UpstoxHistoricalClient("token");

        URI historicalUri = client.buildHistoricalUri(
                "NSE_EQ|INE002A01018",
                "minutes/1",
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 2));

        assertEquals(
                "https://api.upstox.com/v3/historical-candle/NSE_EQ%7CINE002A01018/minutes/1/2025-01-02/2025-01-01",
                historicalUri.toString());
    }
}
