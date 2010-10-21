Nerot
=====

Nerot schedules execution of a Java instance or static method (with arguments you provide) and stores the result in-memory for quick asynchronous retrieval. Basically, it is like traditional response caching, only faster. You never wait on a request after cache expires, because cache never expires. Cache is only refreshed when the scheduled task executes without error. You can wrap any class or Object instance in a GenericTask to call it and store the result in cache. If you are interested in getting an RSS feed, you can use the built-in RSS feed retrieval task. If you are interested in just doing an HTTP GET request (without authentication), you can use the built-in HTTP GET task. Tasks and Stores are fully customizable and extensible.

Note that unlike most caching solutions, Nerot uses unreliable scheduled caching, and therefore does not always provide the content requested. For example, if you use the RSS functionality to schedule feed retrieval, and the feed was down every time Nerot checked, the attempt to retrieve the feed via Nerot will return null, even if the feed is currently working.

In addition, Nerot would likely cause a higher load than other caching solutions. For example, if using an RSS task, it attempts on the schedule provided to refresh the feed whether a client has requested it recently or not.

But the tradeoff for additional task executions and the possibility of sometimes returning null, is that Nerot should in many cases provide much faster delivery of the result.

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
      <version>3.2</version>
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

       <bean name="yourController" class="com.acme.YourController">
           <property name="nerot" ref="nerot"/>
           ...
       </bean>

3. If using Spring, you can use the implementation InitializingBean and definition of afterPropertiesSet() (or similar hook) as a way to schedule the task execution immediately after properties are set on the bean. Nerot has a configuable and optional delay to help the cache get populated from what is probably the first execution (called the "prime run"). Then, in the rendering method, you just call Nerot to get the result from the Store. See [PortletController.java][PortletController.java] for an example.

(In 3.2, we've also tried to add support for autowiring, but autowiring from a parent context (like the Root Web Application Spring context) to child context (like a portlet or servlet application Spring context) doesn't work, so you would have to access the context that Nerot is in from a child context.)

### Debugging

To enable logging to console, you may add a log4j.properties file to the classpath containing something like:

    log4j.rootLogger=WARN, A1
    
    log4j.appender.A1=org.apache.log4j.ConsoleAppender
    log4j.appender.A1.layout=org.apache.log4j.TTCCLayout
    log4j.appender.A1.layout.ContextPrinting=enabled
    log4j.appender.A1.layout.DateFormat=ISO8601
    
    log4j.logger.nerot=DEBUG

Or, if you'd like Perf4J statistics to periodically show up in logs, add something like the following log4j.xml to your classpath:

    <?xml version="1.0" encoding="UTF-8" ?>
    <!DOCTYPE log4j:configuration SYSTEM "log4j.dtd">
    <log4j:configuration xmlns:log4j="http://jakarta.apache.org/log4j/">
        <appender class="org.apache.log4j.ConsoleAppender" name="A1">
            <layout class="org.apache.log4j.TTCCLayout">
                <param value="enabled" name="ContextPrinting"/>
                <param value="ISO8601" name="DateFormat"/>
            </layout>
        </appender>
        <appender name="console" class="org.apache.log4j.ConsoleAppender">
            <layout class="org.apache.log4j.PatternLayout">
                <param name="ConversionPattern" value="%-5p %c{1} - %m%n"/>
            </layout>
        </appender>
        <appender name="CoalescingStatistics"
                  class="org.perf4j.log4j.AsyncCoalescingStatisticsAppender">
            <param name="TimeSlice" value="5000"/>
            <appender-ref ref="nerotPerf4JAppender"/>
        </appender>
        <appender name="nerotPerf4JAppender" class="org.apache.log4j.FileAppender">
            <param name="File" value="nerot-perf4j.log"/>
            <layout class="org.apache.log4j.PatternLayout">
                <param name="ConversionPattern" value="%m%n"/>
            </layout>
        </appender>
        <logger name="nerot">
            <level value="info"/>
        </logger>
        <logger name="org.perf4j.TimingLogger" additivity="false">
            <level value="INFO"/>
            <appender-ref ref="CoalescingStatistics"/>
        </logger>
        <root>
            <level value="warn"/>
            <appender-ref ref="A1"/>
        </root>
    </log4j:configuration>

### Release History

v3.2 - Replaced commons-logging with slf4j for better OSGi compliance. Added support for interval-based scheduling via Quartz. Replaced log4j.properties with log4j.xml. Refactored task error handling and task start and stop logging into BaseTask. Implementing perf4j usage to support getting stats on tasks via BaseTask.

v3.1 - Fixed access to a method in Nerot class and added MANIFEST.MF in attempt to help people trying to load as OSGi bundle.

v3.0 - More changes to method names/API for simplification and better class organization. Addition of HTTP GET task. Addition of prime run and related Primeable interface to make it easier to define tasks that check after schedule to try to give the optional first execution time to populate cache.

v2.0 - Changes to method names/API for clarification. Addition of initial task execution option/default.

v1.0 - Initial Release. Support for RSS and generic tasks.

### License

Copyright (c) 2010 Gary S. Weaver, released under the [MIT license][lic].

[lic]: http://github.com/garysweaver/nerot/blob/master/LICENSE
[rel]: http://garysweaver.github.com/nerot/releases
[config]: http://github.com/garysweaver/nerot/blob/master/src/main/resources/nerot.xml
[PortletController.java]: http://github.com/garysweaver/nerot/blob/master/examples/PortletController.java
[test]: http://github.com/garysweaver/nerot/blob/master/src/test/java/nerot/SystemTest.java
[apidocs]: http://garysweaver.github.com/nerot/apidocs
