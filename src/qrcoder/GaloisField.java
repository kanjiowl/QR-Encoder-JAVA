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


/**
 * Class containing methods regarding GF(256) polynomial arithmetic
 *
 * @author kanjiowl
 */

public class GaloisField {

    public static int[] alphaToIntegerArray, integerToAlphaArray;

    /**
     * Creates tables (arrays) to be used for integer-alpha notation conversion
     *
     * @returns void
     */
    public static void makeConversionTable() {
        final int GFSIZE = 256;

        alphaToIntegerArray = new int[GFSIZE];
        integerToAlphaArray = new int[GFSIZE];

        for (int i = 0; i < GFSIZE; i++) {
            int value = alphaToIntegerCoeffs(i);
            alphaToIntegerArray[i] = value;
            integerToAlphaArray[value] = i;
        }
        integerToAlphaArray[1] = 0;
    }


    /**
     * Resize the array by adding the 0 terms
     *
     * @param toBeResized
     * @return resized array
     */
    private static double[] resize(double[] toBeResized, int size) {
        double[] temp = new double[size];
        System.arraycopy(toBeResized, 0, temp, 0, toBeResized.length);
        return temp;

    }


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Divides two polynomials in GF(256) using polynomial long division algorithm. Implements the pseudocode found at
     * the Rosetta Code.
     *
     * @param N = integer array containing INTEGER coefficients of message polynomial
     * @param D = integer array containing INTEGER coefficients of generator polynomial
     * @return array containing the remainders
     * @throws java.lang.Exception
     */
    public static int[] divide(int[] N, int[] D) throws Exception {

        N = shiftToRight(N, D.length - 1);

        int[] quotient = new int[N.length];
        int[] remainder = new int[N.length];

        if (degree(N) >= degree(D)) {
            while (degree(N) >= degree(D)) {

                int[] d = shiftToRight(D, degree(N) - degree(D));               // Shifting the divisor to fit the divident
                int alphaFactor = getFactor(N[degree(N)], d[degree(d)]);        // Finding the factor to multiply the divisor by

                /*Marking the null areas*/
                int[] markZero = new int[d.length];
                for (int i = 0; i < d.length; i++) {
                    if (d[i] == 0) {
                        markZero[i] = 0;
                    } else {
                        markZero[i] = 1;
                    }
                }
                quotient[degree(N) - degree(D)] = toInteger(alphaFactor);
                for (int i = 0; i < d.length; i++) {                            // Multiplying divisor by the factor 
                    int alphaProduct = multiply(integerToAlphaArray[d[i]], alphaFactor);
                    d[i] = toInteger(alphaProduct);
                }

                for (int i = 0; i < d.length; i++) {
                    if (markZero[i] == 0) {
                        d[i] = 0;
                    }
                }

                if (N.length != d.length) {
                    throw new Exception("Can't Subtract");
                }

                N = subtract(N, d);                                             // Subtracting the result
                N = resize(N);                                                  // Resizing to get rid of the leading zeroes
            }
            remainder = N;
        }
        return remainder;
    }

    /**
     * Multiply two alpha coefficients
     *
     * @param x = alpha1
     * @param y = alpha2
     * @return product of alpha
     */
    public static int multiply(int x, int y) {

        return (x + y) % 255;

    }

    /**
     * Multiplies array of coefficients with the given alpha value.
     *
     * @param x = array of alpha exponents
     * @param y == alpha exponent 2
     * @return array product
     */
    public static int[] multiply(int[] x, int y) {

        for (int i = 0; i < x.length; i++) {
            x[i] = (x[i] + y) % 255;

        }
        return x;

    }

    /**
     * Create the generator polynomial (using naive algorithm).
     *
     * @param cw = number of codewords
     * @return generator polynomial with  integer coefficients
     */

    public static int[] getGeneratorPoly(int cw) {

        // it's no longer a mess. Hurray!!!!!
        // Fucked up the complexity though. Can't be bothered to implement CFFT.

        int[] a = {1, 1};
        int[] product = {};

        int sizeTemp;

        for (int iteration = 1; iteration < cw; iteration++) {

            sizeTemp = a.length;
            int[] b = {alphaToIntegerArray[iteration], 1};
            product = new int[sizeTemp + 1];

            // TODO: fix the zero coffs.
            for (int i = 0; i < a.length; i++) {
                for (int j = 0; j < b.length; ++j) {
                    int x = integerToAlphaArray[a[i]];
                    int y = integerToAlphaArray[b[j]];
                    int p = multiply(x, y);
                    int val = alphaToIntegerArray[p];
                    product[i + j] = subtract_resultIn_integer(product[i + j], val);

                }
            }
            a = product;
            //sizeTemp++;
        }

        return product;

    }

    /**
     * Add/Subtract two GF(256) polynomials
     *
     * @param msgIntegers = integer array containing the message coefficients
     * @param generator   = integer array containing the generator coefficients
     * @return array resulting in GF(256) subtraction
     */
    public static int[] subtract(int[] msgIntegers, int[] generator) {
        for (int i = 0; i < msgIntegers.length; i++) {
            msgIntegers[i] ^= generator[i];
        }

        return msgIntegers;
    }

