package com.testing.load.order.service;

public enum OrderConsumerType {
    OPTIMISTIC,
    PESSIMISTIC,
    LUA,
    ATOMIC,
    NONE
}