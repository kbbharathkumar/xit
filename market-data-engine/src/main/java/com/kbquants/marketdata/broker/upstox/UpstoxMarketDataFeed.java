package com.kbquants.marketdata.broker.upstox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kbquants.marketdata.feed.MarketDataFeed;
import com.kbquants.marketdata.model.Candle;
import com.kbquants.marketdata.model.Timeframe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class UpstoxMarketDataFeed implements MarketDataFeed {

    private static final Logger log = LoggerFactory.getLogger(UpstoxMarketDataFeed.class);

    private static final Map<Timeframe, String> TIMEFRAME_PATHS = buildTimeframePaths();

    private final UpstoxHistoricalClient client;
    private final UpstoxCandleParser parser;
    private final ObjectMapper mapper;

    public UpstoxMarketDataFeed(String token, ObjectMapper mapper) {
        this.client = new UpstoxHistoricalClient(token);
        this.parser = new UpstoxCandleParser();
        this.mapper = mapper;
    }

    @Override
    public List<Candle> getHistoricalCandles(String symbol, Timeframe timeframe, LocalDate from, LocalDate to) {

        try {
            String raw = client.fetch(symbol, mapTimeframePath(timeframe), from, to);
            UpstoxCandleResponse response = mapper.readValue(raw, UpstoxCandleResponse.class);

            return parser.parse(response, symbol, timeframe);

        } catch (Exception exception) {
            log.error("Failed to fetch historical candles for symbol={} timeframe={}", symbol, timeframe, exception);
            throw new RuntimeException(exception);
        }
    }

//    @Override
//    public void subscribe(String symbol,
//                          Timeframe timeframe,
//                          CandleListener listener) {
//
//        // TODO: WebSocket implementation (Phase 2)
//    }

    @Override
    public void unsubscribe(String symbol) {
        // TODO
    }

    String mapTimeframePath(Timeframe timeframe) {
        String timeframePath = TIMEFRAME_PATHS.get(timeframe);
        if (timeframePath == null) {
            throw new IllegalArgumentException("Unsupported timeframe: " + timeframe);
        }
        return timeframePath;
    }

    private static Map<Timeframe, String> buildTimeframePaths() {
        Map<Timeframe, String> timeframePaths = new EnumMap<>(Timeframe.class);
        timeframePaths.put(Timeframe.ONE_MIN, "minutes/1");
        timeframePaths.put(Timeframe.THREE_MIN, "minutes/3");
        timeframePaths.put(Timeframe.FIVE_MIN, "minutes/5");
        timeframePaths.put(Timeframe.FIFTEEN_MIN, "minutes/15");
        timeframePaths.put(Timeframe.ONE_HOUR, "hours/1");
        timeframePaths.put(Timeframe.ONE_DAY, "days/1");
        return timeframePaths;
    }
}
