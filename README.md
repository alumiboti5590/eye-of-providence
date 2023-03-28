# Eye of Providence

This is a collection of reusable code pieces for the Alumiboti to use in their robot projects. The goal is to reduce the amount of code that needs to be created or copied year-over-year.

![Eye of Providence](./images/eop.png)

The Eye of Providence is frequently tied to the ~~Alumiboti~~ Illuminati, but it really is meant to represent a higher being watching over humanity. It is one of the more recognized symbols, and we feel as though this repository aims to "watch over" our code ensure it works as well as it can. Does this make any sense? No? Well, it's a neat symbol and sounds cool, so we are sticking with it.

## Documentation

Eye of providence uses `javadocs` and is rebuilt on every deployment to the default branch or
release creation. You can view the documentation at <https://javadocs.alumiboti5590.com/>,
and then select a version to be taken to the appropriate documentation.

## Using Eye of Providence

In the `build.gradle` of your Robot project, include the following section(s):

```groovy
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/alumiboti5590/*")
    }
}
```

```groovy
// This 'dependencies' block should already be in your build.gradle file,
// you will just need to add the `implementation` line below
dependencies {
    // Add this line to the dependencies block. Update the version you want to target
    // instead of the `0.0.0` which is just a filler.
    implementation 'com.alumiboti5590:eop:0.0.0'
    
    // Other dependencies
    // ....
}
```
