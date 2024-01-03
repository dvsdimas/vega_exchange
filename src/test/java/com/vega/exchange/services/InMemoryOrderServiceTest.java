package com.vega.exchange.services;

import com.vega.exchange.helper.Helper;
import com.vega.exchange.instruments.Quote;
import com.vega.exchange.orders.ExecutionResult;
import com.vega.exchange.trades.Trade;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.time.Instant.now;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

public class InMemoryOrderServiceTest implements Helper {

    @Test
    void should_not_match_buy_market_order_in_case_if_empty() {
        //given
        var instrument = aRegularInstrument(randomUUID());
        var register = anInstrumentsRegister(List.of(instrument));
        var quote = new Quote(instrument.id, 26L, now());
        var quoting = aQuoting(Map.of(instrument.id, quote));
        var orderService = new InMemoryOrderService(register, quoting);
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
        var orderService = new InMemoryOrderService(register, quoting);
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
        var orderService = new InMemoryOrderService(register, quoting);
        var buyMarketOrder = aBuyMarketOrder(instrument.id, 30L);
        var sellMarketOrder = aSellMarketOrder(instrument.id, 30L);
        var expectedResult = new ExecutionResult(
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
        var orderService = new InMemoryOrderService(register, quoting);
        var buyMarketOrder = aBuyMarketOrder(instrument.id, 19L);
        var sellMarketOrder = aSellMarketOrder(instrument.id, 19L);
        var expectedResult = new ExecutionResult(
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

}
