package github.io.pedrogao.tinyspring.beans;

public class BeansException extends Exception {

    public BeansException(String message) {
        super(message);
    }

    public BeansException(String message, Throwable cause) {
        super(message, cause);
    }
}
