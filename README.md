# qrcoder
A rudimentary QR code (2D Barcode) encoder written from scratch in Java.

What can it do :
=================
 * It is able to generate a proper **version 1** 2D barcode decodable by [zxing decoder](zxing.org) (or any other QR Code decoder) containining numeric and alphanumeric characters. 

Limitation(s):
================
  * No capacity table available. Version information and relevant parameters are hardcoded. 
  * Only supports Alphanumeric and numeric modes.
  * No support for "blocking" or "grouping", hence no support for QR Code that requires doing so.
  * No support for alignment pointers required by high level versions.

Note(s):
=================
 * Most of the version paramerters are currently hardcoded because I have yet to get the version-capacity tables in; they apparently don't have any formulas to generate them.
