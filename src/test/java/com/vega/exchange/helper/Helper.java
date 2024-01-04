package com.vega.exchange.helper;

import com.vega.exchange.instruments.CompositeInstrument;
import com.vega.exchange.instruments.Instrument;
import com.vega.exchange.instruments.Quote;
import com.vega.exchange.orders.MatchResult;
import com.vega.exchange.orders.Order;
import com.vega.exchange.services.InstrumentsRegister;
import com.vega.exchange.services.Quoting;
import com.vega.exchange.services.StaticInstrumentsRegister;
import com.vega.exchange.services.StaticQuoting;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;

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

    default Order aBuyMarketOrder(UUID instrumentId, Long amount, Consumer<MatchResult> callBack) {
        return new Order(randomUUID(), BUY, instrumentId, randomUUID(), amount, empty(), empty(), Optional.of(callBack));
    }

    default Order aSellMarketOrder(UUID instrumentId, Long amount, Consumer<MatchResult> callBack) {
        return new Order(randomUUID(), SELL, instrumentId, randomUUID(), amount, empty(), empty(), Optional.of(callBack));
    }

    default Order aBuyLimitOrder(Long amount, Long price) {
        return new Order(randomUUID(), BUY, randomUUID(), randomUUID(), amount, Optional.of(price), empty());
    }

    default Order aSellLimitOrder(Long amount, Long price) {
        return new Order(randomUUID(), SELL, randomUUID(), randomUUID(), amount, Optional.of(price), empty());
    }

    default Order aBuyLimitOrder(UUID instrumentId, Long amount, Long price) {
        return new Order(randomUUID(), BUY, instrumentId, randomUUID(), amount, Optional.of(price), empty());
    }

    default Order aSellLimitOrder(UUID instrumentId, Long amount, Long price) {
        return new Order(randomUUID(), SELL, instrumentId, randomUUID(), amount, Optional.of(price), empty());
    }

    default Instrument aRegularInstrument() {
        return new Instrument(randomUUID(), "symbol", REGULAR);
    }

    default Instrument aRegularInstrument(UUID id) {
        return new Instrument(id, "symbol", REGULAR);
    }

    default Instrument aCompositeInstrument(UUID id, Set<Instrument> instruments) {
        return new CompositeInstrument(id, "composite_symbol", instruments);
    }

    default InstrumentsRegister anInstrumentsRegister(Collection<Instrument> instruments) {
        return new StaticInstrumentsRegister(instruments);
    }

    default Quoting aQuoting(Map<UUID, Quote> prices) {
        return new StaticQuoting(prices);
    }

}
