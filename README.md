[![Build Status](https://github.com/hsluv/hsluv-java/actions/workflows/test.yml/badge.svg)](https://github.com/hsluv/hsluv-java/actions/workflows/test.yml)
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

# Deployment

Docs: 
- https://central.sonatype.org/publish/publish-maven/
- https://central.sonatype.org/publish/requirements/gpg/

Set `~/m2/settings.xml`:

```xml
<settings>
    <servers>
        <server>
            <id>ossrh</id>
            <username>hsluv</username>
            <password>REDACTEDREDACTEDREDACTED</password>
        </server>
    </servers>
    <profiles>
        <profile>
            <id>ossrh</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
            <properties>
                <gpg.passphrase>REDACTEDREDACTEDREDACTED</gpg.passphrase>
            </properties>
        </profile>
    </profiles>
</settings>
```

Then run:

```bash
mvn versions:set -DnewVersion=0.3 # bump version
mvn clean deploy -P release
```

