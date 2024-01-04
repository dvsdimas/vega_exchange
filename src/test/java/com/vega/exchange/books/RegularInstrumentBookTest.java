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
    void should_fail_for_buy_limit_order_with_high_price() {
        //given
        var order = aBuyLimitOrder(10L, 20L);
        var quote = new Quote(randomUUID(), 21L, now());

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
    void should_cancel_order() {
        //given
        var order = aSellMarketOrder(10L);
        var instrument = aRegularInstrument(order.instrumentId);
        var book = new RegularInstrumentBook(instrument);
        book.add(order, new Quote(order.instrumentId, 12L, now()));

        //when
        var result = book.cancel(order);

        //then
        assertThat(result).isTrue();
        assertThat(book.contains(order)).isFalse();
    }

    @Test
    void should_produce_trade_on_match_for_buy_market_order_against_sell_market_order() {
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
        assertThat(book.contains(buyMarketOrder)).isFalse();
        assertThat(book.contains(sellMarketOrder)).isFalse();
    }

    @Test
    void should_produce_trade_on_match_for_sell_market_order_against_buy_market_order() {
        //given
        var instrument = aRegularInstrument(randomUUID());
        var buyMarketOrder = aBuyMarketOrder(instrument.id, 30L);
        var sellMarketOrder = aSellMarketOrder(instrument.id, 30L);

        var book = new RegularInstrumentBook(instrument);
        var quote = new Quote(instrument.id, 12L, now());
        book.add(buyMarketOrder, quote);

        //when
        var result = book.add(sellMarketOrder, quote);

        //then
        assertThat(result).isPresent();
        assertThat(result).contains(new Trade(buyMarketOrder, sellMarketOrder, quote));
        assertThat(book.contains(buyMarketOrder)).isFalse();
        assertThat(book.contains(sellMarketOrder)).isFalse();
    }

    @Test
    void should_produce_trade_on_match_for_buy_market_order_against_sell_limit_order() {
        //given
        var instrument = aRegularInstrument(randomUUID());
        var buyMarketOrder = aBuyMarketOrder(instrument.id, 20L);
        var sellLimitOrder = aSellLimitOrder(instrument.id, 20L, 25L);

        var book = new RegularInstrumentBook(instrument);
        var quote = new Quote(instrument.id, 26L, now());
        book.add(sellLimitOrder, quote);

        //when
        var result = book.add(buyMarketOrder, quote);

        //then
        assertThat(result).isPresent();
        assertThat(result).contains(new Trade(buyMarketOrder, sellLimitOrder, quote));
        assertThat(book.contains(buyMarketOrder)).isFalse();
        assertThat(book.contains(sellLimitOrder)).isFalse();
    }

    @Test
    void should_produce_trade_on_match_for_sell_market_order_against_buy_limit_order() {
        //given
        var instrument = aRegularInstrument(randomUUID());
        var buyLimitOrder = aBuyLimitOrder(instrument.id, 20L, 26L);
        var sellMarketOrder = aSellMarketOrder(instrument.id, 20L);

        var book = new RegularInstrumentBook(instrument);
        var quote = new Quote(instrument.id, 26L, now());
        book.add(buyLimitOrder, quote);

        //when
        var result = book.add(sellMarketOrder, quote);

        //then
        assertThat(result).isPresent();
        assertThat(result).contains(new Trade(buyLimitOrder, sellMarketOrder, quote));
        assertThat(book.contains(buyLimitOrder)).isFalse();
        assertThat(book.contains(sellMarketOrder)).isFalse();
    }

    @Test
    void should_produce_trade_on_match_for_buy_limit_order_against_sell_limit_order() {
        //given
        var instrument = aRegularInstrument(randomUUID());
        var buyLimitOrder = aBuyLimitOrder(instrument.id, 20L, 25L);
        var sellLimitOrder = aSellLimitOrder(instrument.id, 20L, 25L);

        var book = new RegularInstrumentBook(instrument);
        var quote = new Quote(instrument.id, 25L, now());
        book.add(sellLimitOrder, quote);

        //when
        var result = book.add(buyLimitOrder, quote);

        //then
        assertThat(result).isPresent();
        assertThat(result).contains(new Trade(buyLimitOrder, sellLimitOrder, quote));
        assertThat(book.contains(buyLimitOrder)).isFalse();
        assertThat(book.contains(sellLimitOrder)).isFalse();
    }

    @Test
    void should_produce_trade_on_match_for_sell_limit_order_against_buy_limit_order() {
        //given
        var instrument = aRegularInstrument(randomUUID());
        var buyLimitOrder = aBuyLimitOrder(instrument.id, 20L, 25L);
        var sellLimitOrder = aSellLimitOrder(instrument.id, 20L, 25L);

        var book = new RegularInstrumentBook(instrument);
        var quote = new Quote(instrument.id, 25L, now());
        book.add(buyLimitOrder, quote);

        //when
        var result = book.add(sellLimitOrder, quote);

        //then
        assertThat(result).isPresent();
        assertThat(result).contains(new Trade(buyLimitOrder, sellLimitOrder, quote));
        assertThat(book.contains(buyLimitOrder)).isFalse();
        assertThat(book.contains(sellLimitOrder)).isFalse();
    }

    @Test
    void should_produce_trade_on_match_for_buy_limit_order_against_sell_market_order() {
        //given
        var instrument = aRegularInstrument(randomUUID());
        var buyLimitOrder = aBuyLimitOrder(instrument.id, 20L, 25L);
        var sellMarketOrder = aSellMarketOrder(instrument.id, 20L);

        var book = new RegularInstrumentBook(instrument);
        var quote = new Quote(instrument.id, 25L, now());
        book.add(sellMarketOrder, quote);

        //when
        var result = book.add(buyLimitOrder, quote);

        //then
        assertThat(result).isPresent();
        assertThat(result).contains(new Trade(buyLimitOrder, sellMarketOrder, quote));
        assertThat(book.contains(buyLimitOrder)).isFalse();
        assertThat(book.contains(sellMarketOrder)).isFalse();
    }

    @Test
    void should_produce_trade_on_match_for_sell_limit_order_against_buy_market_order() {
        //given
        var instrument = aRegularInstrument(randomUUID());
        var buyMarketOrder = aBuyMarketOrder(instrument.id, 20L);
        var sellLimitOrder = aSellLimitOrder(instrument.id, 20L, 25L);

        var book = new RegularInstrumentBook(instrument);
        var quote = new Quote(instrument.id, 25L, now());
        book.add(buyMarketOrder, quote);

        //when
        var result = book.add(sellLimitOrder, quote);

        //then
        assertThat(result).isPresent();
        assertThat(result).contains(new Trade(buyMarketOrder, sellLimitOrder, quote));
        assertThat(book.contains(buyMarketOrder)).isFalse();
        assertThat(book.contains(sellLimitOrder)).isFalse();
    }

    @Test
    void should_fail_on_match_for_buy_market_order_against_sell_market_order_if_balance_not_match() {
        //given
        var instrument = aRegularInstrument(randomUUID());
        var buyMarketOrder = aBuyMarketOrder(instrument.id, 20L);
        var sellMarketOrder = aSellMarketOrder(instrument.id, 21L);

        var book = new RegularInstrumentBook(instrument);
        var quote = new Quote(instrument.id, 12L, now());
        book.add(sellMarketOrder, quote);

        //when
        var result = book.add(buyMarketOrder, quote);

        //then
        assertThat(result).isEmpty();
        assertThat(book.contains(buyMarketOrder)).isTrue();
        assertThat(book.contains(sellMarketOrder)).isTrue();
    }

    @Test
    void should_fail_trade_on_match_for_sell_market_order_against_buy_market_order_if_balance_not_match() {
        //given
        var instrument = aRegularInstrument(randomUUID());
        var buyMarketOrder = aBuyMarketOrder(instrument.id, 31L);
        var sellMarketOrder = aSellMarketOrder(instrument.id, 30L);

        var book = new RegularInstrumentBook(instrument);
        var quote = new Quote(instrument.id, 12L, now());
        book.add(buyMarketOrder, quote);

        //when
        var result = book.add(sellMarketOrder, quote);

        //then
        assertThat(result).isEmpty();
        assertThat(book.contains(buyMarketOrder)).isTrue();
        assertThat(book.contains(sellMarketOrder)).isTrue();
    }

    @Test
    void should_fail_on_match_for_two_buy_market_orders() {
        //given
        var instrument = aRegularInstrument(randomUUID());
        var buyMarketOrder1 = aBuyMarketOrder(instrument.id, 20L);
        var buyMarketOrder2 = aBuyMarketOrder(instrument.id, 20L);


        var book = new RegularInstrumentBook(instrument);
        var quote = new Quote(instrument.id, 12L, now());
        book.add(buyMarketOrder1, quote);

        //when
        var result = book.add(buyMarketOrder2, quote);

        //then
        assertThat(result).isEmpty();
        assertThat(book.contains(buyMarketOrder1)).isTrue();
        assertThat(book.contains(buyMarketOrder2)).isTrue();
    }

    @Test
    void should_fail_on_match_for_two_sell_market_orders() {
        //given
        var instrument = aRegularInstrument(randomUUID());
        var sellMarketOrder1 = aSellMarketOrder(instrument.id, 30L);
        var sellMarketOrder2 = aSellMarketOrder(instrument.id, 30L);

        var book = new RegularInstrumentBook(instrument);
        var quote = new Quote(instrument.id, 12L, now());
        book.add(sellMarketOrder1, quote);

        //when
        var result = book.add(sellMarketOrder2, quote);

        //then
        assertThat(result).isEmpty();
        assertThat(book.contains(sellMarketOrder1)).isTrue();
        assertThat(book.contains(sellMarketOrder2)).isTrue();
    }

}
