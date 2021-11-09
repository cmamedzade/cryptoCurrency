package com.example

import cats.data.Validated
import sttp.model.QueryParams

import java.time.LocalDateTime
import java.util.UUID
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Payment {

  def addPayment(paymentRequest: PaymentRequest): Future[Either[Unit, String]] = {
    FormValidatorNec.validateForm(paymentRequest) match {
      case Validated.Valid(pmt) =>
        DataBase.payments = DataBase.payments ++ List(pmt)
        Future.successful(Right(s"payment created with uuid: ${pmt.id.toString}"))
      case Validated.Invalid(e) =>
        Future.successful(Left(e.head.errorMessage.error))
    }
  }

  def getPayment(id: String): Future[Either[Unit, Option[PaymentResponse]]] = {
    DataBase.payments.find(p => p.id == UUID.fromString(id)) match {
      case Some(value) => getPaymentResponse(value)
      case None =>
        Future.successful(Left(None))
    }
  }

  def getPaymentByCurrency(queryParams: QueryParams): Future[Either[Unit, List[PaymentResponse]]] = {
    val currency = queryParams.get("currency").getOrElse("nothing")
    val result: List[PaymentResponse] = DataBase.payments.filter(p => p.fiatCurrency == currency).map(p => getPaymentResponseByDefault(p))
    Future.successful(Right(result))
  }

  def getPaymentResponse(singlePayment: SinglePayment): Future[Right[Nothing, Some[PaymentResponse]]] = {
    Future.successful(Right(Some(PaymentResponse(singlePayment.id,
      singlePayment.fiatAmount,
      singlePayment.fiatCurrency,
      singlePayment.coinAmount,
      singlePayment.coinCurrency,
      singlePayment.exchangeRate,
      singlePayment.createdAt,
      singlePayment.expirationTime))))
  }

  def getPaymentResponseByDefault(singlePayment: SinglePayment): PaymentResponse = {
    PaymentResponse(singlePayment.id,
      singlePayment.fiatAmount,
      singlePayment.fiatCurrency,
      singlePayment.coinAmount,
      singlePayment.coinCurrency,
      singlePayment.exchangeRate,
      singlePayment.createdAt,
      singlePayment.expirationTime)
  }

  def getNumberOfBtc(queryParams: QueryParams): Future[Either[Unit, Int]] = {
    val btc: String = queryParams.get("currency").getOrElse("nothing")
    val count = DataBase.payments.count(p => p.coinCurrency == btc)
    Future.successful(Right(count))
  }

  case class PaymentRequest(fiatAmount: BigDecimal, fiatCurrency: String, coinCurrency: String)

  case class SinglePayment(
                      id: UUID,
                      fiatAmount: BigDecimal,
                      fiatCurrency: String,
                      coinAmount: BigDecimal,
                      coinCurrency: String,
                      exchangeRate: BigDecimal,
                      eurExchangeRate: BigDecimal,
                      createdAt: LocalDateTime,
                      expirationTime: LocalDateTime)

  case class PaymentResponse(
                              id: UUID,
                              fiatAmount: BigDecimal,
                              fiatCurrency: String,
                              coinAmount: BigDecimal,
                              coinCurrency: String,
                              exchangeRate: BigDecimal,
                              createdAt: LocalDateTime,
                              expirationTime: LocalDateTime)

  case class StatsResponse(
                          stats: String
                          )

  def getCoinAmount(coin: String, fiatAmount: BigDecimal): BigDecimal = MarketData.exchangeRatesOfBTC(coin) * fiatAmount
  def getExchangeRate(fiatCurrency: String): BigDecimal = MarketData.exchangeRatesOfBTC(fiatCurrency)
  def getEurExchangeRate(currency: String): BigDecimal = MarketData.exchangeRatesToEUR(currency)
  def getCreatedAt: LocalDateTime = LocalDateTime.now()
  def getExpirationTime: LocalDateTime = LocalDateTime.now().plusDays(30)
  def getCoinCcy(coin: String): Option[String] = {
    DataBase.cryptoCurrencies.find( p => p == coin)
  }

}