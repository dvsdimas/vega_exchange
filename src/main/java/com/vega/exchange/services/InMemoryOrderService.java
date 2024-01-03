package com.vega.exchange.services;

import com.vega.exchange.books.InstrumentBook;
import com.vega.exchange.books.RegularInstrumentBook;
import com.vega.exchange.orders.Order;
import com.vega.exchange.trades.Trade;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.requireNonNull;

public class InMemoryOrderService implements OrderService {

    private final InstrumentsRegister register;
    private final Quoting quoting;
    private final Map<UUID, InstrumentBook> instrumentBooks = new ConcurrentHashMap<>();

    public InMemoryOrderService(InstrumentsRegister register, Quoting quoting) {
        this.register = requireNonNull(register);
        this.quoting = requireNonNull(quoting);
    }

    @Override
    public void add(Order order) {

        final var instrument = register.getInstrument(order.instrumentId);




    }

    private Optional<Trade> addRegularOrder(Order order) {

        final var instrument = register.getInstrument(order.instrumentId);

        if(!instrument.tradable()) {
            throw new IllegalArgumentException("Cannot add non tradable order %s".formatted(order.id));
        }

        final var book = instrumentBooks.computeIfAbsent(order.instrumentId, o -> new RegularInstrumentBook(instrument));

        return book.add(order, quoting.getQuote(instrument.id));
    }

    @Override
    public boolean cancel(Order order) {

        if(!instrumentBooks.containsKey(order.instrumentId)) {
            return false;
        }

        return instrumentBooks.get(order.instrumentId).cancel(order);
    }

}
