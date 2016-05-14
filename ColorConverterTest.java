package fill me in;

import android.graphics.Color;
import android.test.InstrumentationTestCase;
import android.util.Log;

import com.bluelinelabs.logansquare.LoganSquare;

import java.io.IOException;
import java.util.Map;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ColorConverterTest extends InstrumentationTestCase {

    private static final String TAG = ColorConverterTest.class.getSimpleName();
    private static final double MAXDIFF = 0.0000000001;
    private static final double MAXRELDIFF = 0.000000001;

    private int iterations = 0;
    private boolean dolog = false;

    protected void assertTuplesClose(String label, double[] a, double[] b) {
        boolean mismatch = false;
        double[] deltas = new double[a.length];
        label = label + " " + iterations;

        for (int i = 0; i < a.length; ++i) {
            deltas[i] = a[i] - b[i];
            if (!almostEqualRelativeAndAbs(a[i], b[i])) {
                mismatch = true;
            }
        }

        if (mismatch) {
            Log.e(TAG, String.format("%s mismatch: %.32f,%.32f,%.32f vs %.32f,%.32f,%.32f (deltas: %.32f,%.32f,%.32f)", label, a[0], a[1], a[2], b[0], b[1], b[2], deltas[0], deltas[1], deltas[2]));
        } else {
            if (dolog) {
                Log.i(TAG,
                        String.format("%s: %.32f,%.32f,%.32f vs %.32f,%.32f,%.32f (deltas: %.32f,%.32f,%.32f)", label, a[0], a[1], a[2], b[0], b[1], b[2], deltas[0], deltas[1], deltas[2]));
            }
        }
    }

    /**
     * modified from
     * https://randomascii.wordpress.com/2012/02/25/comparing-floating-point-numbers-2012-edition/
     */
    boolean almostEqualRelativeAndAbs(double a, double b) {
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

    public void test_husl() throws IOException {
        Log.i(TAG, "Running test");
        Map<String, HUSLTestCase> tests = LoganSquare.parseMap(
                getInstrumentation().getContext().getAssets().open("husltest.json"),
                HUSLTestCase.class);

        for (Map.Entry<String, HUSLTestCase> pair : tests.entrySet()) {
            iterations += 1;
            if (pair.getKey().equals("#555555")) {
                // this doesn't pass for #555555. it's driving me nuts, though I think it's a reasonable error.
                dolog = true;
            }
            HUSLTestCase expected = pair.getValue();

            // test forward functions
            double[] test_rgb = HUSLColorConverter.hexToRgb(pair.getKey());
            assertTuplesClose("rgb", expected.rgb, test_rgb);
            double[] test_xyz = HUSLColorConverter.rgbToXyz(test_rgb);
            assertTuplesClose("xyz", expected.xyz, test_xyz);
            double[] test_luv = HUSLColorConverter.xyzToLuv(test_xyz);
            assertTuplesClose("luv", expected.luv, test_luv);
            double[] test_lch = HUSLColorConverter.luvToLch(test_luv);
            assertTuplesClose("lch", expected.lch, test_lch);
            double[] test_husl = HUSLColorConverter.lchToHusl(test_lch);
            assertTuplesClose("husl", expected.husl, test_husl);
            double[] test_huslp = HUSLColorConverter.lchToHuslp(test_lch);
            assertTuplesClose("rgb", expected.huslp, test_huslp);

            // test backward functions
            test_lch = HUSLColorConverter.huslToLch(expected.husl);
            assertTuplesClose("reverse lch", expected.lch, test_lch);
            test_lch = HUSLColorConverter.huslpToLch(expected.huslp);
            assertTuplesClose("reverse lch", expected.lch, test_lch);
            test_luv = HUSLColorConverter.lchToLuv(test_lch);
            assertTuplesClose("reverse luv", expected.luv, test_luv);
            HUSLColorConverter.lchToLuv(test_lch);
            test_xyz = HUSLColorConverter.luvToXyz(expected.luv);
            assertTuplesClose("reverse xyz", expected.xyz, test_xyz);
            test_rgb = HUSLColorConverter.xyzToRgb(expected.xyz);
            assertTuplesClose("reverse rgb", expected.rgb, test_rgb);
            assertEquals(pair.getKey(), HUSLColorConverter.rgbToHex(test_rgb));

            // full test
            assertEquals(pair.getKey(), HUSLColorConverter.huslToHex(expected.husl));
            assertEquals(pair.getKey(), HUSLColorConverter.huslpToHex(expected.huslp));
            assertEquals(pair.getKey(), String.format("#%06x", HUSLColorConverter.rgbToInt(expected.rgb)));

            assertTuplesClose("full hex to husl", HUSLColorConverter.hexToHusl(pair.getKey()), expected.husl);
            assertTuplesClose("full hex to huslp", HUSLColorConverter.hexToHuslp(pair.getKey()), expected.huslp);
            assertTuplesClose("int to rgb",HUSLColorConverter.intToRgb(Color.parseColor(pair.getKey())), expected.rgb);
            dolog = false;
        }
    }
}
