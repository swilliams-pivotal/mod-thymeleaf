package org.vertx.thymeleaf;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.test.TestVerticle;
import org.vertx.java.test.VertxConfiguration;
import org.vertx.java.test.VertxTestBase;
import org.vertx.java.test.junit.VertxJUnit4ClassRunner;

@RunWith(VertxJUnit4ClassRunner.class)
@VertxConfiguration
@TestVerticle(main="deployer.js")
public class ThymeleafModTest extends VertxTestBase {

  private static final String TEMPLATE_FILE = "test.html";

  private static final long TIMEOUT = Long.getLong("vertx.test.timeout", 10);

  @Before
  public void setup() {
    lightSleep(1000L);
  }

  @Test
  public void testSimpleEventBusTemplateTest() {
    final CountDownLatch latch = new CountDownLatch(1);

    Map<String, Object> varmap = new HashMap<>();
    varmap.put("testmessage", "SUCCEEDED");
    varmap.put("one", "SUCCEEDED");
    varmap.put("two", new JsonObject().putString("val", "SUCCEEDED"));
    JsonObject variables = new JsonObject(varmap);

    JsonObject json = new JsonObject();
    json.putString("templateName", TEMPLATE_FILE);
    json.putString("language", "en");
    json.putObject("variables", variables);

    final LinkedBlockingQueue<String> answers = new LinkedBlockingQueue<>();

    getVertx().eventBus().send("vertx.thymeleaf.parser", json, new Handler<Message<JsonObject>>() {

      @Override
      public void handle(Message<JsonObject> event) {
        String body = event.body.getString("body");
        answers.offer(body);
        latch.countDown();
      }

    });

    try {
      System.out.printf("Waiting for response for up to %d%n", TIMEOUT);

      latch.await(TIMEOUT, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      fail(e.getMessage());
    }

    String body = answers.poll();
    System.out.println(body);
    assertNotNull(body);

    assertTrue(body.indexOf("<p>SUCCEEDED</p>") > -1);
  }

}
