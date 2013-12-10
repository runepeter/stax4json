package org.brylex.stax4json;

import org.junit.Test;

import javax.xml.stream.events.EndDocument;
import javax.xml.stream.events.StartDocument;
import java.io.Reader;
import java.io.StringReader;

import static org.fest.assertions.Assertions.assertThat;

public class JsonXMLEventReaderTest {

    @Test
    public void testName() throws Exception {

        String json = "{}";

        try (Reader reader = new StringReader(json)) {

            JsonXMLEventReader parser = new JsonXMLEventReader(reader);

            assertThat(parser.hasNext()).isTrue();
            assertThat(parser.nextEvent()).isInstanceOf(StartDocument.class);
            assertThat(parser.nextEvent()).isInstanceOf(EndDocument.class);
        }
    }

}
