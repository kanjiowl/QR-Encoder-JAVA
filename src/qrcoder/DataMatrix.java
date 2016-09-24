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

// * -------------------------------------
// * -------------------------------------
// * 0 | Null value 
// * 1 | 0 (bit) 
// * 2 | 1 (bit) 
// * 5 | QuietZone/ Padding
// * 6 | Position marker (black) 
// * 7 | Position (white) 
// * 8 | Reserved (Version, Formating information)
// * 9 | Dark Module
// * -------------------------------------
// * -------------------------------------


import java.io.IOException;
import java.util.Arrays;

import static qrcoder.DataMatrix.*;

/**
 * The Data Structure Class.
 *
 * @author kanjiowl
 */


class DataMatrix {

    public static final int NULL = 0,
            ZERO = 1,
            ONE = 2,
            PAD = 4,
            FINDER_ZERO = 5,
            FINDER_ONE = 6,
            FORMAT_OR_VERSION = 8,
            FORMAT_ZERO = 11,
            FORMAT_ONE = 12,
            DARK_MODULE = 9,
            PAD_OFFSET = 0;
    int[][] data;
    int[][] dataForMasking;

    // ZERO = WHITE ; 1 = BLACK
    // odd = black; even = white
    int MASK_PATTERN = 3;



    /**
     * Constructor for Data Matrix.
     *
     * @param msg  = String of message to be placed in the matrix
     * @param size = Size of the modules
     */

    DataMatrix(String msg, int size) {

        this.data = new int[size][size];

        placePositionPatterns(data);
        markPositionPatterns(data);
        placeTimingPatterns(data);
        placeFormatInfoArea(data);
        placeDarkModule(data);
        placeMessage(data, msg);
        maskData(data,MASK_PATTERN);

        String formatString = Encoder.generateFormatString(MASK_PATTERN);
        placeFormatString(formatString, data);
    }

    /**
     * Applies mask on the data.
     *
     * @param data = data matrix
     * @param maskPattern = number representing the mask pattern to use
     */

    private static void  maskData(int[][] data, int maskPattern ){
        Mask.applyMask(data, maskPattern);
    }

    /**
     * Places the dark module required by the standard.
     *
     * @param toBeFilled = data matrix to be filled
     */

    private static void placeDarkModule(int[][] toBeFilled) {
        int row = toBeFilled.length - (8 + PAD_OFFSET);
        int col = (8 + PAD_OFFSET);
        toBeFilled[row][col] = DARK_MODULE;
    }

    /**
     * Mark the position patterns.
     *
     * @param toBeFilled
     */

    private static void markPositionPatterns(int[][] toBeFilled) {

        /*Marking the position markers*/
        for (int i = PAD_OFFSET; i < (8 + PAD_OFFSET); i++) {
            for (int j = PAD_OFFSET; j < (8 + PAD_OFFSET); j++) {
                if (toBeFilled[i][j] == NULL) {
                    toBeFilled[i][j] = FINDER_ZERO;
                } else if (toBeFilled[i][j] == PAD) {

                    toBeFilled[i][j] = FINDER_ONE;

                }
                if (i == 7) toBeFilled[i][j] = FINDER_ONE;
                if (j == 7) toBeFilled[i][j] = FINDER_ONE;

            }
        }


        for (int i = PAD_OFFSET; i < (8 + PAD_OFFSET); i++) {
            for (int j = toBeFilled.length - (8 + PAD_OFFSET); j < toBeFilled.length - PAD_OFFSET; j++) {
                if (toBeFilled[i][j] == NULL) {
                    toBeFilled[i][j] = FINDER_ZERO;
                } else if (toBeFilled[i][j] == PAD) {
                    toBeFilled[i][j] = FINDER_ONE;
                }
                if (i == 7) toBeFilled[i][j] = FINDER_ONE;
                if (j == toBeFilled.length - 8) toBeFilled[i][j] = FINDER_ONE;

            }
        }

        for (int i = toBeFilled.length - (8 + PAD_OFFSET); i < toBeFilled.length - PAD_OFFSET; i++) {
            for (int j = PAD_OFFSET; j < (8 + PAD_OFFSET); j++) {
                if (toBeFilled[i][j] == NULL) {
                    toBeFilled[i][j] = FINDER_ZERO;
                } else if (toBeFilled[i][j] == PAD) {
                    toBeFilled[i][j] = FINDER_ONE;
                }
                if (i == toBeFilled.length - 8) toBeFilled[i][j] = FINDER_ONE;
                if (j == 7) toBeFilled[i][j] = FINDER_ONE;
            }
        }
    }

