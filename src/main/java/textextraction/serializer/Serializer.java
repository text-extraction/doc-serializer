package textextraction.serializer;

import java.util.Collection;
import textextraction.common.models.Document;
import textextraction.common.models.ElementClass;
import textextraction.serializer.exception.SerializerException;

/**
 * A serializer to serialize a document in a specific format.
 *
 * @author Claudius Korzen
 */
public interface Serializer {
  /**
   * Serializes *all* elements of the given document.
   * 
   * @param doc The document to serialize.
   * 
   * @return The serialization as a byte array.
   * 
   * @throws SerializerException If something went wrong on serializing the document.
   */
  byte[] serialize(Document doc) throws SerializerException;

  /**
   * Serializes the elements with the given types of the given document.
   * 
   * @param doc     The document to serialize.
   * @param clazzes The types of elements to serialize from the document.
   * 
   * @return The serialization as a byte array.
   * 
   * @throws SerializerException If something went wrong on serializing the document.
   */
  byte[] serialize(Document doc, Collection<ElementClass> clazzes) throws SerializerException;
}
