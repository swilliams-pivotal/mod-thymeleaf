load('vertx.js')

var config = {
  'port': 8080
}

vertx.deployModule('vertx.thymeleaf-v1.0', config, 1, function() {
  console.log('deployed module: vertx.thymeleaf-v1.0')
});
