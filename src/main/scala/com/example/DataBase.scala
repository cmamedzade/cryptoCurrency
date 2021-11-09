package com.example

import com.example.Payment.SinglePayment

object DataBase {
    val fiatCurrencies: List[String] = List("EUR", "USD")
    val cryptoCurrencies: List[String] = List("BTC")
    var payments: List[SinglePayment] = List.empty
}
