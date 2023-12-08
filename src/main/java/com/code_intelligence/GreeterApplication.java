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

import com.code_intelligence.jazzer.api.FuzzerSecurityIssueMedium;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@SpringBootApplication
@RestController
class GreeterApplication {

  private static AtomicInteger firstAtomicInteger = new AtomicInteger(0);
  private static AtomicBoolean atomicBool = new AtomicBoolean(false);

  private static ReadWriteLock lock = new ReentrantReadWriteLock();


  @GetMapping("/hello")
  public String insecureHello(@RequestParam(required = false, defaultValue = "World") String name) {
    // We trigger an exception in the special case where the name is "attacker". This shows
    // how CI Fuzz can find this out and generates a test case triggering the exception
    // guarded by this check.
    // Black-box approaches lack insights into the code and thus cannot handle these cases.
    if (name.equalsIgnoreCase("attacker")) {
      // We throw an exception here to mimic the situation that something unexpected
      // occurred while handling the request.
      throw new FuzzerSecurityIssueMedium("We panic when trying to greet an attacker!");
    }
    return "Hello " + name + "!";
  }

  @GetMapping("/first")
  public String first(@RequestParam(required = false, defaultValue = "World") String param) {
    if (param.equalsIgnoreCase("SomeThingRandom")) {
      if (firstAtomicInteger.get() == 0) {
        if (!atomicBool.get()) {
          lock.writeLock().lock();
          atomicBool.set(true);
          firstAtomicInteger.set(1);
          lock.writeLock().unlock();
        } else {
          throw new FuzzerSecurityIssueMedium("Access should not have been permitted!");
        }
      }
    }
    return "First endpoint";
  }

    @GetMapping("/second")
  public String second(@RequestParam(required = false, defaultValue = "World") String param) {
    if (param.equalsIgnoreCase("SomeThingElseRandom")) {
      if (firstAtomicInteger.get() == 1) {
        firstAtomicInteger.set(0);
      }
    }
    return "Second endpoint";
  }

  @GetMapping("/bye")
  public String unstableBye(@RequestParam(required = false, defaultValue = "World") String name) {
    // We trigger an exception in the special case where the name is "attacker". This shows
    // how CI Fuzz can find this out and generates a test case triggering the exception
    // guarded by this check.
    // Black-box approaches lack insights into the code and thus cannot handle these cases.
    if (name.equalsIgnoreCase("something")) {
      // We throw an exception here to mimic the situation that something unexpected
      // occurred while handling the request.
      throw new RuntimeException("RuntimeException");
    }
    return "Bye " + name + "!";
  }


  public static void main(String[] args) {
    SpringApplication.run(GreeterApplication.class, args);
  }
}
