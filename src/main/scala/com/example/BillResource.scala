package com.example

import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol
import spray.routing.HttpService

case class Bill(id: String, amount: Double)

object BillJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val BillFormatter = jsonFormat2(Bill)
}

trait BillResource extends HttpService{
  import com.example.BillJsonSupport._

  val billRoute =
  path("bills") {
    post {
      entity(as[Bill]) { bill =>
        complete {
          bill
        }
      }
    }
  }
}
