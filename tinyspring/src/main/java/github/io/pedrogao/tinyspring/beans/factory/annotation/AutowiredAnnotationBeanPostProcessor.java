package github.io.pedrogao.tinyspring.beans.factory.annotation;

import github.io.pedrogao.tinyspring.beans.factory.config.BeanPostProcessor;
import github.io.pedrogao.tinyspring.beans.BeansException;
import github.io.pedrogao.tinyspring.beans.factory.config.AutowireCapableBeanFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class AutowiredAnnotationBeanPostProcessor implements BeanPostProcessor {

    private final Logger log = LoggerFactory.getLogger(AutowiredAnnotationBeanPostProcessor.class);

    private AutowireCapableBeanFactory beanFactory;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Object result = bean;

        Class<?> clazz = bean.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            Autowired annotation = field.getAnnotation(Autowired.class);
            if (annotation != null) {
                String fieldName = field.getName();
                Object value = beanFactory.getBean(fieldName);
                if (value != null) {
                    field.setAccessible(true);
                    try {
                        field.set(bean, value);
                    } catch (IllegalAccessException e) {
                        log.error("Error setting field value", e);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return null;
    }

    public AutowireCapableBeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void setBeanFactory(AutowireCapableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
