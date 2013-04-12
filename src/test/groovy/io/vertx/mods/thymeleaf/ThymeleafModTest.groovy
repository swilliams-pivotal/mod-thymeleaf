/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.vertx.mods.thymeleaf

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


/**
 * @author swilliams
 *
 */
class ThymeleafModTest extends TestVerticle {

  @Test
  public void testSimpleTemplate1() throws Exception {
    container.deployVerticle('thymeleaf-server.js', { sid->

      container.deployWorkerVerticle('groovy:'+ThymeleafTemplateParser.name, new JsonObject(), 1, false, { did->

        def client = vertx.createHttpClient().setPort(7080)
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

      container.deployWorkerVerticle('groovy:'+ThymeleafTemplateParser.name, new JsonObject(), 1, false, { did->

        def client = vertx.createHttpClient().setPort(7080)
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

      container.deployWorkerVerticle('groovy:'+ThymeleafTemplateParser.name, new JsonObject(), 1, false, { did->

        def client = vertx.createHttpClient().setPort(7080)
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
  
  @Test
  public void testSimpleTemplate4() throws Exception {
    container.deployVerticle('thymeleaf-server.js', { sid->

      container.deployWorkerVerticle('groovy:'+ThymeleafTemplateParser.name, new JsonObject(), 1, false, { did->

        def client = vertx.createHttpClient().setPort(7080)
        client?.getNow('/simple4', { HttpClientResponse resp->
          resp.dataHandler({ Buffer data->
            String rendered = data.toString()

            VertxAssert.assertEquals('''<!DOCTYPE html>

<html>
  <body>
    <p>four</p>
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
