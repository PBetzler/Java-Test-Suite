/*
 * Copyright 2023 Code Intelligence GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.code_intelligence;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.api.FuzzerSecurityIssueMedium;
import com.code_intelligence.jazzer.junit.FuzzTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest()
public class GreeterApplicationTests {
  @Autowired private MockMvc mockMvc;

  private boolean beforeCalled = false;

  @BeforeEach
  public void beforeEach() {
    beforeCalled = true;
  }

  @AfterEach
  public void afterEach() {
    beforeCalled = false;
  }

  @Test
  public void unitTestHelloDeveloper() throws Exception {
    mockMvc.perform(get("/hello").param("name", "Developer"));
  }

  @Test
  public void unitTestHelloHacker() throws Exception {
    mockMvc.perform(get("/hello").param("name", "Contributor"));
  }

  @FuzzTest
  public void fuzzTestHello(FuzzedDataProvider data) throws Exception {
    for (int i = 0; i < data.consumeInt(); i++) {

      switch (data.consumeInt(0,3)) {
        case 0:
          mockMvc.perform(get("/hello").param("name", data.consumeRemainingAsString()));
          break;
        case 1:
          mockMvc.perform(get("/first").param("name", data.consumeRemainingAsString()));
          break;
        case 2:
          mockMvc.perform(get("/second").param("name", data.consumeRemainingAsString()));
          break;
        case 4:
          try {
            mockMvc.perform(get("/bye").param("name", data.consumeRemainingAsString()));
          } catch (Exception ignored) {
            throw new FuzzerSecurityIssueMedium("Endpoint /bye crashed");
          }
          break;
      }
    }

  }
}
