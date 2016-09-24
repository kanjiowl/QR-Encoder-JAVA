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

    static final int MODULES = 21;
    // version  = 1Q
    private static int[] msgCoeffs = {236, 17, 236, 64, 67, 77, 220, 114, 209, 120, 11, 91, 32};
    private static int[] genCoeffs = {45, 32, 94, 64, 70, 118, 61, 46, 67, 251, 0};
    private static int[] ec = {196, 35, 39, 119, 235, 215, 231, 226, 93, 23};
    private static String msg = "";

    /**
     * Entry point.
     *
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {

        GaloisField.makeConversionTable();
//        runMessageTest(); // test with hardcoded message
        test();
    }


    /**
     * Primary test case.
     */
    private static void test() {

        /*Encode message*/
        String message = Encoder.encoder("FOOBAR", 1, "H");

        assert message.length() == 208 : "Failed! Message length too long..";  // Assert test for version 1 msg length

        /*Generate Image */
        DataMatrix mat = new DataMatrix(message, MODULES);
        mat.printMatrix();

        try {
            Image.showImage(mat.toArray(), Image.type.INT, "testImage1");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

////////////////////////////////////// Deprecated /////////////////////////////////////////

    /**
     * Test with hardcoded Message.
     */
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

        assert (msg.equals(test));
        for (int i = 0; i < errorCodes.length; ++i) {
            msg += leftPad(Integer.toBinaryString(errorCodes[i]));
        }

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


}



