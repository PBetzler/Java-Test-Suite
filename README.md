# Java Test Suite

This Java Test Suit is an example project to showcase the usage of white-box fuzz testing for developers.
It contains examples for the usage as security issue as well as robustness issue detector.

As a general advise: 
Let the fuzzer not only control the inputs, but also the control flow to achieve the best possible results.
Robustness/business-logic issues are best detected by testcases written the way of property based tests.

To execute the tests you can either use [cifuzz](https://github.com/CodeIntelligenceTesting/cifuzz) (recommended)  and/or [CISense](https://app.code-intelligence.com/dashboard/start) (recommended) 
or set the env variable JAZZER=FUZZ=1 and execute via mvn (not recommended).