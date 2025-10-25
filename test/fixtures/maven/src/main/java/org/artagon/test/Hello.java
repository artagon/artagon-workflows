package org.artagon.test;

/**
 * Simple class for workflow testing.
 */
public class Hello {
    /**
     * Returns a greeting message.
     *
     * @return greeting string
     */
    public String greet() {
        return "Hello, World!";
    }

    /**
     * Returns a greeting message with a name.
     *
     * @param name the name to greet
     * @return personalized greeting string
     */
    public String greet(String name) {
        if (name == null || name.trim().isEmpty()) {
            return greet();
        }
        return "Hello, " + name + "!";
    }
}
