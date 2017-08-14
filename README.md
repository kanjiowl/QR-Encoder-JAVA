# qr-encoder-JAVA

A QR code (2D Barcode) encoder written from scratch in Java. In case anyone worders, this is a hobby project with remote likelihood of getting any update. 

What can it do :
=================
 * It is currently able to generate 2D barcode (only version 1) decodable by [ZXing decoder](http://zxing.org) (or any other QR Code decoder for the matter) containing only alphanumeric characters. 

Limitation(s):
================
  * No interface 
  * Only supports Alphanumeric mode.
  * No capacity table available. Version information and relevant parameters are hardcoded. 
  * Can't find the best mask pattern to be used.
  * No support for "blocking" or "grouping", hence no support for QR Code that requires doing so.
  * No support for alignment pointers required by high level versions.
  
References Used:
================
* [Swetake - How to create QR Code] (http://www.swetake.com/qrcode/qr1_en.html)
* [Thonky - QR Code Tutorial ] (http://www.thonky.com/qr-code-tutorial/introduction)
