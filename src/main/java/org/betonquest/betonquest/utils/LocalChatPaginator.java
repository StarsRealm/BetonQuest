package org.betonquest.betonquest.utils;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Provide a slightly more intelligent wordwrap that will return the last last space if required
 * <p>
 * We also try to handle variable width characters.
 */
public final class LocalChatPaginator {
    /**
     * Pixel-length of characters in the default resource pack Minecraft font.
     * Only the most common characters are defined.
     */
    public static final Map<Character, Integer> FONT_SIZES;

    /**
     * Default assumption for pixel-length of characters that are not covered by {@link #FONT_SIZES}.
     */
    public static final int DEFAULT_CHAR_WIDTH = 6;

    /**
     * Pixel-length of a space character.
     */
    public static final int SPACE_WIDTH;

    static {
        FONT_SIZES = Stream.of(new Object[][]{
                {' ', 4}, {'!', 2}, {'"', 5}, {'#', 6}, {'$', 6}, {'%', 6}, {'&', 6}, {'\'', 3},
                {'(', 6}, {')', 6}, {'*', 5}, {'+', 6}, {',', 2}, {'-', 6}, {'.', 2}, {'/', 6},
                {'0', 6}, {'1', 6}, {'2', 6}, {'3', 6}, {'4', 6}, {'5', 6}, {'6', 6}, {'7', 6},
                {'8', 6}, {'9', 6}, {':', 2}, {';', 2}, {'<', 5}, {'=', 6}, {'>', 5}, {'?', 6},
                {'@', 7}, {'A', 6}, {'B', 6}, {'C', 6}, {'D', 6}, {'E', 6}, {'F', 6}, {'G', 6},
                {'H', 6}, {'I', 4}, {'J', 6}, {'K', 6}, {'L', 6}, {'M', 6}, {'N', 6}, {'O', 6},
                {'P', 6}, {'Q', 6}, {'R', 6}, {'S', 6}, {'T', 6}, {'U', 6}, {'V', 6}, {'W', 6},
                {'X', 6}, {'Y', 6}, {'Z', 6}, {'[', 4}, {'\\', 6}, {']', 4}, {'^', 6}, {'_', 6},
                {'`', 3}, {'a', 6}, {'b', 6}, {'c', 6}, {'d', 6}, {'e', 6}, {'f', 5}, {'g', 6},
                {'h', 6}, {'i', 2}, {'j', 6}, {'k', 5}, {'l', 3}, {'m', 6}, {'n', 6}, {'o', 6},
                {'p', 6}, {'q', 6}, {'r', 6}, {'s', 6}, {'t', 4}, {'u', 6}, {'v', 6}, {'w', 6},
                {'x', 6}, {'y', 6}, {'z', 6}, {'{', 5}, {'|', 2}, {'}', 5}, {'~', 7},
        }).collect(Collectors.toMap(data -> (Character) data[0], data -> (Integer) data[1]));
        SPACE_WIDTH = FONT_SIZES.get(' ');
    }

    private LocalChatPaginator() {
    }

    /**
     * Breaks a raw string up into a series of lines that have similar
     * length when displayed with the default Minecraft font.
     * Wrapping happens on space characters if possible,
     * but very long words will be broken if necessary.
     *
     * @param rawString  input string to wrap
     * @param lineLength expected line length in characters to aim for
     * @return array containing lines
     */

    public static String[] wordWrap(final String rawString, final int lineLength) {
        return wordWrap(rawString, lineLength, "");
    }

    /**
     * Breaks a raw string up into a series of lines. Words are wrapped using
     * spaces as decimeters and the newline character is respected.
     *
     * @param rawString  The raw string to break.
     * @param lineLength The length of a line of text.
     * @param wrapPrefix The string to prefix the wrapped line with
     * @return An array of word-wrapped lines.
     */
    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.CognitiveComplexity", "PMD.NcssCount"})

