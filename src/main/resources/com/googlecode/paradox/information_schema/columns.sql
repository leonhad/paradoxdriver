select c."catalog"                     as table_catalog,
       c."schema"                      as table_schema,
       "table"                         as table_name,
       c.name                          as column_name,
       ordinal                         as ordinal_position,
       cast(null as VARCHAR)           as column_default,
       is_nullable,
       c.type                          as data_type,
       maximum_length                  as character_maximum_length,
       "octet_length"                  as character_octet_length,
       "precision"                     as numeric_precision,
       10                              as numeric_precision_radix,
       scale                           as numeric_scale,
       cast(null as NUMERIC)           as datetime_precision,
       cast(null as NUMERIC)           as interval_type,
       cast(null as NUMERIC)           as interval_precision,
       s.default_character_set_catalog as character_set_catalog,
       s.default_character_set_schema  as character_set_schema,
       t.charset                       as character_set_name,
       cast(null as VARCHAR)           as collation_catalog,
       cast(null as VARCHAR)           as collation_schema,
       cast(null as VARCHAR)           as collation_name,
       cast(null as VARCHAR)           as domain_catalog,
       cast(null as VARCHAR)           as domain_schema,
       cast(null as VARCHAR)           as domain_name
from information_schema.pdx_columns c
         inner join information_schema.pdx_tables t
                    on c.catalog = t.catalog
                        and c.schema = t.schema
                        and c.table = t.name
         inner join information_schema.pdx_schemata s
                    on c.catalog = s.catalog_name
                        and c.schema = s.schema_name
