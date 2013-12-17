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

    @Test
    public void testBooleanElement() throws Exception {

        String json = "{\"profile\": {\"developer\": true, \"tester\": false}}";

        try (Reader reader = new StringReader(json)) {

            XMLEventReader parser = new JsonXMLEventReader(reader);

            Document document = new STAXEventReader().readDocument(parser);
            print(document);

            assertThat(document.createXPath("/profile/developer").selectSingleNode(document).getText()).isEqualTo("true");
            assertThat(document.createXPath("/profile/tester").selectSingleNode(document).getText()).isEqualTo("false");
        }
    }

    @Test
    public void testNullElement() throws Exception {

        String json = "{\"profile\": null}";

        try (Reader reader = new StringReader(json)) {

            XMLEventReader parser = new JsonXMLEventReader(reader);

            Document document = new STAXEventReader().readDocument(parser);
            print(document);

            assertThat(document.createXPath("/profile").selectNodes(document)).hasSize(1);
            assertThat(document.createXPath("/profile/child::*").selectNodes(document)).isEmpty();
        }
    }

    @Test
    public void testEmptyArrayElement() throws Exception {

        String json = "{\"person\": []}";

        try (Reader reader = new StringReader(json)) {

            XMLEventReader parser = new JsonXMLEventReader(reader);

            Document document = new STAXEventReader().readDocument(parser);
            print(document);

            assertThat(document.createXPath("/person").selectNodes(document)).hasSize(1);
            assertThat(document.createXPath("/person/child::*").selectNodes(document)).isEmpty();
        }
    }

    @Test
    public void testSingleElementArray() throws Exception {

        String json = "{\"person\": [{\"firstName\": \"rune\", \"lastName\": \"bjornstad\"}]}";

        try (Reader reader = new StringReader(json)) {

            XMLEventReader parser = new JsonXMLEventReader(reader);

            Document document = new STAXEventReader().readDocument(parser);
            print(document);

            assertThat(document.createXPath("/person/firstName").selectSingleNode(document).getText()).isEqualTo("rune");
            assertThat(document.createXPath("/person/lastName").selectSingleNode(document).getText()).isEqualTo("bjornstad");
            assertThat(document.createXPath("//node()").selectNodes(document)).hasSize(5);
        }
    }

    @Test
    public void testMultiElementArray() throws Exception {

        String json = "{\"data\": {\"person\": [{\"firstName\": \"rune\", \"lastName\": \"bjornstad\"}, {\"firstName\": \"steve\", \"lastName\": \"jobs\"}]}}";

        try (Reader reader = new StringReader(json)) {

            XMLEventReader parser = new JsonXMLEventReader(reader);

            Document document = new STAXEventReader().readDocument(parser);
            print(document);

            assertThat(document.createXPath("/data/person").selectNodes(document)).hasSize(2);
            assertThat(document.createXPath("/data/person[1]/firstName").selectSingleNode(document).getText()).isEqualTo("rune");
            assertThat(document.createXPath("/data/person[1]/lastName").selectSingleNode(document).getText()).isEqualTo("bjornstad");
            assertThat(document.createXPath("/data/person[2]/firstName").selectSingleNode(document).getText()).isEqualTo("steve");
            assertThat(document.createXPath("/data/person[2]/lastName").selectSingleNode(document).getText()).isEqualTo("jobs");
        }
    }

    @Test
        public void testMultiValueArray() throws Exception {

            String json = "{\"data\": {\"id\": [1, 2, 3]}}";

            try (Reader reader = new StringReader(json)) {

                XMLEventReader parser = new JsonXMLEventReader(reader);

                Document document = new STAXEventReader().readDocument(parser);
                print(document);

                assertThat(document.createXPath("/data/id").selectNodes(document)).hasSize(3);
                assertThat(document.createXPath("/data/id[1]").selectSingleNode(document).getText()).isEqualTo("1");
                assertThat(document.createXPath("/data/id[2]").selectSingleNode(document).getText()).isEqualTo("2");
                assertThat(document.createXPath("/data/id[3]").selectSingleNode(document).getText()).isEqualTo("3");
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
