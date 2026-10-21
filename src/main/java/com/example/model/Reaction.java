package com.example.model;

/** The emoji replies a teacher or administrator can send to a report. */
public enum Reaction {
    HUGGING_FACE("🤗", "Sending you a warm hug"),
    OPEN_HANDS("👐", "We're here for you with open hands"),
    PEOPLE_HUGGING("🫂", "You're not alone, here's a comforting hug"),
    BROKEN_HEART("💔", "We're heartbroken that this happened to you"),
    FROWNING_FACE("🙁", "We're sorry you went through this"),
    CRYING_FACE("😢", "We feel for you");

    private final String symbol;
    private final String meaning;

    Reaction(String symbol, String meaning) {
        this.symbol = symbol;
        this.meaning = meaning;
    }

    public String getSymbol() { return symbol; }

    public String getMeaning() { return meaning; }

    @Override
    public String toString() { return symbol; }
}
