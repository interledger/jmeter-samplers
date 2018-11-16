# JMeter ILP Samplers

JMeter add-on that defines a number of samplers for load testing WebSocket applications.

## Usage

Put the output of the shadowJar task into `$JMETER_HOME/lib/ext` and start JMeter.

## Building

Gradle is used as build tool, so execute

    gradle shadowJar

to build and assemble a single JAR file with all dependencies. If you don't have installed gradle, use the gradle wrapper:

    ./gradlew
    
Gradle can also generate IntelliJ Idea project files for you:

    gradle idea


## Acknowledgements

This plugin is heavily based upon (and depends upon) the [JMeter WebSocket Samplers](https://bitbucket.org/pjtr/jmeter-websocket-samplers) by Peter Doornbosch.

This provided an excellent starting point for sending BTP messages over Websockets. Unfortunately the final version will need to remove all dependencies on his library so that this library can be Apache 2.0 licensed.

## License

The current pre-release version of this program is dependant on an LGPL library and so is necessarily also LGPL licensed.
The final release will be Apache 2.0 licensed.
