select "catalog" as constraint_catalog,
       "schema"  as contraint_schema,
       constraint_name,
       "catalog" as table_catalog,
       "schema"  as table_schema,
       "table"   as table_name,
       name      as column_name,
       ordinal   as ordinal_position
from information_schema.pdx_key_columns
order by "catalog", "schema", "constraint_name", ordinal, name