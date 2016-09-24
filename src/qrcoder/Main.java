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

import java.io.IOException;

/**
 * @author kanjiowl
 */


public class Main {

    // version  = 1Q

    static int[] msgCoeffs = {236, 17, 236, 64, 67, 77, 220, 114, 209, 120, 11, 91, 32};
    static int[] genCoeffs = {45, 32, 94, 64, 70, 118, 61, 46, 67, 251, 0};
    static int[] ec = {196, 35, 39, 119, 235, 215, 231, 226, 93, 23};
    static String  msg = "";


    static final int MODULES = 21;

    private static void test_cs() {
//        String msgTest  = Encoder.encoder("HELLO  WORLD", 1, "H");
//        String msgResult = "";
//
//        assert msgTest.equals(msgResult);
    }

    private static void test_GF() {
        // TODO: Write test for testing the polynomial generation.
    }


    private static void runMessageTest() {
        int[] _genCoeffs = new int[genCoeffs.length];

        for (int i = 0; i < _genCoeffs.length; i++) {
            _genCoeffs[i] = GaloisField.toInteger(genCoeffs[i]);
        }
        int[] errorCodes = ErrorCorrection.generateErrorCodewords(msgCoeffs, _genCoeffs);

        flipArray(msgCoeffs);

        for (int i = 0; i < msgCoeffs.length; ++i) {
            msg += leftPad(Integer.toBinaryString(msgCoeffs[i]));

        }

        String test = "00100000010110110000101101111000110100010111001011011100010011010100001101000000111011000001000111101100";

        System.out.println(msg.length());
        System.out.println(test.length());

        assert(msg == test);

        for (int i = 0; i < errorCodes.length; ++i) {
            msg += leftPad(Integer.toBinaryString(errorCodes[i]));
        }


        System.out.println(msg.length());

    }

    public static void flipArray(int[] arr) {

        for (int i = 0; i < arr.length / 2; ++i) {
            int temp = arr[arr.length - (i + 1)];
            arr[arr.length - (i + 1)] = arr[i];
            arr[i] = temp;
        }
    }


    public static String leftPad(String _byte) {
        String pad = "";
        for (int j = 0; j < 8 - _byte.length(); j++) {
            pad += "0";
        }
        return pad + _byte;

    }

//    /**
//     * Runs test.
//     */
//    public static void runTotalTest() {
//
//        String outputWithoutPadding = InitialBinaryData(MODEINDICATOR, getCharCountInBinary(MSG)) + NumericalEncoder.encoder(MSG);
//        String output = padderFinal(outputWithoutPadding);
//        output = padToMakeMultipleofEight(output);
//        output = padToFillCapacity(output, VERSION, ECL);
//        int[] msgCoeffs = getMSGPolynomial(output);
//        int[] genCoeffs = getGeneratorPolynomial(ERRORCONTROLBYTES);
//        int[] _genCoeffs = new int[genCoeffs.length];
//
//        for (int i = 0; i < _genCoeffs.length; i++) {
//            _genCoeffs[i] = GaloisField.toInteger(genCoeffs[i]);
//        }
//
//        int[] errorCodes = ErrorCorrection.generateErrorCodewords(msgCoeffs, _genCoeffs);
//        System.out.println(Arrays.toString(errorCodes));
//
//    }

//    public static void emptyDataTest() throws IOException {
//
//        // Create DataMatrix matrix
//        int[][] table = new int[MODULES][MODULES];
//
//        // Place Finder Patterns
//
//        DataMatrix.placeFinderPatterns(table);
//        DataMatrix.markFinderPatterns(table);
//
//        // Place Timing pattern
//        DataMatrix.placeTimingPatterns(table);
//
//        // Place Format and version information
//        DataMatrix.placeFormatInfoArea(table);
//
//        // Place dark Module
//        DataMatrix.placeDarkModule(table);
//
//        // Fill data
//        DataMatrix.placeMessage(table, msg);
//
/////*
//        //----------- Masking begins---------------
//// Setting the data for masking.
//        DataMatrix.setData(table);
//        System.out.println();
//
//// Finding the best masking pattern
//          int bestMaskPattern = 0;
////        bestMaskPattern = Mask.bestMask();
////        System.out.println("bestMaskPattern : " + bestMaskPattern);
//        int[][] mask1 = Mask.maskData(bestMaskPattern);
//
////      Print out the mask matrix
////        for (int i = 0; i < mask1.length; i++) {
////            for (int j = 0; j < mask1.length; ++j) {
////                System.out.print(mask1[i][j] + " ");
////
////            }
////            System.out.println();
////        }
////        System.out.println();
//
////      Converting for evaluation.
//        for (int i = 0; i < MODULES; i++) {
//            for (int j = 0; j < MODULES; j++) {
//                if (mask1[i][j] % 2 == 1) {
//                    mask1[i][j] = 0;
//                } else {
//                    mask1[i][j] = 1;
//                }
//                System.out.print(mask1[i][j] + " ");
//            }
//            System.out.println();
//        }
//
//        // Printing the eval result
//        System.out.println(Mask.eval1(mask1) + Mask.eval2(mask1) + Mask.eval3(mask1) + Mask.eval4(mask1));
//        // Outputting the image to disk
//        Image.showImage(mask1);
//
//        //-------------- Masking ends----------
////*/
//        Image.showImage(table);
//
//        // Print the data matrix
//        for (int i = 0; i < MODULES; i++) {
//            for (int j = 0; j < MODULES; j++) {
//                System.out.print(table[i][j] + " ");
//            }
//            System.out.println();
//        }
////        System.out.println(msg.length());
//    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {

