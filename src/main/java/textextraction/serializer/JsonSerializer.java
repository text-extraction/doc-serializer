package textextraction.serializer;

import static textextraction.serializer.SerializerConstants.B;
import static textextraction.serializer.SerializerConstants.CHARACTER;
import static textextraction.serializer.SerializerConstants.CHARACTERS;
import static textextraction.serializer.SerializerConstants.COLOR;
import static textextraction.serializer.SerializerConstants.COLORS;
import static textextraction.serializer.SerializerConstants.DEFAULT_ENCODING;
import static textextraction.serializer.SerializerConstants.FIGURE;
import static textextraction.serializer.SerializerConstants.FIGURES;
import static textextraction.serializer.SerializerConstants.FONT;
import static textextraction.serializer.SerializerConstants.FONTS;
import static textextraction.serializer.SerializerConstants.FONTSIZE;
import static textextraction.serializer.SerializerConstants.G;
import static textextraction.serializer.SerializerConstants.HEIGHT;
import static textextraction.serializer.SerializerConstants.ID;
import static textextraction.serializer.SerializerConstants.IS_BOLD;
import static textextraction.serializer.SerializerConstants.IS_ITALIC;
import static textextraction.serializer.SerializerConstants.MAX_X;
import static textextraction.serializer.SerializerConstants.MAX_Y;
import static textextraction.serializer.SerializerConstants.MIN_X;
import static textextraction.serializer.SerializerConstants.MIN_Y;
import static textextraction.serializer.SerializerConstants.NAME;
import static textextraction.serializer.SerializerConstants.PAGE;
import static textextraction.serializer.SerializerConstants.PAGES;
import static textextraction.serializer.SerializerConstants.POSITION;
import static textextraction.serializer.SerializerConstants.R;
import static textextraction.serializer.SerializerConstants.SHAPE;
import static textextraction.serializer.SerializerConstants.SHAPES;
import static textextraction.serializer.SerializerConstants.TEXT;
import static textextraction.serializer.SerializerConstants.WIDTH;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import textextraction.common.models.Character;
import textextraction.common.models.Color;
import textextraction.common.models.Document;
import textextraction.common.models.ElementClass;
import textextraction.common.models.Figure;
import textextraction.common.models.Font;
import textextraction.common.models.FontFace;
import textextraction.common.models.Page;
import textextraction.common.models.Position;
import textextraction.common.models.Rectangle;
import textextraction.common.models.Shape;
import textextraction.serializer.exception.SerializerException;


/**
 * A serializer to serialize a document in JSON format.
 *
 * @author Claudius Korzen
 */
public class JsonSerializer implements Serializer {
  /**
   * The indentation length.
   */
  protected static final int INDENT_LENGTH = 2;

  /**
   * The fonts of the document elements which were in fact serialized.
   */
  protected Set<Font> usedFonts;

  /**
   * The colors of the document elements which were in fact serialized.
   */
  protected Set<Color> usedColors;

  // ==============================================================================================
  // Constructors.

  /**
   * Creates a new serializer that serializes a document in JSON format.
   */
  public JsonSerializer() {
    this.usedFonts = new HashSet<>();
    this.usedColors = new HashSet<>();
  }

  // ==============================================================================================

  @Override
  public byte[] serialize(Document doc) throws SerializerException {
    return serialize(doc, ElementClass.getElementClasses());
  }

  @Override
  public byte[] serialize(Document doc, Collection<ElementClass> clazzes)
          throws SerializerException {
    if (doc == null) {
      return null;
    }

    // The JSON object to serialize the document.
    JSONObject json = new JSONObject();

    // Serialize the elements.
    serializeElements(doc, json, clazzes);

    // Serialize the used fonts.
    JSONArray fontsJson = serializeFonts(this.usedFonts);
    if (fontsJson != null && fontsJson.length() > 0) {
      json.put(FONTS, fontsJson);
    }

    // Serialize the used colors.
    JSONArray colorsJson = serializeColors(this.usedColors);
    if (colorsJson != null && colorsJson.length() > 0) {
      json.put(COLORS, colorsJson);
    }

    // Serialize the metadata of the pages.
    JSONArray pagesJson = serializePages(doc.getPages());
    if (pagesJson != null && pagesJson.length() > 0) {
      json.put(PAGES, pagesJson);
    }

    try {
      // Serialize the JSON object.
      return json.toString(INDENT_LENGTH).getBytes(DEFAULT_ENCODING);
    } catch (UnsupportedEncodingException e) {
      throw new SerializerException("Couldn't serialize the document.", e);
    }
  }

  // ==============================================================================================

  /**
   * Serializes the document elements of the given types and writes them to the given JSON object.
   * 
   * @param doc     The document to process.
   * @param json    The JSON object to write the serialization to.
   * @param clazzes The types of elements to serialize.
   */
  public void serializeElements(Document doc, JSONObject json, Collection<ElementClass> clazzes) {
    for (ElementClass clazz : clazzes) {
      switch (clazz) {
        case CHARACTERS:
          serializeCharacters(doc, json);
          break;
        case FIGURES:
          serializeFigures(doc, json);
          break;
        case SHAPES:
          serializeShapes(doc, json);
          break;
        default:
          break;
      }
    }
  }

