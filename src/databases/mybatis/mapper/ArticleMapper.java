package databases.mybatis.mapper;

import org.apache.ibatis.annotations.Param;
import server.comm.DataMap;

import java.util.List;

public interface ArticleMapper {
    List<DataMap> getArticleList(@Param("lastId") int lastId, @Param("search") String search);

    Integer getArticleCount(@Param("search") String search);

    DataMap getArticleInfo(@Param("id") int id);
}
