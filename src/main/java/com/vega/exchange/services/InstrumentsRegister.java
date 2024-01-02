package com.vega.exchange.services;

import com.vega.exchange.instruments.Instrument;

import java.util.UUID;

public interface InstrumentsRegister {

    Instrument getInstrument(UUID instrumentId);

}
