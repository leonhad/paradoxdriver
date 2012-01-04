package org.paradox.procedures;

import java.util.ArrayList;
import org.paradox.procedures.math.Averange;
import org.paradox.procedures.math.Count;
import org.paradox.procedures.math.Max;
import org.paradox.procedures.math.Min;
import org.paradox.procedures.math.Sum;

/**
 *
 * @author 72330554168
 */
public final class ProcedureAS {

    private static final ProcedureAS instance = new ProcedureAS();

    private ProcedureAS() {
        register(new Averange());
        register(new Count());
        register(new Max());
        register(new Min());
        register(new Sum());
    }

    public static ProcedureAS getInstance() {
        return instance;
    }

    private ArrayList<CallableProcedure> procedures = new ArrayList<CallableProcedure>();

    public void register(final CallableProcedure procedure) {
        procedures.add(procedure);
    }

    public ArrayList<CallableProcedure> list() {
        return procedures;
    }

    public CallableProcedure get(final String name) {
        for (final CallableProcedure procedure : procedures) {
            if (procedure.getName().equalsIgnoreCase(name)) {
                return procedure;
            }
        }
        return null;
    }
}
