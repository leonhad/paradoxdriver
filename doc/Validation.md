# The Validation File Format (.VAL files)

**Note:** This file documentation is valid only for fileVersionID from 0x09 to 0x0B.

## Structure

The validation file is made from three parts:

- **Header:** at the beginning of the file and contains metadata.
- **Body:** the validations itself.
- **Footer:** Table, fields and size information.

All field values is little endian by default.

### Picture validation format

The "picture" validation in Paradox is most likely a mask or a standard check validation. But, in documentation it said
to not force a check validation on table and validation changes. It's a guide to format values in insertion and updates
only.

As the official documentation said (version 7.0):

> A picture acts as a template that formats the value you enter a field.
> For example, if you specify the picture (###)###-### (a common template for U.S. phone numbers) and enter the value
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
> the field in the table, as described in this help topic, you cannot specify one for a field object bound to that
> field.

The picture is made of pattern expression. If the char is not in the list, it treats like a constant:

| Character | Stands for                                                                                       |
|-----------|--------------------------------------------------------------------------------------------------|
| #         | Numeric Digit                                                                                    |
| ?         | Any letter (uppercase or lowercase)                                                              |
| &         | Any letter (convert to uppercase)                                                                |
| ~         | Any letter (convert to lowercase)                                                                |
| @         | Any character                                                                                    |
| !         | Any character (convert to uppercase)                                                             |
| ;         | (semicolon) Interpret the next character as a literal, not as a special picture-string character |
| *         | Any number of repeats of the following character                                                 |
| [abc]     | Optional characters a, b, or c                                                                   |
| {a,b,c}   | Optional characters a, b, or c                                                                   |

For example

| Picture      | Description                                        |
|--------------|----------------------------------------------------|
| #&#&#&       | Canadian postal code; for example, 1A2B3C          |
| #####[-####] | U.S. postal code; for example, 12345 or 12345-6789 |
| *!           | Any entry; all letters will be in uppercase        |
| {Yes,No}     | Either "Yes" or "No"                               |

## Header

|  position   | size (bytes) | type    | description                                          |
|:-----------:|:------------:|---------|------------------------------------------------------|
|    0x00     |      1       | byte    | Table change count (must match with the table value) |
|    0x01     |      1       | byte    | fileVersionID                                        |
|    0x02     |      1       | byte    | Validation count                                     |
|    0x03     |      4       | int     | Internal pointer, can be ignored                     |
|    0x08     |      1       | byte    | Always zero?                                         |
|    0x09     |      4       | integer | Footer position                                      |
|    0x0D     |      4       | integer | Referential integrity position                       |
| 0x11 ~ 0x34 |      ?       | ?       | Always zero?                                         |

## Body

The body start at position 0x35, and it repeats for all field. The field count and information is in the footer part.

|  position   |  size (bytes)  | type    | description                                                              |
|:-----------:|:--------------:|---------|--------------------------------------------------------------------------|
|    0x00     |       1        | byte    | Field order, starts with 1                                               |
|    0x01     |       1        | byte    | Picture size                                                             |
|    0x02     |       1        | byte    | Dependency table order (starts with 1)                                   |
|    0x03     |       1        | byte    | Integrity status*                                                        |
|    0x04     |       4        | integer | Table lookup key indicator. Seems to finish always in 6B. No value here? |
| 0x08 ~ 0x0B |       ?        | ?       | ?                                                                        |
|    0x0C     |       4        | integer | Minimum value indicator. Seems to finish always in 6B. No value here?    |
|    0x10     |       4        | integer | Maximum value indicator. Seems to finish always in 6B. No value here?    |
|    0x14     |       4        | integer | Default value indicator. Seems to finish always in 6B. No value here?    |
|    0x18     |       4        | integer | Picture indicator. No value here?                                        |
|    0x1D     | field size\*\* | any     | Field value by type. Picture definition is an ending zero string.        |
|     XXX     |       X        | any     | Referential integrity.                                                   |

**\*** Integrity status (bit status in one byte):

- **Lookup table:**
    - 00b - **Just current field:** Only the current field gets its value from the lookup table, even if the current
      table and the lookup table have other fields in common.
    - 01b - **All corresponding fields:** All fields of the current table that correspond to fields in the lookup table
      take their values from the lookup table. Corresponding fields must have identical field names and compatible field
      types in both tables. Only the first field of the lookup table is used as part of the validity check.
- **Lookup access:** (only guide in Paradox application use, not for data validation)
    - 00b - **Fill no help:** You cannot view the lookup table from the table you are entering. You can view the lookup
      table by opening it in its own window.
    - 10b - **Help and fill:** You can view the lookup table from the table you are editing.

**\*\*** Values are presented here based on indicators in sequence by presence. For example, if it has a maximum and
default, the sequence has the maximum and default only in that order.

**Note:** Referential integrity seems to be a table lookup with some additional attributes...

### Table lookup value

The table lookup field uses any field on the original table by its index, but the destination is always the first field
in destination table.

| position | size (bytes) | type   | description                                                                        |
|:--------:|:------------:|--------|------------------------------------------------------------------------------------|
|   0x00   |     0x1A     | string | Destination table                                                                  |
|   0x1A   |     0x36     | ?      | ? These bytes seems to be internal pointers. The same file works with zeros or not |

### Referential integrity

Create a referential integrity implies to create a secondary index to the same field on source table. On
destination table, it creates a validation files with dependent table relation.

The referential integrity starts when field validation stops. So, to find one, use remaining bytes after
validation counts stops and the footer sections don't start yet. If the validation count is zero, is because there is
only referential integrity.

| position | size (bytes) | type | description                                                 |
|----------|--------------|------|-------------------------------------------------------------|
| 0x00     | 0x02         | word | Referential integrity count. 0 identify a dependency table. |

This section repeats by referential integrity count (total count 0x1E0)

| position | size (bytes) | type    | description                                                                  |
|----------|--------------|---------|------------------------------------------------------------------------------|
| 0x00     | 0x40         | string  | Referential integrity name                                                   |
| 0x40     | 0x40         | string  | Origin field name (referential integrity name if it has more than one field) |
| 0xB2     | 0x01         | byte    | Field name size (zero if we have more than one field) - Ignored by Paradox   |
| 0xB3     | 0x01         | byte    | 1 if there are more than one field, zero otherwise - Ignored by Paradox      |
| 0xB4     | 0x20         | ?       | Zeros?                                                                       |
| 0xD4     | 0x72         | string  | Destination table name                                                       |
| 0x146    | ?            | ?       | Zeros?                                                                       |
| 0x19A    | 0x04         | integer | Update Rule: 1 - Cascade, 0 - Prohibit*                                      |
| 0x19E    | 0x02         | byte    | Field count                                                                  |
| 0x1A0    | 0x02 x 10    | word    | Field position in origin table (2 bytes per field)                           |
| 0x1C0    | 0x02 x 10    | byte    | Field position in destination table (2 bytes per field)                      |

**\* Update rule:**

* 1 - **Cascade:** Cascade specifies that any change you make to the value in the key of the parent table
  is automatically made in the child table. If you delete a value in the key of the parent table, dependent records in
  the child table are also deleted. Cascade is the default update rule for Paradox.
* 0 - **Prohibit:** Prohibit specifies that you cannot change or delete a value in the parent's key if there are
  records that match the value in the child table.

### Dependent table

This section occurs after the referential integrity node if exists one and if there space before the footer.

If the referential integrity field 0x00 is zero, is because it is a dependency table list, not a foreign key (this is
the destination table, not source)

| position | size (bytes) | type | description                              |
|:--------:|:------------:|------|------------------------------------------|
|   0x00   |     0x01     | byte | Zero (identify a dependency table)       |
|   0x02   |     0x01     | byte | 01 (identify a dependency table)         |
|   0x98   |     0x02     | word | ID field count in origin table           |
|   0x9A   |     0x20     | word | Field list used in referential integrity |
|   0xBA   |     0x02     | word | Dependency table count                   |

This section repeats by dependency table count (total count 0x12A)

| position | size (bytes) | type   | description                                                             |
|:--------:|:------------:|--------|-------------------------------------------------------------------------|
|   0x00   |     0x40     | string | Referential integrity name                                              |
|   0x40   |     0x52     | string | Origin table name                                                       |
|   0x92   |     0x72     | string | Destination field name, reference name if there are more than one field |
|  0x104   |     0x01     | byte   | Dependency table order                                                  |
|  0x105   |     0x05     | ?      | ? (ignored by Paradox)                                                  |
|  0x10A   |     0x20     | word   | Field list used in referential, starts with 1                           |

## Footer

| position | size (bytes) | type | description |
|:--------:|:------------:|------|-------------|
|   0x00   |      2       | word | Field count |
|   0x02   |      4       | ?    | ?           |

This section repeats by field count

|   position    | size (bytes) | type | description                                    |
|:-------------:|:------------:|------|------------------------------------------------|
| last position |      2       | word | Field order. These values are not sequential.* |

**\*** Example, if a table has 3 fields and se second is removed, in val file is stored 01 03, not 01 02 as expected.

This section repeats by field count

|       position       | size (bytes) | type | description                          |
|:--------------------:|:------------:|------|--------------------------------------|
|    last position     |      1       | byte | Field type (Same as the field table) |
| last position + 0x01 |      1       | byte | Field size in bytes                  |

Here return to be fixed.

|   position    | size (bytes) | type   | description                                          |
|:-------------:|:------------:|--------|------------------------------------------------------|
| last position |     0x4F     | string | Original table name (can be different from VAL file) |

This section repeats by field count

|   position    |      size (bytes)       | type   | description |
|:-------------:|:-----------------------:|--------|-------------|
| last position | Variable ending in zero | string | Field name  |
