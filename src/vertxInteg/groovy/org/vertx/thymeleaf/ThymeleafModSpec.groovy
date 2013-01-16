package org.vertx.thymeleaf

import static org.junit.Assert.*

import java.util.HashMap
import java.util.Map
import java.util.concurrent.CountDownLatch
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import spock.lang.Specification

import org.vertx.java.core.Handler
import org.vertx.java.core.Vertx
import org.vertx.java.core.eventbus.Message
import org.vertx.java.core.json.JsonObject
import org.vertx.java.test.TestVerticle
import org.vertx.java.test.VertxAware

import org.vertx.java.test.VertxConfiguration


@VertxConfiguration
@TestVerticle(main="deployer.js")
class ThymeleafModSpec extends Specification {

  static def TEMPLATE_FILE = "test.html"

  static def TIMEOUT = Long.getLong("vertx.test.timeout", 10)

  def vertx

  def "setup and wait"() {
    lightSleep(1000L)
  }

  def "Simple eventBus template test"() {
    setup: "setup and wait"

    final latch = new CountDownLatch(1)

    def varmap = [:]
    varmap.put("testmessage", "SUCCEEDED")
    varmap.put("one", "SUCCEEDED")
    varmap.put("two", new JsonObject().putString("val", "SUCCEEDED"))
    def variables = new JsonObject(varmap)

    def json = new JsonObject()
    json.putString("templateName", TEMPLATE_FILE)
    json.putString("language", "en")
    json.putObject("variables", variables)

    final answers = new LinkedBlockingQueue<>()

    vertx.eventBus().send("vertx.thymeleaf.parser", json, new Handler<Message<JsonObject>>() {
      @Override
      public void handle(Message<JsonObject> event) {
        answers.offer(event.body.getString("body"))
        latch.countDown()
      }
    })

    latch.await(TIMEOUT, TimeUnit.SECONDS)

    String body = answers.poll()
    println(body)

    expect: body != null
    and: body.indexOf("<p>SUCCEEDED</p>") > -1
  }

}
