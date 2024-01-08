package com.demo;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.junit.FuzzTest;
import com.demo.RemoteCodeExecution.Book;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.util.Objects;

import static com.demo.RemoteCodeExecution.*;
import static com.demo.RemoteCodeExecution.serialize;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class RemoteCodeExecutionTest {

    @Test
    public void unitTest() throws IOException {
        Book book = new Book("Title", "Author");

        byte[] serializedBook = serialize(book).toByteArray();
        Book newBook = deserialize(new ByteArrayInputStream(serializedBook));

        assertEquals(book, newBook, "Deserialized object does not match input");
    }

    @FuzzTest
    public void myFuzzTest(FuzzedDataProvider data) {
        try {
            deserialize(new ByteArrayInputStream(data.consumeRemainingAsBytes()));
        } catch (Exception ignored) {
            // We can ignore all exception as the RCE will be caught by Jazzer
        }
    }
}
