load('vertx.js')


var server = vertx.createHttpServer().requestHandler(function(req) {

  var json = {
    templateName: req.path + '.html',
    foo: { man: 'chu' },
    hello: 'world',
    path: req.uri,
    params: req.params(),
    headers: req.headers()
  }

  vertx.eventBus.send('vertx.thymeleaf.parser', json, function(reply) {
    req.response.statusCode = reply.status
    req.response.end(reply.rendered)
  })

}).listen(7080)
