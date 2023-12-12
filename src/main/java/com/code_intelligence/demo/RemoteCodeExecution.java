package com.code_intelligence.demo;

import java.io.*;

public class RemoteCodeExecution {

    /**
     * Inner class book contains a title and an author field and implements the necessary serializable functions.
     */
    public static class Book implements Serializable {
        private static final long serialVersionUID = 123456789L;
        public String title;
        public String author;

        public Book(String title, String author) {
            title = title;
            author = author;
        }

        private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
            title = (String) ois.readObject();
            author = (String) ois.readObject();
        }

        private void writeObject(ByteArrayOutputStream byteArrayOutputStream) throws IOException {
            ObjectOutputStream oos = new ObjectOutputStream(byteArrayOutputStream);
            oos.writeObject(this);
        }
    }

    /**
     * Faulty deserialize function that checks type of deserialized object to late and allows for gadget chain attacks
     * @param stream
     * @return
     * @throws IOException
     */
    public static Book deserialize(ByteArrayInputStream stream) throws IOException {
        ObjectInputStream ois = new ObjectInputStream(stream);
        try {
            // Casting the result of readObject() occurs after the deserialization process ends
            // which make it possible to read any object and can lead to gadget chain attacks
            Object o = ois.readObject();
            o.toString();
            return (Book) o;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Working serialize function that writes the serialized book object to the given ByteArrayOutputStream
     * @param book
     * @return
     * @throws IOException
     */
    public static ByteArrayOutputStream serialize(Book book) throws IOException {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            book.writeObject(stream);

            return stream;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
