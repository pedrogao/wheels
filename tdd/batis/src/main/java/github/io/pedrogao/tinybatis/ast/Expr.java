package github.io.pedrogao.tinybatis.ast;

public abstract class Expr {
    public interface Visitor<R> {
        R visitVariableExpr(Variable expr);

        R visitLiteralExpr(Literal expr);

        R visitGroupingExpr(Grouping expr);

        R visitUnaryExpr(Unary expr);

        R visitBinaryExpr(Binary expr);
    }

    public static class Binary extends Expr {
        Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }

        final Expr left;
        final Token operator;
        final Expr right;
    }

    public static class Grouping extends Expr {
        Grouping(Expr expression) {
            this.expression = expression;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }

        final Expr expression;
    }

    public static class Literal extends Expr {
        Literal(Object value) {
            this.value = value;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }

        final Object value;

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Literal) {
                if (value == null)
                    return ((Literal) obj).value == null;
                return ((Literal) obj).value.equals(value);
            }
            return false;
        }
    }

    public static class Unary extends Expr {
        Unary(Token operator, Expr right) {
            this.operator = operator;
            this.right = right;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }

        final Token operator;
        final Expr right;
    }

    public static class Variable extends Expr {
        Variable(Token name) {
            this.name = name;
        }

        @Override
        public <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableExpr(this);
        }

        final Token name;

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Variable) {
                return ((Variable) obj).name.equals(name);
            }
            return false;
        }
    }

    public abstract <R> R accept(Visitor<R> visitor);
}
