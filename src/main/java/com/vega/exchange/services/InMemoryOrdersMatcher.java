package com.vega.exchange.services;

import com.vega.exchange.books.InstrumentBook;
import com.vega.exchange.books.RegularInstrumentBook;
import com.vega.exchange.instruments.CompositeInstrument;
import com.vega.exchange.orders.MatchResult;
import com.vega.exchange.orders.Order;
import com.vega.exchange.trades.Trade;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.UUID.randomUUID;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toSet;

public class InMemoryOrdersMatcher implements OrdersMatcher {

    private final InstrumentsRegister register;
    private final Quoting quoting;
    private final Map<UUID, InstrumentBook> instrumentBooks = new ConcurrentHashMap<>();

    public InMemoryOrdersMatcher(InstrumentsRegister register, Quoting quoting) {
        this.register = requireNonNull(register);
        this.quoting = requireNonNull(quoting);
    }

    @Override
    public Optional<MatchResult> add(Order order) {

        final var instrument = register.getInstrument(order.instrumentId);

        if(instrument.tradable()) {

            final var maybeTrade = addRegularOrder(order);

            if(maybeTrade.isPresent()) {
                return Optional.of(new MatchResult(order, Set.of(maybeTrade.orElseThrow())));
            } else {
                return empty();
            }

        }

        final var results = compositeToRegularOrders((CompositeInstrument) instrument, order)
                .stream()
                .collect(Collectors.toUnmodifiableMap(identity(), this::add));

        final var trades = results.values()
                .stream()
                .filter(Optional::isPresent)
                .map(val -> val.orElseThrow().trades.stream().findFirst().orElseThrow())
                .collect(toSet());

        if(results.size() == trades.size()) {
            return Optional.of(new MatchResult(order, trades));
        }


        // todo handle composite orders partial completion   !!!!!!!



        return empty();
    }

    private static Collection<Order> compositeToRegularOrders(CompositeInstrument compositeInstrument, Order order) {
        return compositeInstrument.instruments
                .stream()
                .map(i -> new Order(
                        randomUUID(),
                        order.type,
                        i.id,
                        order.traderId,
                        order.quantity,
                        order.price,
                        Optional.of(order.id)))
                .collect(Collectors.toList());
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

//        todo handle composite case
    }

}
