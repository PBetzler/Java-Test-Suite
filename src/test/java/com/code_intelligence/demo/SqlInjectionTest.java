package com.code_intelligence.demo;

import com.code_intelligence.demo.SqlInjection;
import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.junit.FuzzTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

public class SqlInjectionTest {


    @Test
    public void unitTest() throws SQLException {
        SqlInjection project = new SqlInjection();
        project.connect();
        project.getUserByUsername("Dave");
    }

    @FuzzTest
    public void myFuzzTest(FuzzedDataProvider data) throws SQLException {
        SqlInjection project = new SqlInjection();
        project.connect();
        project.getUserByUsername(data.consumeString(100));
    }
}
