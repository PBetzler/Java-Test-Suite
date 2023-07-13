package com.code_intelligence;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.api.FuzzerSecurityIssueHigh;
import com.code_intelligence.jazzer.junit.FuzzTest;
import com.google.json.JsonSanitizer;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class PropertyBasedFuzzTest {

    /**
     * Real test that found the XSS vulnerability in json-sanitizer version 1.2.1
     * @param data
     * @throws Exception
     */
    @FuzzTest
    public void fuzzTestHello(FuzzedDataProvider data) throws Exception {
        String input = data.consumeRemainingAsString();
        String safeJSON;

        try {
            safeJSON = JsonSanitizer.sanitize(input, 10);
        } catch (Exception e){
            return;
        }

        // Property based evaluation as sort of custom bug detector.
        assert !safeJSON.contains("</script")
                : new FuzzerSecurityIssueHigh("XSS Vulnerability in JsonSanitizer");
    }
}
