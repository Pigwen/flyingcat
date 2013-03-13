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
package org.maodian.flyingcat.xmpp;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * @author Cole Wen
 * 
 */
public class Roster implements Iterable<Contact> {
  private String version;
  private final Set<Contact> contacts = new HashSet<>();
  

  /**
   * @param version
   * @param contacts
   */
  public Roster(String version, Contact...contacts) {
    this.version = version;
    for (Contact c : contacts) {
      this.contacts.add(c);
    }
  }
  
  public Roster() {
    this(null);
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }
  
  public void addContact(Contact contact) {
    contacts.add(contact);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Iterable#iterator()
   */
  @Override
  public Iterator<Contact> iterator() {
    return new RosterIter();
  }

  private class RosterIter implements Iterator<Contact> {
    private Iterator<Contact> iter = contacts.iterator();

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Iterator#hasNext()
     */
    @Override
    public boolean hasNext() {
      return iter.hasNext();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Iterator#next()
     */
    @Override
    public Contact next() {
      return iter.next();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Iterator#remove()
     */
    @Override
    public void remove() {
      iter.remove();
    }

  }
}
