# Charset data

The charset data is made of two fields: sort order (table at 0x29) and code page (table at 0x6A) and sortOrderID (table
at 0xD5, but can change in table version or don't even exists). The Paradox database shows the following charsets to
chose from:

| Sort order | Sort Order ID | Code Page | Paradox charset name | Modern alternative               |
|:----------:|:-------------:|:---------:|----------------------|----------------------------------|
|     76     |   DBWINUS0    |   1252    | 'ascii' ANSI         | windows-1252                     |
|     20     |   BLLT1DA0    |   1252    | Borland DAN Latin-1  | windows-1252                     |
|     24     |   BLLT1DE0    |   1252    | Borland DEU Latin-1  | windows-1252                     |
|     47     |   BLLT1UK0    |   1252    | Borland ENG Latin-1  | windows-1252                     |
|     55     |   BLLT1US0    |   1252    | Borland ENU Latin-1  | windows-1252                     |
|     39     |   BLLT1ES0    |   1252    | Borland ESP Latin-1  | windows-1252                     |
|     30     |   BLLT1FI0    |   1252    | Borland FIN Latin-1  | windows-1252                     |
|     39     |   BLLT1FR0    |   1252    | Borland FRA Latin-1  | windows-1252                     |
|     19     |   BLLT1CA0    |   1252    | Borland FRC Latin-1  | windows-1252                     |
|     43     |   BLLT1IS0    |   1252    | Borland ISL Latin-1  | windows-1252                     |
|     44     |   BLLT1IT0    |   1252    | Borland ITA Latin-1  | windows-1252                     |
|     41     |   BLLT1NL0    |   1252    | Borland NLD Latin-1  | windows-1252                     |
|     44     |   BLLT1NO0    |   1252    | Borland NOR Latin-1  | windows-1252                     |
|     51     |   BLLT1PT0    |   1252    | Borland PTG Latin-1  | windows-1252                     |
|     56     |   BLLT1SV0    |   1252    | Borland SVE Latin-1  | windows-1252                     |
|    242     |   DB852CZ0    |    852    | dBASE CSY cp852      | cp852                            |
|    248     |   DB867CZ0    |    867    | dBASE CSY cp867      | cp862 (not the same, but almost) |
|    222     |   DB865DA0    |    865    | dBASE DAN cp865      | cp865                            |
|    221     |   DB437DE0    |    437    | dBASE DEU cp437      | cp437                            |
|    220     |   DB850DE0    |    850    | dBASE DEU cp850      | cp850                            |
|    109     |   db437gr0    |    737    | dBASE ELL GR437      | cp737                            |
|    244     |   DB437UK0    |    437    | dBASE ENG cp437      | cp437                            |
|    243     |   DB850UK0    |    850    | dBASE ENG cp850      | cp850                            |
|    252     |   DB437US0    |    437    | dBASE ENU cp437      | cp437                            |
|    251     |   DB850US0    |    850    | dBASE ENU cp850      | cp850                            |
|    237     |   DB437ES1    |    437    | dBASE ESP cp437      | cp437                            |
|    235     |   DB850ES0    |    850    | dBASE ESP cp850      | cp850                            |
|    227     |   DB437FI0    |    437    | dBASE FIN cp437      | cp437                            |
|    236     |   DB437FR0    |    437    | dBASE FRA cp437      | cp437                            |
|    235     |   DB850FR0    |    850    | dBASE FRA cp850      | cp850                            |
|    220     |   DB850CF0    |    850    | dBASE FRC cp850      | cp850                            |
|    225     |   DB863CF1    |    863    | dBASE FRC cp863      | cp863                            |
|    148     |   db852hdc    |    852    | dBASE HUN cp852      | cp852                            |
|    241     |   DB437IT0    |    437    | dBASE ITA cp437      | cp437                            |
|    241     |   DB850IT1    |    850    | dBASE ITA cp850      | cp850                            |
|    238     |   DB437NL0    |    437    | dBASE NLD cp437      | cp437                            |
|    237     |   DB850NL0    |    850    | dBASE NLD cp850      | cp850                            |
|    246     |   DB865NO0    |    865    | dBASE NOR cp865      | cp865                            |
|    116     |   db852po0    |    852    | dBASE PLK cp852      | cp852                            |
|    247     |   DB850PT0    |    850    | dBASE PTB cp850      | cp850                            |
|    248     |   DB860PT0    |    860    | dBASE PTG cp860      | cp860                            |
|    129     |   db866ru0    |    866    | dBASE RUS cp866      | cp866                            |
|    116     |   db852sl0    |    852    | dBASE SLO cp852      | cp852                            |
|    253     |   DB437SV0    |    437    | dBASE SVE cp437      | cp437                            |
|    253     |   DB850SV1    |    850    | dBASE SVE cp850      | cp850                            |
|    112     |   db437th0    |    437    | dBASE THA cp437      | cp437                            |
|     0      |   DB857TR0    |    857    | dBASE TRK cp857      | cp857                            |
|     35     |   dbHebrew    |    862    | Hebrew dBASE         | cp862                            |
|     27     |   ORAWE850    |    850    | Oracle SQL WE850     | cp850                            |
|     76     |   ANHEBREW    |   1255    | Paradox ANSI HEBREW  | windows-1255                     |
|     0      |     ascii     |    850    | Paradox 'ascii'      | cp850                            |
|    192     |     cyrr      |    866    | Paradox Cyrr 866     | cp866                            |
|     13     |     czech     |    852    | Paradox Czech 852    | cp852                            |
|    226     |    cskamen    |    867    | Paradox Czech 867    | cp862 (not the same, but almost) |
|     22     |    SPANISH    |    437    | Paradox ESP 437      | cp437                            |
|     74     |    grcp437    |    737    | Paradox Greek GR437  | cp737                            |
|    125     |    hebrew     |    862    | Paradox 'hebrew'     | cp862                            |
|    177     |   hun852dc    |    852    | Paradox Hun 852 DC   | cp852                            |
|    183     |     intl      |    437    | Paradox 'intl'       | cp437                            |
|     84     |    intl850    |    850    | Paradox 'intl' 850   | cp850                            |
|    208     |    iceland    |    861    | Paradox ISL 861      | cp861                            |
|    130     |    nordan     |    865    | Paradox 'nordan'     | cp865                            |
|    230     |   nordan40    |    865    | Paradox 'nordan40'   | cp865                            |
|    143     |    polish     |    852    | Paradox Polish 852   | cp852                            |
|    252     |    slovene    |    852    | Paradox Slovene 852  | cp852                            |
|    240     |    swedfin    |    437    | Paradox 'swedfin'    | cp437                            |
|    166     |     thai      |    437    | Paradox Thai 437     | cp437                            |
|    198     |     turk      |    857    | Paradox 'turk'       | cp857                            |
|    143     |    ancyrr     |   1251    | Pdox ANSI Cyrillic   | windows-1251                     |
|    220     |    anczech    |   1250    | Pdox ANSI Czech      | windows-1250                     |
|     14     |   angreek1    |   1253    | Pdox ANSI Greek      | windows-1253                     |
|    225     |    anhundc    |   1250    | Pdox ANSI Hun. DC    | windows-1250                     |
|     98     |   ANSIINTL    |   1252    | Pdox ANSI Intl       | windows-1252                     |
|     17     |   ANSII850    |   1252    | Pdox ANSI Intl850    | windows-1252                     |
|     78     |   ANSINOR4    |   1252    | Pdox ANSI Nordan4    | windows-1252                     |
|     94     |   anpolish    |   1250    | Pdox ANSI Polish     | windows-1250                     |
|    111     |   ansislov    |   1250    | Pdox ANSI Slovene    | windows-1250                     |
|     93     |   ANSISPAN    |   1252    | Pdox ANSI Spanish    | windows-1252                     |
|    105     |   ANSISWFN    |   1252    | Pdox ANSI Swedfin    | windows-1252                     |
|    117     |    anthai     |   1252    | Pdox ANSI Thai       | windows-1252                     |
|    213     |    ANTURK     |   1254    | Pdox ANSI Turkish    | windows-1254                     |
|     60     |   DBWINES0    |   1252    | 'Spanish' ANSI       | windows-1252                     |
|     20     |   BLROM800    |     0     | SQL Link ROMAN8      | roman8 / cp1051                  |
|    209     |    SYDC437    |    437    | Sybase SQL Dic437    | cp437                            |
|    208     |    SYDC850    |    850    | Sybase SQL Dic850    | cp850                            |
|     64     |   DBWINWE0    |   1252    | 'WEurope' ANSI       | windows-1252                     |
