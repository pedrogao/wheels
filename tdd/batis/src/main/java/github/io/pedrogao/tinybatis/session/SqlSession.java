package github.io.pedrogao.tinybatis.session;

import github.io.pedrogao.tinybatis.ast.Expr;
import github.io.pedrogao.tinybatis.ast.Interpreter;
import github.io.pedrogao.tinybatis.ast.Parser;
import github.io.pedrogao.tinybatis.xml.SelectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class SqlSession implements AutoCloseable {

    private final static Logger log = LoggerFactory.getLogger(SqlSession.class);

    private Connection connection;

    private Map<String, SelectNode> selectNodeMap;

    private final Interpreter interpreter = new Interpreter();

    public SqlSession(Connection connection, Map<String, SelectNode> selectNodeMap) {
        this.connection = connection;
        this.selectNodeMap = selectNodeMap;
    }

    public Object selectOne(String id, Object... params) {
        return selectOne(id, Map.of(), params);
    }

    /**
     * We don't use `interface` to indicate the name of `params`, so we need to use Map to store the variables
     * Which is not a good idea, but it's enough for now
     * Just give a try.
     */
    public Object selectOne(String id, Map<String, Object> variables, Object... params) {
        try {
            SelectNode selectNode = selectNodeMap.get(id);
            if (selectNode == null) {
                throw new RuntimeException("selectNode not found");
            }

            StringBuilder sql = new StringBuilder(selectNode.getSql());
            for (var ifNode : selectNode.getIfNodes()) {
                String subSql = ifNode.getSubSql();
                Parser parser = new Parser(ifNode.getTokens());
                Expr expr = parser.parseExpr();
                Object res = interpreter.interpret(expr, variables);
                if (res != null && (Boolean) res) {
                    sql.append(" ").append(subSql);
                }
            }

            PreparedStatement statement = connection.prepareStatement(sql.toString());
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
            log.info("Executing SQL: {} with params: {}", sql, params);

            ResultSet resultSet = statement.executeQuery();
            Object result = mapResultSet(selectNode, resultSet);
            // Remember to commit the transaction
            connection.commit();
            return result;
        } catch (Exception e) {
            log.error("selectOne error", e);
            try {
                connection.rollback();
            } catch (SQLException ex) {
                log.error("rollback error", ex);
                throw new RuntimeException(ex);
            }
        }
        return null;
    }

    private Object mapResultSet(SelectNode selectNode, ResultSet resultSet) throws
            ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException {
        // Map ResultSet to Object
        // github.io.pedrogao.tinybatis.Blog
        String resultType = selectNode.getResultType();
        Class<?> clazz = Class.forName(resultType);
        List<Field> fields = Arrays.asList(clazz.getDeclaredFields());
        fields.forEach(field -> field.setAccessible(true));

        List<Object> resultList = new ArrayList<>();
        while (resultSet.next()) {
            Object instance = clazz.getConstructor().newInstance();
            for (Field field : fields) {
                String name = field.getName();
                Object value = resultSet.getObject(name);
                field.set(instance, value);
            }
            resultList.add(instance);
        }

        if (resultList.size() == 1) {
            return resultList.get(0);
        } else if (resultList.isEmpty()) {
            return null;
        } else {
            return resultList;
        }
    }


    @Override
    public void close() throws Exception {
        connection.close();
    }
}
