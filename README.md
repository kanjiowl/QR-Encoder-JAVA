# QRGen-JAVA

A QR code (2D Barcode) encoder written from scratch in Java.

What can it do :
=================
 * It is able to generate a proper **version 1** 2D barcode decodable by [zxing decoder](zxing.org) (or any other QR Code decoder for the matter) containing alphanumeric characters. 

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
