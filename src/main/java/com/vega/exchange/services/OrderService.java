package com.vega.exchange.services;

import com.vega.exchange.orders.Order;

public interface OrderService {

    void add(Order order);

    void cancel(Order order);

}


// todo Implement methods to add and cancel orders.
// todo When a buy order matches a sell order, a trade occurs.
// todo Matching algorithm should execute trades accurately and update the order book accordingly.
// todo Trades happen at the best available market price.