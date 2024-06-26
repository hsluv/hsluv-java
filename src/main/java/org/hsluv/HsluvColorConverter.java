package org.hsluv;

/**
 * README: https://github.com/hsluv/hsluv-java
 * 
 */
public class HsluvColorConverter {
    private static double refY = 1.0;
    private static double refU = 0.19783000664283;
    private static double refV = 0.46831999493879;
    private static double kappa = 903.2962962;
    private static double epsilon = 0.0088564516;
    private static double m_r0 = 3.240969941904521;
    private static double m_r1 = -1.537383177570093;
    private static double m_r2 = -0.498610760293;
    private static double m_g0 = -0.96924363628087;
    private static double m_g1 = 1.87596750150772;
    private static double m_g2 = 0.041555057407175;
    private static double m_b0 = 0.055630079696993;
    private static double m_b1 = -0.20397695888897;
    private static double m_b2 = 1.056971514242878;

    // RGB
    public String hex = "#000000";
    public double rgb_r = 0;
    public double rgb_g = 0;
    public double rgb_b = 0;

    // CIE XYZ
    public double xyz_x = 0;
    public double xyz_y = 0;
    public double xyz_z = 0;

    // CIE LUV
    public double luv_l = 0;
    public double luv_u = 0;
    public double luv_v = 0;

    // CIE LUV LCh
    public double lch_l = 0;
    public double lch_c = 0;
    public double lch_h = 0;

    // HSLuv
    public double hsluv_h = 0;
    public double hsluv_s = 0;
    public double hsluv_l = 0;

    // HPLuv
    public double hpluv_h = 0;
    public double hpluv_p = 0;
    public double hpluv_l = 0;

    // 6 lines in slope-intercept format: R < 0, R > 1, G < 0, G > 1, B < 0, B > 1
    public double r0s = 0;
    public double r0i = 0;
    public double r1s = 0;
    public double r1i = 0;

    public double g0s = 0;
    public double g0i = 0;
    public double g1s = 0;
    public double g1i = 0;

    public double b0s = 0;
    public double b0i = 0;
    public double b1s = 0;
    public double b1i = 0;

    private static double fromLinear(double c) {
        if (c <= 0.0031308) {
            return 12.92 * c;
        } else {
            return 1.055 * Math.pow(c, 1 / 2.4) - 0.055;
        }
    }

    private static double toLinear(double c) {
        if (c > 0.04045) {
            return Math.pow((c + 0.055) / 1.055, 2.4);
        } else {
            return c / 12.92;
        }
    }

    private static double yToL(double Y) {
        if (Y <= epsilon) {
            return Y / refY * kappa;
        } else {
            return 116 * Math.pow(Y / refY, 1.0 / 3) - 16;
        }
    }

    private static double lToY(double L) {
        if (L <= 8) {
            return refY * L / kappa;
        } else {
            return refY * Math.pow((L + 16) / 116, 3);
        }
    }

    private static double hexToRgbChannel(String hex, int offset) {
        return Integer.parseInt(hex.substring(offset, offset + 2), 16) / 255.0;
    }

    private static double distanceFromOriginAngle(double slope, double intercept, double angle) {
        double d = intercept / (Math.sin(angle) - slope * Math.cos(angle));
        if (d < 0) {
            return Double.POSITIVE_INFINITY;
        } else {
            return d;
        }
    }

    private static double distanceFromOrigin(double slope, double intercept) {
        return Math.abs(intercept) / Math.sqrt(Math.pow(slope, 2) + 1);
    }

    private static double min6(double f1, double f2, double f3, double f4, double f5, double f6) {
        return Math.min(f1, Math.min(f2, Math.min(f3, Math.min(f4, Math.min(f5, f6)))));
    }

    public void rgbToHex() {
        long r = Math.round(this.rgb_r * 255);
        long g = Math.round(this.rgb_g * 255);
        long b = Math.round(this.rgb_b * 255);
        this.hex = "#" + String.format("%06x", r * 256 * 256 + g * 256 + b);
    }

    public void hexToRgb() {
        this.hex = this.hex.toLowerCase();
        this.rgb_r = hexToRgbChannel(this.hex, 1);
        this.rgb_g = hexToRgbChannel(this.hex, 3);
        this.rgb_b = hexToRgbChannel(this.hex, 5);
    }

    public void xyzToRgb() {
        this.rgb_r = fromLinear(m_r0 * this.xyz_x + m_r1 * this.xyz_y + m_r2 * this.xyz_z);
        this.rgb_g = fromLinear(m_g0 * this.xyz_x + m_g1 * this.xyz_y + m_g2 * this.xyz_z);
        this.rgb_b = fromLinear(m_b0 * this.xyz_x + m_b1 * this.xyz_y + m_b2 * this.xyz_z);
    }

