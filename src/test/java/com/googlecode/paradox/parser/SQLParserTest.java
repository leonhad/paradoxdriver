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
package com.googlecode.paradox.parser;

import com.googlecode.paradox.parser.nodes.*;
import com.googlecode.paradox.planner.nodes.FieldNode;
import com.googlecode.paradox.planner.nodes.FunctionNode;
import com.googlecode.paradox.planner.nodes.ParameterNode;
import com.googlecode.paradox.planner.nodes.ValueNode;
import com.googlecode.paradox.planner.nodes.comparable.*;
import com.googlecode.paradox.planner.nodes.join.ANDNode;
import com.googlecode.paradox.planner.nodes.join.ORNode;
import com.googlecode.paradox.planner.sorting.OrderType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test for {@link SQLParser}.
 *
 * @since 1.0
 */
class SQLParserTest {

    /**
     * Test for is null expressions.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testIsNull() throws SQLException {
        final SQLParser parser = new SQLParser("SELECT A FROM db.B WHERE A is NULL");
        final SQLNode tree = parser.parse();

        final SelectNode select = (SelectNode) tree;

        assertInstanceOf(IsNullNode.class, select.getCondition());

        final IsNullNode node = (IsNullNode) select.getCondition();
        assertEquals("A", node.getField().getName());
        assertNull(node.getLast());
    }

    /**
     * Test like expressions.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testLike() throws SQLException {
        final SQLParser parser = new SQLParser("select ac.AreasCovered from geog.tblAC ac " +
                " where ac.AreasCovered like 'Hackensack%'");
        final SQLNode tree = parser.parse();

        final SelectNode select = (SelectNode) tree;

        assertInstanceOf(LikeNode.class, select.getCondition());

        final LikeNode node = (LikeNode) select.getCondition();
        assertEquals("AreasCovered", node.getField().getName());
        assertEquals("Hackensack%", node.getLast().getName());
    }

    /**
     * Test ilike expressions.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testILike() throws SQLException {
        final SQLParser parser = new SQLParser("select ac.AreasCovered from geog.tblAC ac " +
                " where ac.AreasCovered ilike 'Hackensack%'");
        final SQLNode tree = parser.parse();

        final SelectNode select = (SelectNode) tree;

        assertInstanceOf(ILikeNode.class, select.getCondition());

        final ILikeNode node = (ILikeNode) select.getCondition();
        assertEquals("AreasCovered", node.getField().getName());
        assertEquals("Hackensack%", node.getLast().getName());
    }

    /**
     * Test for is not null expressions.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testIsNotNull() throws SQLException {
        final SQLParser parser = new SQLParser("SELECT A FROM db.B WHERE A is not NULL");
        final SQLNode tree = parser.parse();

        final SelectNode select = (SelectNode) tree;

        assertInstanceOf(IsNotNullNode.class, select.getCondition());

        final IsNotNullNode node = (IsNotNullNode) select.getCondition();
        assertEquals("A", node.getField().getName());
        assertNull(node.getLast());
    }

    /**
     * Test for not as a value.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testNullAsValue() throws SQLException {
        final SQLParser parser = new SQLParser("SELECT A FROM db.B WHERE A = NULL");
        final SQLNode tree = parser.parse();

        final SelectNode select = (SelectNode) tree;

        assertInstanceOf(EqualsNode.class, select.getCondition());

        final EqualsNode node = (EqualsNode) select.getCondition();
        assertEquals("A", node.getField().getName());
        assertInstanceOf(ValueNode.class, node.getLast());
    }

    /**
     * Test select with alias in fields.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testSelectWithAlias() throws SQLException {
        final SQLParser parser = new SQLParser("SELECT t.* FROM table t");
        final SQLNode tree = parser.parse();

        final SelectNode select = (SelectNode) tree;

        assertEquals(1, select.getFields().size());
        final SQLNode node = select.getFields().get(0);

        assertInstanceOf(AsteriskNode.class, node);
        final AsteriskNode asteriskNode = (AsteriskNode) node;

        assertEquals("t", asteriskNode.getTableName());
    }

    /**
     * Test for schema name.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testSchemaName() throws SQLException {
        final SQLParser parser = new SQLParser("SELECT t.* FROM db.table t");
        final SQLNode tree = parser.parse();

        final SelectNode select = (SelectNode) tree;

        assertEquals(1, select.getFields().size());
        final SQLNode node = select.getFields().get(0);

        assertInstanceOf(AsteriskNode.class, node);
        final AsteriskNode asteriskNode = (AsteriskNode) node;

        assertEquals("t", asteriskNode.getTableName());
    }

    /**
     * Test for column values.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testColumnValue() throws SQLException {
        final SQLParser parser = new SQLParser("SELECT 'test', 123 as number, null FROM client");
        final SQLNode tree = parser.parse();

        assertInstanceOf(SelectNode.class, tree);

        final SelectNode select = (SelectNode) tree;

        assertEquals(3, select.getFields().size());

        assertInstanceOf(ValueNode.class, select.getFields().get(0));
        assertEquals("test", select.getFields().get(0).getName());

        assertInstanceOf(ValueNode.class, select.getFields().get(1));
        assertEquals("123", select.getFields().get(1).getName());
        assertEquals("number", select.getFields().get(1).getAlias());

        assertInstanceOf(ValueNode.class, select.getFields().get(2));
        assertNull(select.getFields().get(2).getName());
        assertEquals("null", select.getFields().get(2).getAlias());

        assertEquals(1, select.getTables().size());
        assertEquals("client", select.getTables().get(0).getName());
    }

    /**
     * Test for join token.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testJoin() throws SQLException {
        final SQLParser parser = new SQLParser(
                "SELECT * FROM client c inner join test t on test_id = id and a <> b left join table on a = b");
        final SQLNode tree = parser.parse();

        assertInstanceOf(SelectNode.class, tree);

        final SelectNode select = (SelectNode) tree;

        assertEquals(1, select.getFields().size());
        assertEquals(TokenType.ASTERISK.name(), select.getFields().get(0).getName());

        assertEquals(3, select.getTables().size());
        assertEquals("client", select.getTables().get(0).getName());
    }

    /**
     * Test for full join token.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testFullJoin() throws SQLException {
        final SQLParser parser = new SQLParser("SELECT * FROM client c full join test t on test_id = id ");
        final SQLNode tree = parser.parse();

        assertInstanceOf(SelectNode.class, tree);

        final SelectNode select = (SelectNode) tree;

        assertEquals(1, select.getFields().size());
        assertEquals(TokenType.ASTERISK.name(), select.getFields().get(0).getName());

        assertEquals(2, select.getTables().size());
        assertEquals("client", select.getTables().get(0).getName());
        assertEquals("test", select.getTables().get(1).getName());
        assertInstanceOf(JoinNode.class, select.getTables().get(1));
        assertEquals(JoinType.FULL, ((JoinNode) select.getTables().get(1)).getJoinType());
    }

    /**
     * Test for SELECT token.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testSelect() throws SQLException {
        final SQLParser parser = new SQLParser("SELECT * FROM client");
        final SQLNode tree = parser.parse();

        assertInstanceOf(SelectNode.class, tree);

        final SelectNode select = (SelectNode) tree;

        assertEquals(1, select.getFields().size());
        assertEquals(TokenType.ASTERISK.name(), select.getFields().get(0).getName());

        assertEquals(1, select.getTables().size());
        assertEquals("client", select.getTables().get(0).getName());
    }

    /**
     * Test for no table after FROM.
     *
     * @throws SQLException in case of failures.
     */
    @ParameterizedTest
    @ValueSource(strings = {
            "select",
            "SELECT * FROM",
            "SELECT * FROM a ORDER BY",
            "SELECT * FROM a WHERE",
            "select a. FROM AREACODES a",
            "SELECT count(*) FROM AREACODES group by ?",
            "select * from fields.date7 offset -10",
            "select * from fields.date7 limit -10",
            "select * from a where ab = 1 aaa ba = 2",
            "select * from fields.DATE4 a a",
            "select * from fields.DATE4 a order by 1 a",
            "select lower(1, null, a.b) from a",
            "select upper('2'",
            "select * from table group by count(Id)",
            "select * from table where count(Id) = 1"
    })
    void testSyntaxErrors(String sql) throws SQLException {
        final SQLParser parser = new SQLParser(sql);
        assertThrows(SQLException.class, parser::parse);
    }

