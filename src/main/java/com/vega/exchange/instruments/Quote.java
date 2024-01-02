package com.vega.exchange.instruments;

import java.time.Instant;
import java.util.UUID;

public record Quote(UUID instrumentId, long price, Instant timestamp) {
}
