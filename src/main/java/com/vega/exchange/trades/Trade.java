package com.vega.exchange.trades;

import com.vega.exchange.instruments.Quote;
import com.vega.exchange.orders.Order;

public record Trade(Order buyOrder,
                    Order sellOrder,
                    Quote quote) {
    public Trade {
        if(!buyOrder.instrumentId.equals(sellOrder.instrumentId)) {
            throw new IllegalArgumentException("Instrument id not match for orders %s %s".formatted(buyOrder.id, sellOrder.id));
        }
        if(!buyOrder.instrumentId.equals(quote.instrumentId())) {
            throw new IllegalArgumentException("Instrument id not match for orders %s %s and quote".formatted(buyOrder.id, sellOrder.id));
        }
        if(!buyOrder.quantity.equals(sellOrder.quantity)) {
            throw new IllegalArgumentException("Orders with not match amount %s %s".formatted(buyOrder.id, sellOrder.id));
        }
    }

}
