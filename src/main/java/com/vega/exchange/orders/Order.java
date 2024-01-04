package com.vega.exchange.orders;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static java.util.Objects.hash;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;

public class Order {

    public final UUID id; // use raw UUID instead of micro types, just for speed up
    public final OrderType type;
    public final UUID instrumentId;
    public final UUID traderId;

    public final Long quantity;
    public final Optional<Long> price;
    public final Optional<UUID> parentId;
    public final Optional<Consumer<MatchResult>> callBack; // can provide call for async update about composite order completion

    public Order(UUID id,
                 OrderType type,
                 UUID instrumentId,
                 UUID traderId,
                 Long quantity,
                 Optional<Long> price,
                 Optional<UUID> parentId,
                 Optional<Consumer<MatchResult>> callBack) {
        this.id = requireNonNull(id);
        this.type = requireNonNull(type);
        this.instrumentId = requireNonNull(instrumentId);
        this.traderId = requireNonNull(traderId);
        this.quantity = requireNonNull(quantity);
        this.price = requireNonNull(price);
        this.parentId = requireNonNull(parentId);
        this.callBack = requireNonNull(callBack);
    }

    public Order(UUID id, OrderType type, UUID instrumentId, UUID traderId, Long quantity, Optional<Long> price, Optional<UUID> parentId) {
        this(id, type, instrumentId, traderId, quantity, price, parentId, empty());
    }

    public Order(UUID id, OrderType type, UUID instrumentId, UUID traderId, Long quantity) {
        this(id, type, instrumentId, traderId, quantity, empty(), empty(), empty());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final var order = (Order) o;
        return Objects.equals(id, order.id) &&
                type == order.type &&
                Objects.equals(instrumentId, order.instrumentId) &&
                Objects.equals(traderId, order.traderId) &&
                Objects.equals(quantity, order.quantity) &&
                Objects.equals(price, order.price) &&
                Objects.equals(parentId, order.parentId) &&
                Objects.equals(callBack, order.callBack);
    }

    @Override
    public int hashCode() {
        return hash(id, type, instrumentId, traderId, quantity, price, parentId, callBack);
    }
}
