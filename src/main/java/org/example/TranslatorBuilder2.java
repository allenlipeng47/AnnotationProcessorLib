package org.example;

public class TranslatorBuilder2 {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int i;
        public Builder i(int i) {
            this.i = i;
            return this;
        }

        private String j;
        public Builder j(String j) {
            this.j = j;
            return this;
        }

        public Translator build() {
            return new Translator(i, j);
        }
    }

}