  // ==============================================================================================

  /**
   * Serializes the characters of the given document and writes them to the given JSON object.
   * 
   * @param doc  The document to process.
   * @param json The JSON object to write the serialization to.
   */
  protected void serializeCharacters(Document doc, JSONObject json) {
    JSONArray result = new JSONArray();

    if (doc != null) {
      for (Page page : doc.getPages()) {
        for (Character character : page.getCharacters()) {
          JSONObject characterJson = serializeCharacter(character);
          if (characterJson != null) {
            result.put(characterJson);
          }
        }
      }
    }

    json.put(CHARACTERS, result);
  }

  /**
   * Serializes the given character.
   * 
   * @param character The character to serialize.
   *
   * @return A JSON object representing the serialized character.
   */
  protected JSONObject serializeCharacter(Character character) {
    if (character == null) {
      return null;
    }

    JSONObject charJson = new JSONObject();

    // Serialize the position.
    JSONObject serialized = serializePosition(character.getPosition());
    if (serialized != null && serialized.length() > 0) {
      charJson.put(POSITION, serialized);
    }

    // Serialize the font face.
    FontFace fontFace = character.getFontFace();
    if (fontFace != null) {
      Font font = fontFace.getFont();
      float size = fontFace.getFontSize();
      if (font != null) {
        String fontId = font.getId();
        if (fontId != null && size > 0) {
          JSONObject fontJson = new JSONObject();
          fontJson.put(ID, fontId);
          fontJson.put(FONTSIZE, size);
          charJson.put(FONT, fontJson);
          this.usedFonts.add(font);
        }
      }
    }

    // Serialize the color.
    Color color = character.getColor();
    if (color != null) {
      String colorId = color.getId();
      if (colorId != null) {
        JSONObject colorJson = new JSONObject();
        colorJson.put(ID, colorId);
        charJson.put(COLOR, colorJson);
        this.usedColors.add(color);
      }
    }

    // Serialize the text.
    String text = character.getText();
    if (text != null) {
      charJson.put(TEXT, text);
    }

    JSONObject result = new JSONObject();
    if (charJson != null && charJson.length() > 0) {
      result.put(CHARACTER, charJson);
    }

    return result;
  }

  // ==============================================================================================

  /**
   * Serializes the figures of the given document and writes them to the given JSON object.
   * 
   * @param doc  The document to process.
   * @param json The JSON object to write the serialization to.
   */
  protected void serializeFigures(Document doc, JSONObject json) {
    JSONArray result = new JSONArray();

    if (doc != null) {
      for (Page page : doc.getPages()) {
        for (Figure figure : page.getFigures()) {
          JSONObject figureJson = serializeFigure(figure);
          if (figureJson != null) {
            result.put(figureJson);
          }
        }
      }
    }

    json.put(FIGURES, result);
  }

  /**
   * Serializes the given figure.
   * 
   * @param character The figure to serialize.
   *
   * @return A JSON object representing the serialized figure.
   */
  protected JSONObject serializeFigure(Figure figure) {
    if (figure == null) {
      return null;
    }

    JSONObject figureJson = new JSONObject();

    // Serialize the position.
    JSONObject serialized = serializePosition(figure.getPosition());
    if (serialized != null && serialized.length() > 0) {
      figureJson.put(POSITION, serialized);
    }

    JSONObject result = new JSONObject();
    if (figureJson != null && figureJson.length() > 0) {
      result.put(FIGURE, figureJson);
    }

    return result;
  }

  // ==============================================================================================

  /**
   * Serializes the shapes of the given document and writes them to the given JSON object.
   * 
   * @param doc  The document to process.
   * @param json The JSON object to write the serialization to.
   */
  protected void serializeShapes(Document doc, JSONObject json) {
    JSONArray result = new JSONArray();

    if (doc != null) {
      for (Page page : doc.getPages()) {
        for (Shape shape : page.getShapes()) {
          JSONObject shapeJson = serializeShape(shape);
          if (shapeJson != null) {
            result.put(shapeJson);
          }
        }
      }
    }

    json.put(SHAPES, result);
  }

  /**
   * Serializes the given shape.
   * 
   * @param shape The shape to serialize.
   *
   * @return A JSON object representing the serialized shape.
   */
  protected JSONObject serializeShape(Shape shape) {
    JSONObject shapeJson = new JSONObject();

    if (shape != null) {
      // Serialize the position.
      JSONObject serialized = serializePosition(shape.getPosition());
      if (serialized != null && serialized.length() > 0) {
        shapeJson.put(POSITION, serialized);
      }

      // Serialize the color.
      Color color = shape.getColor();
      if (color != null) {
        String colorId = color.getId();
        if (colorId != null) {
          JSONObject colorJson = new JSONObject();
          colorJson.put(ID, colorId);
          shapeJson.put(COLOR, colorJson);
          this.usedColors.add(color);
        }
      }
    }

    JSONObject result = new JSONObject();
    if (shapeJson != null && shapeJson.length() > 0) {
      result.put(SHAPE, shapeJson);
    }

    return result;
  }

