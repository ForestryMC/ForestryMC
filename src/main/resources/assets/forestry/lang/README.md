Forestry Localizations
======================

Localizations for Forestry

### Encoding

Files must be encoded in UTF-8. Anything else will not work. Pull requests, submissions or contributions not encoded in UTF-8 will have to be rejected or reverted.

### Testing your language files

Put the files into your .minecraft directory into a subfolder "assets/" that mirrors that of the Forestry jar. Any correctly named localization file should take precedence over the defaults in the jar.

Alternatively you can also add your localization files to the Forestry .jar directly. 

### Notes

Script used to translate .lang files to .json files (except en_us)

```awk
BEGIN {
    FS="="
    print "{"
}

# enable to persist comments
# /^#/ {
#     sub(/^# /, "")
#     gsub(/"/, "\\\"", $0)
#     print "  \"_comment\": \"" $0 "\","
#     next
# }

NF==2 {
    gsub(/"/, "\\\"", $2)
    print "  \"" $1 "\": \"" $2 "\","
}

END {
    print "}"
}
```
