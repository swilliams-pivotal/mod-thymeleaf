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

import java.util.HashSet
import java.util.Locale
import java.util.Map
import java.util.Set

import groovy.transform.CompileStatic

import org.thymeleaf.TemplateEngine
import org.thymeleaf.exceptions.TemplateEngineException
import org.thymeleaf.context.Context
import org.thymeleaf.messageresolver.StandardMessageResolver
import org.thymeleaf.templatemode.ITemplateModeHandler
import org.thymeleaf.templatemode.StandardTemplateModeHandlers
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver
import org.thymeleaf.templateresolver.ITemplateResolver

import org.vertx.groovy.core.eventbus.Message
import org.vertx.groovy.platform.Verticle
import org.vertx.java.core.AsyncResult
import org.vertx.java.core.Future


/**
 * @author swilliams
 *
 */
@CompileStatic
public class ThymeleafTemplateParser extends Verticle {

  public static final String DEFAULT_ADDRESS = 'vertx.thymeleaf.parser'
  public static final String DEFAULT_TEMPLATE_DIR = 'templates'

  private Map<String, Locale> localeCache = [:]

  private String address
  private String templateDir
  private TemplateEngine engine

  @Override
  def start(Future<Void> result) {

    this.address = container.config['address'] ?: DEFAULT_ADDRESS
    this.templateDir = container.config['templateDir'] ?: DEFAULT_TEMPLATE_DIR

    def templateResolver = new ClassLoaderTemplateResolver()
    templateResolver.cacheable = container.config['cacheable'] as boolean ?: true
    templateResolver.characterEncoding = container.config['characterEncoding'] ?: 'UTF-8'
    templateResolver.prefix = templateDir
    templateResolver.templateMode = container.config['templateMode'] ?: 'HTML5'
    templateResolver.suffix = container.config['suffix'] ?: '.html'

    def templateResolvers = new HashSet<ITemplateResolver>()
    templateResolvers.add(templateResolver)

    def messageResolver = new StandardMessageResolver()
    // TODO permit alternative message property loading

    this.engine = new TemplateEngine()
    engine.setTemplateResolvers(templateResolvers)
    engine.addMessageResolver(messageResolver)

    for (ITemplateModeHandler tmh : StandardTemplateModeHandlers.ALL_TEMPLATE_MODE_HANDLERS) {
      engine.addTemplateModeHandler(tmh)
    }

    // we use a local handler to ensure we're not doing 
    // unnecessary IO for each template operation
    vertx.eventBus.registerLocalHandler(address, this.&processor)

    result.setResult(null)
  }

  @Override
  def stop() {
    vertx.eventBus.unregisterHandler(address, this.&processor)
  }

  def processor(Message msg) {
    int status = 500
    String rendered = errorHtml('Unknown error')

    String templateName = msg.body['templateName']
    String language = msg.body['language'] ?: 'en'
    String templateFile = templateDir + templateName

    vertx.fileSystem.exists(templateFile) { AsyncResult res->

      if (res.succeeded()) {
        Locale locale
        if (localeCache.containsKey(language)) {
          locale = localeCache.get(language)
        }
        else {
          locale = buildLocale(language)
          localeCache.put(language, locale)
        }

        Context context = new Context(locale)
        context.setVariables(msg.body as Map)

        try {
          rendered = engine.process(templateName, context)
          status = 200
        }
        catch (TemplateEngineException e) {
          rendered = errorHtml(e.message)
          status = 500
        }
      }
      else {
        rendered = errorHtml('Not Found')
        status = 404
      }

      msg.reply([status: status, rendered: rendered])
    }
  }

  private Locale buildLocale(String language) {
    new Locale.Builder().setLanguage(language).build()
  }

  def errorHtml(String message) {
    """
<!DOCTYPE html>
    <html>
      <body>
        <p>${message}</p>
      </body>
    </html>
"""
  }

}
