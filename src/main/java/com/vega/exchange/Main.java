package com.vega.exchange;

import com.vega.exchange.services.Quoting;
import com.vega.exchange.services.StaticQuoting;

import java.util.Map;

import static java.lang.Thread.sleep;
import static java.time.Duration.ofSeconds;

public class Main {
    public static void main(String[] args) {

        final var quoting = initQuoting();


        awaitTermination();
    }

    private static Quoting initQuoting() {


        // todo


        return new StaticQuoting(Map.of());
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