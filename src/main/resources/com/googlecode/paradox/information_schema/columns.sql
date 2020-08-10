select c."catalog"    as table_catalog,
       c."schema"     as table_schema,
       "table"        as table_name,
       c.name         as column_name,
       ordinal        as ordinal_position,
       null           as column_default,
       is_nullable,
       c.type         as data_type,
       maximum_length as character_maximum_length,
       "octet_length" as character_octet_length,
       "precision"    as numeric_precision,
       10             as numeric_precision_radix,
       scale          as numeric_scale,
       null           as datetime_precision,
       null           as interval_type,
       null           as interval_precision,
       null           as character_set_catalog,
       null           as character_set_schema,
       pt.charset     as character_set_name,
       null           as collation_catalog,
       null           as collation_schema,
       null           as collation_name,
       null           as domain_catalog,
       null           as domain_schema,
       null           as domain_name
from information_schema.pdx_columns c
         inner join information_schema.pdx_tables pt
                    on c."catalog" = pt."catalog"
                        and c.schema = pt.schema
                        and c.table = pt.name