    /**
     * Place message in the data matrix.
     *
     * @param toBeFilled = data matrix to be filled
     * @param msg        = String containing the message
     */
    private static void placeMessage(int[][] toBeFilled, String msg) {

        int row = (toBeFilled.length - 1) - PAD_OFFSET;
        int col = row;
        int steps = 0;

        boolean goingUpward = true,
                switching_toDown = true,
                switching_toUp = false;

        for (int index = 0; index < msg.length(); ) {

            // Traversing upwards
            if (goingUpward) {

                // In case of switching
                if (switching_toUp) {
                    col -= 2;
                    row--;
                    switching_toUp = false;
                }

                // Conditon for filling.
                if (toBeFilled[row][col] <= PAD) {
                    toBeFilled[row][col] = Character.getNumericValue(msg.charAt(index)) + 1;
                    index++;
                }

                // Traversal logic
                steps++;

                if (steps % 2 == 1) {
                    col -= 1;
                } else {
                    col += 1;
                }
                if (steps % 2 == 0) {
                    row--;
                }

                // Condition for switching
                if (row == -1) { //3
                    goingUpward = false;
                    switching_toDown = true;

                }
            }
            // traversing downwards
            if (!goingUpward) {

                // In case of switching
                if (switching_toDown) {
                    if (col == 8 + PAD_OFFSET) { // Skip the vertical timing pattern
                        col -= 3;
                    } else {
                        col -= 2;
                    }
                    row++;
                    switching_toDown = false;
                }


                // Filling logic
                if (toBeFilled[row][col] <= PAD) {
                    toBeFilled[row][col] = Character.getNumericValue(msg.charAt(index)) + 1;
                    index++;
                }

                // Traversal Logic
                steps++;
                if (steps % 2 == 1) {
                    col -= 1;
                } else {
                    col += 1;
                }
                if (steps % 2 == 0) {
                    row++;
                }
                // Condition for switching
                if (row == toBeFilled.length) {
                    goingUpward = true;
                    switching_toUp = true;
                }

            }

        }
    }

    /**
     * Draw the vertical and horizontal timing patterns.
     *
     * @param toBeFilled = data array
     */
    private static void placeTimingPatterns(int[][] toBeFilled) {

        /* The Horizontal timing pattern */
        int xHorizontalPatternRow = PAD_OFFSET + 6;
        int yHorizontalPatternCol = (8 + PAD_OFFSET);
        for (int i = yHorizontalPatternCol; i < toBeFilled.length; ) {
            if (i % 2 == 1) {
                toBeFilled[xHorizontalPatternRow][i] = FINDER_ONE;
            } else {
                toBeFilled[xHorizontalPatternRow][i] = FINDER_ZERO;
            }
            i++;
            if (toBeFilled[xHorizontalPatternRow][i] == FINDER_ONE) {
                break;
            }
        }

        /* The vertical timing pattern */
        int xVeritcalPatternRow = (8 + PAD_OFFSET);
        int yVerticalPatternCol = PAD_OFFSET + 6;

        for (int i = xVeritcalPatternRow; i < toBeFilled.length; ) {
            if (i % 2 == 1) {
                toBeFilled[i][yVerticalPatternCol] = FINDER_ONE;
            } else {
                toBeFilled[i][yVerticalPatternCol] = FINDER_ZERO;
            }
            i++;
            if (toBeFilled[i][yVerticalPatternCol] == FINDER_ONE) {
                break;
            }

        }
    }

