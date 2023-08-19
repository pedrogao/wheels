package github.io.pedrogao.tinyspring.beans;

public interface SingletonBeanRegistry {
    void registerSingleton(String beanName, Object bean);

    Object getSingleton(String beanName);

    boolean containsSingleton(String beanName);

    String[] getSingletonNames();
}
