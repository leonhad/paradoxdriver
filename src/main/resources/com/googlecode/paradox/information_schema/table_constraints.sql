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

select "catalog" as constraint_catalog,
       "schema"  as constraint_schema,
       "name"    as constraint_name,
       "catalog" as table_catalog,
       "schema"  as table_schema,
       "table"   as table_name,
       type      as constraint_type,
       is_deferrable,
       initially_deferred
from information_schema.pdx_table_constraints
order by "catalog", "schema", "table", "name", type
