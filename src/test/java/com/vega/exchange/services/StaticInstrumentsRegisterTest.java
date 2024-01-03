package com.vega.exchange.services;

import com.vega.exchange.instruments.Instrument;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.vega.exchange.instruments.InstrumentType.REGULAR;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class StaticInstrumentsRegisterTest {

    @Test
    void should_return_instrument() {
        //give
        var instrumentId = randomUUID();
        var instrument = new Instrument(instrumentId, "", REGULAR);
        var register = new StaticInstrumentsRegister(List.of(instrument));

        //when
        var result = register.getInstrument(instrumentId);

        //then
        assertThat(result).isEqualTo(instrument);
    }

    @Test
    void should_fail_for_unsupported_instrument() {
        //give
        var instrumentId = randomUUID();
        var register = new StaticInstrumentsRegister(List.of());

        //when
        assertThatThrownBy(() -> register.getInstrument(instrumentId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unsupported instrument %s".formatted(instrumentId));
    }

}
