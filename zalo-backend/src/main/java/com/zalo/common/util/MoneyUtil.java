package com.zalo.common.util;

import org.joda.money.CurrencyUnit;
import org.joda.money.IllegalCurrencyException;
import org.joda.money.Money;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

public class MoneyUtil {
    public static Money toMoney(String currency, BigDecimal amount) {
        try {
            CurrencyUnit currencyCode = CurrencyUnit.of(currency); // ném lỗi nếu code sai
            Money money = Money.of(currencyCode, amount);

            if (money.isNegativeOrZero()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must be positive");
            }

            return money;
        } catch (IllegalCurrencyException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid currency code: " + currency);
        }
    }

    public static Money toMoney(String currency, Long amount) {
        try {
            CurrencyUnit currencyCode = CurrencyUnit.of(currency); // ném lỗi nếu code sai
            Money money = Money.of(currencyCode, amount);

            if (money.isNegativeOrZero()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Amount must be positive");
            }

            return money;
        } catch (IllegalCurrencyException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid currency code: " + currency);
        }
    }
}
