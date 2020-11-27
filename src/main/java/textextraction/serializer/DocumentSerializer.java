package textextraction.serializer;

import java.util.Collection;

import textextraction.common.models.Document;
import textextraction.common.models.ElementClass;
import textextraction.serializer.exception.SerializerException;
import textextraction.serializer.model.SerializationFormat;

/**
 * A serializer to serialize a document.
 *
 * @author Claudius Korzen
 */
public class DocumentSerializer {
  /**
   * Serializes *all* elements of the given document in the given format.
   * 
   * @param doc    The document to serialize.
   * @param format The format of the serialization.
   * 
   * @return The serialization as a byte array.
   * 
   * @throws SerializerException If something went wrong on serializing the document.
   */
  byte[] serialize(Document doc, SerializationFormat format) throws SerializerException {
    return serialize(doc, format, ElementClass.getElementClasses());
  }

  /**
   * Serializes the elements with the given types of the given document in the given format.
   * 
   * @param doc     The document to serialize.
   * @param format  The format of the serialization.
   * @param clazzes The types of elements to serialize from the document.
   * 
   * @return The serialization as a byte array.
   * 
   * @throws SerializerException If something went wrong on serializing the document.
   */
  byte[] serialize(Document doc, SerializationFormat format, Collection<ElementClass> clazzes)
          throws SerializerException {
    switch (format) {
      case XML:
        return new XmlDocumentSerializer().serialize(doc, clazzes);
      case JSON:
      default:
        return new JsonDocumentSerializer().serialize(doc, clazzes);
    }
  }
}
