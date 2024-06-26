package org.hsluv;

import jakarta.json.*;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

public class ColorConverterTest {
    static void assertFloatClose(double expected, double actual) {
        if (Math.abs(expected - actual) > 1e-10) {
            System.out.println(expected);
            System.out.println(actual);
            throw new RuntimeException("Not equals");
        }
    }

    static void assertClose(HsluvColorConverter expected, HsluvColorConverter actual) {
        assertEquals(expected.hex, actual.hex);
        assertFloatClose(expected.rgb_r, actual.rgb_r);
        assertFloatClose(expected.rgb_g, actual.rgb_g);
        assertFloatClose(expected.rgb_b, actual.rgb_b);
        assertFloatClose(expected.xyz_x, actual.xyz_x);
        assertFloatClose(expected.xyz_y, actual.xyz_y);
        assertFloatClose(expected.xyz_z, actual.xyz_z);
        assertFloatClose(expected.luv_l, actual.luv_l);
        assertFloatClose(expected.luv_u, actual.luv_u);
        assertFloatClose(expected.luv_v, actual.luv_v);
        assertFloatClose(expected.lch_l, actual.lch_l);
        assertFloatClose(expected.lch_c, actual.lch_c);
        assertFloatClose(expected.lch_h, actual.lch_h);
        assertFloatClose(expected.hsluv_h, actual.hsluv_h);
        assertFloatClose(expected.hsluv_s, actual.hsluv_s);
        assertFloatClose(expected.hsluv_l, actual.hsluv_l);
        assertFloatClose(expected.hpluv_h, actual.hpluv_h);
        assertFloatClose(expected.hpluv_p, actual.hpluv_p);
        assertFloatClose(expected.hpluv_l, actual.hpluv_l);
    }

    static double getSample(JsonObject s, String cs, int index) {
        return s.getJsonArray(cs).getJsonNumber(index).doubleValue();
    }

    @Test
    public void testHsluv() throws IOException {
        System.out.println("Running test");
        InputStream snapshotStream = ColorConverterTest.class.getResourceAsStream("/snapshot-rev4.json");

        JsonReader reader = Json.createReader(snapshotStream);
        JsonObject tests = reader.readObject();
        HsluvColorConverter conv = new HsluvColorConverter();

        for (String hex : tests.keySet()) {
            JsonObject s = tests.getJsonObject(hex);
            HsluvColorConverter sample = new HsluvColorConverter();
            sample.hex = hex;
            sample.rgb_r = getSample(s, "rgb", 0);
            sample.rgb_g = getSample(s, "rgb", 1);
            sample.rgb_b = getSample(s, "rgb", 2);
            sample.xyz_x = getSample(s, "xyz", 0);
            sample.xyz_y = getSample(s, "xyz", 1);
            sample.xyz_z = getSample(s, "xyz", 2);
            sample.luv_l = getSample(s, "luv", 0);
            sample.luv_u = getSample(s, "luv", 1);
            sample.luv_v = getSample(s, "luv", 2);
            sample.lch_l = getSample(s, "lch", 0);
            sample.lch_c = getSample(s, "lch", 1);
            sample.lch_h = getSample(s, "lch", 2);
            sample.hsluv_h = getSample(s, "hsluv", 0);
            sample.hsluv_s = getSample(s, "hsluv", 1);
            sample.hsluv_l = getSample(s, "hsluv", 2);
            sample.hpluv_h = getSample(s, "hpluv", 0);
            sample.hpluv_p = getSample(s, "hpluv", 1);
            sample.hpluv_l = getSample(s, "hpluv", 2);
            conv.hex = hex;
            conv.hexToHsluv();
            assertClose(conv, sample);
            conv.hexToHpluv();
            assertClose(conv, sample);
            conv.hsluv_h = sample.hsluv_h;
            conv.hsluv_s = sample.hsluv_s;
            conv.hsluv_l = sample.hsluv_l;
            conv.hsluvToHex();
            assertClose(conv, sample);
            conv.hpluv_h = sample.hpluv_h;
            conv.hpluv_p = sample.hpluv_p;
            conv.hpluv_l = sample.hpluv_l;
            conv.hpluvToHex();
            assertClose(conv, sample);
        }
    }
}
