package org.brylex.stax4json;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import java.io.Writer;

public class JsonCharacters extends JsonXMLEvent implements Characters {

    private final String data;

    public JsonCharacters(String data) {
        super(XMLStreamConstants.CHARACTERS);
        this.data = data;
    }

    @Override
    public boolean isCharacters() {
        return true;
    }

    @Override
    public Characters asCharacters() {
        return this;
    }

    @Override
    public String getData() {
        return data;
    }

    @Override
    public boolean isWhiteSpace() {
        return false;
    }

    @Override
    public boolean isCData() {
        return false;
    }

    @Override
    public boolean isIgnorableWhiteSpace() {
        return false;
    }

    @Override
    public String toString() {
        return data;
    }
}
