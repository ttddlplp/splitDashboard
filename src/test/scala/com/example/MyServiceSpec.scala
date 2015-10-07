package com.example

import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.http._
import StatusCodes._

class MyServiceSpec extends Specification with Specs2RouteTest with MyService {
  import spray.http.MediaTypes._
  import com.example.BillJsonSupport

  def actorRefFactory = system
  
  "MyService" should {

    "return a greeting for GET requests to the root path" in {
      Get() ~> myRoute ~> check {
        responseAs[String] must contain("Say hello")
      }
    }


    "return a greeting for POST requests to the root path" in {
      Post() ~> myRoute ~> check {
        responseAs[String] must contain("Say hello")
      }
    }

    "leave GET requests to other paths unhandled" in {
      Get("/kermit") ~> myRoute ~> check {
        handled must beFalse
      }
    }

    "POST request to BILL" in {
      Post("/bills", HttpEntity(`application/json`, """{ "id": "123", "amount" : 5.55 }""" )) ~> myRoute ~> check {
        responseAs[String] must equalTo {"""{ "id": "123", "amount" : 5.55 }"""}
      }
    }

    "return a MethodNotAllowed error for PUT requests to the root path" in {
      Put() ~> sealRoute(myRoute) ~> check {
        status === MethodNotAllowed
        responseAs[String] must contain {"HTTP method not allowed, supported methods: GET"}
      }
    }
  }
}
