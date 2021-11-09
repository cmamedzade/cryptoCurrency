package com.example

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.server.Route
import com.example.Payment.{PaymentRequest, PaymentResponse}
import sttp.model.QueryParams
import sttp.tapir.{Endpoint, endpoint, plainBody}
import sttp.tapir.generic.auto._
import sttp.tapir.json.spray.jsonBody
import sttp.tapir._
import sttp.tapir.server.akkahttp.AkkaHttpServerInterpreter

class PaymentRoutes(implicit val system: ActorSystem[_])  extends JsonFormats {

    val paymentRequestBody: EndpointIO.Body[String, PaymentRequest] = jsonBody[PaymentRequest]
      .description("The payment")
      .example(PaymentRequest(BigDecimal(1),"some","some"))

   val paymentResponseBody = jsonBody[PaymentResponse]

   val addPayment: Endpoint[Unit, PaymentRequest, Unit, String, Any] =
       endpoint
       .post
       .in("payment" / "new")
       .in(paymentRequestBody)
       .out(plainBody[String])

  val getPayment =
    endpoint
      .get
      .in("payment" / path[String] )
      .out(jsonBody[Option[PaymentResponse]])

  val getPaymentByCurrency: Endpoint[Unit, QueryParams, Unit, List[PaymentResponse], Any] =
    endpoint
      .get
      .in("payment" / queryParams)
      .out(jsonBody[List[PaymentResponse]])

  val getNumberOfBtcByBtc: Endpoint[Unit, QueryParams, Unit, Int, Any] =
    endpoint
      .get
      .in("payment")
       .in("stats" / queryParams)
      .out(jsonBody[Int])

  val paymentRoutes: Route =
    AkkaHttpServerInterpreter().toRoute(List(
      getNumberOfBtcByBtc.serverLogic(Payment.getNumberOfBtc),
      addPayment.serverLogic(Payment.addPayment),
      getPayment.serverLogic(Payment.getPayment),
      getPaymentByCurrency.serverLogic(Payment.getPaymentByCurrency)
      ))

}
