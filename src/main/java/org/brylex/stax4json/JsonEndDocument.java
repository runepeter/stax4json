package org.brylex.stax4json;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndDocument;
import java.io.IOException;
import java.io.Writer;

public class JsonEndDocument extends JsonXMLEvent implements EndDocument {

    public JsonEndDocument() {
        super(XMLStreamConstants.END_DOCUMENT);
    }

    @Override
    public boolean isEndDocument() {
        return true;
    }

    @Override
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        try {
            writer.flush();
        } catch (IOException e) {
            throw new XMLStreamException("Unable to serialize event to Writer.", e);
        }
    }
}
