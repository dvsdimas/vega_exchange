package com.vega.exchange.orders;

import java.util.Optional;
import java.util.UUID;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;

public class Order {

    public final UUID id;
    public final OrderType type;
    public final UUID instrumentId;
    public final UUID traderId;

    public final Long quantity;
    public final Optional<Long> price;
    public final Optional<UUID> parentId;

    public Order(UUID id, OrderType type, UUID instrumentId, UUID traderId, Long quantity, Optional<Long> price, Optional<UUID> parentId) {
        this.id = requireNonNull(id);
        this.type = requireNonNull(type);
        this.instrumentId = requireNonNull(instrumentId);
        this.traderId = requireNonNull(traderId);
        this.quantity = requireNonNull(quantity);
        this.price = requireNonNull(price);
        this.parentId = requireNonNull(parentId);
    }

    public Order(UUID id, OrderType type, UUID instrumentId, UUID traderId, Long quantity) {
        this.id = requireNonNull(id);
        this.type = requireNonNull(type);
        this.instrumentId = requireNonNull(instrumentId);
        this.traderId = requireNonNull(traderId);
        this.quantity = requireNonNull(quantity);
        this.price = empty();
        this.parentId = empty();
    }
}
