package github.io.pedrogao.tinybatis.xml;

import github.io.pedrogao.tinybatis.utils.ResourceUtil;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class ConfigParser {
    public ConfigurationConfig parse(InputStream is) throws DocumentException {
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(is);

        Element configurationElement = document.getRootElement();
        if (!configurationElement.getName().equals("configuration")) {
            throw new RuntimeException("root should be <configuration>");
        }

        Element environmentsElement = configurationElement.element("environments");
        String defaultEnvironment = environmentsElement.attribute("default").getValue();
        List<EnvironmentConfig> environmentConfigs = parseEnvironments(environmentsElement);
        Element mappersElement = configurationElement.element("mappers");
        List<MapperConfig> mapperConfigs = parseMappers(mappersElement);
        List<MapperNode> mapperNodes = new ArrayList<>(mapperConfigs.size());
        // Parse Mapper XML files
        MapperParser mapperParser = new MapperParser();
        for (MapperConfig mapperConfig : mapperConfigs) {
            InputStream mapperConfigInputStream = ResourceUtil.getResourceAsStream(mapperConfig.getResource());
            MapperNode mapperNode = mapperParser.parse(mapperConfigInputStream);
            mapperNodes.add(mapperNode);
        }

        return new ConfigurationConfig(defaultEnvironment, environmentConfigs, mapperConfigs, mapperNodes);
    }

    private List<MapperConfig> parseMappers(Element mappersElement) {
        List<Element> mapperElements = mappersElement.elements("mapper");
        List<MapperConfig> mapperConfigs = new ArrayList<>(mapperElements.size());
        for (Element mapperElement : mapperElements) {
            String resource = mapperElement.attribute("resource").getValue();
            mapperConfigs.add(new MapperConfig(resource));
        }
        return mapperConfigs;
    }

    private List<EnvironmentConfig> parseEnvironments(Element environmentsElement) {
        List<Element> environmentElements = environmentsElement.elements("environment");
        List<EnvironmentConfig> environmentConfigs = new ArrayList<>(environmentElements.size());
        for (Element environmentElement : environmentElements) {
            String id = environmentElement.attribute("id").getValue();
            Element transactionManagerElement = environmentElement.element("transactionManager");
            Element dataSourceElement = environmentElement.element("dataSource");
            TransactionManagerConfig transactionManagerConfig = parseTransactionManager(transactionManagerElement);
            DataSourceConfig dataSourceConfig = parseDataSource(dataSourceElement);
            environmentConfigs.add(new EnvironmentConfig(id, transactionManagerConfig, dataSourceConfig));
        }
        return environmentConfigs;
    }

    private DataSourceConfig parseDataSource(Element dataSourceElement) {
        String type = dataSourceElement.attribute("type").getValue();
        List<Element> propertyElements = dataSourceElement.elements("property");
        List<PropertyConfig> propertyConfigs = new ArrayList<>(propertyElements.size());
        for (Element propertyElement : propertyElements) {
            String name = propertyElement.attribute("name").getValue();
            String value = propertyElement.attribute("value").getValue();
            propertyConfigs.add(new PropertyConfig(name, value));
        }
        return new DataSourceConfig(type, propertyConfigs);
    }

    private TransactionManagerConfig parseTransactionManager(Element transactionManagerElement) {
        String type = transactionManagerElement.attribute("type").getValue();
        return new TransactionManagerConfig(type);
    }
}
