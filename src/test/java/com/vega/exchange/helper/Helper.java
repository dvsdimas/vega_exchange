package com.vega.exchange.helper;

import com.vega.exchange.instruments.Instrument;
import com.vega.exchange.orders.Order;

import java.util.Optional;
import java.util.UUID;

import static com.vega.exchange.instruments.InstrumentType.REGULAR;
import static com.vega.exchange.orders.OrderType.BUY;
import static com.vega.exchange.orders.OrderType.SELL;
import static java.util.Optional.empty;
import static java.util.UUID.randomUUID;

public interface Helper {

    default Order aBuyMarketOrder(Long amount) {
        return new Order(randomUUID(), BUY, randomUUID(), randomUUID(), amount);
    }

    default Order aSellMarketOrder(Long amount) {
        return new Order(randomUUID(), SELL, randomUUID(), randomUUID(), amount);
    }

    default Order aBuyMarketOrder(UUID instrumentId, Long amount) {
        return new Order(randomUUID(), BUY, instrumentId, randomUUID(), amount);
    }

    default Order aSellMarketOrder(UUID instrumentId, Long amount) {
        return new Order(randomUUID(), SELL, instrumentId, randomUUID(), amount);
    }

    default Order aBuyLimitOrder(Long amount, Long price) {
        return new Order(randomUUID(), BUY, randomUUID(), randomUUID(), amount, Optional.of(price), empty());
    }

    default Order aSellLimitOrder(Long amount, Long price) {
        return new Order(randomUUID(), SELL, randomUUID(), randomUUID(), amount, Optional.of(price), empty());
    }

    default Instrument aRegularInstrument() {
        return new Instrument(randomUUID(), "symbol", REGULAR);
    }

    default Instrument aRegularInstrument(UUID id) {
        return new Instrument(id, "symbol", REGULAR);
    }

}
