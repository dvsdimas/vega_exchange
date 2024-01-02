# vega_exchange

# Simplified trading system for a financial exchange. 
The system should be able to handle buy and sell orders for a composite financial instrument.
(e.g. order placement for a basket of two underlying stocks).

# Requirements

## The financial instrument
The instrument has a unique identifier (ID), a symbol, and a current market price.
The market price should be accurate and up-to-date.

## Composite financial instruments
Similar to a normal financial instrument - has an ID, symbol and current market price.
Basket of financial instruments (1-3) is not tradable. 
Matching logic works for underlying instruments.

## Order
There are two types of orders: buy and sell.
Each order has a unique order ID, a trader ID, an order type (buy or sell),  price(optional) and a quantity.

* **The system should support adding and canceling orders.**
* **Composite order completed when underlying stocks orders matched**

## Matching algorithm / Trading system:

* Implement methods to add and cancel orders.
* When a buy order matches a sell order, a trade occurs.
* Matching algorithm should execute trades accurately and update the order book accordingly.
* Trades happen at the best available market price.