    /**
     * Place the reserved area for format and version information.
     *
     * @param toBeFilled
     */
    private static void placeVersionInfoArea(int[][] toBeFilled) {

        /* Version Information area*/
        for (int col = toBeFilled.length - 15; col < toBeFilled.length - (8 + PAD_OFFSET); col++) {
            for (int row = 4; row < 10; row++) {
                toBeFilled[row][col] = FORMAT_OR_VERSION;
            }
        }

        for (int row = toBeFilled.length - 15; row < toBeFilled.length - (8 + PAD_OFFSET); row++) {
            for (int col = 4; col < 10; col++) {
                toBeFilled[row][col] = FORMAT_OR_VERSION;
            }

        }
    }

    private static void placeFormatInfoArea(int[][] toBeFilled) {

        /*Format information*/
        int row = (8 + PAD_OFFSET);
        int col = PAD_OFFSET;
        for (; col < PAD_OFFSET + 9; col++) {
            if (col == PAD_OFFSET + 6) {
                continue;
            }
            toBeFilled[row][col] = FORMAT_OR_VERSION;
        }
        col--;
        for (; row >= PAD_OFFSET; row--) {
            if (row == PAD_OFFSET + 6) {
                continue;
            }
            toBeFilled[row][col] = FORMAT_OR_VERSION;
        }
        col = (8 + PAD_OFFSET);
        row = toBeFilled.length - (PAD_OFFSET + 7);
        for (; row < toBeFilled.length - PAD_OFFSET; row++) {
            toBeFilled[row][col] = FORMAT_OR_VERSION;
        }

        row = (8 + PAD_OFFSET);
        col = toBeFilled.length - (8 + PAD_OFFSET);
        for (; col < toBeFilled.length - PAD_OFFSET; col++) {
            toBeFilled[row][col] = FORMAT_OR_VERSION;
        }
    }

    /**
     * Draws the three position markers.
     *
     * @param toBeFilled
     */
    private static void placePositionPatterns(int[][] toBeFilled) {

        int markerSize = 5;

        /* Placement of the 1st marker*/
        int row1stMarker = PAD_OFFSET + 1, col1stMarker = PAD_OFFSET + 1;
        placeSquare(toBeFilled, row1stMarker, col1stMarker, markerSize);

        /* Placement of the 2nd marker*/
        int row2ndMarker = PAD_OFFSET + 1, col2ndMarker = toBeFilled.length - (PAD_OFFSET + 6);
        placeSquare(toBeFilled, row2ndMarker, col2ndMarker, markerSize);

        /* Placement of the 3rd marker*/
        int row3rdMarker = toBeFilled.length - (PAD_OFFSET + 6), col3rdMarker = PAD_OFFSET + 1;
        placeSquare(toBeFilled, row3rdMarker, col3rdMarker, markerSize);

    }

    /**
     * Draw a square.
     *
     * @param toBeFilled = data array to be filled
     * @param xPos       = starting x coordinate
     * @param yPos       = starting y coordinate
     * @param size       = size of the square
     */
    private static void placeSquare(int[][] toBeFilled, int xPos, int yPos, int size) {

        int totalSteps = 4 * (size - 1);
        int steps = totalSteps / 4;
        int row = xPos, col = yPos;

        for (int i = 1; i <= totalSteps; i++) {
            if (i <= steps) {
                toBeFilled[row][col++] = PAD;
            } else if (i <= 2 * steps) {
                toBeFilled[row++][col] = PAD;
            } else if (i <= 3 * steps) {
                toBeFilled[row][col--] = PAD;
            } else {
                toBeFilled[row--][col] = PAD;
            }
        }

    }

    /**
     * Draws the quiet zone required by the QR code standard.
     *
     * @param toBeFilled
     */
    private static void placeQuietZone(int[][] toBeFilled) {
        int x = 0, y = 0;
        int size = toBeFilled.length;
        int quietModules = 0;
        while (quietModules < 4) {
            placeSquare(toBeFilled, x, y, size);
            x++;
            y++;
            size -= 2;
            quietModules++;

        }
    }

