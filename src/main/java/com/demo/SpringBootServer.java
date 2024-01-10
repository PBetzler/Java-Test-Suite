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

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@SpringBootApplication
@RestController
public class SpringBootServer {

  private static final AtomicInteger atomicInteger = new AtomicInteger(0);
  private static final AtomicBoolean atomicBool = new AtomicBoolean(false);
  private static final ReadWriteLock lock = new ReentrantReadWriteLock();

  public static class User {
    public String name;
  }

  /**
   * Insecure /hello endpoint function that crashes when param name equals attacker
   * @param name
   * @return
   */
  @GetMapping("/hello")
  public String insecureHello(@RequestParam(required = false, defaultValue = "World") String name) {
    // We trigger an exception in the special case where the name is "attacker". This shows
    // how CI Fuzz can find this out and generates a test case triggering the exception
    // guarded by this check.
    // Black-box approaches lack insights into the code and thus cannot handle these cases.
    if (name.equalsIgnoreCase("attacker")) {
      // We throw an exception here to mimic the situation that something unexpected
      // occurred while handling the request.
      throw new SecurityException("We panic when trying to greet an attacker!");
    }
    return "Hello " + name + "!";
  }

  /**
   * Insecure /json endpoint that expects a JSON object that contains a name key and crashes if the value is attacker
   * @param user
   * @return
   */
  @PostMapping("/json")
  public String insecureJson(@RequestBody User user) {
    // We trigger an exception in the special case where the name is "attacker". This shows
    // how CI Fuzz can find this out and generates a test case triggering the exception
    // guarded by this check.
    // Black-box approaches lack insights into the code and thus cannot handle these cases.
    if (user.name.equalsIgnoreCase("attacker")) {
      // We throw an exception here to mimic the situation that something unexpected
      // occurred while handling the request.
      throw new SecurityException("We panic when trying to greet an attacker!");
    }
    return "Hello " + user.name + "!";
  }

  /**
   * Endpoint that creates side effects if param equals "SomeThingRandom" and crashes
   * when side effects not in correct state
   * @param param
   * @return
   */
  @GetMapping("/first")
  public String first(@RequestParam(required = false, defaultValue = "World") String param) {
    if (param.equalsIgnoreCase("SomeThingRandom")) {

      lock.writeLock().lock();
      // Check if atomicInteger is 0 and if atomicBool is false
      // if both are 0 and false, set both to 1 and true
      // else if only atomicInteger is 0 throw an exception that's caught by the fuzzer
      if (atomicInteger.get() == 0 ) {
        if (!atomicBool.get()) {
          atomicBool.set(true);
          atomicInteger.set(1);
          lock.writeLock().unlock();
        } else {
          lock.writeLock().unlock();
          // We throw an exception here to mimic the situation that something unexpected
          // occurred while handling the request.
          throw new SecurityException("Access should not have been permitted!");
        }
      }
    }
    return "First endpoint";
  }

  /**
   * Endpoint that incorrectly resets side effect state of /first endpoint if param equals "SomeThingElseRandom"
   * @param param
   * @return
   */
    @GetMapping("/second")
  public String second(@RequestParam(required = false, defaultValue = "World") String param) {
    if (param.equalsIgnoreCase("SomeThingElseRandom")) {
      // Set atomicInteger to 0 but do not change atomicBool to trigger the exception
      // in endpoint /first
      atomicInteger.set(0);
    }
    return "Second endpoint";
  }

  /**
   * Endpoint crashes if base64 encoded id of user id equals "YWRtaW46"
   * @param id
   * @return
   */
  @GetMapping("/user")
  public String getUser(@RequestParam String id) {
    Base64.Encoder base64 = Base64.getEncoder();
    // We trigger an exception in the special case where the base64 encoded id equals "YWRtaW46".
    // This shows how CI Fuzz can find this out and generates a test case triggering the exception
    // guarded by this check.
    // Black-box approaches lack insights into the code and thus cannot handle these cases.
    if (base64.encodeToString(id.getBytes()).equals("YWRtaW46")) {
      // We throw an exception here to mimic the situation that something unexpected
      // occurred while handling the request.
      throw new SecurityException("Restricted username");
    }
    return "Hello user " + id + "!";
  }

  public static void main(String[] args) {
    SpringApplication.run(SpringBootServer.class, args);
  }
}
