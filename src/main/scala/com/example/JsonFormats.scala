package com.example

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.example.Payment.{PaymentRequest, PaymentResponse}
import spray.json.DefaultJsonProtocol
import com.example.TypeMapper.{LocalDT, UUIDFormat}

trait JsonFormats extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val paymentFormat = jsonFormat3(PaymentRequest)
  implicit val responseFormat = jsonFormat8(PaymentResponse)
}
