package com.vega.exchange.trades;

import com.vega.exchange.instruments.Quote;
import com.vega.exchange.orders.Order;

public record Trade(Order buyOrder,
                    Order sellOrder,
                    Quote quote) {
}
