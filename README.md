[![Build Status](https://github.com/hsluv/hsluv-java/actions/workflows/test.yml/badge.svg?branch=master)](https://github.com/hsluv/hsluv-java/actions/workflows/test.yml)
[![Maven Central](https://img.shields.io/maven-central/v/org.hsluv/hsluv)](https://central.sonatype.com/artifact/org.hsluv/hsluv)
[![Javadoc](https://javadoc.io/badge2/org.hsluv/hsluv/javadoc.svg)](https://javadoc.io/doc/org.hsluv/hsluv)

About: http://www.hsluv.org

# Installation

If you are using Maven, add the following to your `pom.xml` file:

    <dependency>
        <groupId>org.hsluv</groupId>
        <artifactId>hsluv</artifactId>
        <version>1.0.2</version>
    </dependency>

# Usage

The API is designed to avoid heap allocation. The `HSLuv` class defines the following public fields:

- RGB: `hex:String`, `rgb_r` [0;1], `rgb_g` [0;1], `rgb_r` [0;1]
- CIE XYZ: `xyz_x`, `xyz_y`, `xyz_z`
- CIE LUV: `luv_l`, `luv_u`, `luv_v`
- CIE LUV LCh: `lch_l`, `lch_c`, `lch_h`
- HSLuv: `hsluv_h` [0;360], `hsluv_s` [0;100], `hsluv_l` [0;100]
- HPLuv: `hpluv_h` [0;360], `hpluv_p` [0;100], `hpluv_l` [0;100]

To convert between color spaces, simply set the properties of the source color space, run the
conversion methods, then read the properties of the target color space.

Use the following methods to convert to and from RGB:

- HSLuv: `hsluvToRgb()`, `hsluvToHex()`, `rgbToHsluv()`, `hexToHsluv()`
- HPLuv: `hpluvToRgb()`, `hpluvToHex()`, `rgbToHpluv()`, `hexToHpluv()`

Use the following methods to do step-by-step conversion:

- Forward: `hsluvToLch()` (or `hpluvToLch()`), `lchToLuv()`, `luvToXyz()`, `xyzToRgb()`, `rgbToHex()`
- Backward: `hexToRgb()`, `rgbToXyz()`, `xyzToLuv()`, `luvToLch()`, `lchToHsluv()` (or `lchToHpluv()`)

For advanced usage, we also export the [bounding lines](https://www.hsluv.org/math/) in slope-intercept
format, two for each RGB channel representing the limit of the gamut.

- R < 0: `r0s`, `r0i`
- R > 1: `r1s`, `r1i`
- G < 0: `g0s`, `g0i`
- G > 1: `g1s`, `g1i`
- B < 0: `b0s`, `b0i`
- B > 1: `b1s`, `b1i`

Example:

```java
HsluvColorConverter conv = new HsluvColorConverter();
conv.hsluv_h = 10;
conv.hsluv_s = 75;
conv.hsluv_l = 65;
conv.hsluvToHex();
System.out.println(conv.hex); // Will print "#ec7d82"
float r = Math.max(0, (float) conv.rgb_r);
float g = Math.max(0, (float) conv.rgb_g);
float b = Math.max(0, (float) conv.rgb_b);
var color = new java.awt.Color(r, g, b);
// For Android
var color = android.graphics.Color.rgb(r, g, b);
```

# Testing

    mvn test

# Deployment

Releases are published to Maven Central by the [`release.yml`](.github/workflows/release.yml)
workflow, triggered by a `v*` tag.

One-time setup: generate a Portal token at https://central.sonatype.com, then store the
credentials as repo secrets:

```bash
gh secret set GPG_PRIVATE_KEY --repo hsluv/hsluv-java < signing-key.asc
gh secret set GPG_PASSPHRASE --repo hsluv/hsluv-java --body "<passphrase>"
gh secret set CENTRAL_TOKEN_USERNAME --repo hsluv/hsluv-java --body "<token username>"
gh secret set CENTRAL_TOKEN_PASSWORD --repo hsluv/hsluv-java --body "<token password>"
```

Then bump the version in `pom.xml` and create a release:

```bash
gh release create v1.0.2 --generate-notes
```
