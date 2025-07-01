package ru.netology.graphics.image;

public class TextColorSchemaImpl implements TextColorSchema {
    private static final int MAX_COLOR_VALUE = 255;
    private static final int MAX_INDEX_OFFSET = 1;
    private final char[] symbols = {'▇', '●', '◉', '◍', '◎', '○', '☉', '◌', '-'};

    @Override
    public char convert(int color) {
        int index = color * (symbols.length - MAX_INDEX_OFFSET) / MAX_COLOR_VALUE;
        return symbols[index];
    }
}
