select "catalog"  as table_catalog,
       "schema"   as table_schema,
       name       as table_name,
       definition as view_definition,
       check_option,
       is_updatable
from information_schema.pdx_views
order by "catalog", "schema", name, definition
