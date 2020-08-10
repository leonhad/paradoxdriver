select constraint_catalog,
       constraint_schema,
       constraint_name,
       check_clause
from information_schema.pdx_check_constraints
order by constraint_catalog, constraint_schema, constraint_name