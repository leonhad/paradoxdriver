# The Validation File Format (VAL)

This file is under work in progress, do not take for granted.

## Structure

The validation file is made from three parts:

- **Header:** at the beginning of the file and contains metadata.
- **Body:** the validations itself.
- **Footer:** Table, fields and size information.

All field values is little endian by default.

###  Picture validation format

The "picture" validation in Paradox is most likely a mask or a standard check validation. But, in documentation it said
to not force a check validation on table and validation chages. It's a guide to format values in insertion and updates
only.

As the official documentation said (version 7.0):

> A picture acts as a template that formats the value you enter a field.
> For example, if you specify the picture (###)###-### (a common template for U.S. phone numbers) and enter the value (
> 4085551234, Paradox formats the value into (408)555-1234.
> 
> ...
> 
> See Picture string characters for a table of the characters you can use in a picture and their meanings. If you use
> any printable (visible) character in a picture string different from those listed in the table, Paradox treats it as a
> constant.
> When you enter a value in a field that has a picture validity check, and you come to a point at which a constant is
> specified, Paradox automatically enters the constant. For example, if you create the picture (408)###-#### and then
> type (5551234 in the field, Paradox inserts (408)555-1234 in the table.
> 
> If you create a picture validity check when restructuring a table that contains data, Paradox does not reformat
> existing data to match the picture nor validate existing data to check that it matches.
> 
> **Note:** You can also specify pictures on field objects in design documents. However, if you specify a picture for
> the field in the table, as described in this help topic, you cannot specify one for a field object bound to that field.

The picture is made of pattern expression. If the char is not in the list, it treats like a constant:

| Character | Stands for                                                                                        |
| --------- | ------------------------------------------------------------------------------------------------- |
| #         | Numeric Digit                                                                                     |
| ?         | Any letter (uppercase or lowercase)                                                               |
| &         | Any letter (convert to uppercase)                                                                 |
| ~         | Any letter (convert to lowercase)                                                                 |
| @         | Any character                                                                                     |
| !         | Any character (convert to uppercase)                                                              |
| ;         | (semicolon) Interpret the next character as a literal, not as a special picture-string character. |
| *         | Any number of repeats of the following character                                                  |
| [abc]     | Optional characters a, b, or c                                                                    |
| {a,b,c}   | Optional characters a, b, or c                                                                    |

For example

| Picture      | Description                                        |
| ------------ | -------------------------------------------------- |
| #&#&#&       | Canadian postal code; for example, 1A2B3C          |
| #####[-####] | U.S. postal code; for example, 12345 or 12345-6789 |
| *!           | Any entry; all letters will be in uppercase        |
| {Yes,No}     | Either "Yes" or "No"                               |

## Header

| position    | size (bytes) | type    | description      |
|:-----------:|:------------:| ------- | ---------------- |
| 0x00        | 1            | byte    | ?                |
| 0x01        | 1            | byte    | fileVersionID    |
| 0x02        | 1            | byte    | Validation count |
| 0x03 ~ 0x07 | ?            | ?       | ?                |
| 0x08        | 1            | byte    | Always zero?     |
| 0x09        | 4            | integer | Footer position  |
| 0x0A ~ 0x34 | ?            | ?       | Always zero?     |

This file is valid only for fileVersionID 0x09 to 0x0B.

## Body

The body start at position 0x35, and it repeats for all field. The field count and information is in the footer part.

| position    | size (bytes) | type    | description                                                           |
|:-----------:|:------------:| ------- | --------------------------------------------------------------------- |
| 0x00        | 1            | byte    | Field order, start with 1                                             |
| 0x01        | 1            | byte    | Picture size                                                          |
| 0x02        | ?            | ?       | value?                                                                |
| 0x03        | ?            | ?       | value?                                                                |
| 0x04        | 4            | integer | Foreign key indicator. Seams to finish always in 6B. No value here?   |
| 0x08 ~ 0x0B | ?            | ?       | ?                                                                     |
| 0x0C        | 4            | integer | Minimum value indicator. Seams to finish always in 6B. No value here? |
| 0x10        | 4            | integer | Maximum value indicator. Seams to finish always in 6B. No value here? |
| 0x14        | 4            | integer | Default value indicator. Seams to finish always in 6B. No value here? |
| 0x18        | 4            | integer | Picture indicator. No value here?                                     |
| 0x1D        | field size*  | any     | Field value by type. Picture definition is an ending zero string.     |

**\*** Values are presented here based on indicators in sequence of indicated presence. For example, if it has a maximum
and default, the sequence has the maximum and default only in that order.

### Foreign key value

| position | size (bytes) | type   | description                     |
|:--------:|:------------:| ------ | ------------------------------- |
| 0x00     | 1A           | string | destination table?              |
| 0x1B     | 4            | ?      | ? From here 36 bytes to the end |
| 0x1C     | ?            | ?      | zero?                           |
| 0x1D     | ?            | ?      | zero?                           |

**Note:** Unfinished...

## Footer

| position | size (bytes) | type  | description |
|:--------:|:------------:| ----- | ----------- |
| 0x00     | 2            | short | Field count |
| 0x02     | 4            | ?     | ?           |

This section repeats by field count

| position      | size (bytes) | type  | description |
|:-------------:|:------------:| ----- | ----------- |
| last position | 2            | short | Field order |

This section repeats by field count

| position             | size (bytes) | type | description                          |
|:--------------------:|:------------:| ---- | ------------------------------------ |
| last position        | 1            | byte | Field type (Same as the field table) |
| last position + 0x01 | 1            | byte | Field size in bytes                  |

Here return to be fixed.

| position      | size (bytes) | type   | description                                          |
|:-------------:|:------------:| ------ | ---------------------------------------------------- |
| last position | 0x4F         | string | Original table name (can be different from VAL file) |

This section repeats by field count

| position      | size (bytes)            | type   | description |
|:-------------:|:-----------------------:| ------ | ----------- |
| last position | Variable ending in zero | string | Field name  |
