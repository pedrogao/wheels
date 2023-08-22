package github.io.pedrogao.tinyspring.beans.factory.config;

public interface SingletonBeanRegistry {
    void registerSingleton(String beanName, Object bean);

    Object getSingleton(String beanName);

    boolean containsSingleton(String beanName);

    String[] getSingletonNames();
}
