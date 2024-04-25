# The Validation File Format (VAL)

This file is under work in progress, do not take for granted.

## Structure

The validation file is made from three parts:

- **Header:** at the beginning of the file and contains metadata.
- **Body:** the validations itself.
- **Footer:** Table, fields and size information.

All field values is little endian by default.

## Header

| position | size (bytes) | type    | description      |
|:--------:|:------------:|---------|------------------|
|   0x00   |      1       | byte    | Unknown          |
|   0x01   |      1       | byte    | fileVersionID    |
|   0x02   |      1       | byte    | Validation count |
|   0x03   |      ?       | ?       | ?                |
|   0x08   |      1       | byte    | Always zero?     |
|   0x09   |      4       | integer | Footer position  | 

Here to body seams to be always zeros. Why?

## Body

The body start at position 0x35, and it repeats for all field. The field count and information is in the tail part.

| position | size (bytes) | type   | description                                              |
|:--------:|:------------:|--------|----------------------------------------------------------|
|   0x00   |      1       | byte   | Field order, start with 1                                |
|   0x01   |      1       | byte   | Mask size                                                | 
|    ?     |      ?       | ?      | ?                                                        |
|   0x1D   |  mask size   | string | Mask definition if has a size defined (ending with zero) |

Note: traditional validation starts at the same address as mask definition and any value (minimum, maximum and default)
use the field size and same in table rules. But how it defines the values count?

Note 2: Foreign Key is presented here, but the rules seams to be a different...

## Footer

| position | size (bytes) | type  | description |
|:--------:|:------------:|-------|-------------|
|   0x00   |      2       | short | Field count |
|   0x02   |      4       | ?     | ?           | 

This section repeats by field count

| position | size (bytes) | type  | description |
|:--------:|:------------:|-------|-------------|
|   0x04   |      2       | short | Field order |

This section repeats by field count

| position | size (bytes) | type | description                          |
|:--------:|:------------:|------|--------------------------------------|
|   XXX    |      1       | byte | Field type (Same as the field table) |
| XXX + 1  |      1       | byte | Field size in bytes                  |

Here return to be fixed.

| position | size (bytes) | type   | description                                          |
|:--------:|:------------:|--------|------------------------------------------------------|
|   XXX    |     0x4F     | string | Original table name (can be different from VAL file) |

This section repeats by field count

|  position  |      size (bytes)       | type   | description |
|:----------:|:-----------------------:|--------|-------------|
| XXX + 0x4F | Variable ending in zero | string | Field name  |
