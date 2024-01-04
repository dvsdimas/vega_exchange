package com.vega.exchange.orders;

import com.vega.exchange.trades.Trade;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;

public class CompositeOrderContainer {

    public final Order compositeOrder;
    public final Set<Order> partialOrders;
    public final Set<Trade> completedTrades;

    public CompositeOrderContainer(Order compositeOrder, Set<Order> partialOrders, Set<Trade> completedTrades) {
        this.compositeOrder = requireNonNull(compositeOrder);
        this.partialOrders = requireNonNull(partialOrders);
        this.completedTrades = requireNonNull(completedTrades);
    }

    public CompositeOrderContainer addCompletedTrade(Trade trade) { // todo tests
        requireNonNull(trade);

        final var trades = Stream.concat(completedTrades.stream(), Stream.of(trade)).collect(Collectors.toSet());

        return new CompositeOrderContainer(compositeOrder, partialOrders, trades);
    }

    public boolean completed() { // todo tests
        return partialOrders.size() == completedTrades.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final var that = (CompositeOrderContainer) o;
        return Objects.equals(compositeOrder, that.compositeOrder) &&
                Objects.equals(partialOrders, that.partialOrders) &&
                Objects.equals(completedTrades, that.completedTrades);
    }

    @Override
    public int hashCode() {
        return hash(compositeOrder, partialOrders, completedTrades);
    }

}
