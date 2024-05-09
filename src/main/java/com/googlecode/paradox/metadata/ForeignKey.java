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

package com.googlecode.paradox.metadata;

import com.googlecode.paradox.data.ValidationField;
import com.googlecode.paradox.metadata.paradox.ParadoxReferentialIntegrity;
import com.googlecode.paradox.utils.Utils;

import java.util.Arrays;

public class ForeignKey {

    private String name;

    private boolean cascade;

    private Field[] originFields;

    private Field[] referencedFields;

    private Table referencedTable;

    private Table originTable;

    public ForeignKey(Table table, ParadoxReferentialIntegrity referentialIntegrity) {
        this.originTable = table;
        this.name = referentialIntegrity.getName();
        this.referencedTable = referentialIntegrity.getDestinationTable();
        Field[] fields = referentialIntegrity.getDestinationTable().getFields();
        this.referencedFields = Arrays.stream(referentialIntegrity.getDestinationFields()).mapToObj(i -> fields[i - 1]).toArray(Field[]::new);
        this.originFields = Arrays.stream(referentialIntegrity.getFields()).mapToObj(i -> table.getFields()[i - 1]).toArray(Field[]::new);
    }

    public ForeignKey(Field field, ValidationField validationField) {
        this.name = String.format("DT_%s_%s", field.getTable(), field.getName());
        this.originTable = field.getTable();
        this.originFields = new Field[]{field};
        this.referencedTable = validationField.getReferencedTable();
        this.referencedFields = new Field[]{referencedTable.getFields()[0]};
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCascade() {
        return cascade;
    }

    public void setCascade(boolean cascade) {
        this.cascade = cascade;
    }

    public Field[] getOriginFields() {
        return originFields;
    }

    public void setOriginFields(Field[] originFields) {
        this.originFields = originFields;
    }

    public Field[] getReferencedFields() {
        return referencedFields;
    }

    public void setReferencedFields(Field[] referencedFields) {
        this.referencedFields = referencedFields;
    }

    public Table getReferencedTable() {
        return referencedTable;
    }

    public void setReferencedTable(Table referencedTable) {
        this.referencedTable = referencedTable;
    }

    public Table getOriginTable() {
        return originTable;
    }

    public void setOriginTable(Table originTable) {
        this.originTable = originTable;
    }

}
