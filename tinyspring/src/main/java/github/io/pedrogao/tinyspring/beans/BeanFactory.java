package github.io.pedrogao.tinyspring.beans;

public interface BeanFactory {
    Object getBean(String beanName) throws BeansException;

    boolean containsBean(String beanName);

    boolean isSingleton(String beanName);

    boolean isPrototype(String beanName);

    Class<?> getType(String beanName);
}
