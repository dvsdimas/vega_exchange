package com.vega.exchange.books;

import com.vega.exchange.instruments.Quote;
import com.vega.exchange.orders.Order;
import com.vega.exchange.trades.Trade;

import java.util.Optional;

public interface InstrumentBook {

    Optional<Trade> add(Order order, Quote quote);

    boolean cancel(Order order);
}
