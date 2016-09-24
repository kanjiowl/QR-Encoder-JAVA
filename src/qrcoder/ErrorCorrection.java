package qrcoder;

/**
 * Created by kanjiowl on 9/14/16.
 */

class ErrorCorrection {

    public static int[] generateErrorCodewords(int[] msg, int[] gen) {
        int[] errorCodes = new int[0];
        try {
            errorCodes = GaloisField.divide(msg, gen);
        } catch (Exception e) {
            e.printStackTrace();

        }

        return errorCodes;
    }
}
