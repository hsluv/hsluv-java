package org.huslcolors;

import com.bluelinelabs.logansquare.LoganSquare;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Scanner;


public class ColorConverterTest extends TestCase {

    private static final double MAXDIFF = 0.000000001;
    private static final double MAXRELDIFF = 0.00000001;

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

    private void assertTuplesClose(String label, double[] a, double[] b) {
        boolean mismatch = false;
        double[] deltas = new double[a.length];

        for (int i = 0; i < a.length; ++i) {
            deltas[i] = a[i] - b[i];
            if (!assertAlmostEqualRelativeAndAbs(a[i], b[i])) {
                mismatch = true;
            }
        }

        if (mismatch) {
            System.out.println("mismatch: " + label);
            System.out.printf("a: %.32f,%.32f,%.32f\n", a[0], a[1], a[2]);
            System.out.printf("b: %.32f,%.32f,%.32f\n", b[0], b[1], b[2]);
            System.out.printf("deltas: %.32f,%.32f,%.32f\n", deltas[0], deltas[1], deltas[2]);
        }
    }


    public static Test suite() {
        return new TestSuite(ColorConverterTest.class);
    }

    public void testHusl() throws IOException {
        System.out.println("Running test");
        InputStream snapshotStream = ColorConverterTest.class.getResourceAsStream("/snapshot-rev4.json");
        String snapshotString = new Scanner(snapshotStream, "UTF-8").useDelimiter("\\A").next();

        Map<String, HUSLTestCase> tests = LoganSquare.parseMap(snapshotString, HUSLTestCase.class);

        for (Map.Entry<String, HUSLTestCase> pair : tests.entrySet()) {

            HUSLTestCase expected = pair.getValue();
            String hex = pair.getKey();

            System.out.println(hex);

            // forward functions
            double[] rgbFromHex = HUSLColorConverter.hexToRgb(hex);
            double[] xyzFromRgb = HUSLColorConverter.rgbToXyz(expected.rgb);
            double[] luvFromXyz = HUSLColorConverter.xyzToLuv(expected.xyz);
            double[] lchFromLuv = HUSLColorConverter.luvToLch(expected.luv);
            double[] huslFromLch = HUSLColorConverter.lchToHusl(expected.lch);
            double[] huslpFromLch = HUSLColorConverter.lchToHuslp(expected.lch);
            double[] huslFromHex = HUSLColorConverter.hexToHusl(hex);
            double[] huslpFromHex = HUSLColorConverter.hexToHuslp(hex);
            // backward functions
            double[] lchFromHusl = HUSLColorConverter.huslToLch(expected.husl);
            double[] lchFromHuslp = HUSLColorConverter.huslpToLch(expected.huslp);
            double[] luvFromLch = HUSLColorConverter.lchToLuv(expected.lch);
            double[] xyzFromLuv = HUSLColorConverter.luvToXyz(expected.luv);
            double[] rgbFromXyz = HUSLColorConverter.xyzToRgb(expected.xyz);
            String hexFromRgb = HUSLColorConverter.rgbToHex(expected.rgb);
            String hexFromHusl = HUSLColorConverter.huslToHex(expected.husl);
            String hexFromHuslp = HUSLColorConverter.huslpToHex(expected.huslp);

            assertTuplesClose("hexToRgb", expected.rgb, rgbFromHex);
            assertTuplesClose("rgbToXyz", expected.xyz, xyzFromRgb);
            assertTuplesClose("xyzToLuv", expected.luv, luvFromXyz);
            assertTuplesClose("luvToLch", expected.lch, lchFromLuv);
            assertTuplesClose("lchToHusl", expected.husl, huslFromLch);
            assertTuplesClose("lchToHuslp", expected.huslp, huslpFromLch);
            assertTuplesClose("hexToHusl", expected.huslp, huslFromHex);
            assertTuplesClose("hexToHuslp", expected.huslp, huslpFromHex);

            assertTuplesClose("huslToLch", expected.lch, lchFromHusl);
            assertTuplesClose("huslpToLch", expected.lch, lchFromHuslp);
            assertTuplesClose("lchToLuv", expected.luv, luvFromLch);
            assertTuplesClose("luvToXyz", expected.xyz, xyzFromLuv);
            assertTuplesClose("xyzToRgb", expected.rgb, rgbFromXyz);
            assertEquals(hex, hexFromRgb);
            assertEquals(hex, hexFromHusl);
            assertEquals(hex, hexFromHuslp);

            assertEquals(hex, String.format("#%06x", HUSLColorConverter.rgbToInt(expected.rgb)));

        }
    }
}
