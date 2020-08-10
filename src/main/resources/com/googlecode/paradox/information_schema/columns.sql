select c."catalog" as table_catalog,
       c."schema"  as table_schema,
       "table"     as table_name,
       c.name      as column_name,
       ordinal     as ordinal_position,
    varchar (null) as column_default,
    is_nullable,
    c.type as data_type,
    maximum_length as character_maximum_length,
    "octet_length" as character_octet_length,
    "precision" as numeric_precision,
    10 as numeric_precision_radix,
    scale as numeric_scale,
    numeric (null) as datetime_precision,
    numeric (null) as interval_type,
    numeric (null) as interval_precision,
    varchar (null) as character_set_catalog,
    varchar (null) as character_set_schema,
    t.charset as character_set_name,
    varchar (null) as collation_catalog,
    varchar (null) as collation_schema,
    varchar (null) as collation_name,
    varchar (null) as domain_catalog,
    varchar (null) as domain_schema,
    varchar (null) as domain_name
from information_schema.pdx_columns c
    inner join information_schema.pdx_tables t
on c.catalog = t.catalog
    and c.schema = t.schema
    and c.table = t.name
