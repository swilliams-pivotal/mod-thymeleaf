package org.vertx.thymeleaf.support;

import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Element;
import org.thymeleaf.processor.attr.AbstractTextChildModifierAttrProcessor;

public class VertxExampleAttributeProcessor extends AbstractTextChildModifierAttrProcessor {

  public VertxExampleAttributeProcessor() {
    super("example");
  }

  public int getPrecedence() {
    return 1;
  }

  @Override
  protected String getText(final Arguments arguments, final Element element, final String attributeName) {
    String value = element.getAttributeValue(attributeName);
    return "Hello, " + value + "!";
  }

}