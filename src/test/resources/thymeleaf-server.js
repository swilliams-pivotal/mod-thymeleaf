var vertx = require('vertx')
var http = require('http')
var eb = require('event_bus')

var httpPort = 7080

var server = http.createHttpServer().requestHandler(function(req) {

  var json = {
    templateName: req.path(),
    uri: req.uri(),
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

  eb.send('vertx.thymeleaf.parser', json, function(reply) {
    req.response.statusCode = reply.status
    req.response.end(reply.rendered)
  })

}).listen(httpPort, '127.0.0.1')
