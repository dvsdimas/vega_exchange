package com.vega.exchange.instruments;

import java.util.Set;
import java.util.UUID;

import static com.vega.exchange.instruments.InstrumentType.COMPOSITE;
import static java.util.Objects.requireNonNull;

public class CompositeInstrument extends Instrument {

    public final Set<Instrument> instruments;

    public CompositeInstrument(UUID id, String symbol, Set<Instrument> instruments) {
        super(id, symbol, COMPOSITE);
        this.instruments = requireNonNull(instruments);

        if(instruments.stream().anyMatch(i -> i.type == COMPOSITE)) {
            throw new IllegalArgumentException("Composite instrument %s cannot has COMPOSITE partials".formatted(id));
        }
    }

    public boolean tradable() {
        return false;
    }

}
