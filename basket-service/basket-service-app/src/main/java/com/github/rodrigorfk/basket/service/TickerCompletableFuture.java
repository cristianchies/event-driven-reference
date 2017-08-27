package com.github.rodrigorfk.basket.service;

import lombok.Getter;

import java.util.concurrent.CompletableFuture;

/**
 * @author Rodrigo Fior Kuntzer <rodrigo.kuntzer@cwi.com.br>
 */
public class TickerCompletableFuture<T, K> extends CompletableFuture<T> {

    private @Getter String ticker;
    private @Getter K additionalData;

    public TickerCompletableFuture(String ticker, K additionalData) {
        this.ticker = ticker;
        this.additionalData = additionalData;
    }
}
