package org.brylex.stax4json;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartDocument;
import java.io.IOException;
import java.io.Writer;

public class JsonStartDocument extends JsonXMLEvent implements StartDocument {

    public JsonStartDocument() {
        super(XMLStreamConstants.START_DOCUMENT);
    }

    @Override
    public boolean isStartDocument() {
        return true;
    }

    @Override
    public String getSystemId() {
        return "";
    }

    @Override
    public String getCharacterEncodingScheme() {
        return "utf-8";
    }

    @Override
    public boolean encodingSet() {
        return false;
    }

    @Override
    public boolean isStandalone() {
        return true;
    }

    @Override
    public boolean standaloneSet() {
        return false;
    }

    @Override
    public String getVersion() {
        return "1.0";
    }

    @Override
    public String toString() {
        return "<?xml version=\"" + getVersion() + "\" encoding=\"" + getCharacterEncodingScheme() + "\" standalone=\"" + isStandalone() + "\"?>";
    }
}
