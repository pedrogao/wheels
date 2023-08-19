package github.io.pedrogao.tinyspring.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleBeanFactory extends DefaultSingletonBeanRegistry implements BeanFactory, BeanDefinitionRegistry {

    private final Logger log = LoggerFactory.getLogger(SimpleBeanFactory.class);

    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);

    private final List<String> beanDefinitionNames = new ArrayList<>();

    public SimpleBeanFactory() {
    }

    @Override
    public Object getBean(String beanName) throws BeansException {
        Object singleton = getSingleton(beanName);
        if (singleton != null) {
            return singleton;
        }

        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null) {
            throw new BeansException("No bean named '" + beanName + "' is defined");
        }
        singleton = createBean(beanDefinition);
        registerBean(beanName, singleton);
        if (beanDefinition.getInitMethodName() != null) {
            // TODO do when init method is defined
        }
        return singletons;
    }

    @Override
    public boolean containsBean(String beanName) {
        return containsSingleton(beanName);
    }

    public void registerBean(String beanName, Object bean) {
        registerSingleton(beanName, bean);
    }

    @Override
    public boolean isSingleton(String beanName) {
        return beanDefinitionMap.get(beanName).isSingleton();
    }

    @Override
    public boolean isPrototype(String beanName) {
        return beanDefinitionMap.get(beanName).isPrototype();
    }

    @Override
    public Class<?> getType(String beanName) {
        return beanDefinitionMap.get(beanName).getClass();
    }

    @Override
    public void registerBeanDefinition(String name, BeanDefinition beanDefinition) {
        beanDefinitionMap.put(name, beanDefinition);
        beanDefinitionNames.add(name);
        if (beanDefinition.isLazyInit())
            return;

        try {
            getBean(name);
        } catch (BeansException e) {
            log.error("registerBeanDefinition error", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeBeanDefinition(String name) {
        beanDefinitionMap.remove(name);
        beanDefinitionNames.remove(name);
        removeSingleton(name);
    }

    @Override
    public BeanDefinition getBeanDefinition(String name) {
        return beanDefinitionMap.get(name);
    }

    @Override
    public boolean containsBeanDefinition(String name) {
        return beanDefinitionMap.containsKey(name);
    }

    private Object createBean(BeanDefinition bd) {
        Class<?> clz = null;
        Object obj = null;
        Constructor<?> con = null;

        try {
            clz = Class.forName(bd.getClassName());
            // handle constructor
            ArgumentValues argumentValues = bd.getConstructorArgumentValues();
            if (!argumentValues.isEmpty()) {
                Class<?>[] paramTypes = new Class<?>[argumentValues.getArgumentCount()];
                Object[] paramValues = new Object[argumentValues.getArgumentCount()];
                for (int i = 0; i < argumentValues.getArgumentCount(); i++) {
                    ArgumentValue argumentValue = argumentValues.getIndexedArgumentValue(i);
                    if ("String".equals(argumentValue.getType()) || "java.lang.String".equals(argumentValue.getType())) {
                        paramTypes[i] = String.class;
                        paramValues[i] = argumentValue.getValue();
                    } else if ("Integer".equals(argumentValue.getType()) || "java.lang.Integer".equals(argumentValue.getType())) {
                        paramTypes[i] = Integer.class;
                        paramValues[i] = Integer.valueOf((String) argumentValue.getValue());
                    } else if ("int".equals(argumentValue.getType())) {
                        paramTypes[i] = int.class;
                        paramValues[i] = Integer.valueOf((String) argumentValue.getValue());
                    } else {
                        paramTypes[i] = String.class;
                        paramValues[i] = argumentValue.getValue();
                    }
                }
                try {
                    con = clz.getConstructor(paramTypes);
                    obj = con.newInstance(paramValues);
                } catch (NoSuchMethodException | SecurityException | IllegalArgumentException |
                         InvocationTargetException e) {
                    log.error("No such method or security exception or illegal argument or invocation target exception", e);
                }
            } else {
                obj = clz.newInstance();
            }

        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            log.error("Instantiation or illegal access or class not found exception", e);
        }

        // handle properties
        PropertyValues propertyValues = bd.getPropertyValues();
        if (!propertyValues.isEmpty()) {
            for (int i = 0; i < propertyValues.size(); i++) {
                PropertyValue propertyValue = propertyValues.getPropertyValueList().get(i);
                String pName = propertyValue.getName();
                String pType = propertyValue.getType();
                Object pValue = propertyValue.getValue();

                Class<?>[] paramTypes = new Class<?>[1];
                if ("String".equals(pType) || "java.lang.String".equals(pType)) {
                    paramTypes[0] = String.class;
                } else if ("Integer".equals(pType) || "java.lang.Integer".equals(pType)) {
                    paramTypes[0] = Integer.class;
                } else if ("int".equals(pType)) {
                    paramTypes[0] = int.class;
                } else {
                    paramTypes[0] = String.class;
                }

                Object[] paramValues = new Object[1];
                paramValues[0] = pValue;
                String methodName = "set" + pName.substring(0, 1).toUpperCase() + pName.substring(1);
                Method method = null;
                try {
                    method = clz.getMethod(methodName, paramTypes);
                } catch (NoSuchMethodException | SecurityException e) {
                    log.error("No such method or security exception", e);
                }
                try {
                    method.invoke(obj, paramValues);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    log.error("Illegal access or illegal argument or invocation target exception", e);
                }

            }
        }
        return obj;
    }

}
