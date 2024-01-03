package com.vega.exchange.services;

import com.vega.exchange.orders.MatchResult;
import com.vega.exchange.orders.Order;

import java.util.Optional;

public interface OrdersMatcher {

    Optional<MatchResult> add(Order order);

    boolean cancel(Order order);

}
