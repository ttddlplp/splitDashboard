package com.example

import org.specs2.mutable.Specification
import spray.http.StatusCodes._
import spray.http._
import spray.testkit.Specs2RouteTest

class MyServiceSpec extends Specification with Specs2RouteTest with MyService {
  import com.example.BillJsonSupport._

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
      Post("/bills", Bill("123", 5.55)) ~> myRoute ~> check {
        responseAs[Bill] must equalTo(Bill("123", 5.55))
        entity.toOption.get.contentType.toString() must contain("application/json")
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
