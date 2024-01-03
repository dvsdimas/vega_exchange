package com.vega.exchange.books;

import com.vega.exchange.helper.OrderHelper;
import com.vega.exchange.instruments.Quote;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static com.vega.exchange.books.RegularInstrumentBook.triggerPrice;
import static java.time.Instant.now;
import static org.assertj.core.api.Assertions.assertThat;

public class RegularInstrumentBookTest implements OrderHelper {

    @Test
    void should_pass_for_buy_market_order() {
        //given
        var order = aBuyOrder(10L);
        var quote = new Quote(UUID.randomUUID(), 20L, now());

        //when
        var result = triggerPrice(order, quote);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void should_pass_for_sell_market_order() {
        //given
        var order = aSellOrder(10L);
        var quote = new Quote(UUID.randomUUID(), 20L, now());

        //when
        var result = triggerPrice(order, quote);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void should_fail_for_buy_limit_order_with_low_price() {
        //given
        var order = aBuyOrder(10L, 19L);
        var quote = new Quote(UUID.randomUUID(), 20L, now());

        //when
        var result = triggerPrice(order, quote);

        //then
        assertThat(result).isFalse();
    }

    @Test
    void should_fail_for_sell_limit_order_with_low_price() {
        //given
        var order = aSellOrder(10L, 20L);
        var quote = new Quote(UUID.randomUUID(), 19L, now());

        //when
        var result = triggerPrice(order, quote);

        //then
        assertThat(result).isFalse();
    }

    @Test
    void should_pass_for_buy_limit_order_with_correct_price() {
        //given
        var order = aBuyOrder(10L, 20L);
        var quote = new Quote(UUID.randomUUID(), 20L, now());

        //when
        var result = triggerPrice(order, quote);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void should_pass_for_sell_limit_order_with_correct_price() {
        //given
        var order = aSellOrder(10L, 20L);
        var quote = new Quote(UUID.randomUUID(), 21L, now());

        //when
        var result = triggerPrice(order, quote);

        //then
        assertThat(result).isTrue();
    }

}
