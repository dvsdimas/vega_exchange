package com.vega.exchange;

import com.vega.exchange.services.InMemoryOrdersMatcher;
import com.vega.exchange.services.InstrumentsRegister;
import com.vega.exchange.services.OrdersMatcher;
import com.vega.exchange.services.Quoting;
import com.vega.exchange.services.StaticInstrumentsRegister;
import com.vega.exchange.services.StaticQuoting;

import java.util.List;
import java.util.Map;

import static java.lang.Thread.sleep;
import static java.time.Duration.ofSeconds;

public class Main {
    public static void main(String[] args) {

        final var quoting = initQuoting();

        final var register = initInstrumentsRegister();

        var ordersMatcher = initOrdersMatcher(register, quoting);

        // then matcher could handle user's request

        awaitTermination();
    }

    private static OrdersMatcher initOrdersMatcher(InstrumentsRegister register, Quoting quoting) {
        return new InMemoryOrdersMatcher(register, quoting);
    }

    private static Quoting initQuoting() {
        return new StaticQuoting(Map.of());
    }

    private static InstrumentsRegister initInstrumentsRegister() {
        return new StaticInstrumentsRegister(List.of());
    }

    private static void awaitTermination() {
        while (true) {
            try {
                sleep(ofSeconds(10));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }

}