select constraint_catalog,
       constraint_schema,
       constraint_name,
       constraint_catalog as unique_constraint_catalog,
       constraint_schema  as unique_constraint_schema,
       constraint_name    as unique_constraint_name,
       match_option,
       update_rule,
       delete_rule
from information_schema.pdx_referential_constraints c
