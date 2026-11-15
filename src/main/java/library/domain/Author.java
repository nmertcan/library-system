package library.domain;

public final class Author extends Person {
    public Author(String id, String name) { super(id, name); }
    @Override public String toString() { return name(); }
}
