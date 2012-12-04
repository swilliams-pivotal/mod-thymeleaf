package org.vertx.thymeleaf.support;

import java.util.HashMap;
import java.util.Map;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.attr.AbstractTextChildModifierAttrProcessor;
import org.vertx.java.core.json.JsonObject;

public class VertxJSONAttributeProcessor extends AbstractTextChildModifierAttrProcessor {

  public VertxJSONAttributeProcessor() {
    super("json");
  }

  public int getPrecedence() {
    return 1;
  }

  @Override
  protected String getText(final Arguments arguments, final Element element, final String attributeName) {
    String value = element.getAttributeValue(attributeName);

    String resolved = value;
    if (value.startsWith("${") && value.endsWith("}")) {
      value = value.substring(2, value.length() - 1);
      System.out.println("value: " + value);
      resolved = resolve(value, arguments.getExpressionObjects());
    }

    return resolved;
  }

  @SuppressWarnings("unchecked")
  private String resolve(String value, Map<String, Object> expressionObjects) {

    System.out.println("expressionObjects: " + expressionObjects);

    Map<String, Object> vars = new HashMap<>();
    vars.putAll(expressionObjects);

    String[] attributes = value.split("\\.");
    LOOP: for (String attr : attributes) {
      if (!vars.containsKey(attr)) {
        break LOOP;
      }

      Object object = vars.get(attr);
      System.out.println(attr + "=" + object.getClass() + " " + object);

      if (object instanceof Map) {
        vars = (Map<String, Object>) object;
      }
      else if (object instanceof JsonObject) {
        vars = ((JsonObject) object).toMap();
      }
    }

    return value;
  }

}