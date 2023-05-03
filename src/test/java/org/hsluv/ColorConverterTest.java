package org.hsluv;

import jakarta.json.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.IOException;
import java.io.InputStream;


public class ColorConverterTest extends TestCase {

    private static final double MAXDIFF = 0.0000000001;
    private static final double MAXRELDIFF = 0.000000001;

    /**
     * modified from
     * https://randomascii.wordpress.com/2012/02/25/comparing-floating-point-numbers-2012-edition/
     */
    private boolean assertAlmostEqualRelativeAndAbs(double a, double b) {
        // Check if the numbers are really close -- needed
        // when comparing numbers near zero.
        double diff = Math.abs(a - b);
        if (diff <= MAXDIFF) {
            return true;
        }

        a = Math.abs(a);
        b = Math.abs(b);
        double largest = (b > a) ? b : a;

        return diff <= largest * MAXRELDIFF;
    }

    private void assertTuplesClose(String label, double[] expected, double[] actual) {
        boolean mismatch = false;
        double[] deltas = new double[expected.length];

        for (int i = 0; i < expected.length; ++i) {
            deltas[i] = Math.abs(expected[i] - actual[i]);
            if (!assertAlmostEqualRelativeAndAbs(expected[i], actual[i])) {
                mismatch = true;
            }
        }

        if (mismatch) {
            System.out.printf("MISMATCH %s\n", label);
            System.out.printf(" expected: %.10f,%.10f,%.10f\n", expected[0], expected[1], expected[2]);
            System.out.printf("   actual: %.10f,%.10f,%.10f\n", actual[0], actual[1], actual[2]);
            System.out.printf("   deltas: %.10f,%.10f,%.10f\n", deltas[0], deltas[1], deltas[2]);
        }

        assertFalse(mismatch);
    }


    public static Test suite() {
        return new TestSuite(ColorConverterTest.class);
    }

    private double[] tupleFromJsonArray(JsonArray arr) {
        return new double[]{
                arr.getJsonNumber(0).doubleValue(),
                arr.getJsonNumber(1).doubleValue(),
                arr.getJsonNumber(2).doubleValue()
        };
    }

    public void testHsluv() throws IOException {
        System.out.println("Running test");
        InputStream snapshotStream = ColorConverterTest.class.getResourceAsStream("/snapshot-rev4.json");

        JsonReader reader = Json.createReader(snapshotStream);
        JsonObject tests = reader.readObject();

        for (String hex : tests.keySet()) {
            JsonObject expected = tests.getJsonObject(hex);
            double[] rgb = tupleFromJsonArray(expected.getJsonArray("rgb"));
            double[] xyz = tupleFromJsonArray(expected.getJsonArray("xyz"));
            double[] luv = tupleFromJsonArray(expected.getJsonArray("luv"));
            double[] lch = tupleFromJsonArray(expected.getJsonArray("lch"));
            double[] hsluv = tupleFromJsonArray(expected.getJsonArray("hsluv"));
            double[] hpluv = tupleFromJsonArray(expected.getJsonArray("hpluv"));

            System.out.println("testing " + hex);

            // forward functions

            double[] rgbFromHex = HUSLColorConverter.hexToRgb(hex);
            double[] xyzFromRgb = HUSLColorConverter.rgbToXyz(rgbFromHex);
            double[] luvFromXyz = HUSLColorConverter.xyzToLuv(xyzFromRgb);
            double[] lchFromLuv = HUSLColorConverter.luvToLch(luvFromXyz);
            double[] hsluvFromLch = HUSLColorConverter.lchToHsluv(lchFromLuv);
            double[] hpluvFromLch = HUSLColorConverter.lchToHpluv(lchFromLuv);
            double[] hsluvFromHex = HUSLColorConverter.hexToHsluv(hex);
            double[] hpluvFromHex = HUSLColorConverter.hexToHpluv(hex);

            assertTuplesClose("hexToRgb", rgb, rgbFromHex);
            assertTuplesClose("rgbToXyz", xyz, xyzFromRgb);
            assertTuplesClose("xyzToLuv", luv, luvFromXyz);
            assertTuplesClose("luvToLch", lch, lchFromLuv);
            assertTuplesClose("lchToHsluv", hsluv, hsluvFromLch);
            assertTuplesClose("lchToHpluv", hpluv, hpluvFromLch);
            assertTuplesClose("hexToHsluv", hsluv, hsluvFromHex);
            assertTuplesClose("hexToHpluv", hpluv, hpluvFromHex);

            // backward functions

            double[] lchFromHsluv = HUSLColorConverter.hsluvToLch(hsluv);
            double[] lchFromHpluv = HUSLColorConverter.hpluvToLch(hpluv);
            double[] luvFromLch = HUSLColorConverter.lchToLuv(lch);
            double[] xyzFromLuv = HUSLColorConverter.luvToXyz(luv);
            double[] rgbFromXyz = HUSLColorConverter.xyzToRgb(xyz);
            String hexFromRgb = HUSLColorConverter.rgbToHex(rgb);
            String hexFromHsluv = HUSLColorConverter.hsluvToHex(hsluv);
            String hexFromHpluv = HUSLColorConverter.hpluvToHex(hpluv);

            assertTuplesClose("hsluvToLch", lch, lchFromHsluv);
            assertTuplesClose("hpluvToLch", lch, lchFromHpluv);
            assertTuplesClose("lchToLuv", luv, luvFromLch);
            assertTuplesClose("luvToXyz", xyz, xyzFromLuv);
            assertTuplesClose("xyzToRgb", rgb, rgbFromXyz);
            assertEquals(hex, hexFromRgb);
            assertEquals(hex, hexFromHsluv);
            assertEquals(hex, hexFromHpluv);

        }
    }
}
