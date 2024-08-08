select distinct "catalog" as table_catalog,
                "schema"  as table_schema,
                "table"   as table_name,
                "catalog" as constraint_catalog,
                "schema"  as constraint_schema,
                "name"    as constraint_name
from information_schema.pdx_table_constraints
order by "catalog", "schema", "table", "name"
