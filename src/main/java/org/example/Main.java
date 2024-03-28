package org.example;

public class Main {
    public static void main(String[] args) {
        Translator translator = TranslatorBuilder.builder()
            .a("asdf")
            .i(123)
            .build();
        System.out.println(translator);
    }

}
