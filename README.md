# qr-encoder-JAVA

A QR code (2D Barcode) encoder written from scratch in Java.

About the project:
===================
As a hobby project I wanted to build an encoder that could encode qr codes; perhaps the idea was influenced by their ubiquiti. It also marks my first forray into any form of cryptography. So far, the most satisfying part for me was the implementation of Galois field arithmetics and being able to generate the Reed-Solomon error codes (although I wish I could say I learned enough).


What can it do :
=================
 * It is currently able to generate a proper **version 1** 2D barcode decodable by [ZXing decoder](http://zxing.org) (or any other QR Code decoder for the matter) containing alphanumeric characters. 

Limitation(s):
================
  * Only supports Alphanumeric mode.
  * No capacity table available. Version information and relevant parameters are hardcoded. 
  * Can't find the best mask pattern to be used.
  * No support for "blocking" or "grouping", hence no support for QR Code that requires doing so.
  * No support for alignment pointers required by high level versions.
  

Note(s):
=================
 * Most of the version parameters are currently hardcoded because I have yet to get the version-capacity tables in; they apparently don't have any formulas to generate them.


References Used:
================
* [Swetake - How to create QR Code] (http://www.swetake.com/qrcode/qr1_en.html)
* [Thonky - QR Code Tutorial ] (http://www.thonky.com/qr-code-tutorial/introduction)
