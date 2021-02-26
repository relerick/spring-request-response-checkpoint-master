package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@WebMvcTest (CamelizeClass.class)
public class ControllerTest {
    @Autowired
    MockMvc mvc;


    @Test
    public void camelCaseTest() throws Exception {

        this.mvc.perform(get("/camelize?original=this_is_a_thing")
                .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())// return 200
                .andExpect(content().string("thisIsAThing"));
    }

    @Test
    public void capCaseTest() throws Exception {

        this.mvc.perform(get("/camelize?original=this_is_a_thing&initialCap=true")
                .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())// return 200
                .andExpect(content().string("ThisIsAThing"));


    }

    @Test
    public void redactbadwordTest() throws Exception {

        this.mvc.perform(get("/redact?original=A little of this and a little of that&badWord=little")
                .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())// return 200
                .andExpect(content().string("A ****** of this and a ****** of that"));


    }

    @Test
    public void redactbadwordTesttwo() throws Exception {

        this.mvc.perform(get("/redact?original=A little of this and a little of " +
                "that&badWord=little&badWord=this")
                .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())// return 200
                .andExpect(content().string("A ****** of **** and a ****** of that"));

    }

    // Redact
    @Test
    public void redactTest() throws Exception{
        // setup code

        // destination localhost:8080
        this.mvc.perform(get("/redact?original=A little of this and a little of that&badWord=little&badWord=this")
                .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk()) // 200 class
                .andExpect(content().string("A ****** of **** and a ****** of that")); // expected good
    }
    @Test
    public void encodeTest() throws Exception{
        // setup code

        // destination localhost:8080
        this.mvc.perform(post("/encode?message=a little of this and a little of that&key=mcxrstunopqabyzdefghijklvw")
                .accept(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk()) // 200 class
                .andExpect(content().string("m aohhas zt hnog myr m aohhas zt hnmh")); // expected good
    }


    //This one was far more difficult because it was not well explained.  Joshua Schoonover had to explain it to me.
    @Test
    public void sedTest() throws Exception{
        // setup body of html
        String addressin = "/s/little/lot";
        String bodytext = "a little of this and a little of that";

        MockHttpServletRequestBuilder request1 = (post(addressin)
                .accept(MediaType.TEXT_PLAIN)
                .contentType(MediaType.TEXT_PLAIN)
                .content(bodytext));

        // destination localhost:8080
        this.mvc.perform(request1)
                .andExpect(status().isOk()) // 200 class
                .andExpect(content().string("a lot of this and a lot of that")); // expected good
    }

}