    public static void printMatrix(int[][] mat) {
        for (int i = 0; i < mat.length; i++) {
            System.out.println(Arrays.toString(mat[i]));

        }
    }

    private void placeFormatString(String formatString, int[][] toBeFilled) {

        // Top Left Corner
        // Starting pos = (8,0)
        // finishing pos = (0,8)
        // skip over = (8,6) & (6,8)

        int row = 8, col = 0;
        int i = 0;

        for (int steps = 0; steps < 17; steps++) {

            if (row == 8 && col == 6) {
                col++;
                continue;
            } else if (row == 6 && col == 8) {

                row--;
                continue;
            }


            if (steps < 9) {
                toBeFilled[row][col] = Character.getNumericValue(formatString.charAt(i)) + 1;
                col++;
                i++;
                if (steps == 8) {
                    col--;
                    row--;
                }

            } else {
                toBeFilled[row][col] = Character.getNumericValue(formatString.charAt(i)) + 1;
                row--;
                i++;
            }
        }

        // Lower Left and Upper Right corner.
        int row2 = toBeFilled.length - 1, col2 = 8;
        boolean swtch = true;

        i = 0;

        for (int steps = 0; steps < 17; steps++) {

            if (row2 == (toBeFilled.length - 8)) {
                row2 = 8;
                col2 = toBeFilled.length - 8;
                continue;
            }

            if (steps < 9) {
                System.out.println("{" + row2 + "," + col2 + "}");

                toBeFilled[row2][col2] = Character.getNumericValue(formatString.charAt(i)) + 1;
                row2--;
                i++;
                if (steps == 8) {
                    i--;
                    row2++;
                }
            } else {
                System.out.println("{" + row2 + "," + col2 + "}");
                toBeFilled[row2][col2] = Character.getNumericValue(formatString.charAt(i)) + 1;
                col2++;
                i++;
            }
        }
    }

    /**
     * Print the matrix.
     */
     void printMatrix() {
        for (int i = 0; i < this.data.length; i++) {
            System.out.println(Arrays.toString(this.data[i]));
        }
    }

    /**
     * Returns the data array with INT type data.
     *
     * @return = data array
     */
    int[][] toArray() {
        return data;
    }
}


///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

class Mask {

    public static int[][] finalData;
    private static int[][] workData;

    /**
     * Constructor for Data Matrix.
    */


//    public static int[][] getMaskedData(int maskPattern){
//        finalData = maskData (maskPattern);
//        return finalData;
//    }


    public static void  applyMask(int[][] workData, int maskPattern){

        int[][] mask = maskData(workData, maskPattern);

        try {
            Image.showImage(mask, Image.type.INT, "mask1");
        } catch (IOException e) {
            e.printStackTrace();
        }

        DataMatrix.printMatrix(mask);
        System.out.println();
        DataMatrix.printMatrix(workData);

        for (int i = 0; i < mask.length; i++){
            for (int j = 0; j < mask.length; j++){
                if (workData[i][j] < PAD &&  mask[i][j] == ONE){
                    if (workData[i][j] == ZERO) workData[i][j] = ONE;
                    else {workData[i][j] = ZERO;}
                }


            }
        }

    }

    /**
     * Copies the values to data variable.
     *
     * @param _data = source data
     */
    public static void setData(int[][] _data) {
        workData = new int[_data.length][_data.length];

        for (int i = 0; i < _data.length; ++i) {
            System.arraycopy(_data[i], 0, workData[i], 0, _data[i].length);
        }
    }

    /**
     * Generates mask array.
     *
     * @param maskPattern = number of mask pattern
     * @return = mask pattern
     */

