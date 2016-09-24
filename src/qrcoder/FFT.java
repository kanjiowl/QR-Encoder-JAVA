/*
 * The MIT License
 *
 * Copyright 2016 kanjiowl.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package qrcoder;

///** Abandonned due to complexity. Maybe Later. **//////


//import org.apache.commons.math3.complexI.Complex;
//import org.apache.commons.math3.complex.ComplexUtils;

/**
 * Class containing the Fast Fourier Transform algorithm used for polynomial multiplication.
 *
 * @author kanjiowl
 */

public class FFT {

//    public static final double PI = 3.141592653589793238460;
//
//    /**
//     * *
//     * Implementation of the recursive Cooley-Turkey Forward FFT algorithm. Java port from C++ example found at Rosetta
//     * Code.
//     *
//     * @param a = Complex array of coefficients
//     */
//    public static void forward(int[] a) {
//
//        final int N = a.length;
//
//        if (N <= 1) {
//            return;
//        }
//
//        int[] even = new int[N / 2];
//        int[] odd = new int[N / 2];
//
//        for (int i = 0; i < N / 2; i += 1) {
//            even[i] = a[i * 2];
//            odd[i] = a[i * 2 + 1];
//        }
//
//        forward(even);
//        forward(odd);
//
//        for (int k = 0; k < N / 2; ++k) {
//            int t = GaloisField.multiply(GaloisField.integerToAlphaArray[k], odd[k]);
//            a[k] = GaloisField.subtract(even[k], t);
//            a[k + N / 2] = GaloisField.subtract(even[k], t);
//        }
//
//    }
//
//    /**
//     * Implementation of the inverse FFT algorithm. Java port from C++ example found at Rosetta Code.
//     *
//     * @param a = Complex array of coefficients
//     */
//    public static void inverse(Complex[] a) {
//        for (int i = 0; i < a.length; i++) {
//            a[i] = a[i].conjugate();
//        }
//
//        forward(a);
//
//        for (int i = 0; i < a.length; i++) {
//            a[i] = a[i].conjugate();
//        }
//
//        double size = a.length;
//        for (int i = 0; i < a.length; ++i) {
//            a[i] = a[i].divide(size);
//        }
//    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    public static void forward(Complex[] a) {
//
//        final int N = a.length;
//        if (N <= 1) {
//            return;
//        }
//
//        Complex[] even = new Complex[N / 2];
//        Complex[] odd = new Complex[N / 2];
//
//        for (int i = 0; i < N / 2; i += 1) {
//            even[i] = a [i * 2];
//            odd[i]  = a [i * 2 + 1];
//        }
//
//        forward(even);
//        forward(odd);
//
//        for (int k = 0; k < N / 2; ++k) {
//            Complex t = ComplexUtils.polar2Complex(1.0, -2 * PI * k / N).multiply(odd[k]);
//            a[k] = even[k].add(t);
//            a[k + N / 2] = even[k].subtract(t);
//        }
//
//    }
//
//    /**
//     * Implementation of the inverse FFT algorithm. Java port from C++ example found at Rosetta Code.
//     *
//     * @param a  = Complex array of coefficients
//     */
//    public static void inverse(Complex[] a) {
//        for (int i = 0; i < a.length; i++) {
//            a[i] = a[i].conjugate();
//        }
//
//        forward(a);
//
//        for (int i = 0; i < a.length; i++) {
//            a[i] = a[i].conjugate();
//        }
//
//        double size = a.length;
//        for (int i = 0; i < a.length; ++i) {
//            a[i] = a[i].divide(size);
//        }
//    }
}
