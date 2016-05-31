[![Build Status](https://travis-ci.org/husl-colors/husl-java.svg?branch=master)](https://travis-ci.org/husl-colors/husl-java)
[![Package Version](https://img.shields.io/maven-central/v/org.husl-colors/husl.svg)](http://repo1.maven.org/maven2/org/husl-colors/husl/)

# Installation

With Maven:

    <dependency>
        <groupId>org.husl-colors</groupId>
        <artifactId>husl</artifactId>
        <version>1.2</version>
    </dependency>

# Testing

    mvn test

# Maven Deploy

Follow the documentation

http://central.sonatype.org/pages/working-with-pgp-signatures.html

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
    mvn versions:set -DnewVersion=1.2.3
    mvn clean deploy -P release
    