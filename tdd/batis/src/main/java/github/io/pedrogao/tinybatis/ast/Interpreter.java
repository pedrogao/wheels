package github.io.pedrogao.tinybatis.ast;

import java.util.Map;

public class Interpreter implements Expr.Visitor<Object> {

    private Map<String, Object> variables;

    public Object interpret(Expr expression) {
        try {
            Object value = evaluate(expression);
            return value;
        } catch (Exception error) {
            throw new RuntimeException(error);
        }
    }

    public Object interpret(Expr expression, Map<String, Object> variables) {
        this.variables = variables;
        try {
            Object value = evaluate(expression);
            return value;
        } catch (Exception error) {
            throw new RuntimeException(error);
        }
    }

    @Override
    public Object visitBinaryExpr(Expr.Binary expr) {
        var left = evaluate(expr.left);
        var right = evaluate(expr.right);

        return switch (expr.operator.getType()) {
            case MINUS -> (double) left - (double) right;
            case SLASH -> (double) left / (double) right;
            case STAR -> (double) left * (double) right;
            case PLUS -> (double) left + (double) right;
            case BANG_EQUAL -> !isEqual(left, right);
            case EQUAL_EQUAL -> isEqual(left, right);
            default -> null;
        };
    }

    @Override
    public Object visitGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitVariableExpr(Expr.Variable expr) {
        return variables.get(expr.name.getText());
    }

    @Override
    public Object visitLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitUnaryExpr(Expr.Unary expr) {
        var right = evaluate(expr.right);

        return switch (expr.operator.getType()) {
            case MINUS -> -(double) right;
            case BANG -> !isTruthy(right);
            default -> null;
        };
    }

    private Object evaluate(Expr expression) {
        return expression.accept(this);
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null) return false;

        return a.equals(b);
    }

    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean) object;
        return true;
    }
}
