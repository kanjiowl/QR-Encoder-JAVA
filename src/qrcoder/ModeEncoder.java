package qrcoder;

import static qrcoder.Encoder.leftPad;

/**
 * Created by kanjiowl on 9/15/16.
 */


public class ModeEncoder {

    public static String alphaNumericMode(String msg) {
        int len = msg.length();

        String binValue = "";

        for (int i = 0; i < len; i +=2 ){
            int value = 0;

            if (i+1 != len){
                value = (getCharValue(msg.charAt(i)) * 45);
                value += getCharValue(msg.charAt(i+1));
                binValue += leftPad(Integer.toBinaryString(value),11);
            }else if (i+1 == len){
                value = Character.getNumericValue(msg.charAt(i));
                binValue += leftPad(Integer.toBinaryString(value),6);
            }
        }
        return binValue;
    }

    private static int getCharValue (char c){
        if ((int) c  >= 48 && (int) c <= 57 ){
            return (int) c  - 48;
        }
        else if ((int) c >= 65 && (int) c <= 90){
            return ((int)c - 65)  + 10;
        }
        else {
            switch(c){
                case ' ':  return 36;
                case '$':  return 37;
                case '%':  return 38;
                case '*':  return 39;
                case '+':  return 40;
                case '-':  return 41;
                case '.':  return 42;
                case '/':  return 43;
                case ':':  return 44;
            }
        }
        // Fucked.
        return 0;
    }
}


