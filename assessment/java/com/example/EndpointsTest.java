package com.example;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EndpointsTest {

    @Autowired
    private TestRestTemplate template;

    @Test
    public void camelizeReturnsTheCamelizedString() throws Exception {
        Map<String, String> testCases = new LinkedHashMap<>();
        testCases.put("single", "single");
        testCases.put("what_is_this_thing", "whatIsThisThing");

        testCases.keySet().forEach(key -> {
            ResponseEntity<String> response = template.getForEntity("/camelize?original=" + key, String.class);

            assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
            assertThat(response.getBody(), containsString(testCases.get(key)));
        });
    }

    @Test
    public void camelizeReturnsTheCamelizedStringWithInitialCaps() throws Exception {
        Map<String, String> testCases = new LinkedHashMap<>();
        testCases.put("single", "Single");
        testCases.put("what_is_this_thing", "WhatIsThisThing");

        testCases.keySet().forEach(key -> {
            ResponseEntity<String> response = template.getForEntity("/camelize?initialCap=true&original=" + key, String.class);

            assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
            assertThat(response.getBody(), containsString(testCases.get(key)));
        });
    }

    static class RedactTestCase {
        final String original;
        final String[] badWords;
        final String expected;

        RedactTestCase(String original, String[] badWords, String expected) {
            this.original = original;
            this.badWords = badWords;
            this.expected = expected;
        }
    }

    @Test
    public void redactReplacesTheBadWordsWithStars() throws Exception {
        List<RedactTestCase> testCases = new ArrayList<>();

        testCases.add(new RedactTestCase(
                "A little of this and a little of that",
                new String[]{"little", "this"},
                "A ****** of **** and a ****** of that"));

        testCases.add(new RedactTestCase(
                "That is a global variable",
                new String[]{"variable", "global"},
                "That is a ****** ********"));

        testCases.forEach(testCase -> {
            UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/redact").queryParam("original", testCase.original);
            Arrays.stream(testCase.badWords).forEach(word -> builder.queryParam("badWord", word));

            ResponseEntity<String> response = template.getForEntity(builder.build(false).toUriString(), String.class);

            assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
            assertThat(response.getBody(), containsString(testCase.expected));
        });
    }

    static class EncodeTestCase {
        final String message;
        final String alphabet;
        final String expected;

        EncodeTestCase(String message, String alphabet, String expected) {
            this.message = message;
            this.alphabet = alphabet;
            this.expected = expected;
        }
    }

    @Test
    public void encode() throws Exception {
        List<EncodeTestCase> testCases = new ArrayList<>();

        testCases.add(new EncodeTestCase(
                "a little of this and a little of that",
                "abcdefghijklmnopqrstuvwxyz",
                "a little of this and a little of that"));

        testCases.add(new EncodeTestCase(
                "a little of this and a little of that",
                "mcxrstunopqabyzdefghijklvw",
                "m aohhas zt hnog myr m aohhas zt hnmh"));

        testCases.forEach(testCase -> {
            String body = String.format(
                    "message=%s&key=%s",
                    testCase.message,
                    testCase.alphabet
            );

            RequestEntity<String> request = RequestEntity
                    .post(URI.create("/encode"))
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(body);

            ResponseEntity<String> response = template.exchange(
                    "/encode",
                    HttpMethod.POST,
                    request,
                    String.class
            );

            assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
            assertThat(response.getBody(), equalTo(testCase.expected));
        });
    }

    static class SedTestCase {
        final String document;
        final String find;
        final String replace;
        final String expected;

        SedTestCase(String document, String find, String replace, String expected) {
            this.document = document;
            this.find = find;
            this.replace = replace;
            this.expected = expected;
        }
    }

    @Test
    public void sed() throws Exception {
        List<SedTestCase> testCases = new ArrayList<>();

        testCases.add(new SedTestCase(
                "Love doesn't discriminate",
                "Love",
                "Death",
                "Death doesn't discriminate"));

        testCases.add(new SedTestCase(
                "We'll bleed and fight for you",
                "bleed and fight",
                "make it right",
                "We'll make it right for you"));

        testCases.forEach(testCase -> {
            String uri = String.format("/s/%s/%s", testCase.find, testCase.replace);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            HttpEntity<String> request = new HttpEntity<>(testCase.document, headers);

            ResponseEntity<String> response = template.exchange(
                    uri,
                    HttpMethod.POST,
                    request,
                    String.class
            );

            assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
            assertThat(response.getBody(), equalTo(testCase.expected));
        });
    }

}
