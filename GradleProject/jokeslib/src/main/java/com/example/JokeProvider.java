package com.example;

public class JokeProvider {

    private final static String[] jokes = {
            "A man walks into a bar.\nThe second man ducks.",
            "This is the setup to a bad joke.\nThis is the punchline.........",
            "A priest, a rabbi and a minister walk into a bar. And the bartender says,\n\"What is this!? Some kind of joke?\"",
    };

    public static String getJoke() {
        int joke = (int) (Math.random() * jokes.length);
        return jokes[joke];
    }
}
