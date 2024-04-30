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

select c."catalog"                     as table_catalog,
       c."schema"                      as table_schema,
       "table"                         as table_name,
       c.name                          as column_name,
       ordinal                         as ordinal_position,
       cast(null as VARCHAR)           as column_default,
       is_nullable,
       c.type                          as data_type,
       maximum_length                  as character_maximum_length,
       "octet_length"                  as character_octet_length,
       "precision"                     as numeric_precision,
       radix                           as numeric_precision_radix,
       scale                           as numeric_scale,
       cast(null as NUMERIC)           as datetime_precision,
       cast(null as NUMERIC)           as interval_type,
       cast(null as NUMERIC)           as interval_precision,
       s.default_character_set_catalog as character_set_catalog,
       s.default_character_set_schema  as character_set_schema,
       t.charset                       as character_set_name,
       cast(null as VARCHAR)           as collation_catalog,
       cast(null as VARCHAR)           as collation_schema,
       cast(null as VARCHAR)           as collation_name,
       cast(null as VARCHAR)           as domain_catalog,
       cast(null as VARCHAR)           as domain_schema,
       cast(null as VARCHAR)           as domain_name,
       is_autoincrement                as is_identity,
       cast(null as VARCHAR)           as identity_generation,
       cast(null as numeric)           as identity_start,
       autoincrement_step              as identity_increment,
       autoincrement_value             as identity_value
from information_schema.pdx_columns c
         inner join information_schema.pdx_tables t
                    on c.catalog = t.catalog
                        and c.schema = t.schema
                        and c.table = t.name
         inner join information_schema.pdx_schemata s
                    on c.catalog = s.catalog_name
                        and c.schema = s.schema_name
