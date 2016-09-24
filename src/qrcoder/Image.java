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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * The Image Class handles the image generation.
 *
 * @author kanjiowl
 */


class Image {


   enum type {
        INT,
        BIN
    }

    public static final int WIDTH = Main.MODULES * 32;
    public static final int HEIGHT = Main.MODULES * 32;
    public static final int TYPE = BufferedImage.TYPE_INT_RGB;

    /**
     * Draws the image based on the data.
     *
     * @param data
     * @param data_type
     * @param outputFileName
     * @throws IOException
     */
    public static void showImage(int[][] data, type data_type,  String outputFileName) throws IOException {

        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, TYPE);
        Graphics2D painter = image.createGraphics();

        int pixelsPerModule = WIDTH / data.length;

        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.length; j++) {
                int d = data[i][j];
                if (data_type == type.INT) {
                    switch (d) {
                        case DataMatrix.NULL:                          // Null
                            d = 50;
                            painter.setPaint(new Color(d, d, d));
                            break;
                        case DataMatrix.ZERO:                          // 0
                            d = 255;
                            painter.setPaint(new Color(d, d, d));
                            break;
                        case DataMatrix.ONE:                           // 1
                            d = 0;
                            painter.setPaint(new Color(d, d, d));
                            break;

                        case DataMatrix.FINDER_ZERO:                   // Padding
                            d = 0;
                            painter.setPaint(new Color(d, d, d));
                            break;

                        case DataMatrix.FINDER_ONE:                    // Position Marker (dark)
                            d = 255;
                            painter.setPaint(new Color(d, d, d));
                            break;

                        case DataMatrix.FORMAT_OR_VERSION:             // Reserved (Version and Format)
                            d = 200;
                            painter.setPaint(new Color(0, 0, d));
                            break;
                        case DataMatrix.DARK_MODULE:                   // Dark Module
                            d = 0;
                            painter.setPaint(new Color(d, d, d));
                            break;
                        case DataMatrix.PAD:                            // PAD
                            d = 175;
                            painter.setPaint(new Color(d, 0, 0));
                            break;
                        case DataMatrix.FORMAT_ZERO:
                            d = 255;
                            painter.setPaint(new Color(d, d, d));
                            break;

                        case DataMatrix.FORMAT_ONE:
                            d = 0;
                            painter.setPaint(new Color(d, d, d));
                            break;
                    }
                } else if (data_type == type.BIN ) {
                    switch (d) {
                        case 0:
                            d = 0;
                            painter.setPaint(new Color(d, d, d));
                            break;
                        case 1:
                            d = 255;
                            painter.setPaint(new Color(d, d, d));
                            break;
                    }
                }

                painter.fillRect(j * pixelsPerModule, i * pixelsPerModule, pixelsPerModule, pixelsPerModule);

                /*Draw grid*/
                painter.setPaint(new Color(25, 25, 25));
                painter.drawRect(j * pixelsPerModule, i * pixelsPerModule, pixelsPerModule, pixelsPerModule);

            }
        }

        // Add Quiet Zone
        BufferedImage bi = new BufferedImage(928, 928, TYPE);
        Graphics2D gfx = bi.createGraphics();
        gfx.setPaint(new Color(255, 255, 255));
        gfx.fillRect(0, 0, 928, 928);
        gfx.drawImage(image, null, 4 * 32, 4 * 32);

        outputToDisk(bi, outputFileName);
    }

    /**
     * Outputs the image file to the disk.
     *
     * @param image
     * @param outputFileName
     * @throws IOException
     */
    private static void outputToDisk(BufferedImage image, String outputFileName) throws IOException {
        File outputFile = new File(outputFileName + ".png");
        ImageIO.write(image, "png", outputFile);
    }

}





