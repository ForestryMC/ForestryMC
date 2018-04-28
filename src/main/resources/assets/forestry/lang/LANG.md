Forestry Localizations
======================

Localizations for Forestry

### Encoding

Files must be encoded in UTF-8. Anything else will not work. Pull requests, submissions or contributions not encoded in UTF-8 will have to be rejected or reverted.

### Testing your language files

Put the files into your .minecraft directory into a subfolder "assets/" that mirrors that of the Forestry jar. Any correctly named localization file should take precedence over the defaults in the jar.

Alternatively you can also add your localization files to the Forestry .jar directly. The "lang/"-folder in it is not signed, so modifying it is safe.

### Converting from the old localization method

All files under the old .properties and ISO 8859-1 encoding system will need to be redone. They have been moved into the "old/" directory for reference. It is recommended that you grab the en_US.lang file and start over rather than try to convert the old files.

For those wondering, the changes in the system are:

1. Files now use the .lang extension
2. Files now are encoded in UTF-8
3. Files are now saved in the "assets/forestry/lang/" directory
4. There is only one lang file for the entire mod now.
