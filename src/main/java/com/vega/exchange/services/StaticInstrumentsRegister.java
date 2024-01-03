package com.vega.exchange.services;

import com.vega.exchange.instruments.Instrument;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;

public class StaticInstrumentsRegister implements InstrumentsRegister {

    private final Map<UUID, Instrument> instruments;

    public StaticInstrumentsRegister(Collection<Instrument> instruments) {
        this.instruments = requireNonNull(instruments)
                .stream()
                .collect(Collectors.toMap(instrument -> instrument.id , identity()));
    }

    @Override
    public Instrument getInstrument(UUID instrumentId) {
        if(!instruments.containsKey(instrumentId)) {
            throw new IllegalArgumentException("Unsupported instrument %s".formatted(instrumentId));
        }

        return instruments.get(instrumentId);
    }
}
