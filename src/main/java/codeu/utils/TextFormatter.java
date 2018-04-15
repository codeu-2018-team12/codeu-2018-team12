package codeu.utils;

import com.vdurmont.emoji.EmojiParser;
import org.commonmark.node.*;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class TextFormatter {

  public static String formatForDisplay(String input) {
    // Parses input and replaces markdown characters with the appropriate HTML
    Parser parser = Parser.builder().build();
    Node document = parser.parse(input);
    HtmlRenderer renderer = HtmlRenderer.builder().build();
    String parsedInput = renderer.render(document);
    // Removes unneeded <p> tags
    int parsedInputLength = parsedInput.length();
    if (parsedInput.startsWith("<p>")) {
      parsedInput = parsedInput.substring(3, parsedInputLength - 5);
    }
    // Ensures that new line characters are displayed correctly
    parsedInput = parsedInput.replaceAll("(\r\n|\n\r|\r|\n)", "<br />");
    // Replaces emoji aliases with Hexadecimal values ready for display in HTML
    String finalOutput = EmojiParser.parseToUnicode(parsedInput);
    finalOutput = EmojiParser.parseToHtmlHexadecimal(finalOutput);
    return finalOutput;
  }
}
