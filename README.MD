# Brainfuck implementations

This project was inspired by @kostya's [benchmarks](https://github.com/kostya/benchmarks).

It implements Brainfuck in Java, Kotlin and other languages.

## Compile the jar

* Java only

```
./gradlew jar 
```

* Kotlin

```
./gradlew shadowJar
```

## Run

### With Gradle

* Java

```
./gradlew runJava -Pfile=src/test/resources/example.bf
```

* Kotlin

```
./gradlew runKotlin -Pfile=src/test/resources/example.bf
```

Properties accepted by above tasks:

* file - set to the bf file you wish to run
* count - number of times to run the bf script
* original - if present, runs the original Java implemention from kostya

### With java

> See above how to create the jars first.

* Java

```
java -cp build/libs/bf-jvm.jar Bf src/test/resources/example.bf
```

> to run the original Java implementation, run class `bf` instead of `Bf`.

* Kotlin

```
java -cp build/libs/bf-jvm-all.jar BfKotlin src/test/resources/example.bf
```

Both Java and Kotlin programs accept a second `count` argument to execute the
bf script multiple times.

For example, to run the script 5 times with Java:

```
java -cp build/libs/bf-jvm.jar Bf src/test/resources/example.bf 5
```


