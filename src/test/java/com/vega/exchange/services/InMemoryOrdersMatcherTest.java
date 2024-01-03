package com.vega.exchange.services;

import com.vega.exchange.helper.Helper;
import com.vega.exchange.instruments.Quote;
import com.vega.exchange.orders.MatchResult;
import com.vega.exchange.trades.Trade;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.time.Instant.now;
import static java.util.UUID.randomUUID;
import static java.util.function.Function.identity;
import static org.assertj.core.api.Assertions.assertThat;

public class InMemoryOrdersMatcherTest implements Helper {

    @Test
    void should_not_match_buy_market_order_in_case_if_empty() {
        //given
        var instrument = aRegularInstrument(randomUUID());
        var register = anInstrumentsRegister(List.of(instrument));
        var quote = new Quote(instrument.id, 26L, now());
        var quoting = aQuoting(Map.of(instrument.id, quote));
        var orderService = new InMemoryOrdersMatcher(register, quoting);
        var buyMarketOrder = aBuyMarketOrder(instrument.id, 20L);

        //when
        var result = orderService.add(buyMarketOrder);

        //then
        assertThat(result).isEmpty();
    }

    @Test
    void should_not_match_sell_market_order_in_case_if_empty() {
        //given
        var instrument = aRegularInstrument(randomUUID());
        var register = anInstrumentsRegister(List.of(instrument));
        var quote = new Quote(instrument.id, 26L, now());
        var quoting = aQuoting(Map.of(instrument.id, quote));
        var orderService = new InMemoryOrdersMatcher(register, quoting);
        var sellMarketOrder = aSellMarketOrder(instrument.id, 30L);

        //when
        var result = orderService.add(sellMarketOrder);

        //then
        assertThat(result).isEmpty();
    }

    @Test
    void should_match_buy_market_order_with_sell_market_order() {
        //given
        var instrument = aRegularInstrument(randomUUID());
        var register = anInstrumentsRegister(List.of(instrument));
        var quote = new Quote(instrument.id, 19L, now());
        var quoting = aQuoting(Map.of(instrument.id, quote));
        var orderService = new InMemoryOrdersMatcher(register, quoting);
        var buyMarketOrder = aBuyMarketOrder(instrument.id, 30L);
        var sellMarketOrder = aSellMarketOrder(instrument.id, 30L);
        var expectedResult = new MatchResult(
                buyMarketOrder,
                Set.of(new Trade(buyMarketOrder, sellMarketOrder, quote)));

        //when
        var result1 = orderService.add(sellMarketOrder);

        //then
        assertThat(result1).isEmpty();

        //when
        var result2 = orderService.add(buyMarketOrder);

        //then
        assertThat(result2).isPresent();
        assertThat(result2.orElseThrow()).isEqualTo(expectedResult);
    }

    @Test
    void should_match_sell_market_order_with_buy_market_order() {
        //given
        var instrument = aRegularInstrument(randomUUID());
        var register = anInstrumentsRegister(List.of(instrument));
        var quote = new Quote(instrument.id, 33L, now());
        var quoting = aQuoting(Map.of(instrument.id, quote));
        var orderService = new InMemoryOrdersMatcher(register, quoting);
        var buyMarketOrder = aBuyMarketOrder(instrument.id, 19L);
        var sellMarketOrder = aSellMarketOrder(instrument.id, 19L);
        var expectedResult = new MatchResult(
                sellMarketOrder,
                Set.of(new Trade(buyMarketOrder, sellMarketOrder, quote)));

        //when
        var result1 = orderService.add(buyMarketOrder);

        //then
        assertThat(result1).isEmpty();

        //when
        var result2 = orderService.add(sellMarketOrder);

        //then
        assertThat(result2).isPresent();
        assertThat(result2.orElseThrow()).isEqualTo(expectedResult);
    }

