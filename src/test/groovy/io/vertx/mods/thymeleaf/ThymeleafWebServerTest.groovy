package io.vertx.mods.thymeleaf

import static org.vertx.testtools.VertxAssert.*
import org.junit.Test
import org.vertx.java.core.Handler
import org.vertx.java.core.buffer.Buffer
import org.vertx.java.core.http.HttpClientResponse
import org.vertx.java.core.json.JsonObject
import org.vertx.testtools.TestVerticle
import org.vertx.testtools.VertxAssert

public class ThymeleafWebServerTest extends TestVerticle {

  @Test
  public void testRenderedTemplate() {
    // accepts all of the standard web server config
    // plus regex & match attributes
    // plus ThymeleafTemplateParser config
    def conf = [
      port: 7081,
      regex: '/foo/(.*)\\.html',
      match: '/$1',
      templateDir: 'src/test/resource/templates'
    ]

    container.deployVerticle('groovy:io.vertx.mods.thymeleaf.ThymeleafWebServer', new JsonObject(conf), 1, { String did->

        def client = vertx.createHttpClient().setPort(7081)
        client?.getNow('/foo/web1.html', { HttpClientResponse resp->
          resp.dataHandler({ Buffer data->
            String rendered = data.toString()

            VertxAssert.assertEquals('''<!DOCTYPE html>

<html>
  <body>
    <p>world</p>
    <p>bar</p>
    <p>chu</p>
  </body>
</html>
''', rendered)

            testComplete()

          } as Handler)
        } as Handler)
    } as Handler)

  }

  @Test
  public void testFlatPage() {
    // accepts all of the standard web server config
    // plus regex & match attributes
    // plus ThymeleafTemplateParser config
    def conf = [
      port: 7081,
      web_root: 'src/test/resources/webroot',
      regex: '/foo/(.*)\\.html',
      match: '/$1',
      templateDir: 'src/test/resource/templates'
    ]

    container.deployVerticle('groovy:io.vertx.mods.thymeleaf.ThymeleafWebServer', new JsonObject(conf), 1, { String did->

        def client = vertx.createHttpClient().setPort(7081)
        client?.getNow('/flat1.html', { HttpClientResponse resp->
          resp.dataHandler({ Buffer data->
            String rendered = data.toString()

            VertxAssert.assertEquals('''<!DOCTYPE html>
<html>
  <body>
    <p>Hello World</p>
  </body>
</html>
''', rendered)

            testComplete()

          } as Handler)
        } as Handler)
    } as Handler)

  }

}
