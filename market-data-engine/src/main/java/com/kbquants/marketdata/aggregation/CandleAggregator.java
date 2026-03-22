package com.kbquants.marketdata.aggregation;


import com.kbquants.marketdata.model.Candle;
import com.kbquants.marketdata.model.Timeframe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class CandleAggregator {

    private static final Logger log = LoggerFactory.getLogger(CandleAggregator.class);

    private final Timeframe timeframe;
    private final String symbol;
    private final Consumer<Candle> onCandleComplete;

    private TimeBucket currentBucket;

    public CandleAggregator(Timeframe timeframe, String symbol, Consumer<Candle> listener) {
        this.timeframe = timeframe;
        this.symbol = symbol;
        this.onCandleComplete = listener;
    }

    public void onCandle(Candle input) {

        long bucketStart = timeframe.getBucketStart(input.getTimestamp());

        if (currentBucket == null) {
            currentBucket = new TimeBucket(bucketStart);
        }

        if (bucketStart != currentBucket.getBucketStart()) {

            emitCurrent();
            currentBucket = new TimeBucket(bucketStart);
        }

        currentBucket.update(
                input.getOpen(),
                input.getHigh(),
                input.getLow(),
                input.getClose(),
                input.getVolume()
        );
    }

    public void flush() {
        if (currentBucket == null) {
            log.debug("No candle bucket to flush for symbol={} timeframe={}", symbol, timeframe);
            return;
        }

        emitCurrent();
        currentBucket = null;
    }

    private void emitCurrent() {

        Candle candle = Candle.builder()
                .timestamp(currentBucket.getBucketStart())
                .open(currentBucket.getOpen())
                .high(currentBucket.getHigh())
                .low(currentBucket.getLow())
                .close(currentBucket.getClose())
                .volume(currentBucket.getVolume())
                .symbol(symbol)
                .timeframe(timeframe)
                .build();

        onCandleComplete.accept(candle);
    }
}
