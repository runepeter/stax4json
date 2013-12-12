package org.brylex.stax4json;

import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.STAXEventReader;
import org.dom4j.io.XMLWriter;
import org.junit.Test;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.EndDocument;
import javax.xml.stream.events.StartDocument;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import static org.fest.assertions.Assertions.assertThat;

public class JsonXMLEventReaderTest {

    @Test
    public void testEmptyJson() throws Exception {

        String json = "{}";

        try (Reader reader = new StringReader(json)) {

            XMLEventReader parser = new JsonXMLEventReader(reader);

            assertThat(parser.hasNext()).isTrue();
            assertThat(parser.nextEvent()).isInstanceOf(StartDocument.class);
            assertThat(parser.nextEvent()).isInstanceOf(EndDocument.class);
        }
    }

    @Test
    public void testSingleElementDocument() throws Exception {

        String json = "{\"firstName\": \"rune\"}";

        try (Reader reader = new StringReader(json)) {

            XMLEventReader parser = new JsonXMLEventReader(reader);

            Document document = new STAXEventReader().readDocument(parser);
            print(document);

            assertThat(document.createXPath("/firstName").selectSingleNode(document).getText()).isEqualTo("rune");
            assertThat(document.createXPath("//node()").selectNodes(document)).hasSize(2);
        }
    }

    @Test
    public void testSingleRootWithSingleChildElement() throws Exception {

        String json = "{\"person\": {\"firstName\": \"rune\"}}";

        try (Reader reader = new StringReader(json)) {

            XMLEventReader parser = new JsonXMLEventReader(reader);

            Document document = new STAXEventReader().readDocument(parser);
            print(document);

            assertThat(document.createXPath("/person/firstName").selectSingleNode(document).getText()).isEqualTo("rune");
            assertThat(document.createXPath("//node()").selectNodes(document)).hasSize(3);
        }
    }

    @Test
    public void testRootWithMultipleChildren() throws Exception {

        String json = "{\"person\": {\"firstName\": \"rune\", \"lastName\": \"bjornstad\"}}";

        try (Reader reader = new StringReader(json)) {

            XMLEventReader parser = new JsonXMLEventReader(reader);

            Document document = new STAXEventReader().readDocument(parser);
            print(document);

            assertThat(document.createXPath("/person/firstName").selectSingleNode(document).getText()).isEqualTo("rune");
            assertThat(document.createXPath("/person/lastName").selectSingleNode(document).getText()).isEqualTo("bjornstad");
            assertThat(document.createXPath("//node()").selectNodes(document)).hasSize(5);
        }
    }

    public void print(Node node) {
        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setNewLineAfterDeclaration(false);

            XMLWriter writer = new XMLWriter(System.out, format);
            writer.write(node);
        } catch (IOException e) {
            throw new RuntimeException("Unable to serialize node.", e);
        }

    }

}