    public void rgbToXyz() {
        double lr = toLinear(this.rgb_r);
        double lg = toLinear(this.rgb_g);
        double lb = toLinear(this.rgb_b);
        this.xyz_x = 0.41239079926595 * lr + 0.35758433938387 * lg + 0.18048078840183 * lb;
        this.xyz_y = 0.21263900587151 * lr + 0.71516867876775 * lg + 0.072192315360733 * lb;
        this.xyz_z = 0.019330818715591 * lr + 0.11919477979462 * lg + 0.95053215224966 * lb;
    }

    public void xyzToLuv() {
        double divider = this.xyz_x + 15 * this.xyz_y + 3 * this.xyz_z;
        double varU = 4 * this.xyz_x;
        double varV = 9 * this.xyz_y;
        if (divider != 0) {
            varU /= divider;
            varV /= divider;
        } else {
            varU = Double.NaN;
            varV = Double.NaN;
        }
        this.luv_l = yToL(this.xyz_y);
        if (this.luv_l == 0) {
            this.luv_u = 0;
            this.luv_v = 0;
        } else {
            this.luv_u = 13 * this.luv_l * (varU - refU);
            this.luv_v = 13 * this.luv_l * (varV - refV);
        }
    }

    public void luvToXyz() {
        if (this.luv_l == 0) {
            this.xyz_x = 0;
            this.xyz_y = 0;
            this.xyz_z = 0;
            return;
        }
        double varU = this.luv_u / (13 * this.luv_l) + refU;
        double varV = this.luv_v / (13 * this.luv_l) + refV;
        this.xyz_y = lToY(this.luv_l);
        this.xyz_x = 0 - 9 * this.xyz_y * varU / ((varU - 4) * varV - varU * varV);
        this.xyz_z = (9 * this.xyz_y - 15 * varV * this.xyz_y - varV * this.xyz_x) / (3 * varV);
    }

    public void luvToLch() {
        this.lch_l = this.luv_l;
        this.lch_c = Math.sqrt(this.luv_u * this.luv_u + this.luv_v * this.luv_v);
        if (this.lch_c < 0.00000001) {
            this.lch_h = 0;
        } else {
            double hrad = Math.atan2(this.luv_v, this.luv_u);
            this.lch_h = hrad * 180.0 / Math.PI;
            if (this.lch_h < 0) {
                this.lch_h = 360 + this.lch_h;
            }
        }
    }

    public void lchToLuv() {
        double hrad = this.lch_h / 180.0 * Math.PI;
        this.luv_l = this.lch_l;
        this.luv_u = Math.cos(hrad) * this.lch_c;
        this.luv_v = Math.sin(hrad) * this.lch_c;
    }

    public void calculateBoundingLines(double l) {
        double sub1 = Math.pow(l + 16, 3) / 1560896;
        double sub2 = sub1 > epsilon ? sub1 : l / kappa;
        double s1r = sub2 * (284517 * m_r0 - 94839 * m_r2);
        double s2r = sub2 * (838422 * m_r2 + 769860 * m_r1 + 731718 * m_r0);
        double s3r = sub2 * (632260 * m_r2 - 126452 * m_r1);
        double s1g = sub2 * (284517 * m_g0 - 94839 * m_g2);
        double s2g = sub2 * (838422 * m_g2 + 769860 * m_g1 + 731718 * m_g0);
        double s3g = sub2 * (632260 * m_g2 - 126452 * m_g1);
        double s1b = sub2 * (284517 * m_b0 - 94839 * m_b2);
        double s2b = sub2 * (838422 * m_b2 + 769860 * m_b1 + 731718 * m_b0);
        double s3b = sub2 * (632260 * m_b2 - 126452 * m_b1);
        this.r0s = s1r / s3r;
        this.r0i = s2r * l / s3r;
        this.r1s = s1r / (s3r + 126452);
        this.r1i = (s2r - 769860) * l / (s3r + 126452);
        this.g0s = s1g / s3g;
        this.g0i = s2g * l / s3g;
        this.g1s = s1g / (s3g + 126452);
        this.g1i = (s2g - 769860) * l / (s3g + 126452);
        this.b0s = s1b / s3b;
        this.b0i = s2b * l / s3b;
        this.b1s = s1b / (s3b + 126452);
        this.b1i = (s2b - 769860) * l / (s3b + 126452);
    }

