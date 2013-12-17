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
    private Stack<Integer> arrayStack;

    private int count = 0;

    public JsonXMLEventReader(Reader reader) {
        try {
            this.parser = new JsonFactory().createParser(reader);
            this.eventStack = new Stack<>();
            this.fieldStack = new Stack<>();
            this.arrayStack = new Stack<>();
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

        count++;

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

                            String elementName = parser.getCurrentName() != null ? parser.getCurrentName() : fieldStack.peek();

                            eventStack.push(new JsonStartElement(elementName));
                        }

                        break;

                    case END_OBJECT: {

                        String elementName = parser.getCurrentName();

                        if (elementName != null) {

                            String pop = fieldStack.pop();
                            if (!pop.equals(elementName)) {
                                throw new IllegalStateException("Something fishy here!");
                            }

                            eventStack.push(new JsonEndElement(elementName));
                        } else if (!fieldStack.isEmpty()) {
                            eventStack.push(new JsonEndElement(fieldStack.peek()));
                        } else {
                            lookAhead();
                        }

                        break;
                    }

                    case FIELD_NAME:

                        fieldStack.push(parser.getCurrentName());

                        lookAhead();

                        break;

                    case VALUE_STRING:
                    case VALUE_TRUE:
                    case VALUE_FALSE:
                    case VALUE_NUMBER_INT:
                    case VALUE_NUMBER_FLOAT: {

                        String elementName = parser.getCurrentName();
                        if (elementName != null) {

                            String pop = fieldStack.pop();
                            if (!pop.equals(elementName)) {
                                throw new IllegalStateException("Something fishy here!");
                            }

                        } else {
                            elementName = fieldStack.peek();
                        }

                        eventStack.push(new JsonEndElement(elementName));
                        eventStack.push(new JsonCharacters(parser.getText()));
                        eventStack.push(new JsonStartElement(elementName));

                        break;
                    }

                    case VALUE_NULL:

                        fieldStack.pop();

                        eventStack.push(new JsonEndElement(parser.getCurrentName()));
                        eventStack.push(new JsonStartElement(parser.getCurrentName()));

                        break;

                    case START_ARRAY:

                        arrayStack.push(count);

                        lookAhead();

                        break;

                    case END_ARRAY:

                        String name = fieldStack.pop();
                        if (count == arrayStack.pop().intValue()) {
                            eventStack.push(new JsonEndElement(name));
                            eventStack.push(new JsonStartElement(name));
                        } else {
                            lookAhead();
                        }


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
