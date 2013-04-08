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


class ThymeleafModLoopTest extends TestVerticle {

  @Test
  public void testLoopTemplate1() throws Exception {
    container.deployVerticle('thymeleaf-server.js', { sid->

      def config = new JsonObject()
      config.putString('templates', 'src/test/resources/templates')

      container.deployVerticle('groovy:org.vertx.mods.thymeleaf.ThymeleafMod', { did->

        def client = vertx.createHttpClient().setPort(7080)
        client?.getNow('/loop1', { HttpClientResponse resp->
          resp.dataHandler({ Buffer data->
            String rendered = data.toString()

            VertxAssert.assertEquals('''<!DOCTYPE html>

<html>
  <body>
    <table>
      <tr>
        <td>one</td>
      </tr><tr>
        <td>two</td>
      </tr><tr>
        <td>three</td>
      </tr>
    </table>
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
  public void testLoopTemplate2() throws Exception {
    container.deployVerticle('thymeleaf-server.js', { sid->

      def config = new JsonObject()
      config.putString('templates', 'src/test/resources/templates')

      container.deployVerticle('groovy:org.vertx.mods.thymeleaf.ThymeleafMod', { did->

        def client = vertx.createHttpClient().setPort(7080)
        client?.getNow('/loop2', { HttpClientResponse resp->
          resp.dataHandler({ Buffer data->
            String rendered = data.toString()

            VertxAssert.assertEquals('''<!DOCTYPE html>

<html>
  <body>
    <table>
      <tr>
        <td>1</td>
        <td>one</td>
      </tr><tr>
        <td>2</td>
        <td>two</td>
      </tr><tr>
        <td>3</td>
        <td>three</td>
      </tr>
    </table>
  </body>
</html>
''', rendered)

            testComplete()

          } as Handler)
        } as Handler)
      } as Handler)
    } as Handler)
  }

}
