package org.vertx.thymeleaf.support;

import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.dialect.AbstractDialect;
import org.thymeleaf.processor.IProcessor;

public class VertxDialect extends AbstractDialect {

  public VertxDialect() {
    super();
  }

  @Override
  public String getPrefix() {
    return "vertx";
  }

  @Override
  public boolean isLenient() {
    return true;
  }

  @Override
  public Set<IProcessor> getProcessors() {
    final Set<IProcessor> processors = new HashSet<IProcessor>();
    processors.add(new VertxExampleAttributeProcessor());
    processors.add(new VertxJSONAttributeProcessor());
    return processors;
  }

}