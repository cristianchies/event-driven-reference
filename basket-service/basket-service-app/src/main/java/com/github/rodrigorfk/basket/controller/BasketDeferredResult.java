package com.github.rodrigorfk.basket.controller;

import com.github.rodrigorfk.basket.data.BasketEntity;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * @author Rodrigo Fior Kuntzer <rodrigo.kuntzer@cwi.com.br>
 */
public class BasketDeferredResult extends DeferredResult<ResponseEntity<BasketEntity>> {

    private @Getter String ticker;

    public BasketDeferredResult(String ticker) {
        this.ticker = ticker;
    }
}
