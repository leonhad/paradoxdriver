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
       routine                         as specific_name,
       ordinal                         as ordinal_position,
       mode                            as parameter_mode,
       is_result,
       'NO'                            as as_locator,
       name                            as parameter_name,
       data_type,
       character_maximum_length,
       character_octet_length,
       cast(null as varchar)           as collation_catalog,
       cast(null as varchar)           as collation_schema,
       cast(null as varchar)           as collation_name,
       s.default_character_set_catalog as character_set_catalog,
       s.default_character_set_schema  as character_set_schema,
       s.default_character_set_name    as character_set_name,
       "precision"                     as numeric_precision,
       numeric_precision_radix,
       scale                           as numeric_scale,
       CAST(null as INTEGER)           as datetime_precision,
       CAST(null as INTEGER)           as interval_type,
       CAST(null as INTEGER)           as interval_precision,
       "catalog"                       as user_defined_type_catalog,
       "schema"                        as user_defined_type_schema,
       "name"                          as user_defined_type_name,
       CAST(null as VARCHAR)           as scope_catalog,
       CAST(null as VARCHAR)           as scope_schema,
       CAST(null as VARCHAR)           as scope_name
from information_schema.pdx_routine_parameters r
         left join information_schema.pdx_schemata s
                   on s.catalog_name = r.catalog and s.schema_name = r.schema
