package com.kbquants.marketdata.broker.upstox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kbquants.marketdata.model.Timeframe;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UpstoxMarketDataFeedTest {

    @Test
    void shouldMapSupportedTimeframesToUnitAndIntervalPathSegments() {
        UpstoxMarketDataFeed marketDataFeed = new UpstoxMarketDataFeed("token", new ObjectMapper());

        assertEquals("minutes/1", marketDataFeed.mapTimeframePath(Timeframe.ONE_MIN));
        assertEquals("minutes/3", marketDataFeed.mapTimeframePath(Timeframe.THREE_MIN));
        assertEquals("minutes/5", marketDataFeed.mapTimeframePath(Timeframe.FIVE_MIN));
        assertEquals("minutes/15", marketDataFeed.mapTimeframePath(Timeframe.FIFTEEN_MIN));
        assertEquals("hours/1", marketDataFeed.mapTimeframePath(Timeframe.ONE_HOUR));
        assertEquals("days/1", marketDataFeed.mapTimeframePath(Timeframe.ONE_DAY));
    }
}
