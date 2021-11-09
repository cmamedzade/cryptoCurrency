package com.example

import cats.data.ValidatedNec
import cats.implicits.{catsSyntaxTuple2Semigroupal, catsSyntaxValidatedIdBinCompat0}
import com.example.Payment._
import com.typesafe.config.{Config, ConfigFactory}

import java.util.UUID

sealed trait PaymentValidation {
  def errorMessage: ErrorInfo
}

case class ErrorInfo(error: String)

case object FiatAmountDoesNotMatch extends PaymentValidation {
  def errorMessage: ErrorInfo = ErrorInfo("parameters does not match")
}

sealed trait FormValidatorNec {

    type ValidationResult[A] = ValidatedNec[PaymentValidation, A]

    private def validateFiatAmount(fiatAmount: BigDecimal): ValidationResult[BigDecimal] = {
      val config = ConfigFactory.load()
      val minEur = config.getInt("api.payment.min-eur-amount")
      val maxEur = config.getInt("api.payment.max-eur-amount")
      if (fiatAmount > minEur && fiatAmount < maxEur) fiatAmount.validNec else FiatAmountDoesNotMatch.invalidNec
    }

  private def validateFiatCurrency(fiat: String): ValidationResult[String] = {
    if (DataBase.fiatCurrencies.contains(fiat)) fiat.validNec else FiatAmountDoesNotMatch.invalidNec
  }

    def validateForm(paymentRequest: PaymentRequest): ValidationResult[SinglePayment] = {
      (validateFiatAmount(paymentRequest.fiatAmount),
        validateFiatCurrency(paymentRequest.fiatCurrency)).mapN{
        (fiatAmt , fiatCcy) =>
          SinglePayment(UUID.randomUUID(),
            fiatAmt,
            fiatCcy,
            coinAmount = Payment.getCoinAmount(paymentRequest.fiatCurrency, fiatAmt),
            coinCurrency = Payment.getCoinCcy(paymentRequest.coinCurrency).getOrElse(""),
            exchangeRate = Payment.getExchangeRate(paymentRequest.fiatCurrency),
            eurExchangeRate = Payment.getEurExchangeRate(paymentRequest.fiatCurrency),
            createdAt = Payment.getCreatedAt,
            expirationTime = Payment.getExpirationTime)
      }
    }

  }

  object FormValidatorNec extends FormValidatorNec


