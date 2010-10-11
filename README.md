Nerot
=====

Nerot lets you to easily schedule tasks on the fly (using Quartz and Spring), calling any object instance's method with any arguments you specify (class method or instance method) and can cache the result for access in a separate thread with built-in RSS retrieval and caching (using Rome). Scheduling supports the Quartz cron-like syntax. It uses an in-memory store for quick asynchronous retrieval, but Tasks and Storing are fully customizable and extendible via the API (uses interfaces, etc.).

Nerot is somewhat like an unreliable but incredibly fast caching system that lets you call static and instance methods on custom Java classes by wrapping them in a GenericTask, or provides implementations for some tasks (like getting an RSS feed). The retrieval is always fast because it never waits, even if the task to get a valid response failed (in which case, it returns the last in-memory result if no exceptions were thrown, or null if none were ever performed correctly).

Unlike most caching solutions, it is not meant to always provide the content. For example, if you use the RSS functionality to schedule feed retrieval, and the feed was down every time Nerot checked it, the attempt to retrieve the feed via Nerot will return null, even if the feed is currently working. In addition, using Nerot will likely cause a higher load on the RSS server than other caching solutions. It will attempt via Quartz timed job to get the feed whether it has been requested or not. The tradeoff for these is that you have almost guaranteed quick delivery and no intentional dependency on feeds or database being available to return quickly (even though it might just return null).

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

If you wish to just use it as part of your project as a dependency, use this in your pom.xml (Maven 2):

    <dependency>
      <groupId>nerot</groupId>
      <artifactId>nerot</artifactId>
      <version>2.0</version>
    </dependency>
   
Along with the repository:

    <repository>
      <id>nerot-repository</id>
      <name>Nerot Repository</name>
      <url>http://garysweaver.github.com/nerot/m2/releases</url>
    </repository>

### Download

If you'd like to use with Ivy, etc. or just as an Ant dependency and need the jar, see [downloads][rel].

### API

[Latest Nerot API][apidocs]

### Configuration/Usage

1. Either just include [nerot.xml][config] as one of the contexts to load, or add its contents into your Spring context file, or just do the equivalent.

2. Have your controller (or other Spring bean) set Nerot to the nerot bean instance defined in the [nerot bean's config][config]:

       <!-- Controllers -->
       <bean name="yourController" class="com.acme.YourController">
           <property name="nerot" ref="nerot"/>
           ...
       </bean>

3. If using Spring, you can use the implementation InitializingBean and definition of afterPropertiesSet() (or similar hook) as a way to schedule the task execution immediately after properties are set on the bean. This may not be fast enough for Nerot to have stored the result in the Store in some cases, so you could potentially add a delay at the end of the hook, but this isn't recommended. Then, in the rendering method, you just call Nerot to get the result from the Store. See [PortletController.java][example] for an example.

### Debugging

To enable logging to console, you may add a log4j.properties file to the classpath containing something like:

    log4j.rootLogger=WARN, A1
    
    log4j.appender.A1=org.apache.log4j.ConsoleAppender
    log4j.appender.A1.layout=org.apache.log4j.TTCCLayout
    log4j.appender.A1.layout.ContextPrinting=enabled
    log4j.appender.A1.layout.DateFormat=ISO8601
    
    log4j.logger.nerot=DEBUG

### Release History

v2.0 - Changes to method names/API for clarification. Addition of initial task execution option/default.

v1.0 - Initial Release. Support for RSS and generic tasks.

### License

Copyright (c) 2010 Gary S. Weaver, released under the [MIT license][lic].

[lic]: http://github.com/garysweaver/nerot/blob/master/LICENSE
[rel]: http://garysweaver.github.com/nerot/releases
[config]: http://github.com/garysweaver/nerot/blob/master/src/main/resources/nerot.xml
[example]: http://github.com/garysweaver/nerot/blob/master/examples/PortletController.xml
[test]: http://github.com/garysweaver/nerot/blob/master/src/test/java/nerot/SystemTest.java
[apidocs]: http://garysweaver.github.com/nerot/apidocs
