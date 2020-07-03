# Paradox Driver #
This project aims to create a pure Paradox Java Driver by using the JDBC 4 technology.

To use it, install the driver class `com.googlecode.paradox.Driver` and use the JDBC String `jdbc:paradox:/dir/to/files`.

[![License](https://img.shields.io/badge/License-GPL%203.0-blue.svg)](LICENSE) [![Build Status](https://travis-ci.org/leonhad/paradoxdriver.svg?branch=master)](https://travis-ci.org/leonhad/paradoxdriver)

## Example ##

```
    Class.forName("com.googlecode.paradox.Driver");
    java.sql.Connection conn = DriverManager.getConnection("jdbc:paradox:./db");
```

## What's new in this version

- Added encryption support.
- Added all field types support.
- Fixed null values.
- Fixed various memory and performances issues.
- Now the database uses the table charset to parse String values.
- Force Charset and Locale support in connection properties.

# Downloads #
If you're using Maven, you can use the dependencies below:

```
<dependency>
    <groupId>com.googlecode.paradoxdriver</groupId>
    <artifactId>paradoxdriver</artifactId>
    <version>1.5.0</version>
</dependency>
```

If you're using Gradle, add the following line to your dependencies section:

    compile 'com.googlecode.paradoxdriver:paradoxdriver:1.5.0'

If you just want the files, you can grab the [latest release](https://github.com/leonhad/paradoxdriver/releases/latest).

# Licensing

This software is released under the GNU Lesser General Public License v3.0 (LGPLv3+).
Additionally, Autoconf includes a licensing exception in some of its
source files.

For more licensing information, see
<http://www.gnu.org/licenses/gpl-faq.html>.

-----
Copyright (C) 2012 Leonardo Alves da Costa.

Copying and distribution of this file, with or without modification,
are permitted in any medium without royalty provided the copyright
notice and this notice are preserved.  This file is offered as-is,
without warranty of any kind.