    /**
     * Subtract two numbers in GF(256)
     *
     * @param alpha1 =  alpha Coefficient 1
     * @param alpha2 = alpha Coefficient 2
     * @return resulting in GF(256) subtraction
     */
    public static int subtract(int alpha1, int alpha2) {
        return alphaToIntegerArray[alpha1] ^ alphaToIntegerArray[alpha2];
    }

    /**
     * Subtract in GF(256) and return result in Alpha notation.
     *
     * @param alpha1
     * @param alpha2
     * @return
     */
    public static int subtract_resultIn_alpha(int alpha1, int alpha2) {
        return integerToAlphaArray[alphaToIntegerArray[alpha1] ^ alphaToIntegerArray[alpha2]];
    }

    /**
     * Subtract two integers in GF(256).
     *
     * @param integer1
     * @param integer2
     * @return
     */
    public static int subtract_resultIn_integer(int integer1, int integer2) {
        return integer1 ^ integer2;
    }


    /**
     * Resize the array by removing the 0 terms after subtraction
     *
     * @param toBeResized = array to be resized
     * @return resized array
     */
    private static int[] resize(int[] toBeResized) {
        if (toBeResized[toBeResized.length - 1] == 0) {
            int finalSize = toBeResized.length - 1;
            int[] temp = new int[finalSize];
            System.arraycopy(toBeResized, 0, temp, 0, finalSize);
            toBeResized = temp;
            return toBeResized;
        } else {
            return toBeResized;
        }

    }

    /**
     * Shifts the array to the right and adds 0 padding at the beginning.
     *
     * @param toBeShifted
     * @param by
     * @return integer array shifted
     */
    private static int[] shiftToRight(int[] toBeShifted, int by) {

        int finalLength = toBeShifted.length + by;
        int[] temp = new int[finalLength];
        System.arraycopy(toBeShifted, 0, temp, by, toBeShifted.length);
        toBeShifted = temp;
        return toBeShifted;

    }

    /**
     * *
     * Find the additive inverse of the leading term of numerator in GF
     * Helper method for Galois Field Division.
     *
     * @param _msgCoeff = alpha exponent leading term of the message coefficient
     * @param _genCoeff = alpha exponent leading term of the generator coefficient
     * @return alpha exponent factor
     */
    private static int getFactor(int _msgCoeff, int _genCoeff) {
        int factor;
        int msgCoeff = toAlpha(_msgCoeff);
        int genCoeff = toAlpha(_genCoeff);

        if ((msgCoeff > genCoeff) && (msgCoeff < 256)) {
            factor = msgCoeff - genCoeff;
        } else if (genCoeff > msgCoeff) {
            factor = (255 - genCoeff) + msgCoeff;
        } else {
            factor = 0;
        }
        return factor;
    }

    /**
     * Conversion algorithm for converting the alpha coefficients for integer coefficients
     *
     * @param coeff
     * @return array containing integer coefficients
     */
    // TODO: Apply bitwise manipulation to do faster exponentiation.
    public static int[] alphaToIntegerCoeffs(int[] coeff) {

        int[] coeffs = new int[coeff.length];
        System.arraycopy(coeff, 0, coeffs, 0, coeff.length);

        int magic = 29;                                         // 256 xor 285
        for (int i = 0; i < coeffs.length; i++) {
            int x = coeffs[i];
            if (x > 8) {
                x -= 8;
                for (int j = 0; j < x; j++) {
                    magic *= 2;
                    if (magic > 255) {
                        magic ^= 285;
                    }
                }
                coeffs[i] = magic;
                magic = 29;

            } else if (x == 8) {
                coeffs[i] = magic;
            } else {
                coeffs[i] = (int) Math.pow(2, coeffs[i]);
            }
        }
        return coeffs;
    }

    private static int alphaToIntegerCoeffs(int coeffs) {
        int magic = 29;   // 256 XOR 285
        int x = coeffs;
        if (x > 8) {
            x -= 8;
            for (int j = 0; j < x; j++) {
                magic *= 2;
                if (magic > 255) {
                    magic ^= 285;
                }
            }
            coeffs = magic;
            magic = 29;

        } else if (x == 8) {
            coeffs = magic;
        } else {
            coeffs = (int) Math.pow(2, coeffs);
        }
        return coeffs;
    }

    public static int[] integerToAlphaCoeffs(int[] coeff) {
        int[] coeffs = new int[coeff.length];
        System.arraycopy(coeff, 0, coeffs, 0, coeff.length);
        for (int i = 0; i < coeffs.length; i++) {
            coeffs[i] = integerToAlphaArray[coeffs[i]];
        }
        return coeffs;
    }

    /**
     * Convert the integer to alpha exponent
     *
     * @param Int
     * @return alpha exponent
     */
    private static int toAlpha(int Int) {
        if (Int == 0) {
            return 0;
        }
        return integerToAlphaArray[Int];
    }

    /**
     * Convert the alpha exponent to integer
     *
     * @param Int
     * @return integer
     */
    public static int toInteger(int Int) {
        return alphaToIntegerArray[Int];
    }

    /**
     * Degree of the polynomial
     *
     * @param poly
     * @return degree of the polynomial
     */
    public static int degree(int[] poly) {
        return poly.length - 1;
    }
}
