# mod-thymeleaf

This project is used as an example of how to build vert.x modules using some of the developer support tools provided by the vert.x project.


## gradle-plugin

The vert.x Gradle plugin provides classpath management for a separate sourceSet for integration testing in combination with the vertx-junit-annotations (see below).  Add the following to the top of your build.gradle file:

    buildscript {
      repositories {
        maven { url 'https://oss.sonatype.org/content/repositories/snapshots' }
        mavenCentral()
      }
      dependencies {
        classpath "org.vert-x:gradle-plugin:1.3.0-SNAPSHOT"
      }
    }

    apply plugin: 'vertx'

The 'gradle tasks' command will show some additional tasks, but the plugin ties them into the standard Gradle 'test', 'assemble' meta-tasks so you can run 'gradle build' or 'gradle check' as you would do normally.

See the [gradle-plugin project](https://github.com/vert-x/gradle-plugin) for more information.

## vertx-junit-annotations

Add JUnit and the vert.x annotation support for integration testing to your project:

    dependencies {
      ...

      testCompile "junit:junit:$junitVersion"
      testCompile "org.vert-x:vertx-junit-annotations:$junitAnnoVersion"
    }

See the [vertx-junit-annotations](https://github.com/vert-x/vertx-junit-annotations) for more information.


