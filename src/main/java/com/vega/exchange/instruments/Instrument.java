package com.vega.exchange.instruments;

import java.util.UUID;

import static java.util.Objects.requireNonNull;

public class Instrument {

    public final UUID id;
    public final String symbol;
    public final InstrumentType type;

    public Instrument(UUID id, String symbol, InstrumentType type) {
        this.id = requireNonNull(id);
        this.symbol = requireNonNull(symbol);
        this.type = requireNonNull(type);
    }

    public boolean tradable() {
        return true;
    }

}
