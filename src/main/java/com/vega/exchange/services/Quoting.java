package com.vega.exchange.services;

import com.vega.exchange.instruments.Quote;

import java.util.UUID;

public interface Quoting {

    Quote getQuote(UUID instrument);

}