    /**
     * Test for tables.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testTable() throws SQLException {
        final SQLParser parser = new SQLParser("SELECT * FROM \"client.db\"");
        final SQLNode tree = parser.parse();

        assertInstanceOf(SelectNode.class, tree);

        final SelectNode select = (SelectNode) tree;

        assertEquals(1, select.getFields().size());
        assertEquals(TokenType.ASTERISK.name(), select.getFields().get(0).getName());

        assertEquals(1, select.getTables().size());
        assertEquals("client", select.getTables().get(0).getName());
    }

    /**
     * Test a SELECT with two tables.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testTwoTable() throws SQLException {
        final SQLParser parser = new SQLParser("select a.CODE as cod, state.NAME name FROM client, state");
        final SQLNode tree = parser.parse();

        assertInstanceOf(SelectNode.class, tree);

        final SelectNode select = (SelectNode) tree;

        assertEquals(2, select.getFields().size());
        assertEquals("CODE", select.getFields().get(0).getName());
        assertEquals("cod", select.getFields().get(0).getAlias());

        assertEquals("state", ((FieldNode) select.getFields().get(1)).getTableName());
        assertEquals("NAME", select.getFields().get(1).getName());
        assertEquals("name", select.getFields().get(1).getAlias());

        assertEquals(2, select.getTables().size());
        assertEquals("client", select.getTables().get(0).getName());
        assertEquals("state", select.getTables().get(1).getName());
    }

    /**
     * Test tables with alias.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testTwoTableWithAlias() throws SQLException {
        final SQLParser parser = new SQLParser("select *, name FROM client as cli, state STATE");
        final SQLNode tree = parser.parse();

        assertInstanceOf(SelectNode.class, tree);

        final SelectNode select = (SelectNode) tree;

        assertEquals(2, select.getFields().size());
        assertEquals(2, select.getTables().size());

        assertEquals(TokenType.ASTERISK.name(), select.getFields().get(0).getName());

        assertEquals("client", select.getTables().get(0).getName());
        assertEquals("cli", select.getTables().get(0).getAlias());
        assertEquals("state", select.getTables().get(1).getName());
        assertEquals("STATE", select.getTables().get(1).getAlias());
    }

    /**
     * Test for where token.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testWhere() throws SQLException {
        final SQLParser parser = new SQLParser("SELECT * FROM client as test WHERE a = b and c <> t");
        final SQLNode tree = parser.parse();

        assertInstanceOf(SelectNode.class, tree);

        final SelectNode select = (SelectNode) tree;

        assertEquals(1, select.getFields().size());
        assertEquals(TokenType.ASTERISK.name(), select.getFields().get(0).getName());

        assertEquals(1, select.getTables().size());
        assertEquals("client", select.getTables().get(0).getName());
        assertEquals("test", select.getTables().get(0).getAlias());

        assertNotNull(select.getCondition());
        assertInstanceOf(ANDNode.class, select.getCondition());

        ANDNode node = ((ANDNode) select.getCondition());
        assertEquals(2, node.getChildren().size());
        assertInstanceOf(EqualsNode.class, node.getChildren().get(0));
        assertInstanceOf(NotEqualsNode.class, node.getChildren().get(1));
        assertEquals("a", ((EqualsNode) node.getChildren().get(0)).getField().getName());
        assertEquals("b", ((EqualsNode) node.getChildren().get(0)).getLast().getName());
        assertEquals("c", ((NotEqualsNode) node.getChildren().get(1)).getField().getName());
        assertEquals("t", ((NotEqualsNode) node.getChildren().get(1)).getLast().getName());
    }

    /**
     * Test a where with alias.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testWhereWithAlias() throws SQLException {
        final SQLParser parser = new SQLParser("SELECT * FROM client as test WHERE test.a = c.b");
        final SQLNode tree = parser.parse();

        assertInstanceOf(SelectNode.class, tree);

        final SelectNode select = (SelectNode) tree;

        assertEquals(1, select.getFields().size());
        assertEquals(TokenType.ASTERISK.name(), select.getFields().get(0).getName());

        assertEquals(1, select.getTables().size());
        assertEquals("client", select.getTables().get(0).getName());
        assertEquals("test", select.getTables().get(0).getAlias());

        assertNotNull(select.getCondition());
        assertInstanceOf(EqualsNode.class, select.getCondition());

        EqualsNode node = ((EqualsNode) select.getCondition());
        assertEquals("test", node.getField().getTableName());
        assertEquals("a", node.getField().getName());
        assertEquals("c", node.getLast().getTableName());
        assertEquals("b", node.getLast().getName());
    }

    /**
     * Test a where with not.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testNot() throws SQLException {
        final SQLParser parser = new SQLParser("SELECT * FROM AREACODES WHERE NOT State = 'NY'");
        final SQLNode tree = parser.parse();

        assertInstanceOf(SelectNode.class, tree);

        final SelectNode select = (SelectNode) tree;

        assertEquals(1, select.getFields().size());
        assertEquals(TokenType.ASTERISK.name(), select.getFields().get(0).getName());

        assertEquals(1, select.getTables().size());
        assertEquals("AREACODES", select.getTables().get(0).getName());

        assertNotNull(select.getCondition());
        assertInstanceOf(NotNode.class, select.getCondition());

        final NotNode node = (NotNode) select.getCondition();
        assertEquals(1, node.getChildren().size());
        assertInstanceOf(EqualsNode.class, node.getChildren().get(0));
    }

    /**
     * Test a where with not and or.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testNotAndOr() throws SQLException {
        final SQLParser parser = new SQLParser(
                "select * from joins.joinb where not (Id = 2 or Id = 3) or Id = 2 order by Id"
        );

        final SQLNode tree = parser.parse();

        assertInstanceOf(SelectNode.class, tree);

        final SelectNode select = (SelectNode) tree;

        assertEquals(1, select.getFields().size());
        assertEquals(TokenType.ASTERISK.name(), select.getFields().get(0).getName());

        assertEquals(1, select.getTables().size());
        assertEquals("joinb", select.getTables().get(0).getName());

        assertNotNull(select.getCondition());
        assertInstanceOf(ORNode.class, select.getCondition());

        final ORNode node = (ORNode) select.getCondition();
        assertEquals(2, node.getChildren().size());
        assertInstanceOf(NotNode.class, node.getChildren().get(0));
        assertInstanceOf(EqualsNode.class, node.getChildren().get(1));
    }

    /**
     * Test a where with or and not.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testOrAndNot() throws SQLException {
        final SQLParser parser = new SQLParser(
                "select * from joins.joinb where Id = 2 or not (Id = 2 or Id = 3) order by Id"
        );

        final SQLNode tree = parser.parse();

        assertInstanceOf(SelectNode.class, tree);

        final SelectNode select = (SelectNode) tree;

        assertEquals(1, select.getFields().size());
        assertEquals(TokenType.ASTERISK.name(), select.getFields().get(0).getName());

        assertEquals(1, select.getTables().size());
        assertEquals("joinb", select.getTables().get(0).getName());

        assertNotNull(select.getCondition());
        assertInstanceOf(ORNode.class, select.getCondition());

        final ORNode node = (ORNode) select.getCondition();
        assertEquals(2, node.getChildren().size());
        assertInstanceOf(EqualsNode.class, node.getChildren().get(0));
        assertInstanceOf(NotNode.class, node.getChildren().get(1));
    }

    /**
     * Test a where with or and not.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testNotLike() throws SQLException {
        final SQLParser parser = new SQLParser("select * from joins.joinb where Id not like '2' ");

        final SQLNode tree = parser.parse();

        assertInstanceOf(SelectNode.class, tree);

        final SelectNode select = (SelectNode) tree;

        assertEquals(1, select.getFields().size());
        assertEquals(TokenType.ASTERISK.name(), select.getFields().get(0).getName());

        assertEquals(1, select.getTables().size());
        assertEquals("joinb", select.getTables().get(0).getName());

        assertNotNull(select.getCondition());
        assertInstanceOf(NotNode.class, select.getCondition());

        final NotNode node = (NotNode) select.getCondition();
        assertEquals(1, node.getChildren().size());
        assertInstanceOf(LikeNode.class, node.getChildren().get(0));
    }

    /**
     * Test for SELECT without FROM (two arguments).
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testSelectWithoutFromTwoArguments() throws SQLException {
        final SQLParser parser = new SQLParser("select 1, 'b'");
        final SQLNode tree = parser.parse();

        assertInstanceOf(SelectNode.class, tree);

        final SelectNode select = (SelectNode) tree;

        assertEquals(2, select.getFields().size());
        assertEquals("1", select.getFields().get(0).getName());
        assertEquals("b", select.getFields().get(1).getName());
    }

    /**
     * Test for SELECT without FROM.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testSelectWithoutFrom() throws SQLException {
        final SQLParser parser = new SQLParser("select 1");
        final SQLNode tree = parser.parse();

        assertInstanceOf(SelectNode.class, tree);

        final SelectNode select = (SelectNode) tree;

        assertEquals(1, select.getFields().size());
        assertEquals("1", select.getFields().get(0).getName());
    }

    /**
     * Test for JOIN optimization in AND node.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testJoinOptimizationAND() throws SQLException {
        final SQLParser parser = new SQLParser("select * from geog.tblAC ac, geog.tblsttes st, geog.County c " +
                " where c.StateID = st.State and st.State = ac.State and c.CountyID = 201");
        final SQLNode tree = parser.parse();

        assertInstanceOf(SelectNode.class, tree);

        final SelectNode select = (SelectNode) tree;

        assertEquals(1, select.getFields().size());
        assertInstanceOf(ANDNode.class, select.getCondition());

        final ANDNode and = (ANDNode) select.getCondition();
        assertEquals(3, and.getChildren().size());
    }

    /**
     * Test for JOIN optimization OR node.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testJoinOptimizationOR() throws SQLException {
        final SQLParser parser = new SQLParser("select * from geog.tblAC ac, geog.tblsttes st, geog.County c " +
                " where c.StateID = st.State or st.State = ac.State or c.CountyID = 201");
        final SQLNode tree = parser.parse();

        assertInstanceOf(SelectNode.class, tree);

        final SelectNode select = (SelectNode) tree;

        assertEquals(1, select.getFields().size());
        assertInstanceOf(ORNode.class, select.getCondition());

        final ORNode and = (ORNode) select.getCondition();
        assertEquals(3, and.getChildren().size());
    }

    /**
     * Test for parameters.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testParameters() throws SQLException {
        final SQLParser parser = new SQLParser("select * from geog.tblAC ac where ac.State = ? and ? = ac.AreaCode");
        final StatementNode tree = parser.parse();

        assertEquals(2, tree.getParameterCount());

        assertInstanceOf(SelectNode.class, tree);

        final SelectNode select = (SelectNode) tree;

        assertEquals(1, select.getFields().size());
        assertInstanceOf(ANDNode.class, select.getCondition());

        final ANDNode and = (ANDNode) select.getCondition();
        assertEquals(2, and.getChildren().size());

        assertInstanceOf(EqualsNode.class, and.getChildren().get(0));
        EqualsNode equals = (EqualsNode) and.getChildren().get(0);

        assertEquals("ac.State", equals.getField().toString());
        assertInstanceOf(ParameterNode.class, equals.getLast());
        assertEquals(0, ((ParameterNode) equals.getLast()).getParameterIndex());

        assertInstanceOf(EqualsNode.class, and.getChildren().get(1));
        equals = (EqualsNode) and.getChildren().get(1);
        assertInstanceOf(ParameterNode.class, equals.getField());
        assertEquals(1, ((ParameterNode) equals.getField()).getParameterIndex());
        assertEquals("ac.AreaCode", equals.getLast().toString());
    }

    /**
     * Test for parameters in select.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testParametersInSelect() throws SQLException {
        final SQLParser parser = new SQLParser("select ? as test");
        final StatementNode tree = parser.parse();
        assertEquals(1, tree.getParameterCount());

        assertInstanceOf(SelectNode.class, tree);

        final SelectNode select = (SelectNode) tree;

        assertEquals(1, select.getFields().size());
        assertInstanceOf(ParameterNode.class, select.getFields().get(0));
        assertEquals("test", select.getFields().get(0).getAlias());
    }

    /**
     * Test for parenthesis.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testParenthesis() throws SQLException {
        final SQLParser parser = new SQLParser("select * from geog.tblAC ac" +
                " where (ac.State = ? and ? = ac.AreaCode)");
        final StatementNode tree = parser.parse();

        assertEquals(2, tree.getParameterCount());

        assertInstanceOf(SelectNode.class, tree);

        final SelectNode select = (SelectNode) tree;

        assertEquals(1, select.getFields().size());
        assertInstanceOf(ANDNode.class, select.getCondition());

        final ANDNode and = (ANDNode) select.getCondition();
        assertEquals(2, and.getChildren().size());

        assertInstanceOf(EqualsNode.class, and.getChildren().get(0));
        EqualsNode equals = (EqualsNode) and.getChildren().get(0);

        assertEquals("ac.State", equals.getField().toString());
        assertInstanceOf(ParameterNode.class, equals.getLast());
        assertEquals(0, ((ParameterNode) equals.getLast()).getParameterIndex());

        assertInstanceOf(EqualsNode.class, and.getChildren().get(1));
        equals = (EqualsNode) and.getChildren().get(1);
        assertInstanceOf(ParameterNode.class, equals.getField());
        assertEquals(1, ((ParameterNode) equals.getField()).getParameterIndex());
        assertEquals("ac.AreaCode", equals.getLast().toString());
    }

    /**
     * Test for order by.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testOrderBy() throws SQLException {
        final SQLParser parser = new SQLParser("select * from fields.date7 order by \"DATE\"");
        final StatementNode tree = parser.parse();

        assertInstanceOf(SelectNode.class, tree);
        final SelectNode select = (SelectNode) tree;

        assertEquals(1, select.getFields().size());

        assertEquals(1, select.getOrder().size());

        final FieldNode field = select.getOrder().get(0);
        assertNull(field.getTableName());
        assertEquals("DATE", field.getName());
    }

    /**
     * Test for order by desc.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testOrderByDesc() throws SQLException {
        final SQLParser parser = new SQLParser("select * from fields.date7 order by \"DATE\" desc, \"TIME\" asc");
        final StatementNode tree = parser.parse();

        assertInstanceOf(SelectNode.class, tree);
        final SelectNode select = (SelectNode) tree;

        assertEquals(1, select.getFields().size());

        assertEquals(2, select.getOrder().size());

        FieldNode field = select.getOrder().get(0);
        assertNull(field.getTableName());
        assertEquals("DATE", field.getName());
        assertEquals(OrderType.DESC, select.getOrderTypes().get(0));

        field = select.getOrder().get(1);
        assertNull(field.getTableName());
        assertEquals("TIME", field.getName());
        assertEquals(OrderType.ASC, select.getOrderTypes().get(1));
    }

    /**
     * Test for IN expression.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testIn() throws SQLException {
        final SQLParser parser = new SQLParser("select id from fields.long where id in (1, '2')");
        final StatementNode tree = parser.parse();

        assertInstanceOf(SelectNode.class, tree);
        final SelectNode select = (SelectNode) tree;

        assertEquals(1, select.getFields().size());
        assertInstanceOf(InNode.class, select.getCondition());

        final InNode node = (InNode) select.getCondition();
        assertEquals(2, node.getValues().size());
        assertEquals("1", node.getValues().get(0).getName());
        assertEquals("2", node.getValues().get(1).getName());
    }

    /**
     * Test for function in fields.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testFunctionInFields() throws SQLException {
        final SQLParser parser = new SQLParser("select upper('2')");
        final StatementNode tree = parser.parse();

        assertInstanceOf(SelectNode.class, tree);
        final SelectNode select = (SelectNode) tree;

        assertEquals(1, select.getFields().size());
        assertInstanceOf(FunctionNode.class, select.getFields().get(0));

        final FunctionNode node = (FunctionNode) select.getFields().get(0);
        assertEquals("upper", node.getName());
        assertEquals(1, node.getParameters().size());
        assertEquals("2", node.getParameters().get(0).getName());
    }

    /**
     * Test for recursive function call.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testRecursiveFunction() throws SQLException {
        final SQLParser parser = new SQLParser("select upper(lower('2'))");
        final StatementNode tree = parser.parse();

        assertInstanceOf(SelectNode.class, tree);
        final SelectNode select = (SelectNode) tree;

        assertEquals(1, select.getFields().size());
        assertInstanceOf(FunctionNode.class, select.getFields().get(0));

        final FunctionNode upper = (FunctionNode) select.getFields().get(0);
        assertEquals("upper", upper.getName());
        assertEquals(1, upper.getParameters().size());

        assertInstanceOf(FunctionNode.class, upper.getParameters().get(0));

        final FunctionNode lower = (FunctionNode) upper.getParameters().get(0);
        assertEquals("lower", lower.getName());
        assertEquals(1, lower.getParameters().size());
        assertEquals("2", lower.getParameters().get(0).getName());
    }

    /**
     * Test for recursive function with alias.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testRecursiveFunctionAlias() throws SQLException {
        final SQLParser parser = new SQLParser("select varchar(current_date)");
        final StatementNode tree = parser.parse();

        assertInstanceOf(SelectNode.class, tree);
        final SelectNode select = (SelectNode) tree;

        assertEquals(1, select.getFields().size());
        assertInstanceOf(FunctionNode.class, select.getFields().get(0));

        final FunctionNode upper = (FunctionNode) select.getFields().get(0);
        assertEquals("varchar", upper.getName());
        assertEquals(1, upper.getParameters().size());

        assertInstanceOf(FunctionNode.class, upper.getParameters().get(0));

        final FunctionNode lower = (FunctionNode) upper.getParameters().get(0);
        assertEquals("current_date", lower.getName());
        assertEquals(0, lower.getParameters().size());
    }

    /**
     * Test for function alias.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testFunctionAlias() throws SQLException {
        final SQLParser parser = new SQLParser("select upper('2') as alias");
        final StatementNode tree = parser.parse();

        assertInstanceOf(SelectNode.class, tree);
        final SelectNode select = (SelectNode) tree;

        assertEquals(1, select.getFields().size());
        assertInstanceOf(FunctionNode.class, select.getFields().get(0));

        final FunctionNode upper = (FunctionNode) select.getFields().get(0);
        assertEquals("upper", upper.getName());
        assertEquals("alias", upper.getAlias());
        assertEquals(1, upper.getParameters().size());
    }

    /**
     * Test for recursive functions with alias name.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testRecursiveFunctionAliasName() throws SQLException {
        final SQLParser parser = new SQLParser("select upper(lower('Name')) as alias");
        final StatementNode tree = parser.parse();

        assertInstanceOf(SelectNode.class, tree);
        final SelectNode select = (SelectNode) tree;

        assertEquals(1, select.getFields().size());
        assertInstanceOf(FunctionNode.class, select.getFields().get(0));

        final FunctionNode upper = (FunctionNode) select.getFields().get(0);
        assertEquals("upper", upper.getName());
        assertEquals("alias", upper.getAlias());
        assertEquals(1, upper.getParameters().size());

        assertInstanceOf(FunctionNode.class, upper.getParameters().get(0));
    }

    /**
     * Test for function in clause.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testFunctionInClause() throws SQLException {
        final SQLParser parser = new SQLParser("select * from fields.date7 where \"DATE\" = DATE('2018-01-02')");
        final StatementNode tree = parser.parse();

        assertInstanceOf(SelectNode.class, tree);
        final SelectNode select = (SelectNode) tree;

        assertEquals(1, select.getFields().size());
        assertInstanceOf(AsteriskNode.class, select.getFields().get(0));

        assertInstanceOf(EqualsNode.class, select.getCondition());
        final EqualsNode equalsNode = (EqualsNode) select.getCondition();
        assertEquals(0, equalsNode.getChildren().size());
        assertNotNull(equalsNode.getField());
        assertInstanceOf(FunctionNode.class, equalsNode.getLast());
    }

    /**
     * Test for boolean values.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testBooleanValues() throws SQLException {
        final SQLParser parser = new SQLParser("SELECT TRUE, FALSE");
        final StatementNode tree = parser.parse();

        final SelectNode select = (SelectNode) tree;

        assertEquals(2, select.getFields().size());

        assertInstanceOf(ValueNode.class, select.getFields().get(0));
        assertInstanceOf(ValueNode.class, select.getFields().get(1));
        assertEquals("true", select.getFields().get(0).getName());
        assertEquals("false", select.getFields().get(1).getName());
    }

    /**
     * Test for function in order by.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testFunctionInOrderBy() throws SQLException {
        final SQLParser parser = new SQLParser("select * from fields.long order by VARCHAR(Id)");
        final StatementNode tree = parser.parse();

        final SelectNode select = (SelectNode) tree;

        assertEquals(1, select.getOrder().size());
        assertInstanceOf(FunctionNode.class, select.getOrder().get(0));
        final FunctionNode functionNode = (FunctionNode) select.getOrder().get(0);

        assertEquals("VARCHAR", functionNode.getName());
        assertEquals(1, functionNode.getClauseFields().size());

        final FieldNode fieldNode = functionNode.getClauseFields().iterator().next();
        assertEquals("Id", fieldNode.getName());
    }

    /**
     * Test for group by.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testGroupBy() throws SQLException {
        final SQLParser parser = new SQLParser("select * from fields.long group by Id");
        final StatementNode tree = parser.parse();

        final SelectNode select = (SelectNode) tree;

        assertEquals(1, select.getGroups().size());
        final FieldNode field = select.getGroups().get(0);

        assertEquals("Id", field.getName());
    }

    /**
     * Test for group by and where.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testGroupByAndWhere() throws SQLException {
        final SQLParser parser = new SQLParser(
                "SELECT count(*) FROM AREACODES WHERE (NOT State = 'NY') or State = 'NJ' group by State"
        );

        final StatementNode tree = parser.parse();

        final SelectNode select = (SelectNode) tree;

        assertNotNull(select.getCondition());
        assertEquals(1, select.getGroups().size());
        final FieldNode field = select.getGroups().get(0);

        assertEquals("State", field.getName());
    }

    /**
     * Test for group by and order by.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testGroupByAndOrderBy() throws SQLException {
        final SQLParser parser = new SQLParser("SELECT count(*) FROM AREACODES group by State ORDER BY State");

        final StatementNode tree = parser.parse();

        final SelectNode select = (SelectNode) tree;

        assertFalse(select.getOrder().isEmpty());
        assertEquals(1, select.getGroups().size());
        final FieldNode field = select.getGroups().get(0);

        assertEquals("State", field.getName());
    }

    /**
     * Test for group by and fixed values.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testGroupByAndFixedValues() throws SQLException {
        final SQLParser parser = new SQLParser("SELECT count(*) FROM AREACODES group by 123, 'abc'");

        final StatementNode tree = parser.parse();

        final SelectNode select = (SelectNode) tree;

        assertEquals(2, select.getGroups().size());
        assertEquals("123", select.getGroups().get(0).getName());
        assertEquals("abc", select.getGroups().get(1).getName());
    }

    /**
     * Test for aggregate function in select function.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testAggregateFunctionInSelectFunction() throws SQLException {
        final SQLParser parser = new SQLParser("select upper(count(1)) from fields.date7");

        final StatementNode tree = parser.parse();

        final SelectNode select = (SelectNode) tree;

        assertEquals(1, select.getFields().size());
        assertInstanceOf(FunctionNode.class, select.getFields().get(0));
        final FunctionNode node = (FunctionNode) select.getFields().get(0);

        assertEquals(1, node.getClauseFields().size());
        final FunctionNode function = (FunctionNode) node.getClauseFields().iterator().next();
        assertEquals("count", function.getName());

        assertEquals(1, node.getGroupingNodes().size());
        assertSame(function, node.getGroupingNodes().get(0));
    }

    /**
     * Test for offset and limit.
     *
     * @throws SQLException in case of failures.
     */
    @Test
    void testOffsetLimit() throws SQLException {
        final SQLParser parser = new SQLParser("select * from fields.date7 limit 10 offset 2");

        final StatementNode tree = parser.parse();
        final SelectNode select = (SelectNode) tree;

        assertNotNull(select.getLimit());
        assertNotNull(select.getOffset());

        assertEquals(10, select.getLimit().intValue());
        assertEquals(2, select.getOffset().intValue());
    }
}
