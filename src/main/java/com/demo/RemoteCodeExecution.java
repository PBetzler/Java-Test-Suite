package com.demo;

import java.io.*;
import java.util.Objects;

public class RemoteCodeExecution {

    /**
     * Inner class book contains a title and an author field and implements the necessary serializable functions.
     */
    public static class Book implements Serializable {
        private static final long serialVersionUID = 123456789L;
        private final String title;
        private final String author;

        public Book(String title, String author) {
            this.title = title;
            this.author = author;
        }

        public String getTitle() {
            return title;
        }

        public String getAuthor() {
            return author;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Book book = (Book) o;
            return Objects.equals(title, book.title) && Objects.equals(author, book.author);
        }

        @Override
        public int hashCode() {
            return Objects.hash(title, author);
        }

        @Override
        public String toString() {
            return "Book{" +
                    "title='" + title + '\'' +
                    ", author='" + author + '\'' +
                    '}';
        }
    }

    /**
     * Faulty deserialize function that checks type of deserialized object to late and allows for gadget chain attacks
     *
     * @param stream
     * @return
     * @throws IOException
     */
    public static Book deserialize(InputStream stream) throws IOException {
        try (ObjectInputStream ois = new ObjectInputStream(stream)) {
            // Casting the result of readObject() occurs after the deserialization process ends
            // which make it possible to read any object and can lead to gadget chain attacks
            return (Book) ois.readObject();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Working serialize function that writes the serialized book object to the given ByteArrayOutputStream
     *
     * @param book
     * @return
     * @throws IOException
     */
    public static ByteArrayOutputStream serialize(Book book) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(stream)) {
            oos.writeObject(book);
            return stream;
        }
    }
}
