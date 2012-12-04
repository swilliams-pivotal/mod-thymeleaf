load('vertx.js')

var config = {
  'address': 'vertx.thymeleaf.parser'
}

vertx.deployModule('vertx.thymeleaf-v1.0', config, 1, function(id) {
  console.log('deployed module: vertx.thymeleaf-v1.0 with id:' + id)
})
