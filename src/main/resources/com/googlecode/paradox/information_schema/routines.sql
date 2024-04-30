/*
 * Copyright (c) 2009 Leonardo Alves da Costa
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public
 * License for more details. You should have received a copy of the GNU General Public License along with this
 * program. If not, see <http://www.gnu.org/licenses/>.
 */

select "catalog"                       as specific_catalog,
       "schema"                        as specific_schema,
       name                            as specific_name,
       "catalog"                       as routine_catalog,
       "schema"                        as routine_schema,
       name                            as routine_name,
       CAST(null as VARCHAR)           as module_catalog,
       CAST(null as VARCHAR)           as module_schema,
       CAST(null as VARCHAR)           as module_name,
       "catalog"                       as udt_catalog,
       "schema"                        as udt_schema,
       name                            as udt_name,
       data_type,
       character_maximum_length,
       character_octet_length,
       CAST(null as VARCHAR)           as collation_catalog,
       CAST(null as VARCHAR)           as collation_schema,
       CAST(null as VARCHAR)           as collation_name,
       s.default_character_set_catalog as character_set_catalog,
       s.default_character_set_schema  as character_set_schema,
       s.default_character_set_name    as character_set_name,
       "precision"                     as numeric_precision,
       numeric_precision_radix,
       scale                           as numeric_scale,
       CAST(null as INTEGER)           as datetime_precision,
       CAST(null as INTEGER)           as interval_type,
       CAST(null as INTEGER)           as interval_precision,
       "catalog"                       as type_udt_catalog,
       "schema"                        as type_udt_schema,
       "name"                          as type_udt_name,
       CAST(null as VARCHAR)           as scope_catalog,
       CAST(null as VARCHAR)           as scope_schema,
       CAST(null as VARCHAR)           as scope_name,
       CAST(null as INTEGER)           as maximum_cardinality,
       CAST(null as INTEGER)           as interval_precision,
       CAST(null as VARCHAR)           as dtd_identifier,
       routine_body,
       routine_definition,
       CAST(null as VARCHAR)           as external_name,
       'INTERNAL'                      as external_language,
       'GENERAL'                       as parameter_style,
       is_deterministic,
       sql_data_access,
       is_null_call,
       CAST(null as VARCHAR)           as sql_path,
       'YES'                           as schema_level_routine,
       0                               as max_dynamic_result_sets,
       CAST(null as VARCHAR)           as is_user_defined_cast,
       is_implicitly_invocable,
       CURRENT_TIMESTAMP               as created,
       CURRENT_TIMESTAMP               as last_altered
from information_schema.pdx_routines r
         left join information_schema.pdx_schemata s
                   on s.catalog_name = r.catalog and s.schema_name = r.schema
