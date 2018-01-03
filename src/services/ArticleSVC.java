package services;

import databases.mybatis.mapper.ArticleMapper;
import databases.paginator.ListBox;
import databases.paginator.PageInfo;
import databases.paginator.ScrollListBox;
import org.apache.ibatis.session.SqlSession;
import server.comm.DataMap;

import java.util.List;

public class ArticleSVC extends BaseService {
    public ScrollListBox getArticleList(int lastId, String search){
        List<DataMap> list;
        int articleTotal;
        try(SqlSession sqlSession = super.getSession()){
            ArticleMapper articleMapper = sqlSession.getMapper(ArticleMapper.class);
            list = articleMapper.getArticleList(lastId, search);
            articleTotal = articleMapper.getArticleCount(search);
        }

        ScrollListBox scrollListBox = new ScrollListBox(articleTotal, list);
        return scrollListBox;
    }

    public DataMap getArticleInfo(int id){
        try(SqlSession sqlSession = super.getSession()){
            ArticleMapper articleMapper = sqlSession.getMapper(ArticleMapper.class);
            final DataMap article = articleMapper.getArticleInfo(id);
            return article;
        }
    }
}
