package com.sg.floormaster.view;

import java.util.Scanner;

/**
 * Duplicate of my personal UserIOGenerics implementation rather than supplied UserIOConsoleImpl
 */
public class UserIOConsoleImpl implements UserIO{
    // If it gets buggy, replace with mthree's UserIOConsoleImpl.java

    private static final Scanner inputScanner = new Scanner(System.in);
    // create the generic methods that can parse any type with a parsex function from a string:

    // define functional interface so we can use it to pass lambda functions
    interface ParseString<T> {
        T run(String str) throws NumberFormatException;
    }

    private static <T> T returnParsedTypeFromString(String input, ParseString<T> parseFunc) throws NumberFormatException {
        try {
            return parseFunc.run(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid string input");
            System.out.println(e.getMessage());
            throw e;
        }
    }

    private static <T> T readNumberUntilValid(String prompt, ParseString<T> parseFunc) {
        while (true) {
            // ask user to enter input of valid type
            System.out.println(prompt);
            // capture line input as string:
            String input = inputScanner.nextLine();

            // try to parse the input and return
            try {
                return returnParsedTypeFromString(input, parseFunc);
            } catch (NumberFormatException e) {
                System.out.println("Try again"); // prompt them to try again if invalid input entered.
                System.out.println();
            }
        }
    }

    private static <T extends Comparable<T>> T readNumberUntilValid(String prompt, ParseString<T> parseFunc, T min, T max) {
        while (true) {
            // gets user to enter valid input with prompt:
            T validInput = readNumberUntilValid(prompt, parseFunc);

            // if validInput is also within the min and max range given:
            if ((validInput.compareTo(min) >= 0) && (validInput.compareTo(max) <= 0)) {
                return validInput;
            }

            // otherwise prompt them to try again
            System.out.println("Input was out of range [" + min + ", " + max +"]. Try again...");
            System.out.println();
        }
    }

    // implement UserIO class:
    @Override
    public void print(String message) {
        System.out.println(message);
    }

    @Override
    public String readString(String prompt) {
        System.out.println(prompt);
        return inputScanner.nextLine();
    }

    @Override
    public int readInt(String prompt) {
        return readNumberUntilValid(prompt, Integer::parseInt);
    }

    @Override
    public int readInt(String prompt, int min, int max) {
        return readNumberUntilValid(prompt, Integer::parseInt, min, max);
    }

    @Override
    public double readDouble(String prompt) {
        return readNumberUntilValid(prompt, Double::parseDouble);
    }

    @Override
    public double readDouble(String prompt, double min, double max) {
        return readNumberUntilValid(prompt, Double::parseDouble, min, max);
    }

    @Override
    public float readFloat(String prompt) {
        return readNumberUntilValid(prompt, Float::parseFloat);
    }

    @Override
    public float readFloat(String prompt, float min, float max) {
        return readNumberUntilValid(prompt, Float::parseFloat, min, max);
    }

    @Override
    public long readLong(String prompt) {
        return readNumberUntilValid(prompt, Long::parseLong);
    }

    @Override
    public long readLong(String prompt, long min, long max) {
        return readNumberUntilValid(prompt, Long::parseLong, min, max);
    }
}
