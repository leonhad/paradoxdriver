select domain_catalog,
       domain_schema,
       domain_name,
       table_catalog,
       table_schema,
       table_name,
       column_name
from information_schema.pdx_column_domain_usage
order by domain_catalog, domain_schema, domain_name, table_catalog, table_schema, table_name,
         column_name
