package org.brylex.stax4json;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import java.io.IOException;
import java.io.Writer;

public class JsonStartElement extends JsonXMLEvent implements StartElement {

    private final QName qname;

    public JsonStartElement(String elementName) {
        super(XMLStreamConstants.START_ELEMENT);
        this.qname = new QName(elementName);
    }

    @Override
    public QName getName() {
        return qname;
    }

    @Override
    public boolean isStartElement() {
        return true;
    }

    @Override
    public StartElement asStartElement() {
        return this;
    }

    @Override
    public String toString() {
        return "<" + qname.getLocalPart() + ">";
    }
}
