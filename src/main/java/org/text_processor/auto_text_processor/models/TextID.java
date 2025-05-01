package org.text_processor.auto_text_processor.models;

import java.util.Objects;
import java.util.Random;

public class TextID {

    public final String id;

    public  TextID() {
        Random rand = new Random();
        StringBuilder card = new StringBuilder("A");
        for (int i = 0; i < 7; i++) {
            int n = rand.nextInt(10);
            card.append(Integer.toString(n));
        }

        this.id = card.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TextID)) return false;
        TextID textID = (TextID) o;
        return Objects.equals(id, textID.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return id;
    }
}
