1. Use native way for the processor. google `AutoService` doesn't seem work smoothly.
When `MyClassBuilder` is not generated, `AutoService` won't be able to compile successfully. But with native way, compile twice: 1). with -proc:none, it only compile the processors(AbstractProcessor) to generate the class 2). compile the whole project

2. In order to configure annotation processor:
	1). Create the BuildProcessor to decide what type of class it generates
	2). Add resoures/META-INF/services/javax.annotation.processing.Processor. Add "org.example.builder.BuilderProcessor" in the file. This is defined by oralce about how to compile the Processors.
	3). Add [2 executions](https://github.com/allenlipeng47/AnnotationProcessorLib/blob/error-free-compile/pom.xml#L24-L42) in `maven-compiler-plugin` pom. First execution only handle the Processor. Second one compiles whole project.

3. For the use of the common-lib, simple add the dependency in the pom, it will just work! Add annotion defined in common-lib, then compile, it will just work. This compile work for both command line 'mvn compile' and click compile button in maven intelliJ window

4. `mvn install` will build the project and install in local .m2 folder.

5. `mvn deploy` will deploy to remote
