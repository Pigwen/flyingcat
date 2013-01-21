/**
 * 
 */
package org.maodian.flycat.xmpp.schema;

import java.io.File;
import java.io.InputStream;
import java.net.URI;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Cole Wen
 *
 */
public class SchemaTest {

  private SchemaFactory scehmaFactory;
  
  @Before
  public void setup() {
    scehmaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
  }
  
  @Test
  public void validateStream() throws Exception {
    InputStream is = SchemaTest.class.getResourceAsStream("/schema/rfc6120/stream.xsd");
    Schema schema = scehmaFactory.newSchema(new StreamSource(is));
    Validator validator = schema.newValidator();
    
    URI dirURI = SchemaTest.class.getResource("stream").toURI();
    File file = new File(dirURI);
    System.out.println(file.getAbsolutePath());
    for (File xmlDoc : file.listFiles()) {
      validator.validate(new StreamSource(xmlDoc));
    }
  }
}
