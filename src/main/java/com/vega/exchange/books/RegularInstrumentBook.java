package com.vega.exchange.books;

import com.vega.exchange.instruments.Instrument;
import com.vega.exchange.instruments.Quote;
import com.vega.exchange.orders.Order;
import com.vega.exchange.trades.Trade;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static com.vega.exchange.orders.OrderType.BUY;
import static com.vega.exchange.orders.OrderType.SELL;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;

public class RegularInstrumentBook implements InstrumentBook{

    private final Instrument instrument;
    private final Map<UUID, Order> buyBook = new ConcurrentHashMap<>();
    private final Map<UUID, Order> sellBook = new ConcurrentHashMap<>();

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

        final var maybeMatch = (order.type == BUY ? sellBook.values() : buyBook.values())
                .stream()
                .filter(o -> o.quantity.equals(order.quantity) && triggerPrice(o, quote))
                .findAny();

        if(maybeMatch.isPresent()) {
            removeFromBook(maybeMatch.orElseThrow());
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
        return getBook(order).remove(order.id, order);
    }

    boolean contains(Order order) {
        return getBook(order).containsKey(order.id);
    }

    private void addToBook(Order order) {
        getBook(order).put(order.id, order);
    }

    private void removeFromBook(Order order) {
        getBook(order).remove(order.id, order);
    }

    private Map<UUID, Order> getBook(Order order) {
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
