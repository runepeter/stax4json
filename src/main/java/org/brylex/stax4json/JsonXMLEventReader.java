package org.brylex.stax4json;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.Reader;
import java.util.Stack;

public class JsonXMLEventReader implements XMLEventReader {

    private final JsonParser parser;
    private final Stack<JsonToken> stack;

    private String fieldName = null;

    public JsonXMLEventReader(Reader reader) {
        try {
            this.parser = new JsonFactory().createParser(reader);
            this.stack = new Stack<>();
        } catch (IOException e) {
            throw new RuntimeException("Unable to create JSON parser.", e);
        }
    }

    @Override
    public XMLEvent nextEvent() throws XMLStreamException {

        if (!hasNext()) {
            throw new XMLStreamException("There's no more JSON tokens available form the underlyng stream.");
        }

        try {
            JsonToken currentToken = stack.pop();

            switch (currentToken) {
                case START_OBJECT:

                    return new JsonStartDocument();

                case END_OBJECT:

                    return new JsonEndDocument();

                case FIELD_NAME:

                    this.fieldName = parser.getCurrentName();

                    break;
                default:
                    System.err.println("TOKEN: " + currentToken);
            }

            return null;
        } catch (IOException e) {
            throw new XMLStreamException("Unable to parse JSON Stream.", e);
        }
    }

    @Override
    public boolean hasNext() {

        if (!stack.isEmpty()) {
            return true;
        }

        JsonToken token = null;
        try {
            token = parser.nextToken();
            if (token == null) {
                return false;
            }
        } catch (IOException e) {
            throw new RuntimeException("Unable to check JSON stream for tokens.", e);
        }

        stack.push(token);

        return true;
    }

    @Override
    public Object next() {
        return null;
    }

    @Override
    public void remove() {

    }

    @Override
    public XMLEvent peek() throws XMLStreamException {
        return null;
    }

    @Override
    public String getElementText() throws XMLStreamException {
        return null;
    }

    @Override
    public XMLEvent nextTag() throws XMLStreamException {
        return null;
    }

    @Override
    public Object getProperty(String name) throws IllegalArgumentException {
        return null;
    }

    @Override
    public void close() throws XMLStreamException {

    }
}
