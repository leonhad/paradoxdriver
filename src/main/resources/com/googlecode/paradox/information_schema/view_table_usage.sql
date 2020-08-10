select distinct "catalog"  as view_catalog,
       "schema"   as view_schema,
       name       as view_name,
       table_catalog,
       table_schema,
       table_name
from information_schema.pdx_view_column_usage
order by "catalog", "schema", name, table_catalog, table_name