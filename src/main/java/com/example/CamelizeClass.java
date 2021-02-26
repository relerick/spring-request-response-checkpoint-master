package com.example;









import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;



@RestController
public class CamelizeClass {
    // first test and baseline for test
    @GetMapping("/") // root URL localhost:8080
    public String home() {
        return "Hello";
    }

    @GetMapping("/camelize") // root URL localhost:8080
    public String camelCase(@RequestParam String original,
                            @RequestParam(defaultValue = "false") Boolean initialCap) {
        // can we make optional param
        StringBuilder camelBuilder = new StringBuilder(original);
        for (int i = 0; i < camelBuilder.length(); i++) {
            if (camelBuilder.charAt(i) == '_') {
                camelBuilder.deleteCharAt(i);
                camelBuilder.replace(
                        i, i + 1,
                        String.valueOf(Character.toUpperCase(camelBuilder.charAt(i))));
            }
        }
        // if initialCap is true the capitalize first letter
        String camelCase = camelBuilder.toString();
        if (initialCap) {
            return camelCase.substring(0, 1).toUpperCase() + camelCase.substring(1);
        } else return camelCase;
    }


    @GetMapping("/redact")
    public String redact(@RequestParam String original,
                         @RequestParam String[] badWord) {
        //int stars = badWord[i].length();

        // for loop to replace each badWord with single *
        // A little of this and a little of that
        // badWord=little == [0]
        // badWord=this   == [1]
        for (int i = 0; i < badWord.length; i++) {
            String star = "*";
            original = original.replaceAll(badWord[i],
                    star.repeat(badWord[i].length()));
        }
        System.out.println(original);
        return original;
    }
    @PostMapping("/encode")
    public String redact(@RequestParam String message,
                         @RequestParam String key) {

        // generate a hachmap for later use
        HashMap<Character, Character> keyMap = new HashMap<>();
        keyMap.put(' ',' ');

        char[] plainText = message.toCharArray();   // convert message to char array
        char[] secret = key.toCharArray();          // convert key to char array

        // use a hashmap, from above, to generate a alphabetical mapping from a-z
        int i = 0; //declare i for later use
        for (char ch = 'a'; ch <= 'z'; ch++) {
            keyMap.put(ch, secret[i]);
            i++;
        }

        // iterate through char array of message and replace each corresponding character
        for (int p = 0; p < plainText.length; p++) {
            plainText[p]= keyMap.get(plainText[p]);
        }


        System.out.println(String.valueOf(plainText)); // echo out to screen

        return String.valueOf(plainText); // convert char array to string

        // message = a little of this and a little of that
        // key=mcxrstunopqabyzdefghijklvw
    }



    @PostMapping("/s/{find}/{replace}") // the /s signifies that you need a @PathVariable and then variables {find}/{replace}
    public String sed(@PathVariable String find,
                      @PathVariable String replace,
                      @RequestBody String message)  {

        // The message will access the replaceAll method that pulls from the find and replace.
        message = message.replaceAll(find, replace);

        System.out.println(message);
        return message;

    }

}







