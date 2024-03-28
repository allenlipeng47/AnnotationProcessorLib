package org.example.builder;

//import com.google.auto.service.AutoService;
import javax.annotation.processing.*;
import javax.lang.model.*;
import javax.lang.model.element.*;
import javax.tools.*;
import java.io.*;
import java.util.*;

@SupportedAnnotationTypes("org.example.builder.Builder")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
// AutoService helps to remove META-INF/services/javax.annotation.processing.Processor, as well as getting
// rid of -proc:none in pom.xml
public class BuilderProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(Builder.class)) {
            if (annotatedElement.getKind() != ElementKind.CLASS) {
                processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Only classes can be annotated with @Builder");
                return true; // Exit processing
            }

            // Get the annotation value
            String[] fullyClassNames = annotatedElement.getAnnotation(Builder.class).className();
            for (String fullyClassName : fullyClassNames) {
                String packageName = fullyClassName.substring(0, fullyClassName.lastIndexOf('.'));
                String className = fullyClassName.substring(fullyClassName.lastIndexOf('.') + 1);

                // Generate the builder class
                generateBuilderClass(packageName, className);
            }

        }
        return true;
    }

    private void generateBuilderClass(String packageName, String className) {
        try {
            String builderClassName = className + "Builder";

            // Prepare the source code for the builder class
            StringBuilder sourceBuilder = new StringBuilder();
            sourceBuilder.append("package ").append(packageName).append(";\n\n");
            sourceBuilder.append("public class ").append(builderClassName).append(" {\n\n");
            sourceBuilder.append("\tpublic static Builder builder() { ").append("\n");
            sourceBuilder.append("\t\treturn new Builder(); ").append("\n");
            sourceBuilder.append("\t} ").append("\n");
            sourceBuilder.append("\n");

            sourceBuilder.append("\tpublic static class Builder {").append("\n");
            StringBuilder allFields = new StringBuilder();

            // Generate methods for each field in the target class
            for (Element element : processingEnv.getElementUtils().getAllMembers((TypeElement) processingEnv.getElementUtils().getTypeElement(packageName + "." + className))) {
                if (element.getKind() == ElementKind.FIELD) {
                    String name = element.getSimpleName().toString();
                    String type = element.asType().toString();
                    sourceBuilder.append("\t\tprivate ").append(type).append(" ").append(name).append(";\n");
                    sourceBuilder.append("\t\t").append(String.format("public Builder %s(%s %s) {", name, type, name)).append("\n");
                    sourceBuilder.append("\t\t\t").append(String.format("this.%s = %s;", name, name)).append("\n");
                    sourceBuilder.append("\t\t\t").append("return this;").append("\n");
                    sourceBuilder.append("\t\t").append("}").append("\n");
                    sourceBuilder.append("\n");
                    allFields.append(name).append(",");
                }
            }

            sourceBuilder.append("\t\t").append(String.format("public %s build() {", className)).append("\n");
            allFields.deleteCharAt(allFields.length() - 1);
            sourceBuilder.append("\t\t\t").append(String.format("return new %s(%s);", className, allFields)).append(
                "\n");
            sourceBuilder.append("\t\t} ").append("\n");
            sourceBuilder.append("\t} ").append("\n");
            sourceBuilder.append("} ").append("\n");

            // Write the generated source code to a file
            JavaFileObject builderFile = processingEnv.getFiler().createSourceFile(packageName + "." + builderClassName);
            try (Writer writer = builderFile.openWriter()) {
                writer.write(sourceBuilder.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, "Failed to generate builder class: " + e.getMessage());
        }
    }
}
