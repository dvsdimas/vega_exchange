package com.vega.exchange.services;

import com.vega.exchange.orders.Order;

import static java.util.Objects.requireNonNull;

public class InMemoryOrderService implements OrderService {

    private final InstrumentsRegister register;
    private final Quoting quoting;

    public InMemoryOrderService(InstrumentsRegister register, Quoting quoting) {
        this.register = requireNonNull(register);
        this.quoting = requireNonNull(quoting);
    }

    @Override
    public void add(Order order) {

        final var instrument = register.getInstrument(order.instrumentId);

        if(instrument.tradable()) {
            addRegularOrder(order);
        } else {
            addCompositeOrder(order);
        }

    }

    private void addRegularOrder(Order order) {
        // todo
    }

    private void addCompositeOrder(Order order) {
        // todo
    }

    @Override
    public void cancel(Order order) {

        final var instrument = register.getInstrument(order.instrumentId);

        if(instrument.tradable()) {
            cancelRegularOrder(order);
        } else {
            cancelCompositeOrder(order);
        }

    }

    private void cancelRegularOrder(Order order) {
        // todo
    }

    private void cancelCompositeOrder(Order order) {
        // todo
    }
}
