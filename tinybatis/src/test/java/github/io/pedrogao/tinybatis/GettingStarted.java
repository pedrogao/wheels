package github.io.pedrogao.tinybatis;

import java.io.InputStream;

import github.io.pedrogao.tinybatis.session.SqlSession;
import github.io.pedrogao.tinybatis.session.SqlSessionFactory;
import github.io.pedrogao.tinybatis.session.SqlSessionFactoryBuilder;
import github.io.pedrogao.tinybatis.utils.ResourceUtil;
import org.junit.Test;

/**
 * Implement a Tiny Batis by TDD
 *
 * @link <a href="https://mybatis.org/mybatis-3/getting-started.html">getting-started</a>
 */
public class GettingStarted {

    @Test
    public void testWithXml() throws Exception {
        String resource = "mybatis-config.xml";
        InputStream inputStream = ResourceUtil.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

        try (SqlSession session = sqlSessionFactory.openSession()) {
            Blog blog = (Blog) session.selectOne("github.io.pedrogao.tinybatis.BlogMapper.selectBlog", 1);
            System.out.println(blog);
        }
    }

}
