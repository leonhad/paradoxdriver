select "catalog" as table_catalog, "schema" as table_schema, "table" as table_name, name as column_name,
       "catalog" as constraint_catalog, "schema" as constraint_schema, "table" as constraint_name, name as constraint_name
from information_schema.pdx_constraint_column_usage
