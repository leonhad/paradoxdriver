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
