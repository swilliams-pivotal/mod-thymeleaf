/*
 * Copyright 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the 'License');
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an 'AS IS' BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.vertx.mods.thymeleaf

import io.vertx.mods.webserver.base.StaticHttpResourceHandler
import io.vertx.mods.webserver.base.WebServerBase

import java.util.HashMap

import groovy.transform.CompileStatic

import org.vertx.java.core.Handler
import org.vertx.java.core.VoidResult
import org.vertx.java.core.eventbus.Message
import org.vertx.java.core.http.HttpServerRequest
import org.vertx.java.core.http.RouteMatcher
import org.vertx.java.core.json.JsonObject


/**
 * @author pidster
 *
 */
@CompileStatic
class ThymeleafWebServer extends WebServerBase {

  @Override
  public void start() {
    container.deployWorkerVerticle('groovy:io.vertx.mods.thymeleaf.ThymeleafTemplateParser', config, 1, false, { String did->
      //
    } as Handler)
    super.start()
    config.putBoolean('route_matcher', true)
  }

  protected RouteMatcher routeMatcher() {

    String regex = getMandatoryStringConfig('regex')
    String match = getOptionalStringConfig('match', '$1')
    String parserAddress = getOptionalStringConfig('address', ThymeleafTemplateParser.DEFAULT_ADDRESS)

    def rm = new RouteMatcher()
    rm.allWithRegEx(regex, { HttpServerRequest req->

      JsonObject msg = new JsonObject()
      msg.putString('templateName', req.path.replaceAll(regex, match))
      msg.putString('method', req.method)
      msg.putString('path', req.path)
      msg.putString('query', req.query)
      msg.putString('uri', req.uri)
      msg.putObject('params', new JsonObject(new HashMap<String,Object>(req.params())))
      msg.putObject('headers', new JsonObject(new HashMap<String,Object>(req.headers())))

      vertx.eventBus().send(parserAddress, prepareParams(req, msg), { Message event->

        def body = ((JsonObject) event.body).toMap()
        int statusCode = body['status'] as int
        String chunk = body['rendered']
        req.response.statusCode = statusCode
        req.response.end(chunk)

      } as Handler)
    } as Handler)

    rm.noMatch(new StaticHttpResourceHandler(vertx.fileSystem(), getWebRootPrefix(), getIndexPage(), isGzipFiles()))

    return rm
  }

  protected JsonObject prepareParams(HttpServerRequest req, JsonObject msg) {
    msg
  }

}