    @Test
    void should_not_match_buy_market_order_with_sell_market_order_if_amount_not_the_same() {
        //given
        var instrument = aRegularInstrument(randomUUID());
        var register = anInstrumentsRegister(List.of(instrument));
        var quote = new Quote(instrument.id, 19L, now());
        var quoting = aQuoting(Map.of(instrument.id, quote));
        var orderService = new InMemoryOrdersMatcher(register, quoting);
        var buyMarketOrder = aBuyMarketOrder(instrument.id, 30L);
        var sellMarketOrder = aSellMarketOrder(instrument.id, 31L);

        //when
        var result1 = orderService.add(sellMarketOrder);

        //then
        assertThat(result1).isEmpty();

        //when
        var result2 = orderService.add(buyMarketOrder);

        //then
        assertThat(result2).isEmpty();
    }

    @Test
    void should_not_match_buy_market_order_with_sell_market_order_if_instrument_not_the_same() {
        //given
        var instrument1 = aRegularInstrument(randomUUID());
        var instrument2 = aRegularInstrument(randomUUID());
        var register = anInstrumentsRegister(List.of(instrument1, instrument2));
        var quote1 = new Quote(instrument1.id, 19L, now());
        var quote2 = new Quote(instrument2.id, 23L, now());
        var quoting = aQuoting(
                Map.of(instrument1.id, quote1,
                        instrument2.id, quote2));
        var orderService = new InMemoryOrdersMatcher(register, quoting);
        var buyMarketOrder = aBuyMarketOrder(instrument1.id, 30L);
        var sellMarketOrder = aSellMarketOrder(instrument2.id, 30L);

        //when
        var result1 = orderService.add(sellMarketOrder);

        //then
        assertThat(result1).isEmpty();

        //when
        var result2 = orderService.add(buyMarketOrder);

        //then
        assertThat(result2).isEmpty();
    }

    @Test
    void should_match_composite_buy_market_order_with_sell_market_orders() {
        //given
        var instrument1 = aRegularInstrument(randomUUID());
        var instrument2 = aRegularInstrument(randomUUID());
        var instrument3 = aRegularInstrument(randomUUID());
        var compositeInstrument = aCompositeInstrument(randomUUID(), Set.of(instrument1, instrument2, instrument3));
        var register = anInstrumentsRegister(List.of(compositeInstrument, instrument1, instrument2, instrument3));
        var quote1 = new Quote(instrument1.id, 19L, now());
        var quote2 = new Quote(instrument2.id, 34L, now());
        var quote3 = new Quote(instrument3.id, 22L, now());
        var quoting = aQuoting(
                Map.of(
                        instrument1.id, quote1,
                        instrument2.id, quote2,
                        instrument3.id, quote3));
        var orderService = new InMemoryOrdersMatcher(register, quoting);
        var compositeBuyMarketOrder = aBuyMarketOrder(compositeInstrument.id, 30L);
        var sellMarketOrder1 = aSellMarketOrder(instrument1.id, 30L);
        var sellMarketOrder2 = aSellMarketOrder(instrument2.id, 30L);
        var sellMarketOrder3 = aSellMarketOrder(instrument3.id, 30L);

        orderService.add(sellMarketOrder1);
        orderService.add(sellMarketOrder2);
        orderService.add(sellMarketOrder3);

        //when
        var result = orderService.add(compositeBuyMarketOrder);

        //then
        assertThat(result).isPresent();
        assertThat(result.orElseThrow().order).isEqualTo(compositeBuyMarketOrder);
        assertThat(result.orElseThrow().trades).size().isEqualTo(3);

        var trades = result.orElseThrow().trades
                .stream()
                .collect(Collectors.toMap(trade -> trade.quote().instrumentId(), identity()));

        assertThat(trades.get(instrument1.id).sellOrder()).isEqualTo(sellMarketOrder1);
        assertThat(trades.get(instrument2.id).sellOrder()).isEqualTo(sellMarketOrder2);
        assertThat(trades.get(instrument3.id).sellOrder()).isEqualTo(sellMarketOrder3);

        assertThat(trades.get(instrument1.id).quote()).isEqualTo(quote1);
        assertThat(trades.get(instrument2.id).quote()).isEqualTo(quote2);
        assertThat(trades.get(instrument3.id).quote()).isEqualTo(quote3);
    }


}