    public static int[][] maskData(int[][] workData, int maskPattern) {

        int size = workData.length;

        int[][] mask = new int[size][size];

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                if (mask[row][col] < PAD) {
                    switch (maskPattern) {

                        case 0:     // First pattern

                            if ((row + col) % 2 == 0) {
//                                if (mask[row][col] == ZERO) {
//                                    mask[row][col] = ONE;
//                                } else {
//                                    mask[row][col] = ZERO;
//                                }
                                mask[row][col] = ZERO;
                            } else {
                                mask[row][col] = ONE;
                            }

                            break;

                        case 1:     // Second pattern

                            if (row % 2 == 0) {
//                                if (mask[row][col] == ZERO) {
//                                    mask[row][col] = ONE;
//                                } else {
//                                    mask[row][col] = ZERO;
//                                }
//                            } else {
//                                mask[row][col] = mask[row][col];
//                            }
                                mask[row][col] = ONE;
                            } else {
                                mask[row][col] = ZERO;
                            }

                            break;
                        case 2:
                            if (col % 3 == 0) {
                                if (mask[row][col] == ZERO) {
                                    mask[row][col] = ONE;
                                } else {
                                    mask[row][col] = ZERO;
                                }
                            } else {
                                mask[row][col] = mask[row][col];
                            }

                            break;
                        case 3:
                            if ((row + col) % 3 == 0) {
//                                if (mask[row][col] == ZERO) {
//                                    mask[row][col] = ONE;
//                                } else {
//                                    mask[row][col] = ZERO;
//                                }
//                            } else {
//                                mask[row][col] = mask[row][col];
//                            }
                                mask[row][col] = ONE;
                            } else {
                                mask[row][col] = ZERO;
                            }

                            break;
                        case 4:
                            // floor(row / 2) + floor(column / 3) ) mod 2 == 0
                            if ((Math.floor(row / 2) + Math.floor(col / 3)) % 2 == 0) {
                                if (mask[row][col] == ZERO) {
                                    mask[row][col] = ONE;
                                } else {
                                    mask[row][col] = ZERO;
                                }
                            } else {
                                mask[row][col] = mask[row][col];
                            }

                            break;

                        case 5:
//                            ((row * column) mod 2) + ((row * column) mod 3) == 0
                            if ((row * col) % 2 + ((row * col) % 3) == 0) {
                                if (mask[row][col] == ZERO) {
                                    mask[row][col] = ONE;
                                } else {
                                    mask[row][col] = ZERO;
                                }
                            } else {
                                mask[row][col] = mask[row][col];
                            }

                            break;

                        case 6:
//                            ( ((row * column) mod 2) + ((row * column) mod 3) ) mod 2 == 0
                            if (((row * col) % 2 + ((row * col) % 3)) % 2 == 0) {
                                if (mask[row][col] == ZERO) {
                                    mask[row][col] = ONE;
                                } else {
                                    mask[row][col] = ZERO;
                                }
                            } else {
                                mask[row][col] = mask[row][col];
                            }

                            break;

                        case 7:
//                            ( ((row + column) mod 2) + ((row * column) mod 3) ) mod 2 == 0

                            if (((row + col) % 2 + ((row * col) % 3)) % 2 == 0) {
                                if (mask[row][col] == ZERO) {
                                    mask[row][col] = ONE;
                                } else {
                                    mask[row][col] = ZERO;
                                }
                            } else {
                                mask[row][col] = mask[row][col];
                            }

                            break;
                    }
                } else {
                    mask[row][col] = mask[row][col];
                }

            }
        }
        return mask;
    }

    /**
     * Evaluation condition 1.
     *
     * @param mask = mask pattern
     * @return penalty
     */

    public static int eval1(int[][] mask) {

        int penaltyVertical = 0, penaltyHorizontal = 0;

        int length = mask.length - (PAD_OFFSET);

        System.out.println();

        int tmp = 0;
        System.out.println("Length: " + (length - 4));

        for (int row = 4; row < length; row++) {
            for (int col = 4; col < length - 4; ) {

                //System.out.println(col + "::: " + col2);
                // Check in chunks of five modules everytime.
                if ((mask[row][col] == 0
                        && mask[row][col + 1] == 0
                        && mask[row][col + 2] == 0
                        && mask[row][col + 3] == 0
                        && mask[row][col + 4] == 0)
                        || (mask[row][col] == 1
                        && mask[row][col + 1] == 1
                        && mask[row][col + 2] == 1
                        && mask[row][col + 3] == 1
                        && mask[row][col + 4] == 1)) {

                    penaltyHorizontal += 3;
                    int testColor = mask[row][col];

//                    System.out.println("Found 5 : " + testColor);
                    col += 5;
                    int cnt = 0;

                    // If found such chunk, find how many follows of the same color
                    while ((col != length) && (mask[row][col] == testColor)) {
                        penaltyHorizontal++;
                        col++;
                        cnt++;
                        if (cnt % 5 == 0) {
                            penaltyHorizontal -= 2;
                        }

                    }
                } else {
                    col++;
                }

            }
            System.out.println((row - 3) + "  " + (penaltyHorizontal - tmp) + " " + penaltyHorizontal);
            tmp = penaltyHorizontal;

        }

        tmp = 0;
        System.out.println();

        for (int row = 4; row < length; row++) {
            for (int col = 4; col < length - 4; ) {

                //System.out.println(col + "::: " + col2);
                // Check in chunks of five modules everytime.
                if ((mask[col][row] == 0
                        && mask[col + 1][row] == 0
                        && mask[col + 2][row] == 0
                        && mask[col + 3][row] == 0
                        && mask[col + 4][row] == 0)
                        || (mask[col][row] == 1
                        && mask[col + 1][row] == 1
                        && mask[col + 2][row] == 1
                        && mask[col + 3][row] == 1
                        && mask[col + 4][row] == 1)) {

                    penaltyVertical += 3;
                    int testColor = mask[col][row];

//                    System.out.println("Found 5 : " + testColor);
                    col += 5;
                    int cnt = 0;
                    // If found such chunk, find how many follows that are of the same color
                    while ((col != length) && (mask[col][row] == testColor)) {
                        penaltyVertical++;
                        col++;
                        cnt++;
//                        System.out.println("Found 1 " + testColor  );
                        if (cnt % 5 == 0) {
                            penaltyVertical -= 2;
                        }

                    }
                } else {
                    col++;
                }

            }
            System.out.println((row - 3) + "  " + (penaltyVertical - tmp) + " " + penaltyVertical);
            tmp = penaltyVertical;
        }
        System.out.println("Penalty: " + penaltyHorizontal
                + " : " + penaltyVertical);
        System.out.println("Penalty 1 : " + (penaltyHorizontal + penaltyVertical));
        return penaltyHorizontal + penaltyVertical;

    }

    /**
     * Evaluation condition 2.
     *
     * @param mask
     * @return
     */
    public static int eval2(int[][] mask) {

        // Find 2x2 squares in the matrix and calculate penalty
        // Uses the brute force (method of exhaustion) to find the solution.
        // Consider finding a better solution as it may have performance issues with higher version of QR.

        int penalty = 0;
        int length = mask.length - PAD_OFFSET;
        int startPoint = PAD_OFFSET;

        // Complexity = O(n^2)
        for (int row = startPoint; row < length - 1; row++) {
            for (int col = startPoint; col < length - 1; col++) {
                if ((mask[row][col] == 1
                        && mask[row][col + 1] == 1
                        && mask[row + 1][col] == 1
                        && mask[row + 1][col + 1] == 1)
                        || (mask[row][col] == 0
                        && mask[row][col + 1] == 0
                        && mask[row + 1][col] == 0
                        && mask[row + 1][col + 1] == 0)) {
                    System.out.println("[" + row + ", " + col + "]");
                    penalty += 3;

                }
            }
        }

        System.out.println("Penalty 2 : " + penalty);
        return penalty;
    }

    /**
     * Evaluate Condition 3.
     *
     * @param mask
     * @return
     */
    public static int eval3(int[][] mask) {
        int penalty = 0;

        // Pattern : dark-light-dark-dark-dark-light-dark-(4*light modules)
        /// or (4*light modules)-dark-light-dark-dark-dark-light-dark

        int length = mask.length - PAD_OFFSET;
        int startPoint = PAD_OFFSET;

        System.out.println("Horizontal: ");

        for (int row = startPoint; row < length; row++) {
            for (int col = startPoint; col < length - 10; ++col) {
                if ((mask[row][col] == 0
                        && mask[row][col + 1] == 1
                        && mask[row][col + 2] == 0
                        && mask[row][col + 3] == 0
                        && mask[row][col + 4] == 0
                        && mask[row][col + 5] == 1
                        && mask[row][col + 6] == 0
                        && mask[row][col + 7] == 1
                        && mask[row][col + 8] == 1
                        && mask[row][col + 9] == 1
                        && mask[row][col + 10] == 1)
                        || (mask[row][col] == 1
                        && mask[row][col + 1] == 1
                        && mask[row][col + 2] == 1
                        && mask[row][col + 3] == 1
                        && mask[row][col + 4] == 0
                        && mask[row][col + 5] == 1
                        && mask[row][col + 6] == 0
                        && mask[row][col + 7] == 0
                        && mask[row][col + 8] == 0
                        && mask[row][col + 9] == 1
                        && mask[row][col + 10] == 0)) {
                    System.out.println("[" + row + ", " + col + "]");
                    penalty += 40;
                }
            }
        }

        // TODO: Check logic here to see if anything is broken.

        System.out.println("vertical: ");
        for (int row = startPoint; row < length; row++) {
            for (int col = startPoint; col < length - 10; ++col) {
                if ((mask[col][row] == 0
                        && mask[col + 1][row] == 1
                        && mask[col + 2][row] == 0
                        && mask[col + 3][row] == 0
                        && mask[col + 4][row] == 0
                        && mask[col + 5][row] == 1
                        && mask[col + 6][row] == 0
                        && mask[col + 7][row] == 1
                        && mask[col + 8][row] == 1
                        && mask[col + 9][row] == 1
                        && mask[col + 10][row] == 1)
                        || (mask[col][row] == 1
                        && mask[col + 1][row] == 1
                        && mask[col + 2][row] == 1
                        && mask[col + 3][row] == 1
                        && mask[col + 4][row] == 0
                        && mask[col + 5][row] == 1
                        && mask[col + 6][row] == 0
                        && mask[col + 7][row] == 0
                        && mask[col + 8][row] == 0
                        && mask[col + 9][row] == 1
                        && mask[col + 10][row] == 0)) {
                    System.out.println("[" + row + ", " + col + "]");

                    penalty += 40;
                }
            }
        }
        System.out.println("Penalty 3:" + penalty);
        return penalty;
    }

    /**
     * Evaluate conditon 4.
     *
     * @param mask
     * @return
     */
    public static int eval4(int[][] mask) {
        int penalty;

        int length = mask.length - PAD_OFFSET;
        int startPoint = PAD_OFFSET;
        int darkCount = 0, lightCount = 0;

        for (int row = startPoint; row < length; row++) {
            for (int col = startPoint; col < length; col++) {
                if (mask[row][col] == 0) {
                    darkCount++;
                } else {
                    lightCount++;
                }
            }
        }

        double darkPercentage = ((double) darkCount / (darkCount + lightCount)) * 100.0;
        int lowerMultipleOfFive = (int) ((int) Math.floor(darkPercentage) - Math.floor(darkPercentage) % 5);
        int higherMultipleOfFive = lowerMultipleOfFive + 5;
        int fiftyMinusLow = Math.abs(50 - lowerMultipleOfFive);
        int fiftyMinusHigh = Math.abs(50 - higherMultipleOfFive);
        int lowDividedByFive = fiftyMinusLow / 5;
        int highDividedByFive = fiftyMinusHigh / 5;
        penalty = (lowDividedByFive < highDividedByFive) ? lowDividedByFive * 10 : highDividedByFive * 10;

        System.out.println(darkCount + " " + lightCount + " " + darkPercentage + " " + lowerMultipleOfFive + " " + higherMultipleOfFive);
        System.out.println("penalty 4 : " + penalty);
        return penalty;

    }
}
