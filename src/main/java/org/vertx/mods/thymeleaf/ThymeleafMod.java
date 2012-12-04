package org.vertx.mods.thymeleaf;

import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.messageresolver.StandardMessageResolver;
import org.thymeleaf.templatemode.ITemplateModeHandler;
import org.thymeleaf.templatemode.StandardTemplateModeHandlers;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;

import org.vertx.thymeleaf.support.VertxDialect;


public class ThymeleafMod extends BusModBase {

  private String DEFAULT_ADDRESS = "vertx.thymeleaf.parser";

  @Override
  public void start() {
    super.start();

    String address = super.getOptionalStringConfig("address", DEFAULT_ADDRESS);

    final TemplateEngine engine = new TemplateEngine();
    engine.addDialect("vertx", new VertxDialect());

    engine.addMessageResolver(new StandardMessageResolver());

    Set<ITemplateModeHandler> modeHandlers = StandardTemplateModeHandlers.ALL_TEMPLATE_MODE_HANDLERS;
    for (ITemplateModeHandler templateModeHandler : modeHandlers) {
      engine.addTemplateModeHandler(templateModeHandler);
    }

    Set<ITemplateResolver> templateResolvers = new HashSet<>();
    templateResolvers.add(new ClassLoaderTemplateResolver());
    engine.setTemplateResolvers(templateResolvers);

    // Use a local handler to prevent this forcing non-local IO!
    eb.registerLocalHandler(address, new Handler<Message<JsonObject>>() {

      @Override
      public void handle(Message<JsonObject> event) {

        String templateName = event.body.getString("templateName");
        String language = event.body.getString("language", "en");
        Map<String, Object> variables = event.body.getObject("variables").toMap();

        Locale locale = new Locale.Builder().setLanguage(language).build();
        Context context = new Context(locale);
        context.setVariables(variables);

        String processed = engine.process(templateName, context);
        event.reply(new JsonObject().putString("body", processed));
      }
    });
  }

  @Override
  public void stop() throws Exception {
    super.stop();
  }

}
