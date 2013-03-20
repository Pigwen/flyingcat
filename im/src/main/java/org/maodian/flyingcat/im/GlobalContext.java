/*
 * Copyright 2013 - 2013 Cole Wen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.maodian.flyingcat.im;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.maodian.flyingcat.im.template.Domain;
import org.maodian.flyingcat.im.template.Operation;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * @author Cole Wen
 * 
 */
@Component
class GlobalContext {
  private ApplicationContext appCtx;
  private ConcurrentMap<Key, Actor> templateMap = new ConcurrentHashMap<>();

  @Inject
  void setAppCtx(ApplicationContext appCtx) {
    this.appCtx = appCtx;
  }

  @PostConstruct
  void init() {
    Collection<Object> beans = appCtx.getBeansWithAnnotation(Domain.class).values();
    for (Object b : beans) {
      Type type = b.getClass().getAnnotation(Domain.class).value();
      Method[] methods = b.getClass().getMethods();
      for (Method m : methods) {
        if (m.isAnnotationPresent(Operation.class)) {
          Verb verb = m.getAnnotation(Operation.class).value();
          Key key = new Key(verb, type);
          Actor actor = new Actor(b, m);
          templateMap.put(key, actor);
        }
      }
    }
  }

  public Actor getTemlateActor(Verb verb, Type type) {
    Key key = new Key(verb, type);
    Actor actor = templateMap.get(key);
    if (actor == null) {
      throw new IllegalStateException("Can't find template method with Key: " + key.toString());
    }
    return actor;
  }

  private static class Key {
    private final Verb verb;
    private final Type type;

    /**
     * @param verb
     * @param type
     */
    public Key(Verb verb, Type type) {
      this.verb = verb;
      this.type = type;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((type == null) ? 0 : type.hashCode());
      result = prime * result + ((verb == null) ? 0 : verb.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      Key other = (Key) obj;
      if (type != other.type)
        return false;
      if (verb != other.verb)
        return false;
      return true;
    }

    @Override
    public String toString() {
      return "Key [verb=" + verb + ", type=" + type + "]";
    }

  }

  public static class Actor {
    private final Object obj;
    private final Method method;

    /**
     * @param obj
     * @param method
     */
    public Actor(Object obj, Method method) {
      this.obj = obj;
      this.method = method;
    }

    public Object action(Object data, Object target) {
      try {
        return target == null ? method.invoke(obj, data) : method.invoke(obj, data, target);
      } catch (IllegalAccessException | IllegalArgumentException e) {
        throw new IMException(e, GenericError.INTERNAL_ERROR);
      } catch (InvocationTargetException e) {
        Throwable cause = e.getCause();
        if (cause instanceof IMException) {
          throw (IMException) cause;
        }
        throw new IMException(e, GenericError.INTERNAL_ERROR);
      }
    }

    public Object action(Object data) {
      return action(data, null);
    }
  }
}
