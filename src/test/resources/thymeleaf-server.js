load('vertx.js')


var server = vertx.createHttpServer().requestHandler(function(req) {

  var json = {
    templateName: req.path,
    templateMode: 'HTML5',
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
