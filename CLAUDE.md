# Notes for AI assistants

This file is a map of the repository for AI coding agents (Claude Code,
Copilot, Cursor, etc.).

## What this project is

A Java library that adapts a JSON document into a
`javax.xml.stream.XMLEventReader`, so tools that consume StAX events
(dom4j, JAXB, your own walker) can read JSON as if it were XML.

The public entry point is `org.brylex.stax4json.JsonXMLEventReader`.
Construct it with a `java.io.Reader` over JSON text, then drive it with
the standard `XMLEventReader` API (`hasNext()` / `nextEvent()` /
`peek()` / `close()`).

## Mapping rules

The reader translates Jackson `JsonToken`s into StAX events:

- The first `START_OBJECT` becomes `StartDocument`. A matching final
  `END_OBJECT` becomes `EndDocument`.
- Nested objects become `StartElement` / `EndElement` pairs named after
  the enclosing field. The root key-set is *flattened* into the document
  root, so `{"firstName": "rune"}` yields a `<firstName>` root element
  rather than wrapping it in a synthetic outer element.
- Scalar values (`VALUE_STRING`, `VALUE_NUMBER_INT`,
  `VALUE_NUMBER_FLOAT`, `VALUE_TRUE`, `VALUE_FALSE`) become
  `<field>text</field>` triples.
- `null` values emit an empty element (`<field/>`).
- Arrays repeat the surrounding element once per item:
  `{"id": [1,2,3]}` produces three `<id>` elements as siblings.

## Architecture

`JsonXMLEventReader` reads ahead from the Jackson `JsonParser` and
pushes `XMLEvent` instances onto an internal stack which `nextEvent()`
pops. `lookAhead()` is the engine — it consumes one Jackson token at a
time and decides which (zero or more) `XMLEvent`s to push.

Supporting types are local minimal `XMLEvent` implementations:

- `JsonStartDocument`, `JsonEndDocument` — document boundaries
- `JsonStartElement`, `JsonEndElement` — element boundaries (no
  attributes, no namespaces)
- `JsonCharacters` — text content for scalar values
- `JsonXMLEvent` — shared base with the boring `XMLEvent` boilerplate

`fieldStack` tracks open field names so nested JSON properties can emit
the right element close events. `arrayStack` records the `count` of
events emitted when an array started, which is how the reader knows
whether to emit a sibling close/open pair on `END_ARRAY` (empty array →
nothing to repeat).

## Build and test

```bash
mvn verify   # compile + tests
mvn test     # tests only
```

Java 21, Maven 3.9+. Tests use JUnit 5 + AssertJ; XML assertions are
done with dom4j XPath.

## House rules for changes

- Keep `JsonXMLEventReader` a plain `XMLEventReader` — callers depend on
  the StAX contract. New behaviour should fit inside the existing
  `lookAhead()` state machine rather than as a separate API surface.
- Several `XMLEventReader` methods are intentionally
  `UnsupportedOperationException` (`next()`, `nextTag()`,
  `getElementText()`, `getProperty()`, `remove()`). Don't implement them
  speculatively; only flesh them out if a real caller needs them, and
  add a test that pins down the behaviour first.
- Don't reintroduce JUnit 4 or FEST-assert. Tests are JUnit Jupiter +
  AssertJ.
- The artifact is published as `org.brylex:stax4json`; don't rename
  public types without a reason.

## Known rough edges

- The internal stacks use `java.util.Stack` (legacy synchronized class).
  Replacing them with `ArrayDeque` is fine if you also keep the push/pop
  semantics — there's no concurrency story here, the reader is single
  threaded.
- The exception message on a field-stack mismatch is the cheerful
  `"Something fishy here!"`. Leave it unless you're also adding a test
  that pins down what "fishy" means in practice.
