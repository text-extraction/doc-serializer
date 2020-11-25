package textextraction.serializer.exception;

/**
 * The exception to throw on any errors while serializing a file.
 * 
 * @author Claudius Korzen
 */
public class SerializerException extends Exception {
  /**
   * The serial id.
   */
  protected static final long serialVersionUID = -1208363363395692674L;

  /**
   * Creates a new serializer exception.
   * 
   * @param message The error message to show when the exception was caught.
   */
  public SerializerException(String message) {
    super(message);
  }

  /**
   * Creates a new serializer exception.
   * 
   * @param message The error message to show when the exception was caught.
   * @param cause   The cause of this exception (this can be used to trace the error).
   */
  public SerializerException(String message, Throwable cause) {
    super(message, cause);
  }
}
