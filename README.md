Nerot
=====

Nerot lets you to easily schedule tasks on the fly (using Quartz and Spring), calling any object instance's method with any arguments you specify (class method or instance method) and can cache the result for access in a separate thread with built-in RSS retrieval and caching (using Rome). Scheduling supports the Quartz cron-like syntax. It uses an in-memory store for quick asynchronous retrieval, but Tasks and Storing are fully customizable and extendible via the API (uses interfaces, etc.).

Nerot is somewhat like an unreliable but incredibly fast caching system that lets you call anything in Java (well, it needs to be tested with Generics, etc., but handles wrappers and primitives in argument lists). The retrieval is always fast because it never waits, even if the task to get a valid response failed (in which it returns the last in-memory result if no exceptions were thrown, or null if none were ever performed correctly).

Unlike most caching solutions, it is not meant to always provide the content. For example, if you use the RSS functionality to schedule feed retrieval, and the feed was down at least check by the timer, and it never retrieved the feed before while the JVM was up, your code to use Nerot to retrieve the feed will return null, even if it is currently being served. In addition, it likely will cause a higher load on the RSS server than other caching solutions. It will attempt via Quartz timed job to get the feed whether it has been requested or not. The tradeoff for these is that you have almost guaranteed quick delivery and no intentional dependency on feeds or database being available to return quickly (even though it might just return null).

### Why use Nerot?

It's fast and relatively easy to use.

In our case, we had an older portal infrastructure that would be adversely affected by feeds that were down, even when portlets timed out attempting to get the feed. We could have spent time ensuring that Rome, etc. timed out before the portlet timeout. But returning immediately on feed requests, we get an even faster result everytime, and it is cached, even though the result is not guaranteed to always come back, and although it creates more threads and request overhead on the external resources we query. But most importantly, we'd much rather provide a feed quickly almost all of the time than have the server fail because it couldn't handle timeouts correctly.

### Why not?

You may not want to use Nerot in place of the myriad of other caching solutions. The service running the scheduler will be higher request load on external resources, and it isn't guaranteed delivery of content.

Despite the API being easy to use, it uses Spring for configuration and Quartz for scheduling, and perhaps these or other dependencies may get in the way.

It requires Java 5 or later.

### What's with the name?

Nerot is short and unique, which is the primary reason it was chosen.

Nerot was originally short for "Nero task", since it would schedule tasks like getting RSS (via Rome) and providing results without caring whether the task crashed and burned or not, in other words it "played the fiddle" while Rome (for Java RSS) burned.

It seems also that there is a Jewish tradition called Nerot or Hadlakat Nerot which is a commandment for lighting candles. In a way, lighting candles is somewhat like starting new scheduled tasks.

### Build

If you wish to build and install into your personal Maven 2 repository, use:

    cd (some directory)
    git clone http://github.com/garysweaver/nerot.git
    cd nerot
    mvn clean install

### Use as a Maven 2 Dependency

*The following doesn't work yet, but hopefully should have this up shortly...*

If you wish to just use it as part of your project as a dependency, use this in your pom.xml (Maven 2):

   <dependency>
     <groupId>nerot</groupId>
     <artifactId>nerot</artifactId>
     <version>1.0-SNAPSHOT</version>
   </dependency>

   ...
   
   <repository>
       <snapshots>
           <enabled>true</enabled>
       </snapshots>
       <id>nerot-repository</id>
       <name>nerot Repository</name>
       <url>http://garysweaver.github.com/nerot/m2</url>
   </repository>

### Download

If you'd like to use with Ivy, etc. or just as an Ant dependency and need the jar, see [downloads][rel] it.

### Nerot Configuration

Add a file in the classpath or WEB-INF/classes (or src/main/resources in a standard Maven 2 project) called nerot.xml, or perform similar configuration via some other Spring mechanism:

See the [nerot.xml][config] included in the project source for an example of usage.

### Usage

See the [system tests][test] for now, which provide examples of RSS, calling Math.random(), and calling a custom Class instance that doesn't need anything special to make it work with Nerot.

### License

Copyright (c) 2010 Gary S. Weaver, released under the [MIT license][lic].

[lic]: http://github.com/garysweaver/nerot/blob/master/LICENSE
[rel]: http://garysweaver.github.com/nerot/releases
[config]: http://github.com/garysweaver/nerot/blob/master/src/main/resources/nerot.xml
[test]: http://github.com/garysweaver/nerot/blob/master/src/test/java/nerot/SystemTest.java
