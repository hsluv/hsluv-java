[![Build Status](https://travis-ci.org/hsluv/hsluv-java.svg?branch=master)](https://travis-ci.org/hsluv/hsluv-java)
[![Package Version](https://img.shields.io/maven-central/v/org.hsluv/hsluv.svg)](http://repo1.maven.org/maven2/org/hsluv/hsluv/)
[![Javadocs](http://www.javadoc.io/badge/org.hsluv/hsluv.svg)](http://www.javadoc.io/doc/org.hsluv/hsluv)

About: http://www.hsluv.org

# Installation

If you are using Maven, add the following to your `pom.xml` file:

    <dependency>
        <groupId>org.hsluv</groupId>
        <artifactId>hsluv</artifactId>
        <version>0.2</version>
    </dependency>
    
# Documentation

Javadocs: http://www.javadoc.io/doc/org.hsluv/hsluv

# Testing

    mvn test

# Maven Deploy

Follow the documentation: http://central.sonatype.org/pages/producers.html

Add the following to `~/.m2/settings.xml`:

    <settings>
        <profiles>
            <profile>
                <id>ossrh</id>
                <activation>
                    <activeByDefault>true</activeByDefault>
                </activation>
                <properties>
                    <gpg.executable>gpg</gpg.executable>
                    <gpg.passphrase>*******</gpg.passphrase>
                </properties>
            </profile>
        </profiles>
        <servers>
            <server>
                <id>ossrh</id>
                <username>sonatype-jira-username</username>
                <password>sonatype-jira-pass</password>
            </server>
        </servers>
    </settings>
    
Run:

    mvn clean
    mvn deploy
    mvn versions:set -DnewVersion=1.2
    mvn clean deploy -P release
    