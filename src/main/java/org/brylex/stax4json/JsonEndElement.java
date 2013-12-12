package org.brylex.stax4json;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.events.EndElement;

public class JsonEndElement extends JsonXMLEvent implements EndElement {

    private final QName qname;

    public JsonEndElement(String elementName) {
        super(XMLStreamConstants.END_ELEMENT);
        this.qname = new QName(elementName);
    }

    @Override
    public QName getName() {
        return qname;
    }

    @Override
    public boolean isEndElement() {
        return true;
    }

    @Override
    public EndElement asEndElement() {
        return this;
    }

    @Override
    public String toString() {
        return "</" + qname.getLocalPart() + ">";
    }
}