    public static String[] wordWrap(final String rawString, final int lineLength, final String wrapPrefix) {
        final int maxWidth = lineLength * DEFAULT_CHAR_WIDTH;
        if (!rawString.contains("\n")) {
            final String strippedRawString = StringUtils.stripEnd(rawString, null);
            if (getWidth(strippedRawString) <= maxWidth) {
                return new String[]{strippedRawString};
            }
        }

        final int maxWrapWidth = maxWidth - getWidth(wrapPrefix);
        final char[] rawChars = rawString.toCharArray();

        StringBuilder word = new StringBuilder();
        StringBuilder line = new StringBuilder(lineLength);
        final List<String> lines = new LinkedList<>();
        int wordWidth = 0;
        int lineWidth = 0;

        for (int i = 0; i < rawChars.length; i++) {
            final char singleChar = rawChars[i];
            switch (singleChar) {
                case '\n' -> {
                    line.append(word);
                    lines.add(line.toString());
                    line = new StringBuilder(lineLength);
                    word = new StringBuilder();
                    wordWidth = 0;
                    lineWidth = 0;
                }
                case ChatColor.COLOR_CHAR -> {
                    word.append(ChatColor.COLOR_CHAR);
                    if (rawChars.length <= i + 1) {
                        continue;
                    }
                    final char colorCode = rawChars[i + 1];
                    if (colorCode == 'x' || ChatColor.getByChar(colorCode) != null) {
                        word.append(colorCode);
                        i++;
                    }
                }
                case ' ' -> {
                    if (!line.isEmpty() && lineWidth + wordWidth > (lines.isEmpty() ? maxWidth : maxWrapWidth)) {
                        lines.add(line.toString());
                        line = new StringBuilder(lineLength);
                        lineWidth = 0;
                    }
                    word.append(' ');
                    wordWidth += SPACE_WIDTH;
                    line.append(word);
                    lineWidth += wordWidth;
                    word = new StringBuilder();
                    wordWidth = 0;
                }
                default -> {
                    final int singleCharWidth = getWidth(singleChar);
                    if (!line.isEmpty() && lineWidth + wordWidth + singleCharWidth > (lines.isEmpty() ? maxWidth : maxWrapWidth)) {
                        lines.add(line.toString());
                        line = new StringBuilder(lineLength);
                        lineWidth = 0;
                    }
                    if (line.isEmpty() && wordWidth + singleCharWidth > (lines.isEmpty() ? maxWidth : maxWrapWidth)) {
                        lines.add(word.toString());
                        word = new StringBuilder();
                        wordWidth = 0;
                    }
                    word.append(singleChar);
                    wordWidth += singleCharWidth;
                }
            }
        }

        if (!word.isEmpty()) {
            line.append(word);
        }

        if (!line.isEmpty()) {
            lines.add(line.toString());
        }

        lines.replaceAll(str -> StringUtils.stripEnd(str, null));

        for (int i = 1; i < lines.size(); i++) {
            final String previousLine = lines.get(i - 1);
            final String currentLine = lines.get(i);

            lines.set(i, wrapPrefix + ChatColor.getLastColors(previousLine) + currentLine);
        }

        return lines.toArray(new String[0]);
    }

    /**
     * Return the width of text taking into account variable font size and ignoring hidden characters
     *
     * @param input the input string.
     * @return width of text
     */
    public static int getWidth(final String input) {
        int ret = 0;
        final char[] rawChars = input.toCharArray();

        for (int i = 0; i < rawChars.length; i++) {
            if (rawChars[i] == ChatColor.COLOR_CHAR) {
                i += 1;
                continue;
            }
            ret += getWidth(rawChars[i]);
        }
        return ret;
    }

    /**
     * Get the width of a character in pixels. Returned values are for the default Minecraft font.
     *
     * @param character character to look up
     * @return width of the character
     */
    public static int getWidth(final Character character) {
        return FONT_SIZES.getOrDefault(character, DEFAULT_CHAR_WIDTH);
    }
}
