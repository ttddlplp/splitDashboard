package com.example

import akka.actor.Actor
import spray.httpx.SprayJsonSupport
import spray.json.DefaultJsonProtocol
import spray.routing._
import spray.http._
import MediaTypes._

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
class MyServiceActor extends Actor with MyService {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}

case class Bill(id: String, amount: Double)

object BillJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {
  implicit val BillFormatter = jsonFormat2(Bill)
}

// this trait defines our service behavior independently from the service actor
trait MyService extends HttpService {
  import com.example.BillJsonSupport._

  val myRoute =
    path("") {
      get {
        respondWithMediaType(`text/html`) { // XML is marshalled to `text/xml` by default, so we simply override here
          complete {
            <html>
              <body>
                <h1>Say hello to <i>spray-routing</i> on <i>spray-can</i>!</h1>
              </body>
            </html>
          }
        }
      } ~
        post {
          respondWithMediaType(`text/html`) {
            complete {
              <html>
                <body>
                  <h1>Say hello to <i>spray-routing</i> on <i>spray-can</i>!</h1>
                </body>
              </html>
            }
          }
        }
    } ~
      path("aaa" / IntNumber) { id =>
        get {
            respondWithMediaType(`text/html`) {
              complete {
                <html>
                  <body>
                    <h1>$id</h1>
                  </body>
                </html>
              }
            }
          }
        } ~
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