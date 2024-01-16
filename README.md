# Sentiment Analysis Tool for Comments using Roberta Model

## Overview

<!-- Plugin description -->
This is the source code repo for **Comment Sentiment Analyzer** , a plugin for IntelliJ IDEA platform that performs binary sentiment classification on comments from Kotlin files. 
The plugin implements **a pipeline sentiment analysis work**, including comment extraction, preprocessing comment text, text tokenization, prediction using pre-trained roberta model and prediction visualization.
Development of this plugin uses the [Intellij Platform Plugin Template][gh:template].


[gh:template]: https://github.com/JetBrains/intellij-platform-plugin-template?tab=readme-ov-file#getting-started
<!-- Plugin description end -->

This README will cover the following content:
<!-- TOC -->
* [Sentiment Analysis Tool for Comments using Roberta Model](#sentiment-analysis-tool-for-comments-using-roberta-model)
  * [Overview](#overview)
  * [Prerequisites](#prerequisites)
  * [Environment Setup](#environment-setup)
  * [Testing](#testing)
  * [Running](#running)
  * [Troubleshooting](#troubleshooting)
      * [1. Duplicate Handling Strategy](#1-duplicate-handling-strategy)
      * [2. Resource Loading with JAR Application](#2-resource-loading-with-jar-application)
      * [3. Resource Retrieval of Plugin](#3-resource-retrieval-of-plugin)
      * [4. InlayHintProvider Trigger](#4-inlayhintprovider-trigger)
      * [5. Tokenizer Dependencies](#5-tokenizer-dependencies)
  * [Acknowledgments](#acknowledgments)
<!-- TOC -->

---

## Prerequisites

Before you begin, ensure you have the following installed:
- Java Development Kit (JDK): version 17

---

## Environment Setup

1. Clone the Plugin Repository
   ```sh
   git clone https://github.com/d-lu-19/sa_roberta.git
   ```

[gh:project]: https://drive.google.com/file/d/1SmPjZ4gmg4QVQozXJcZmg5igEhkK8rbB/view?usp=sharing 
2. Run the command in the terminal to initiate the plugin
   ```sh
   ./gradlew build
   ```
   This will start initialization of the project and ensure the correct configurations of Gradle(8.5) is downloaded.

---

## Testing

Run the command in the terminal to run tests.
   ```sh
  ./gradlew check
   ```
This will run three tests built for testing plugin functions, including:

- **FileProcessTest**: Testing functions that need to load files from resources, including extract comments from files, load model from resources
    ```sh
    ./gradlew test --tests com.github.dlu19.saroberta.FileProcessTest
    ```
- **SentimentAnalysisTest**: Testing main functions of sentiment analysis, including comment text tokenization and sentiment prediction
    ```shell
    ./gradlew test --tests com.github.dlu19.saroberta.SentimentAnalysisTest
    ```
- **TokenizerResourcesTest**: Testing functions of tokenization process, including bytepair encoding and Bigram mapping
    ```shell
    ./gradlew test --tests com.github.dlu19.saroberta.RobertaTokenizerResourcesTest
    ```


---

## Running
This will launch a new instance of IntelliJ IDEA with the plugin installed.

1. Run the command in the terminal to initiate the plugin **or** Select run configuration -> Click `Run > Run 'Run MyPlugin'`.
   ```sh
   ./gradlew runIde
   ```

2. Open a Kotlin project in the launched IntelliJ IDEA instance

---

## Troubleshooting

This section describes some of the technical obstacles endured in the development process.

#### 1. Duplicate Handling Strategy

*Problem*: Meet `Error: Entry <...> is a duplicate but no duplicate handling strategy has been set.`  when accessing files under resources

*Cause*: The build process of plugin will copy essential files including resource, leading to the duplicate of files.

*Solution*: Add the following lines to `build.gradle.kts`

```sh
  project.tasks.named("processResources", Copy::class.java) {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
  project.tasks.named("processTestResources", Copy::class.java) {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    }
```

#### 2. Resource Loading with JAR Application

*Problem*: Tried to employ published [RoBERTa Java Tokenizer][gh:tokenizer] directly. The packaged JAR application of it cannot load tokenizer file Dependencies.

[gh:tokenizer]: https://github.com/purecloudlabs/roberta-tokenizer

*Cause*: When using a published JAR in a Kotlin project, traditional file paths don't work as expected because the resources are bundled inside the JAR and not available for local filesystem.

*Solution*: Implemented the logic of RoBERTa tokenizer following [RoBERTa Java Tokenizer][gh:template] using Kotlin. 

#### 3. Resource Retrieval of Plugin

*Problem*: When retrieving resource file in with resource path registered correctly in plugin.xml, the plugin fails to find the file under resource directory.

*Cause*: When the plugin is running, the resources are packaged into the plugin JAR file. The resource file cannot be accessed from JAR file.

*Solution*: Read the resource file as inputstream using `this::class.java.classLoader.getResourceAsStream(path)`.


#### 4. InlayHintProvider Trigger

*Problem*: Inlay Hints not showing besides the comments.

*Causes*: 
- The InlayHintProvider collector not triggered upon hint information is generated.
- The current opened file is not under a correct project structure

*Solution*: 
- Use `DaemonCodeAnalyzer.getInstance(project).restart(file)` to trigger the collector upon hint information obtained (refer to [this][gh:collector]). 
  **Note** that hint related APIs are still in experimental stage, relevant functions might change in the future.
- Ensure to open a complete and correct project in the Intellij IDEA instance so that the hint provider can be correctly configured.

[gh:collector]: https://youtrack.jetbrains.com/issue/IDEA-333164?_gl=1*7jvo5*_ga*ODcwOTM1NjczLjE3MDM3MTEwMjM.*_ga_9J976DJZ68*MTcwNDk4OTQyNS4zMy4xLjE3MDQ5ODk0OTguNjAuMC4w&_ga=2.143911740.804732119.1704761910-870935673.1703711023

#### 5. Tokenizer Dependencies

*Problem*: The sentiment analysis result is always negative.

*Cause*: Obtained dependency files (vocabulary.json and merges.txt) for tokenizer from [RoBERTa Java Tokenizer][gh:template]. 
The files gives example on how to describe the tokenization rules and are rather simple, leading to the token generated with low dimension.

*Solution*: Obtained the [roberta-base-vocab.json][gh:vocab] and [robrata-base-merges.txt][gh:merges] files originally used for RoBERTa model implementation.

[gh:vocab]: https://s3.amazonaws.com/models.huggingface.co/bert/roberta-base-vocab.json
[gh:merges]: https://s3.amazonaws.com/models.huggingface.co/bert/roberta-base-merges.txt

## Acknowledgments
This project adapts the [RoBERTa Java Tokenizer][gh:tokenizer] implementations for tokenizer building in Kotlin.


