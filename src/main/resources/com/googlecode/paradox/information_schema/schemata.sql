select catalog_name,
       schema_name,
       schema_owner,
       default_character_set_catalog,
       default_character_set_schema,
       default_character_set_name
from information_schema.pdx_schemata
order by catalog_name, schema_name, schema_owner
