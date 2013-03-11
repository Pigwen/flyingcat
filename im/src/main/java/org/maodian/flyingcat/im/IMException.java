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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Cole Wen
 *
 */
public class IMException extends Exception {
  private static final long serialVersionUID = 6622896641321176634L;
  private final ErrorCode errorCode;
  private final Map<String, Object> properties = new HashMap<>();
  
  public IMException(String message, Throwable cause, ErrorCode errorCode) {
    super(message, cause);
    this.errorCode = errorCode;
  }
  
  public IMException(String message, ErrorCode errorCode) {
    super(message);
    this.errorCode = errorCode;
  }
  
  public IMException(ErrorCode errorCode) {
    super();
    this.errorCode = errorCode;
  }

  public IMException(Throwable cause, ErrorCode errorCode) {
    super(cause);
    this.errorCode = errorCode;
  }

  public ErrorCode getErrorCode() {
    return errorCode;
  }
  
  public IMException set(String key, Object value) {
    properties.put(key, value);
    return this;
  }
}
