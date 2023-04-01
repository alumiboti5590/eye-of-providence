# Eye of Providence

[![Release](https://jitpack.io/v/alumiboti5590/eye-of-providence.svg)](https://jitpack.io/#alumiboti5590/eye-of-providence)

This is a collection of reusable code pieces for the Alumiboti to use in their robot projects. The goal is to reduce the amount of code that needs to be created or copied year-over-year.

![Eye of Providence](./images/eop.png)

The Eye of Providence is frequently tied to the ~~Alumiboti~~ Illuminati, but it really is meant to represent a higher being watching over humanity. It is one of the more recognized symbols, and we feel as though this repository aims to "watch over" our code ensure it works as well as it can. Does this make any sense? No? Well, it's a neat symbol and sounds cool, so we are sticking with it.

## Documentation

Eye of providence uses `javadocs` and is rebuilt on every deployment to the default branch or release creation. You can view the documentation at <https://javadocs.alumiboti5590.com/>, and then select a version to be taken to the appropriate documentation.

## Using Eye of Providence

In the `build.gradle` of your Robot project, include the following section(s):

```groovy
repositories {
    maven {
        maven { url "https://jitpack.io" }
    }
}
```

```groovy
// This 'dependencies' block should already be in your build.gradle file,
// you will just need to add the `implementation` line below
dependencies {
    // Add this line to the dependencies block. Update the version you want to target
    // instead of the `0.0.0` which is just a filler.
    implementation 'com.alumiboti5590:eye-of-providence:0.0.0'

    // Other dependencies
    // ....
}
```

## Development Information

To get started developing on Eye of Providence, `git clone` this repository using the URL in the `<> Code` dropdown button on the repository home page.

Since this project is Java and uses Gradle, it requires that you have both configured already. This should not be an issue if you have already installed and configured the FRC tools, such as their VSCode extensions.

### Basic Gradle Tasks

As stated, this project uses Gradle to perform many of the important tasks, and these are done trough the `./gradlew` and `.\gradlew.bat` files. The examples below will use `./gradlew`, but use
`.\gradlew.bat` if on a Windows machine _without_ [WSL](https://learn.microsoft.com/en-us/windows/wsl/install) - although running these commands on a Mac- or Linux-based kernel is recommended.

#### Testing

The following command will run all tests in the suite. [JUnit5](https://junit.org/junit5/docs/current/user-guide/) is the testing library being used.

```console
./gradlew test
```

#### Spotless Linting

[Spotless](https://github.com/diffplug/spotless) formats the project to ensure consistent styling choices. The project specifically uses [Spotless for Gradle](https://github.com/diffplug/spotless/tree/main/plugin-gradle) with the [`palantir-java-format](https://github.com/palantir/palantir-java-format).

All Pull Requests will validate that Spotless is applied, and you should run the following command before committing any code upstream.

```console
./gradlew spotlessApply
```

#### Generate Javadocs Locally

The project uses Javadocs hosted on GitHub Pages, but it's helpful to know what your page(s) will look like before getting published.

You can run the following command to generate Javadocs for the current code.

```console
./gradlew javadocs
```

And then, **in VSCode**, navigate to the `lib/build/docs/javadocs` directory in the directory tree and find the `index.html` file under it. Right-click on the `index.html` file and click `Copy Path` - **not** the relative path. Open your browser and Right-Click and Paste (or use `Ctrl-V`) and open the file path to see the documentation.
