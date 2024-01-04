package com.vega.exchange.services;

import com.vega.exchange.books.InstrumentBook;
import com.vega.exchange.books.RegularInstrumentBook;
import com.vega.exchange.instruments.CompositeInstrument;
import com.vega.exchange.orders.CompositeOrderContainer;
import com.vega.exchange.orders.MatchResult;
import com.vega.exchange.orders.Order;
import com.vega.exchange.trades.Trade;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.empty;
import static java.util.UUID.randomUUID;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toSet;

public class InMemoryOrdersMatcher implements OrdersMatcher {

    private final InstrumentsRegister register;
    private final Quoting quoting;
    private final Map<UUID, InstrumentBook> instrumentBooks = new ConcurrentHashMap<>();
    private final Map<UUID, CompositeOrderContainer> awaitingCompositeOrders = new ConcurrentHashMap<>();

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

                completeCompositeOrderByTrade(maybeTrade.orElseThrow());

                return Optional.of(new MatchResult(order, Set.of(maybeTrade.orElseThrow())));
            }

            return empty();
        }

        return addCompositeOrder((CompositeInstrument) instrument, order);
    }

    private void completeCompositeOrderByTrade(Trade trade) {

        final var notifyOrderContainers = new HashSet<CompositeOrderContainer>();

        Stream.of(trade.buyOrder(), trade.sellOrder())
                .filter(o -> o.parentId.isPresent())
                .map(o -> o.parentId.orElseThrow())
                .forEach(parentId -> awaitingCompositeOrders.computeIfPresent(parentId, (key, val) -> { // atomic

                    final var newContainer = val.addCompletedTrade(trade);

                    if(newContainer.completed()) {
                        notifyOrderContainers.add(newContainer);
                        return null; // delete key
                    }

                    return newContainer; // replace key
                }));

        notifyOrderContainers.forEach(container ->
                container.compositeOrder.callBack.ifPresent(callback ->
                        callback.accept(new MatchResult(container.compositeOrder, container.completedTrades))));
    }

    private Optional<MatchResult> addCompositeOrder(CompositeInstrument compositeInstrument, Order compositeOrder) {

        final var partialOrders = compositeToRegularOrders(compositeInstrument, compositeOrder);

        final var results = partialOrders.stream()
                .collect(Collectors.toUnmodifiableMap(identity(), this::add));

        final var trades = results.values()
                .stream()
                .filter(Optional::isPresent)
                .map(val -> val.orElseThrow().trades.stream().findFirst().orElseThrow())
                .collect(toSet());

        trades.forEach(this::completeCompositeOrderByTrade);

        if(partialOrders.size() == trades.size()) {
            return Optional.of(new MatchResult(compositeOrder, trades));
        }

        awaitingCompositeOrders.put(compositeOrder.id, new CompositeOrderContainer(compositeOrder, partialOrders, trades));

        return empty();
    }

    private static Set<Order> compositeToRegularOrders(CompositeInstrument compositeInstrument, Order order) {
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
                .collect(Collectors.toSet());
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

        final var instrument = register.getInstrument(order.instrumentId);

        if(instrument.tradable()) {

            if(!instrumentBooks.containsKey(order.instrumentId)) {
                return false;
            }

            return instrumentBooks.get(order.instrumentId).cancel(order);
        }

        if(!awaitingCompositeOrders.containsKey(order.id)) {
            return false;
        }


        final var container = awaitingCompositeOrders.get(order.id);

        if(!container.completedTrades.isEmpty()) { // cannot cancel, already partially executed
            return false;
        }

        container.partialOrders.forEach(this::cancel);

        awaitingCompositeOrders.remove(order.id, container);

        return true;
    }

}
