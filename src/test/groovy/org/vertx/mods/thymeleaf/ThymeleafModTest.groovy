package org.vertx.mods.thymeleaf

import static org.vertx.testtools.VertxAssert.*

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith;
import org.vertx.java.core.Handler
import org.vertx.java.core.buffer.Buffer
import org.vertx.java.core.http.HttpClientResponse
import org.vertx.java.core.json.JsonObject
import org.vertx.testtools.JavaClassRunner;
import org.vertx.testtools.TestVerticle
import org.vertx.testtools.VertxAssert;


@RunWith(JavaClassRunner.class)
class ThymeleafModTest extends TestVerticle {

  def client

  @Before
  public void setup() {
    // client = vertx.createHttpClient().setPort(7080)
    println "setup!"
  }

  @Test
  public void testSimpleTemplate1() throws Exception {
    container.deployVerticle('thymeleaf-server.js', { sid->

      def config = new JsonObject()
      config.putString('templates', 'src/test/resources/templates')

      container.deployVerticle('groovy:org.vertx.mods.thymeleaf.ThymeleafMod', { did->

        client = vertx.createHttpClient().setPort(7080)
        client?.getNow('/simple1', { HttpClientResponse resp->
          resp.dataHandler({ Buffer data->
            String rendered = data.toString()

            VertxAssert.assertEquals('''<!DOCTYPE html>

<html>
  <body>
    <p>world</p>
  </body>
</html>
''', rendered)

            testComplete()

          } as Handler)
        } as Handler)
      } as Handler)
    } as Handler)
  }
  
  @Test
  public void testSimpleTemplate2() throws Exception {
    container.deployVerticle('thymeleaf-server.js', { sid->

      def config = new JsonObject()
      config.putString('templates', 'src/test/resources/templates')

      container.deployVerticle('groovy:org.vertx.mods.thymeleaf.ThymeleafMod', { did->

        client = vertx.createHttpClient().setPort(7080)
        client?.getNow('/simple2', { HttpClientResponse resp->
          resp.dataHandler({ Buffer data->
            String rendered = data.toString()

            VertxAssert.assertEquals('''<!DOCTYPE html>

<html>
  <body>
    <p>chu</p>
  </body>
</html>
''', rendered)

            testComplete()

          } as Handler)
        } as Handler)
      } as Handler)
    } as Handler)
  }

  @Test
  public void testSimpleTemplate3() throws Exception {
    container.deployVerticle('thymeleaf-server.js', { sid->

      def config = new JsonObject()
      config.putString('templates', 'src/test/resources/templates')

      container.deployVerticle('groovy:org.vertx.mods.thymeleaf.ThymeleafMod', { did->

        client = vertx.createHttpClient().setPort(7080)
        client?.getNow('/simple3?foo=bar', { HttpClientResponse resp->
          resp.dataHandler({ Buffer data->
            String rendered = data.toString()

            VertxAssert.assertEquals('''<!DOCTYPE html>

<html>
  <body>
    <p>bar</p>
  </body>
</html>
''', rendered)

            testComplete()

          } as Handler)
        } as Handler)
      } as Handler)
    } as Handler)
  }

  @After
  public void teardown() {
    client?.close()
  }

}
