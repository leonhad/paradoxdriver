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

package com.googlecode.paradox.metadata.paradox;

import com.googlecode.paradox.data.ValidationField;
import com.googlecode.paradox.metadata.Field;
import com.googlecode.paradox.metadata.Table;
import com.googlecode.paradox.utils.Utils;

import java.util.Arrays;

public class ParadoxForeignKey {

    private String name;

    private boolean cascade;

    private Field[] originFields;

    private Field[] referencedFields;

    private Table referencedTable;

    private Table originTable;

    private String referencedTableName;

    private int[] referencedFieldIndexes;

    public ParadoxForeignKey(Table table, ParadoxReferentialIntegrity referentialIntegrity) {
        this.originTable = table;
        this.name = referentialIntegrity.getName();
        this.referencedTableName = Utils.removeSuffix(referentialIntegrity.getDestinationTable(), "DB");
        this.referencedFieldIndexes = referentialIntegrity.getDestinationFields();
        this.originFields = Arrays.stream(referentialIntegrity.getFields()).mapToObj(i -> table.getFields()[i - 1]).toArray(Field[]::new);

        // FIXME destination fields name
    }

    public ParadoxForeignKey(Field field, ValidationField validationField) {
        this.name = String.format("DT_%s_%s", field.getTable(), field.getName());
        this.originTable = field.getTable();
        this.originFields = new Field[]{field};
        this.referencedFieldIndexes = new int[0];
        this.referencedTableName = Utils.removeSuffix(validationField.getDestinationTable(), "DB");

        // FIXME destination fields name
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

    public String getReferencedTableName() {
        return referencedTableName;
    }

    public void setReferencedTableName(String referencedTableName) {
        this.referencedTableName = referencedTableName;
    }

    public int[] getReferencedFieldIndexes() {
        return referencedFieldIndexes;
    }

    public void setReferencedFieldIndexes(int[] referencedFieldIndexes) {
        this.referencedFieldIndexes = referencedFieldIndexes;
    }
}
