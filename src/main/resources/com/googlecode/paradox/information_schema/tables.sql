select "catalog" as table_catalog,
       "schema"  as table_schema,
       name      as table_name,
       type_name as table_type
from information_schema.pdx_tables
order by "catalog", "schema", name, type_name