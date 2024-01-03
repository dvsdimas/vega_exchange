package com.vega.exchange.orders;

import com.vega.exchange.trades.Trade;

import java.util.Objects;
import java.util.Set;

import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

public class MatchResult {

    public final Order order;
    public final Set<Trade> trades;

    public MatchResult(Order order, Set<Trade> trades) {
        this.order = requireNonNull(order);
        this.trades = requireNonNull(trades);
        if(trades.isEmpty()) {
            throw new IllegalArgumentException("Execution result for order %s not have any trades".formatted(order.id));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final var that = (MatchResult) o;
        return Objects.equals(order, that.order) &&
                Objects.equals(trades, that.trades);
    }

    @Override
    public int hashCode() {
        return hash(order, trades);
    }
}
