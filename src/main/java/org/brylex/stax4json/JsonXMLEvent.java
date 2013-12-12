package org.brylex.stax4json;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.Iterator;

abstract class JsonXMLEvent implements XMLEvent {

    private final int eventType;

    public JsonXMLEvent(int eventType) {
        this.eventType = eventType;
    }

    public Iterator getAttributes() {
        return Collections.emptyIterator();
    }

    public Iterator getNamespaces() {
        return Collections.emptyIterator();
    }

    public Attribute getAttributeByName(QName name) {
        return null;
    }

    public NamespaceContext getNamespaceContext() {
        return new NamespaceContext() {
            @Override
            public String getNamespaceURI(String prefix) {
                return null;
            }

            @Override
            public String getPrefix(String namespaceURI) {
                return null;
            }

            @Override
            public Iterator getPrefixes(String namespaceURI) {
                return Collections.emptyIterator();
            }
        };
    }

    public String getNamespaceURI(String prefix) {
        return null;
    }

    public int getEventType() {
        return eventType;
    }

    public Location getLocation() {
        throw new UnsupportedOperationException();
    }

    public boolean isStartElement() {
        return false;
    }

    public boolean isAttribute() {
        return false;
    }

    public boolean isNamespace() {
        return false;
    }

    public boolean isEndElement() {
        return false;
    }

    public boolean isEntityReference() {
        return false;
    }

    public boolean isProcessingInstruction() {
        return false;
    }

    public boolean isCharacters() {
        return false;
    }

    public boolean isStartDocument() {
        return false;
    }

    public boolean isEndDocument() {
        return false;
    }

    public StartElement asStartElement() {
        throw new ClassCastException();
    }

    public EndElement asEndElement() {
        throw new ClassCastException();
    }

    public Characters asCharacters() {
        throw new ClassCastException();
    }

    public QName getSchemaType() {
        return null;
    }

    @Override
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            writer.write(toString());
        } catch (IOException e) {
            throw new XMLStreamException("Unable to write XMLEvent to Writer.", e);
        }
    }

}
