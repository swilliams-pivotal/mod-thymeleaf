# mod-thymeleaf

This vert.x module renders Thymeleaf templates by providing a local event bus address which will render a template into a page (by reply), when it receives a JSON payload.

    var config = {
      address: 'vertx.thymeleaf.parser',
      templateDir: 'templates',
      templateMode: 'HTML5',
      cacheable: true,
      characterEncoding: 'UTF-8',
      suffix: '.html'
    }

    var instances = 2

    vertx.deployModule('io.vertx~thymeleaf-1.0.0-SNAPSHOT', config, instances)

The following example uses Javascript to configure a simple webserver, for each request a JSON object is created and passed to the thymeleaf parser via the event bus.  The reply object is also JSON, containing two fields, an HTTP status code and the rendered template.

    var server = vertx.createHttpServer().requestHandler(function(req) {

      var json = {
        templateName: req.path,
        uri: req.uri,
        params: req.params(),
        headers: req.headers(),
        hello: 'world',
        foo: { man: 'chu' },
        one: { two: { three: 'four' } },
        data1: ['one', 'two', 'three'],
        data2: [
          {id: 1, name: 'one'},
          {id: 2, name: 'two'},
          {id: 3, name: 'three'}
        ]
      }

      vertx.eventBus.send('vertx.thymeleaf.parser', json, function(reply) {
        req.response.statusCode = reply.status
        req.response.end(reply.rendered)
      })

    }).listen(7080)

The complex JSON object `one: { two: { three: 'four' } }` is accessed in a template, as below, using `one.two.three`.

    <!DOCTYPE html>
    <html>
      <body>
        <p th:text="${one.two.three}">Original</p>
      </body>
    </html>

See the examples in the module tests for how each of the above components of the JSON payload are rendered.