        GaloisField.makeConversionTable();

//        int[] _genCoeffs = new int[genCoeffs.length];
//
//        for (int i = 0; i < _genCoeffs.length; i++) {
//            _genCoeffs[i] = GaloisField.toInteger(genCoeffs[i]);
//        }
//        int[] ec = ErrorCorrection.generateErrorCodewords(msgCoeffs, _genCoeffs);
//        flipArray(ec);
//        System.out.println(Arrays.toString(ec));

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////

//        runMessageTest();               // Checks with hardcoded message
        matrixTest();
    }

    private static void matrixTest() {

        String message = Encoder.encoder("JABIR", 1, "H");

        assert message.length() == 208;

        DataMatrix mat = new DataMatrix(message, MODULES);
        mat.printMatrix();

        try {
            Image.showImage(mat.toArray(),Image.type.INT,"testImage1");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//
//    /**
//     * Generate polynomial from message string
//     *
//     * @param encodedData
//     * @return array containing message polynomial coefficients
//     */
//    public static int[] getMSGPolynomial(String encodedData) {
//        int[] coeffs = new int[encodedData.length() / 8];
//        int index = 0;
//        int cnt3 = 0, cnt4 = 0;
//        String temp = "";
//
//        for (int i = 0; i < encodedData.length(); i++) {
//            temp += encodedData.charAt(i);
//            coeffs[index++] = Integer.parseInt(temp, 2);
//            cnt3++;
//            if (cnt3 % 8 == 0) {
//                cnt4++;
//                temp = "";
//            }
//        }
//        return coeffs;
//    }
//
//    /**
//     * *
//     * Generate generator polynomials for given codewords
//     *
//     * @param codeWordsCount
//     * @return array containing generator polynomial coefficients
//     */
//    public static int[] getGeneratorPolynomial(int codeWordsCount) {
//
//        //int[] generatorPolynomial = new int[codeWordsCount + 1];
//        // TODO: Build the Generator Polynomial Algorithm
//        int[] generatorPolynomial = {21, 102, 238, 149, 146, 229, 87, 0}; // For 1-L
//        return generatorPolynomial;
//    }
//
//    /**
//     *
//     * Methods to build Initial Binary data
//     *
//     * @param modeIndicator
//     * @param charCount
//     * @return
//     */
//    public static String InitialBinaryData(String modeIndicator, String charCount) {
//        String intialBinaryData = modeIndicator + pad(charCount, modeIndicator);
//        return intialBinaryData;
//    }
//
//    public static String getCharCountInBinary(String msg) {
//        return Integer.toBinaryString(msg.length());
//    }
//
//    /**
//     * Padding methods
//     *
//     * @param charCount
//     * @param modeIndicator
//     * @return
//     */
//    public static String pad(String charCount, String modeIndicator) {
//        String temp = "";
//        int reqPad = ReqPad(modeIndicator) - charCount.length();
//        for (int i = 0; i < reqPad; i++) {
//            temp += '0';
//        }
//        return temp + charCount;
//    }
//
//    public static String padToFillCapacity(String encodedData, String version, String ecl) {
//        final String padByte236 = "11101100";
//        final String padByte17 = "00010001";
//
//        int reqCapacity = getReqCapacity(version, ecl);
//        int padSize = reqCapacity - encodedData.length();
//        int padSizeInBytes = padSize / 8;
//
//        for (int i = 1; i <= padSizeInBytes; i++) {
//            if (i % 2 == 1) {
//                encodedData += padByte236;
//            } else {
//                encodedData += padByte17;
//            }
//        }
//        return encodedData;
//    }
//
//    public static String padderFinal(String encodedData) {
//        String version = getVersion();
//        String ecl = getECL();
//        int reqSize = getReqSize(version, ecl);
//        String terminator = getTerminator(reqSize);
//        encodedData += terminator;
//        return encodedData;
//    }
//
//    public static String padToMakeMultipleofEight(String encodedData) {
//        int reqSize = 8 - (encodedData.length() % 8);
//        if (reqSize == 0) {
//            return encodedData;
//        } else {
//            for (int i = 0; i < reqSize; i++) {
//                encodedData += '0';
//            }
//            return encodedData;
//        }
//
//    }

//    /**
//     * *
//     * Getter methods
//     */
//    // TODO: Implement the getReqCapacity method
//    public static int getReqCapacity(String version, String ecl) {
//        return 152;
//    }
//
//    // TODO: Implement the methods
//    public static String getTerminator(int reqSize) {
//        String terminator = "0000";
//
//        return terminator;
//    }

//    public static String getVersion() {
//        return VERSION;
//    }

//    public static String getECL() {
//        return ECL;
//    }

//    public static int getReqSize(String version, String ecl) {
//        return 0;
//    }

    // TODO: Implement the required pad size algo
//    public static int ReqPad(String modeIndicator) {
//        return 10;  // 10 Bits for numerical data
//    }

    // TODO: Finish the algo
//    public int getVersion(String msg, String ecl) {
//        int version = 0;
//        return version;
//    }

}
