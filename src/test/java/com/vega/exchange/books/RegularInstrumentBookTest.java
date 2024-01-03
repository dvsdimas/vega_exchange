package com.vega.exchange.books;

import com.vega.exchange.helper.Helper;
import com.vega.exchange.instruments.Quote;
import com.vega.exchange.trades.Trade;
import org.junit.jupiter.api.Test;

import static com.vega.exchange.books.RegularInstrumentBook.triggerPrice;
import static java.time.Instant.now;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

public class RegularInstrumentBookTest implements Helper {

    @Test
    void should_pass_for_buy_market_order() {
        //given
        var order = aBuyMarketOrder(10L);
        var quote = new Quote(randomUUID(), 20L, now());

        //when
        var result = triggerPrice(order, quote);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void should_pass_for_sell_market_order() {
        //given
        var order = aSellMarketOrder(10L);
        var quote = new Quote(randomUUID(), 20L, now());

        //when
        var result = triggerPrice(order, quote);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void should_fail_for_buy_limit_order_with_low_price() {
        //given
        var order = aBuyLimitOrder(10L, 19L);
        var quote = new Quote(randomUUID(), 20L, now());

        //when
        var result = triggerPrice(order, quote);

        //then
        assertThat(result).isFalse();
    }

    @Test
    void should_fail_for_sell_limit_order_with_low_price() {
        //given
        var order = aSellLimitOrder(10L, 20L);
        var quote = new Quote(randomUUID(), 19L, now());

        //when
        var result = triggerPrice(order, quote);

        //then
        assertThat(result).isFalse();
    }

    @Test
    void should_pass_for_buy_limit_order_with_correct_price() {
        //given
        var order = aBuyLimitOrder(10L, 20L);
        var quote = new Quote(randomUUID(), 20L, now());

        //when
        var result = triggerPrice(order, quote);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void should_pass_for_sell_limit_order_with_correct_price() {
        //given
        var order = aSellLimitOrder(10L, 20L);
        var quote = new Quote(randomUUID(), 21L, now());

        //when
        var result = triggerPrice(order, quote);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void should_fail_if_no_order() {
        //given
        var order = aSellMarketOrder(10L);
        var instrument = aRegularInstrument();
        var book = new RegularInstrumentBook(instrument);

        //when
        var result = book.contains(aBuyMarketOrder(12L));

        //then
        assertThat(result).isFalse();
    }

    @Test
    void should_pass_if_order_in_place() {
        //given
        var order = aSellMarketOrder(10L);
        var instrument = aRegularInstrument(order.instrumentId);
        var book = new RegularInstrumentBook(instrument);
        book.add(order, new Quote(order.instrumentId, 12L, now()));

        //when
        var result = book.contains(order);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void should_produce_trade_on_match_for_buy_market_order() {
        //given
        var instrument = aRegularInstrument(randomUUID());
        var buyMarketOrder = aBuyMarketOrder(instrument.id, 20L);
        var sellMarketOrder = aSellMarketOrder(instrument.id, 20L);

        var book = new RegularInstrumentBook(instrument);
        var quote = new Quote(buyMarketOrder.instrumentId, 12L, now());
        book.add(sellMarketOrder, quote);

        //when
        var result = book.add(buyMarketOrder, quote);

        //then
        assertThat(result).isPresent();
        assertThat(result).contains(new Trade(buyMarketOrder, sellMarketOrder, quote));
    }

    @Test
    void should_produce_trade_on_match_for_sell_market_order() {
        //given
        var instrument = aRegularInstrument(randomUUID());
        var buyMarketOrder = aBuyMarketOrder(instrument.id, 30L);
        var sellMarketOrder = aSellMarketOrder(instrument.id, 30L);

        var book = new RegularInstrumentBook(instrument);
        var quote = new Quote(buyMarketOrder.instrumentId, 12L, now());
        book.add(buyMarketOrder, quote);

        //when
        var result = book.add(sellMarketOrder, quote);

        //then
        assertThat(result).isPresent();
        assertThat(result).contains(new Trade(buyMarketOrder, sellMarketOrder, quote));
    }

}
