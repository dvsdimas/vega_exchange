package com.vega.exchange.books;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.vega.exchange.instruments.Instrument;
import com.vega.exchange.instruments.Quote;
import com.vega.exchange.orders.Order;
import com.vega.exchange.trades.Trade;

import java.util.Optional;

import static com.google.common.collect.Multimaps.synchronizedListMultimap;
import static com.vega.exchange.orders.OrderType.BUY;
import static com.vega.exchange.orders.OrderType.SELL;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;

public class RegularInstrumentBook implements InstrumentBook {

    private final Instrument instrument;
    private final Multimap<Long, Order> buyBook = synchronizedListMultimap(ArrayListMultimap.create());
    private final Multimap<Long, Order> sellBook = synchronizedListMultimap(ArrayListMultimap.create());

    public RegularInstrumentBook(Instrument instrument) {
        this.instrument = requireNonNull(instrument);
    }

    @Override
    public Optional<Trade> add(Order order, Quote quote) {

        if(!order.instrumentId.equals(instrument.id)) {
            throw new IllegalArgumentException("Order %s has incorrect instrument id %s".formatted(order.id, order.instrumentId));
        }

        if(!triggerPrice(order, quote)) {
            addToBook(order);
            return empty();
        }

        final var maybeMatch = (order.type == BUY ? sellBook : buyBook)
                .get(order.quantity)
                .stream()
                .filter(o -> triggerPrice(o, quote))
                .findFirst();

        if(maybeMatch.isPresent()) {

            if(!removeFromBook(maybeMatch.orElseThrow())) { // someone still this match, retry
                return add(order, quote);
            }

            return Optional.of(
                    new Trade(
                            order.type == BUY ? order : maybeMatch.orElseThrow(),
                            order.type == SELL ? order : maybeMatch.orElseThrow(),
                            quote
                    )
            );
        } else {
            addToBook(order);
            return empty();
        }
    }

    @Override
    public boolean cancel(Order order) {
        return getBook(order).remove(order.quantity, order);
    }

    boolean contains(Order order) {
        return getBook(order)
                .get(order.quantity)
                .stream()
                .anyMatch(o -> o.equals(order));
    }

    private void addToBook(Order order) {
        getBook(order).put(order.quantity, order);
    }

    private boolean removeFromBook(Order order) {
        return getBook(order).remove(order.quantity, order);
    }

    private Multimap<Long, Order> getBook(Order order) {
        return order.type == BUY ? buyBook : sellBook;
    }

    static boolean triggerPrice(Order order, Quote quote) {
        requireNonNull(order);
        requireNonNull(quote);

        if(order.price.isEmpty()) {
            return true;
        }

        if( (order.type == BUY) && (order.price.orElseThrow() >= quote.price()) ) {
            return true;
        }

        if( (order.type == SELL) && (order.price.orElseThrow() <= quote.price()) ) {
            return true;
        }

        return false;
    }

}
