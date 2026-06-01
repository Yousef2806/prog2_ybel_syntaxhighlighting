package highlighting.presets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import highlighting.core.HighlightRegion;
import java.awt.Color;
import java.util.List;
import org.junit.jupiter.api.Test;

class MiniJavaTokensTest {

    private List<HighlightRegion> regionsForColour(String text, Color colour) {
        return MiniJavaTokens.defaultTokens().stream()
            .flatMap(token -> token.test(text).stream())
            .filter(region -> region.colour().equals(colour))
            .toList();
    }

    @Test
    void stringLiteralMatchesAtBeginningMiddleAndEnd() {
        var text = "\"start\" int x = \"middle\"; \"end\"";
        var regions = regionsForColour(text, MiniJavaColours.STRING_LITERAL_COLOUR);

        assertEquals(3, regions.size());
        assertEquals("\"start\"", text.substring(regions.get(0).start(), regions.get(0).end()));
        assertEquals("\"middle\"", text.substring(regions.get(1).start(), regions.get(1).end()));
        assertEquals("\"end\"", text.substring(regions.get(2).start(), regions.get(2).end()));
    }

    @Test
    void characterLiteralMatchesSingleCharacter() {
        var regions = regionsForColour("char a = 'x'; char b = '\\n';", MiniJavaColours.CHAR_LITERAL_COLOUR);

        assertEquals(2, regions.size());
    }

    @Test
    void keywordMatchesOnlyWholeWords() {
        var regions =
            regionsForColour(
                "class public private return null new newValue classroom", MiniJavaColours.KEYWORD_COLOUR);

        assertEquals(6, regions.size());
    }

    @Test
    void annotationMatchesWithMinus() {
        var regions = regionsForColour("@Override\n  @Over-ride", MiniJavaColours.ANNOTATION_COLOUR);

        assertEquals(2, regions.size());
    }

    @Test
    void lineCommentMatchesUntilLineEnd() {
        var text = "// return new class\npublic class Test";
        var regions = regionsForColour(text, MiniJavaColours.LINE_COMMENT_COLOUR);

        assertEquals(1, regions.size());
        assertEquals("// return new class", text.substring(regions.get(0).start(), regions.get(0).end()));
    }

    @Test
    void blockCommentCanContainKeywordLikeText() {
        var text = "/* public class return */";
        var regions = regionsForColour(text, MiniJavaColours.BLOCK_COMMENT_COLOUR);

        assertEquals(1, regions.size());
        assertEquals(text, text.substring(regions.get(0).start(), regions.get(0).end()));
    }

    @Test
    void javadocCommentMatchesSeparately() {
        var regions = regionsForColour("/** public class return */", MiniJavaColours.JAVADOC_COMMENT_COLOUR);

        assertEquals(1, regions.size());
    }

    @Test
    void stringCanContainCommentLikeText() {
        var text = "\"not a // comment and not a /* block */\"";
        var regions = regionsForColour(text, MiniJavaColours.STRING_LITERAL_COLOUR);

        assertEquals(1, regions.size());
        assertEquals(text, text.substring(regions.get(0).start(), regions.get(0).end()));
    }

    @Test
    void noTokenMatchReturnsEmptyListForUnknownText() {
        var allRegions =
            MiniJavaTokens.defaultTokens().stream()
                .flatMap(token -> token.test("xyz_abc +=").stream())
                .toList();

        assertTrue(allRegions.isEmpty());
    }
}
