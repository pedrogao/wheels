package github.io.pedrogao.tinyspring.beans;

import github.io.pedrogao.tinyspring.core.Resource;
import org.dom4j.Element;

import java.util.List;

public class XmlBeanDefinitionReader {
    private final SimpleBeanFactory simpleBeanFactory;

    public XmlBeanDefinitionReader(SimpleBeanFactory simpleBeanFactory) {
        this.simpleBeanFactory = simpleBeanFactory;
    }

    public void loadBeanDefinitions(Resource resource) {
        while (resource.hasNext()) {
            Object next = resource.next();
            if (next instanceof Element element) {
                String beanId = element.attributeValue("id");
                String beanClass = element.attributeValue("class");
                BeanDefinition beanDefinition = new BeanDefinition(beanId, beanClass);

                List<Element> propertyElements = element.elements("property");
                PropertyValues propertyValues = new PropertyValues();
                for (Element e : propertyElements) {
                    String pType = e.attributeValue("type");
                    String pName = e.attributeValue("name");
                    String pValue = e.attributeValue("value");
                    propertyValues.addPropertyValue(new PropertyValue(pType, pName, pValue));
                }
                beanDefinition.setPropertyValues(propertyValues);

                ArgumentValues argumentValues = new ArgumentValues();
                List<Element> constructorElements = element.elements("constructor-arg");
                for (Element e : constructorElements) {
                    String pType = e.attributeValue("type");
                    String pName = e.attributeValue("name");
                    String pValue = e.attributeValue("value");
                    argumentValues.addArgumentValue(new ArgumentValue(pType, pName, pValue));
                }
                beanDefinition.setConstructorArgumentValues(argumentValues);

                simpleBeanFactory.registerBeanDefinition(beanId, beanDefinition);
            }
        }
    }
}
