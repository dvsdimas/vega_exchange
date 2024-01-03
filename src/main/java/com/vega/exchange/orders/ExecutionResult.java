package com.vega.exchange.orders;

import com.vega.exchange.trades.Trade;

import java.util.Set;

import static java.util.Objects.requireNonNull;

public class ExecutionResult {

    public final Order order;
    public final Set<Trade> trades;

    public ExecutionResult(Order order, Set<Trade> trades) {
        this.order = requireNonNull(order);
        this.trades = requireNonNull(trades);
        if(trades.isEmpty()) {
            throw new IllegalArgumentException("Execution result for order %s not have any trades".formatted(order.id));
        }
    }
}
