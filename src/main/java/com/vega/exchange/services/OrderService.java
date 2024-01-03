package com.vega.exchange.services;

import com.vega.exchange.orders.ExecutionResult;
import com.vega.exchange.orders.Order;

import java.util.Optional;

public interface OrderService {

    Optional<ExecutionResult> add(Order order);

    boolean cancel(Order order);

}


// todo Implement methods to add and cancel orders.
// todo When a buy order matches a sell order, a trade occurs.
// todo Matching algorithm should execute trades accurately and update the order book accordingly.
// todo Trades happen at the best available market price.