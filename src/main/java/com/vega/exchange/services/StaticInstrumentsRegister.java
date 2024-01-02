package com.vega.exchange.services;

import com.vega.exchange.instruments.Instrument;

import java.util.Map;
import java.util.UUID;

import static java.util.Objects.requireNonNull;

public class StaticInstrumentsRegister implements InstrumentsRegister {

    private final Map<UUID, Instrument> instruments;

    public StaticInstrumentsRegister(Map<UUID, Instrument> instruments) {
        this.instruments = requireNonNull(instruments);
    }

    @Override
    public Instrument getInstrument(UUID instrumentId) {
        if(!instruments.containsKey(instrumentId)) {
            throw new IllegalArgumentException("Unsupported instrument %s".formatted(instrumentId));
        }

        return instruments.get(instrumentId);
    }
}