    public double calcMaxChromaHpluv() {
        double r0 = distanceFromOrigin(this.r0s, this.r0i);
        double r1 = distanceFromOrigin(this.r1s, this.r1i);
        double g0 = distanceFromOrigin(this.g0s, this.g0i);
        double g1 = distanceFromOrigin(this.g1s, this.g1i);
        double b0 = distanceFromOrigin(this.b0s, this.b0i);
        double b1 = distanceFromOrigin(this.b1s, this.b1i);
        return min6(r0, r1, g0, g1, b0, b1);
    }

    public double calcMaxChromaHsluv(double h) {
        double hueRad = h / 360 * Math.PI * 2;
        double r0 = distanceFromOriginAngle(this.r0s, this.r0i, hueRad);
        double r1 = distanceFromOriginAngle(this.r1s, this.r1i, hueRad);
        double g0 = distanceFromOriginAngle(this.g0s, this.g0i, hueRad);
        double g1 = distanceFromOriginAngle(this.g1s, this.g1i, hueRad);
        double b0 = distanceFromOriginAngle(this.b0s, this.b0i, hueRad);
        double b1 = distanceFromOriginAngle(this.b1s, this.b1i, hueRad);
        return min6(r0, r1, g0, g1, b0, b1);
    }

    public void hsluvToLch() {
        if (this.hsluv_l > 99.9999999) {
            this.lch_l = 100;
            this.lch_c = 0;
        } else if (this.hsluv_l < 0.00000001) {
            this.lch_l = 0;
            this.lch_c = 0;
        } else {
            this.lch_l = this.hsluv_l;
            this.calculateBoundingLines(this.hsluv_l);
            double max = this.calcMaxChromaHsluv(this.hsluv_h);
            this.lch_c = max / 100 * this.hsluv_s;
        }
        this.lch_h = this.hsluv_h;
    }

    public void lchToHsluv() {
        if (this.lch_l > 99.9999999) {
            this.hsluv_s = 0;
            this.hsluv_l = 100;
        } else if (this.lch_l < 0.00000001) {
            this.hsluv_s = 0;
            this.hsluv_l = 0;
        } else {
            this.calculateBoundingLines(this.lch_l);
            double max = this.calcMaxChromaHsluv(this.lch_h);
            this.hsluv_s = this.lch_c / max * 100;
            this.hsluv_l = this.lch_l;
        }
        this.hsluv_h = this.lch_h;
    }

    public void hpluvToLch() {
        if (this.hpluv_l > 99.9999999) {
            this.lch_l = 100;
            this.lch_c = 0;
        } else if (this.hpluv_l < 0.00000001) {
            this.lch_l = 0;
            this.lch_c = 0;
        } else {
            this.lch_l = this.hpluv_l;
            this.calculateBoundingLines(this.hpluv_l);
            double max = this.calcMaxChromaHpluv();
            this.lch_c = max / 100 * this.hpluv_p;
        }
        this.lch_h = this.hpluv_h;
    }

    public void lchToHpluv() {
        if (this.lch_l > 99.9999999) {
            this.hpluv_p = 0;
            this.hpluv_l = 100;
        } else if (this.lch_l < 0.00000001) {
            this.hpluv_p = 0;
            this.hpluv_l = 0;
        } else {
            this.calculateBoundingLines(this.lch_l);
            double max = this.calcMaxChromaHpluv();
            this.hpluv_p = this.lch_c / max * 100;
            this.hpluv_l = this.lch_l;
        }
        this.hpluv_h = this.lch_h;
    }

    public void hsluvToRgb() {
        this.hsluvToLch();
        this.lchToLuv();
        this.luvToXyz();
        this.xyzToRgb();
    }

    public void hpluvToRgb() {
        this.hpluvToLch();
        this.lchToLuv();
        this.luvToXyz();
        this.xyzToRgb();
    }

    public void hsluvToHex() {
        this.hsluvToRgb();
        this.rgbToHex();
    }

    public void hpluvToHex() {
        this.hpluvToRgb();
        this.rgbToHex();
    }

    public void rgbToHsluv() {
        this.rgbToXyz();
        this.xyzToLuv();
        this.luvToLch();
        this.lchToHpluv();
        this.lchToHsluv();
    }

    public void rgbToHpluv() {
        this.rgbToXyz();
        this.xyzToLuv();
        this.luvToLch();
        this.lchToHpluv();
        this.lchToHpluv();
    }

    public void hexToHsluv() {
        this.hexToRgb();
        this.rgbToHsluv();
    }

    public void hexToHpluv() {
        this.hexToRgb();
        this.rgbToHpluv();
    }
}