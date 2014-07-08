package org.paradox.procedures;

import java.util.ArrayList;
import org.paradox.metadata.ParadoxField;

/**
 *
 * @author 72330554168
 */
public interface CallableProcedure {

    public String getName();

    public String getRemarks();

    public ArrayList<ParadoxField> getCols();

    public int getReturnType();
}
