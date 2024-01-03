package com.vega.exchange.helper;

import com.vega.exchange.orders.Order;

import java.util.Optional;
import java.util.UUID;

import static com.vega.exchange.orders.OrderType.BUY;
import static com.vega.exchange.orders.OrderType.SELL;
import static java.util.Optional.empty;

public interface OrderHelper {

    default Order aBuyOrder(Long amount) {
        return new Order(UUID.randomUUID(), BUY, UUID.randomUUID(), UUID.randomUUID(), amount);
    }

    default Order aSellOrder(Long amount) {
        return new Order(UUID.randomUUID(), SELL, UUID.randomUUID(), UUID.randomUUID(), amount);
    }

    default Order aBuyOrder(Long amount, Long price) {
        return new Order(UUID.randomUUID(), BUY, UUID.randomUUID(), UUID.randomUUID(), amount, Optional.of(price), empty());
    }

    default Order aSellOrder(Long amount, Long price) {
        return new Order(UUID.randomUUID(), SELL, UUID.randomUUID(), UUID.randomUUID(), amount, Optional.of(price), empty());
    }

}
