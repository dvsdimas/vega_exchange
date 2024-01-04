package com.vega.exchange.orders;

import com.vega.exchange.helper.Helper;
import com.vega.exchange.instruments.Quote;
import com.vega.exchange.trades.Trade;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static java.time.Instant.now;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

public class CompositeOrderContainerTest implements Helper {

    @Test
    void container_should_correctly_add_completed_trades() {
        //given
        var instrument1 = aRegularInstrument();
        var instrument2 = aRegularInstrument();
        var instrument3 = aRegularInstrument();
        var compositeInstrument = aCompositeInstrument(randomUUID(), Set.of(instrument1, instrument2, instrument3));
        var quote1 = new Quote(instrument1.id, 19L, now());
        var quote2 = new Quote(instrument2.id, 34L, now());
        var quote3 = new Quote(instrument3.id, 22L, now());

        var compositeBuyMarketOrder = aBuyMarketOrder(compositeInstrument.id, 20L);

        var sellMarketOrder1 = aSellMarketOrder(instrument1.id, 30L);
        var sellMarketOrder2 = aSellMarketOrder(instrument2.id, 30L);
        var sellMarketOrder3 = aSellMarketOrder(instrument3.id, 30L);

        var buyMarketOrder1 = aBuyMarketOrder(instrument1.id, 30L);
        var buyMarketOrder2 = aBuyMarketOrder(instrument2.id, 30L);
        var buyMarketOrder3 = aBuyMarketOrder(instrument3.id, 30L);

        var buyOrders = Set.of(buyMarketOrder1, buyMarketOrder2, buyMarketOrder3);

        var trade1 = new Trade(sellMarketOrder1, buyMarketOrder1, quote1);
        var trade2 = new Trade(sellMarketOrder2, buyMarketOrder2, quote2);
        var trade3 = new Trade(sellMarketOrder3, buyMarketOrder3, quote3);

        var container = new CompositeOrderContainer(compositeBuyMarketOrder, buyOrders, Set.of(trade1));

        //when
        var result1 = container.addCompletedTrade(trade2);

        //then
        assertThat(result1).isEqualTo(
                new CompositeOrderContainer(compositeBuyMarketOrder, buyOrders, Set.of(trade1, trade2)));
        assertThat(result1.completed()).isFalse();

        //when
        var result2 = result1.addCompletedTrade(trade3);

        //then
        assertThat(result2).isEqualTo(
                new CompositeOrderContainer(compositeBuyMarketOrder, buyOrders, Set.of(trade1, trade2, trade3)));
        assertThat(result2.completed()).isTrue();
    }

}
