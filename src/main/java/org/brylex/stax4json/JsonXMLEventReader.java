package org.brylex.stax4json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndDocument;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.Reader;
import java.util.Stack;

public class JsonXMLEventReader implements XMLEventReader {

    private final JsonParser parser;
    private final Stack<XMLEvent> eventStack;

    private final StartDocument startDocument = new JsonStartDocument();
    private final EndDocument endDocument = new JsonEndDocument();

    private boolean initiated = false;
    private boolean finished = false;

    private Stack<String> fieldStack;

    public JsonXMLEventReader(Reader reader) {
        try {
            this.parser = new JsonFactory().createParser(reader);
            this.eventStack = new Stack<>();
            this.fieldStack = new Stack<>();
        } catch (IOException e) {
            throw new RuntimeException("Unable to create JSON parser.", e);
        }
    }

    @Override
    public XMLEvent nextEvent() throws XMLStreamException {

        if (!hasNext()) {
            throw new XMLStreamException("There's no more JSON tokens available form the underlying stream.");
        }

        XMLEvent pop = eventStack.pop();
        if (pop == endDocument) {
            finished = true;
        }

        return pop;
    }

    @Override
    public boolean hasNext() {

        if (finished) {
            return false;
        }

        if (eventStack.isEmpty()) {
            lookAhead();
        }

        return !eventStack.isEmpty();
    }

    private void lookAhead() {

        if (!eventStack.isEmpty() && eventStack.peek().equals(endDocument)) {
            return;
        }

        try {

            JsonToken currentToken = parser.nextToken();
            if (currentToken != null) {

                switch (currentToken) {
                    case START_OBJECT:

                        if (!initiated) {
                            initiated = true;
                            eventStack.push(startDocument);
                            return;
                        } else {

                            eventStack.push(new JsonStartElement(parser.getCurrentName()));

                        }

                        break;

                    case END_OBJECT:

                        String elementName = parser.getCurrentName();

                        if (elementName != null) {
                            eventStack.push(new JsonEndElement(elementName));
                        } else {
                            lookAhead();
                        }

                        break;

                    case FIELD_NAME:

                        lookAhead();

                        break;

                    case VALUE_STRING:

                        eventStack.push(new JsonEndElement(parser.getCurrentName()));
                        eventStack.push(new JsonCharacters(parser.getText()));
                        eventStack.push(new JsonStartElement(parser.getCurrentName()));

                        break;

                    default:
                        eventStack.push(new JsonCharacters(currentToken.toString()));
                }
            } else {
                eventStack.push(endDocument);
            }

        } catch (IOException e) {
            throw new RuntimeException("Unable to parse JSON Stream.", e);
        }
    }

    @Override
    public Object next() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public XMLEvent peek() throws XMLStreamException {

        if (!hasNext()) {
            return null;
        }

        return eventStack.peek();
    }

    @Override
    public String getElementText() throws XMLStreamException {
        throw new UnsupportedOperationException();
    }

    @Override
    public XMLEvent nextTag() throws XMLStreamException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getProperty(String name) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void close() throws XMLStreamException {
        try {
            parser.close();
        } catch (IOException e) {
            throw new XMLStreamException("Unable to close JSONParser.", e);
        }
    }
}
