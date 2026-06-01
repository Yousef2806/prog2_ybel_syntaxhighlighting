package highlighting.regex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import highlighting.core.HighlightRegion;
import highlighting.presets.MiniJavaColours;
import java.util.List;
import org.junit.jupiter.api.Test;

class RegexHighlighterTest {

  @Test
  void collectMatchesFindsKeywords() {
    RegexHighlighter highlighter = new RegexHighlighter();

    List<HighlightRegion> regions =
        highlighter.collectMatches("public class Test { return null; }");

    assertEquals(4, regions.size());
  }

  @Test
  void computeRegionsFindsSimpleTokens() {
    RegexHighlighter highlighter = new RegexHighlighter();

    List<HighlightRegion> regions = highlighter.computeRegions("\"hello\" 'a' public");

    assertEquals(3, regions.size());
  }

  @Test
  void keywordInsideLineCommentIsIgnored() {
    RegexHighlighter highlighter = new RegexHighlighter();

    String text = "// public class return";
    List<HighlightRegion> regions = highlighter.computeRegions(text);

    assertEquals(1, regions.size());
    assertEquals(MiniJavaColours.LINE_COMMENT_COLOUR, regions.get(0).colour());
  }

  @Test
  void javadocIsNotNormalBlockComment() {
    RegexHighlighter highlighter = new RegexHighlighter();

    List<HighlightRegion> regions = highlighter.computeRegions("/** public class */");

    assertEquals(1, regions.size());
    assertEquals(MiniJavaColours.JAVADOC_COMMENT_COLOUR, regions.get(0).colour());
  }

  @Test
  void touchingRegionsAreAllowed() {
    RegexHighlighter highlighter = new RegexHighlighter();

    HighlightRegion first = new HighlightRegion(0, 5, MiniJavaColours.KEYWORD_COLOUR);
    HighlightRegion second = new HighlightRegion(5, 10, MiniJavaColours.STRING_LITERAL_COLOUR);

    List<HighlightRegion> regions = highlighter.resolveConflicts(List.of(first, second));

    assertEquals(2, regions.size());
  }

  @Test
  void overlappingRegionIsRemoved() {
    RegexHighlighter highlighter = new RegexHighlighter();

    HighlightRegion first = new HighlightRegion(0, 10, MiniJavaColours.LINE_COMMENT_COLOUR);
    HighlightRegion second = new HighlightRegion(3, 8, MiniJavaColours.KEYWORD_COLOUR);

    List<HighlightRegion> regions = highlighter.resolveConflicts(List.of(first, second));

    assertEquals(1, regions.size());
    assertEquals(first, regions.get(0));
  }

  @Test
  void emptyTextHasNoRegions() {
    RegexHighlighter highlighter = new RegexHighlighter();

    List<HighlightRegion> regions = highlighter.computeRegions("");

    assertTrue(regions.isEmpty());
  }

  @Test
  void textWithoutTokensHasNoRegions() {
    RegexHighlighter highlighter = new RegexHighlighter();

    List<HighlightRegion> regions = highlighter.computeRegions("abc_xyz += ===");

    assertTrue(regions.isEmpty());
  }
}
