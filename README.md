# qr-encoder-JAVA

A QR code (2D Barcode) encoder written from scratch in Java. This is a hobby project with remote likelihood of getting any update. This is something I built (haphazardly) because I was interested in the QR codes, perhaps due to their tremendous ubiquity. Nevertheless, it was ver fu n.

What can it do :
=================
 * It is currently able to generate a proper **version 1** 2D barcode decodable by [ZXing decoder](http://zxing.org) (or any other QR Code decoder for the matter) containing alphanumeric characters. 

Limitation(s):
================
  * Command line interface only. (Practically useless in current state)
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
