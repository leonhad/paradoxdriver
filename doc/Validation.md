# The Validation File Format (VAL)

This file is under work in progress, do not take for granted.

## Structure

The validation file is made from three parts:

- **Header:** at the beginning of the file and contains metadata.
- **Body:** the validations itself.
- **Footer:** Table, fields and size information.

All field values is little endian by default.

## Header

|  position   | size (bytes) | type    | description      |
|:-----------:|:------------:|---------|------------------|
|    0x00     |      1       | byte    | ?                |
|    0x01     |      1       | byte    | fileVersionID    |
|    0x02     |      1       | byte    | Validation count |
| 0x03 ~ 0x07 |      ?       | ?       | ?                |
|    0x08     |      1       | byte    | Always zero?     |
|    0x09     |      4       | integer | Footer position  | 
| 0x0A ~ 0x34 |      ?       | ?       | Always zero?     |

## Body

The body start at position 0x35, and it repeats for all field. The field count and information is in the footer part.

|  position   | size (bytes) | type    | description                                                           |
|:-----------:|:------------:|---------|-----------------------------------------------------------------------|
|    0x00     |      1       | byte    | Field order, start with 1                                             |
|    0x01     |      1       | byte    | Mask size                                                             | 
| 0x02 ~ 0x0B |      ?       | ?       | ?                                                                     |
|    0x0C     |      4       | integer | Minimum value indicator. Seams to finish always in 6B. No value here. |
|    0x10     |      4       | integer | Maximum value indicator. Seams to finish always in 6B. No value here. |
|    0x14     |      4       | integer | Default value indicator. Seams to finish always in 6B. No value here. |
|    0x18     |      4       | integer | Mask indicator. Seams to finish always in 6B. No value here.          |
|    0x1D     | field size*  | any     | Field value by type. Mask definition is an ending zero string.        |

\* Values are presented here based on indicators in sequence of indicated presence. For example, if it has a maximum and
default, the sequence has the maximum and default only in that order.

**Note:** Foreign Key is presented here, but the rules seams to be a different...

## Footer

| position | size (bytes) | type  | description |
|:--------:|:------------:|-------|-------------|
|   0x00   |      2       | short | Field count |
|   0x02   |      4       | ?     | ?           | 

This section repeats by field count

|   position    | size (bytes) | type  | description |
|:-------------:|:------------:|-------|-------------|
| last position |      2       | short | Field order |

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
