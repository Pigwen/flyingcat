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
package org.maodian.flyingcat.xmpp.state;

/**
 * @author Cole Wen
 *
 */
final class DefaultResult implements Result {
  private final State nextState;
  private final String data;
  
  /**
   * @param nextState
   * @param data
   */
  public DefaultResult(State nextState, String data) {
    this.nextState = nextState;
    this.data = data;
  }

  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.state.Result#getNextState()
   */
  @Override
  public State getNextState() {
    return nextState;
  }

  /* (non-Javadoc)
   * @see org.maodian.flycat.xmpp.state.Result#getData()
   */
  @Override
  public String getData() {
    return data;
  }

}
