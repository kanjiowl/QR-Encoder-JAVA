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

import java.util.Arrays;

/**
 * Class containing the message encoding methods.
 *
 * @author kanjiowl
 */

public class Encoder {

    // Strings to be used as mode indicators; Currently supporting only num nad alphanumeric.
    private static final String[] MODES = {"0001", "0010", "0100", "1000", "0111"}; //NUM | ALPHANUM | BYTE | KANJI |  ECI

    private static String modeString, errorCorrectionLevel;
    private static int version, msgLength, codewords, modeIndex;



    /**
     * Encodes the given message.
     *
     * @param msg =  String containing the message
     * @param _version = version of the message
     * @param errorControlLevel = error control level to be used
     * @return = encoded string
     */


    public static String encoder(String msg, int _version, String errorControlLevel) {

        //Initiate parameters.

        setVersion(_version);           // Version
        setECL(errorControlLevel);      // Error Control Level
        setMode(findMode(msg));         // Mode
        setCodewords();                 // Number of Codewords
        setMaxMsgCapacity();            // Message length

        // Encode Message
        String encodedMsg = encodeMessage(msg);

        //Create polynomials
        int[] msgPoly = generateMsgPolynomial(encodedMsg);
        int[] genPoly = generateGeneratorPolynomial();

        flipArray(msgPoly);

        // Generate error codes
        int[] errorCodes = ErrorCorrection.generateErrorCodewords(msgPoly, genPoly);

        // Generate final String
        flipArray(errorCodes);

        for (int i = 0; i < errorCodes.length; i++) {
            encodedMsg += leftPad (Integer.toBinaryString(errorCodes[i]), 8);
        }
        return encodedMsg;
    }


    /**
     * Generate Format String.
     *
     * @param maskPatternUsed =  number representing the mask pattern used
     * @return = format String
     */
    public static String  generateFormatString (int maskPatternUsed){

        String formatString  = errorCorrectionBitString("H") + leftPad(Integer.toBinaryString(maskPatternUsed), 3);

        int[] formatGenPoly = {1,0,1,0,0,1,1,0,1,1,1};              // Generator Polynomial for the format String
        int[] formatStringPoly = new int[formatString.length()];    // Polyomial containing the format String

        for (int  i = formatString.length() -1 ; i >= 0 ; i--){
            formatStringPoly[i] = Character.getNumericValue(formatString.charAt(i));
        }

        // Polynomial containing the error correction codewords.
        int[] formatECPoly  = ErrorCorrection.generateErrorCodewords(formatStringPoly, formatGenPoly);

        String n_xor = formatString + arrayToString(formatECPoly);
        String xor = "101010000010010";
        String finalString  = "";

        for (int i = 0; i < xor.length(); i++ ){
            finalString += Integer.toString(Character.getNumericValue(xor.charAt(i)) ^ Character.getNumericValue(n_xor.charAt(i)));
        }

        return finalString;
    }

    /**
     * Create String from concatanating the elements of the array
     * @param arr
     * @return
     */
    private static String arrayToString (int[] arr){
        String str  = "";

        for (int  i = 0; i < arr.length; i++){
            str += Integer.toString(arr[i]);
        }

        return str;
    }

    private static void flipArray(int[] arr) {

        for (int i = 0; i < arr.length / 2; ++i) {
            int temp = arr[arr.length - (i + 1)];
            arr[arr.length - (i + 1)] = arr[i];
            arr[i] = temp;
        }
    }

    public static String leftPad(String _byte, int size) {
        String pad = "";
        for (int j = 0; j < size - _byte.length(); j++) {
            pad += "0";
        }
        return pad + _byte;
    }

    /**
     * Return error correction mode bits.
     *
     * @param ec
     * @return
     */
    private static String errorCorrectionBitString (String ec){
        String ecBits;

        switch (ec) {
            case "L":
                ecBits = "01";
                break;
            case "M":
                ecBits = "00";
                break;

            case ("Q"):
                ecBits = "11";
                break;
            default:
                ecBits = "10";
                break;
        }
        return ecBits;
    }

    /**
     * Encode message.( Without error correction codewords )
     *
     * @param msg
     * @return
     */
    private static String encodeMessage(String msg) {

        // Creating header
        String characterCount = getCharCountInBinary(msg);
        String header = createHeaderData(modeString, characterCount);

        // Encoding meessage
        String encodedMsg = ModeEncoder.alphaNumericMode(msg);
        encodedMsg = header + encodedMsg;

        // Pad
        encodedMsg = addTerminator(encodedMsg);             // Add terminator bits
        encodedMsg = padToMakeMultipleofEight(encodedMsg);  // Make the Message length a multiple of eight
        encodedMsg = padToFillCapacity(encodedMsg);         // Fill Capacity.

        return encodedMsg;
    }


    /**
     * Generate message Polynomial from the given message string.
     *
     * @param msg = Message String
     * @return Message polynomial
     */

