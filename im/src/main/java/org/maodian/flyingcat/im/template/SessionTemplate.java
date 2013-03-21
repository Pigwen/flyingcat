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
package org.maodian.flyingcat.im.template;

import java.lang.invoke.MethodHandles;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.maodian.flyingcat.im.IMException;
import org.maodian.flyingcat.im.Type;
import org.maodian.flyingcat.im.UserError;
import org.maodian.flyingcat.im.Verb;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author Cole Wen
 *
 */
@Component
@Domain(Type.SESSION)
public class SessionTemplate extends AbstractTemplate {
  private static final Logger log = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  
  @Operation(Verb.CREATE)
  public void login(UsernamePasswordToken token) {
    try {
      SecurityUtils.getSubject().login(token);
    } catch (AuthenticationException e) {
      log.warn("User [{}] login failed", token.getPrincipal());
      throw IMException.wrap(e, UserError.AUTHENTICATED_FAILS);
    }
  }
}
