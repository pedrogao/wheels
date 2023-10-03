package github.io.pedrogao.tinybatis.session;

import github.io.pedrogao.tinybatis.xml.ConfigParser;
import github.io.pedrogao.tinybatis.xml.ConfigurationConfig;
import org.dom4j.DocumentException;

import java.io.InputStream;

public class SqlSessionFactoryBuilder {
    public SqlSessionFactory build(InputStream inputStream) {
        ConfigParser configParser = new ConfigParser();
        try {
            ConfigurationConfig configurationConfig = configParser.parse(inputStream);
            return new SqlSessionFactory(configurationConfig);
        } catch (DocumentException e) {
            throw new RuntimeException("parse mybatis-config.xml error", e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("load driver error", e);
        }

    }
}
