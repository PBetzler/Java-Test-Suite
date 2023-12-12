package com.code_intelligence.demo;

import com.code_intelligence.demo.RemoteCodeExecution;
import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.junit.FuzzTest;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InvalidObjectException;

public class RemoteCodeExecutionTest {


    @Test
    public void unitTest() throws IOException {
        RemoteCodeExecution.Book book = new RemoteCodeExecution.Book("Title", "Author");

        byte[] bytes = RemoteCodeExecution.serialize(book).toByteArray();
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);

        RemoteCodeExecution.Book newBook = RemoteCodeExecution.deserialize(stream);

        if (book.author != newBook.author || book.title != newBook.title) {
            throw new InvalidObjectException("Deserialized Object does not match input");
        }
    }

    @FuzzTest
    public void myFuzzTest(FuzzedDataProvider data) {
        data.consumeString(512);
        try {
            RemoteCodeExecution.deserialize(new ByteArrayInputStream(data.consumeBytes(100)));
        } catch (Exception ignored) {
            // We can ignore all exception as the RCE will be caught by Jazzer
        }
    }
}