    private static int[] generateMsgPolynomial(String msg) {

        int totalMsgCodewords = msg.length() / 8;

        int[] msgPoly = new int[totalMsgCodewords];

        int start = 0, end = 8;

        for (int index = 0; index < msgPoly.length; index++) {
            String one_byte = msg.substring(start, end);
            start = end;
            end += 8;
            msgPoly[index] = Integer.parseInt(one_byte, 2);
        }

        return msgPoly;

    }

    /**
     * Create generator polynomial for the given number of codewords.
     * Used for generating the Error Codes.
     *
     * @return Generator Polynomial
     */
    private static int[] generateGeneratorPolynomial() {

        return GaloisField.getGeneratorPoly(codewords);
    }

    /**
     * Padding method; adds the terminator.
     *
     * @param msg = encoded msg
     * @return string with added terminator.
     */
    private static String addTerminator(String msg) {

        String terminated = "";
        int difference = getMaxMsgCapacity() - msg.length();

        if (difference >= 4) {
            terminated += msg + "0000";                  // Can add a nibble at max
        } else {
            terminated += msg;
            for (int i = 0; i < difference; i++) {
                terminated += "0";
            }
        }

        return terminated;
    }


    /**
     * Methods to build the header; contains metadata about mode and total Message character count.
     *
     * @param modeIndicator
     * @param charCount
     * @return
     */
    private static String createHeaderData(String modeIndicator, String charCount) {
        return  modeIndicator + pad(charCount, modeIndicator);
    }


    /**
     * Finds the mode of the given message.
     *
     * @param msg
     * @return
     */
    private static String findMode(String msg) {

        String mode = MODES[0];

        for (int i = 0; i < msg.length();++i)
        {
            if (msg.charAt(i) < 48 || msg.charAt(i) > 57){
                mode = MODES[1];
                break;
            }
        }
        return mode;
    }


    /**
     * Padding methods
     *
     * @param charCount
     * @param modeIndicator
     * @return
     */
    private static String pad(String charCount, String modeIndicator) {
        String temp = "";
        int reqPad = ReqPad() - charCount.length();
        for (int i = 0; i < reqPad; i++) {
            temp += '0';
        }
        return temp + charCount;
    }

    private static String padToFillCapacity(String encodedData) {

        final String padByte236 = "11101100";                // Filler Bytes;
        final String padByte17 = "00010001";

        int reqCapacity = getMaxMsgCapacity();               // Find out the required Final Size
        int padSize = reqCapacity - encodedData.length();   // Calculate the required padding
        int padSizeInBytes = padSize / 8;                   // Pad size in bytes

        for (int i = 1; i <= padSizeInBytes; i++) {
            if (i % 2 == 1) {
                encodedData += padByte236;
            } else {
                encodedData += padByte17;
            }
        }
        return encodedData;
    }

    private static String padToMakeMultipleofEight(String encodedData) {

        int reqSize = 8 - (encodedData.length() % 8);

        if (reqSize == 0) {
            return encodedData;
        } else {
            for (int i = 0; i < reqSize; i++) {
                encodedData += '0';
            }
            return encodedData;
        }

    }

    private static int ReqPad() {

        if (version >= 1 && version <= 9) {
            if (modeString.equals(MODES[0])) {
                return 10;
            } else if (modeString.equals(MODES[1])) {
                return 9;
            }
        } else if (version >= 10 && version <= 26) {
            if (modeString.equals(MODES[0])) {
                return 12;
            } else if (modeString.equals(MODES[1])) {
                return 11;
            }
        } else {
            if (modeString.equals(MODES[0])) {
                return 14;
            } else if (modeString.equals(MODES[1])) {
                return 13;
            }
        }

        return 0;
    }


    ///////////////////////////////// Setters and getters //////////////////////////////////////////////////////

    /**
     * Set the number of codewords to be generated.
     *
     */
    private static void setCodewords() {
        codewords = 17;
    }

    /**
     * Set version of message.
     *
     * @param _version = version to be set
     */
    private static void setVersion(int _version) {
        version = _version;
    }

    /**
     * Set error control level.
     *
     * @param _errorControlLevel = error control level to be set.
     *
     */
    private static void setECL(String _errorControlLevel) {
        errorCorrectionLevel = _errorControlLevel;
    }

    /**
     * Gets the max message data capacity for the given version and error Control Level.
     * <p>
     * return max message capacity.
     */
    private static int getMaxMsgCapacity() {
        return 72;
    }

    /**
     * Set max message capacity.
     *
     */

    private static void setMaxMsgCapacity() {
        msgLength = 72;
    }

    /**
     * Return the number of characters in the message.
     *
     * @param msg
     * @return
     */
    private static String getCharCountInBinary(String msg) {
        return Integer.toBinaryString(msg.length());
    }

    /**
     * Set mode of the message encoding.
     *
     * @param mode
     */
    private static void setMode(String mode) {
        modeString = mode;
    }

}