  // ==============================================================================================

  /**
   * Serializes the given position.
   * 
   * @param position The position to serialize.
   * 
   * @return A JSON object representing the serialized position.
   */
  protected JSONObject serializePosition(Position position) {
    if (position == null) {
      return null;
    }

    JSONObject positionJson = new JSONObject();

    Page page = position.getPage();
    int pageNumber = page.getPageNumber();
    Rectangle rect = position.getRectangle();

    if (pageNumber > 0 && rect != null) {
      positionJson.put(PAGE, pageNumber);

      // If we pass primitive floats here, the values would be casted to
      // double values (yielding in inaccurate numbers). So transform the
      // values to Float objects.
      positionJson.put(MIN_X, Float.valueOf(rect.getMinX()));
      positionJson.put(MIN_Y, Float.valueOf(rect.getMinY()));
      positionJson.put(MAX_X, Float.valueOf(rect.getMaxX()));
      positionJson.put(MAX_Y, Float.valueOf(rect.getMaxY()));
    }

    return positionJson;
  }

  // ==============================================================================================

  /**
   * Serializes the given fonts.
   * 
   * @param fonts The fonts to serialize.
   * 
   * @return A JSON array representing the serialized fonts.
   */
  protected JSONArray serializeFonts(Set<Font> fonts) {
    JSONArray result = new JSONArray();
    if (fonts != null) {
      for (Font font : fonts) {
        JSONObject fontJson = serializeFont(font);
        if (fontJson != null && fontJson.length() > 0) {
          result.put(fontJson);
        }
      }
    }
    return result;
  }

  /**
   * Serializes the given font.
   * 
   * @param font The font to serialize.
   * 
   * @return A JSON object representing the serialized font.
   */
  protected JSONObject serializeFont(Font font) {
    JSONObject fontJson = new JSONObject();
    if (font != null) {
      String fontId = font.getId();
      if (fontId != null) {
        fontJson.put(ID, fontId);
      }

      String name = font.getName();
      if (name != null) {
        fontJson.put(NAME, name);
      }

      boolean isBold = font.isBold();
      fontJson.put(IS_BOLD, isBold);

      boolean isItalic = font.isItalic();
      fontJson.put(IS_ITALIC, isItalic);
    }
    return fontJson;
  }

  // ==============================================================================================
  // Methods to serialize colors.

  /**
   * Serializes the given colors.
   * 
   * @param colors The colors to serialize.
   * 
   * @return A JSON array representing the serialized fonts.
   */
  protected JSONArray serializeColors(Set<Color> colors) {
    JSONArray result = new JSONArray();

    if (colors != null) {
      for (Color color : colors) {
        if (color != null) {
          JSONObject colorJson = serializeColor(color);
          if (colorJson != null && colorJson.length() > 0) {
            result.put(colorJson);
          }
        }
      }
    }
    return result;
  }

  /**
   * Serializes the given color.
   * 
   * @param color The color to serialize.
   * 
   * @return A JSON object representing the serialized color.
   */
  protected JSONObject serializeColor(Color color) {
    JSONObject colorJson = new JSONObject();
    if (color != null) {
      String colorId = color.getId();
      int[] rgb = color.getRgb();

      if (colorId != null && rgb != null && rgb.length == 3) {
        colorJson.put(ID, colorId);
        colorJson.put(R, rgb[0]);
        colorJson.put(G, rgb[1]);
        colorJson.put(B, rgb[2]);
      }
    }
    return colorJson;
  }

  // ==============================================================================================
  // Methods to serialize the metadata of pages.

  /**
   * Serializes the given pages.
   * 
   * @param pages The pages to serialize.
   * 
   * @return A JSON array representing the serialized pages.
   */
  protected JSONArray serializePages(List<Page> pages) {
    JSONArray result = new JSONArray();

    if (pages != null) {
      for (Page page : pages) {
        if (page != null) {
          JSONObject pageJson = serializePage(page);
          if (pageJson != null && pageJson.length() > 0) {
            result.put(pageJson);
          }
        }
      }
    }

    return result;
  }

  /**
   * Serializes the given page.
   * 
   * @param page The page to serialize.
   * 
   * @return A JSON object representing the serialized page.
   */
  protected JSONObject serializePage(Page page) {
    JSONObject pageJson = new JSONObject();
    if (page != null) {
      pageJson.put(ID, page.getPageNumber());
      pageJson.put(WIDTH, page.getWidth());
      pageJson.put(HEIGHT, page.getHeight());
    }
    return pageJson;
  }
}
