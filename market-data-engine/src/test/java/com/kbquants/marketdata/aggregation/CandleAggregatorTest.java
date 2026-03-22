package com.kbquants.marketdata.aggregation;

import com.kbquants.marketdata.model.Candle;
import com.kbquants.marketdata.model.Timeframe;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CandleAggregatorTest {

    @Test
    void shouldFlushFinalBucketWhenStreamStops() {
        List<Candle> completedCandles = new ArrayList<>();
        CandleAggregator candleAggregator = new CandleAggregator(Timeframe.ONE_MIN, "NSE_EQ|INE002A01018", completedCandles::add);

        candleAggregator.onCandle(buildCandle(0L, 100.0, 101.0, 99.0, 100.5, 10L));
        candleAggregator.onCandle(buildCandle(30_000L, 100.5, 102.0, 100.0, 101.5, 5L));

        candleAggregator.flush();

        assertEquals(1, completedCandles.size());
        Candle completedCandle = completedCandles.get(0);
        assertEquals(0L, completedCandle.getTimestamp());
        assertEquals(100.0, completedCandle.getOpen());
        assertEquals(102.0, completedCandle.getHigh());
        assertEquals(99.0, completedCandle.getLow());
        assertEquals(101.5, completedCandle.getClose());
        assertEquals(15L, completedCandle.getVolume());
        assertEquals("NSE_EQ|INE002A01018", completedCandle.getSymbol());
        assertEquals(Timeframe.ONE_MIN, completedCandle.getTimeframe());
    }

    @Test
    void shouldContinueAggregatingAfterFlushStartsNewBucket() {
        List<Candle> completedCandles = new ArrayList<>();
        CandleAggregator candleAggregator = new CandleAggregator(Timeframe.ONE_MIN, "NSE_EQ|INE002A01018", completedCandles::add);

        candleAggregator.onCandle(buildCandle(0L, 100.0, 101.0, 99.0, 100.5, 10L));
        candleAggregator.flush();
        candleAggregator.onCandle(buildCandle(60_000L, 101.0, 103.0, 100.5, 102.5, 12L));
        candleAggregator.flush();

        assertEquals(2, completedCandles.size());
        assertEquals(0L, completedCandles.get(0).getTimestamp());
        assertEquals(60_000L, completedCandles.get(1).getTimestamp());
    }

    private Candle buildCandle(long timestamp,
                               double open,
                               double high,
                               double low,
                               double close,
                               long volume) {
        return Candle.builder()
                .timestamp(timestamp)
                .open(open)
                .high(high)
                .low(low)
                .close(close)
                .volume(volume)
                .symbol("NSE_EQ|INE002A01018")
                .timeframe(Timeframe.ONE_MIN)
                .build();
    }
}
