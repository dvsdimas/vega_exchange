package com.vega.exchange.services;

import com.vega.exchange.instruments.Quote;

import java.util.Map;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

public class StaticQuoting implements Quoting {

    private final Map<UUID, Quote> prices;

    public StaticQuoting(Map<UUID, Quote> prices) {
        this.prices = requireNonNull(prices);
    }

    @Override
    public Quote getQuote(UUID instrumentId) {

        if(!prices.containsKey(instrumentId)) {
            throw new IllegalArgumentException("Unsupported instrument %s".formatted(instrumentId));
        }

        return prices.get(instrumentId); // this is for test, in real system it will provide the best available market price
    }

}
