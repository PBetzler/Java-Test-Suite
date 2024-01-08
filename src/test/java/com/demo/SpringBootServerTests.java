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

package com.demo;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import com.code_intelligence.jazzer.api.FuzzedDataProvider;
import com.code_intelligence.jazzer.junit.FuzzTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.MockMvcPrint;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest
@AutoConfigureMockMvc(print = MockMvcPrint.NONE)
public class SpringBootServerTests {
  @Autowired private MockMvc mockMvc;

  @Test
  public void unitTestHelloDeveloper() throws Exception {
    mockMvc.perform(get("/hello").param("name", "Developer"));
  }

  @Test
  public void unitTestHelloHacker() throws Exception {
    mockMvc.perform(get("/hello").param("name", "Hacker"));
  }

  @FuzzTest
  public void fuzzTestHello(FuzzedDataProvider data) throws Exception {
    mockMvc.perform(get("/hello").param("name", data.consumeRemainingAsString()));
  }


  @Test
  public void unitTestJsonHacker() throws Exception {
    ObjectMapper om = new ObjectMapper();
    SpringBootServer.User user = new SpringBootServer.User();
    user.name = "Hacker";
    mockMvc.perform(post("/json").content(om.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON));
  }
  @FuzzTest
  public void fuzzTestJson(FuzzedDataProvider data) throws Exception {
    ObjectMapper om = new ObjectMapper();
    SpringBootServer.User user = new SpringBootServer.User();
    user.name = data.consumeRemainingAsString();
    mockMvc.perform(post("/json").content(om.writeValueAsString(user)).contentType(MediaType.APPLICATION_JSON));
  }

  @Test
  public void unitTestFirstAndSecond() throws Exception {
    mockMvc.perform(get("/first").param("param", "Never gonna give you up"));
    mockMvc.perform(get("/second").param("param", "Never gonna let you down"));
    mockMvc.perform(get("/first").param("param", "Never gonna run around and desert you"));
  }

  @FuzzTest
  public void fuzzTestFirstAndSecond(FuzzedDataProvider data) throws Exception {
    for (int i = 0; i < data.consumeInt(1,100); i++) {

      switch (data.consumeInt(0,1)) {
        case 0:
          mockMvc.perform(get("/first").param("param", data.consumeRemainingAsString()));
          break;
        case 1:
          mockMvc.perform(get("/second").param("param", data.consumeRemainingAsString()));
          break;
      }
    }
  }

  @Test
  public void unitTestGetUser() throws Exception {
    mockMvc.perform(get("/user").param("id", "something"));
  }

  @FuzzTest
  public void fuzzTestGetUser(FuzzedDataProvider data) throws Exception {
    String in = data.consumeRemainingAsString();
    mockMvc.perform(get("/user").queryParam("id", in));
  }

}
