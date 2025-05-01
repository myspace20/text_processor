package org.text_processor.auto_text_processor.models;

import java.util.Objects;

public class Text<T> {
    String path;
    String body;
    public final T id;

    public Text(String path, String body, T id){
        this.body = body;
        this.path = path;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Text<?> text = (Text<?>) o;

        return Objects.equals(path, text.path) &&
                Objects.equals(body, text.body) &&
                Objects.equals(id, text.id);
    }



    @Override
    public int hashCode() {
        return Objects.hash(path, body, id);
    }

    @Override
    public String toString() {
        return "Text {" +
                "ID = " + id +
                ", Path = '" + path + '\'' +
                ", Body = '" + body + '\'' +
                '}';
    }

    public T getTextId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String newValue) {
        body = newValue;
    }


}

