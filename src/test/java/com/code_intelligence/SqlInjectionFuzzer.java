package com.code_intelligence;

import com.code_intelligence.SqlInjection;
import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.junit.FuzzTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class SqlInjectionFuzzer {

    private static SqlInjection project;


    @BeforeAll
    public static void fuzzerInitialize() throws SQLException {
        // Initialize the project and connect to the database
        project = new SqlInjection();
        project.connect();
    }

    @Test
    public static void unitTest() throws SQLException {
        project.getUserByUsername("Dave");
    }

    @FuzzTest
    public static void fuzzerTestOneInput(FuzzedDataProvider data) throws SQLException {
        project.getUserByUsername(data.consumeString(100));
    }
}
