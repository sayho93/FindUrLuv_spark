package services;

import databases.mybatis.mapper.AdminMapper;
import databases.mybatis.mapper.UserMapper;
import org.apache.ibatis.session.SqlSession;
import server.comm.DataMap;
import server.rest.RestUtil;

public class AdminSVC extends BaseService {

    public DataMap checkPassword(String id, String pw){
        pw = RestUtil.getMessageDigest(pw);
        try(SqlSession sqlSession = super.getSession()){
            AdminMapper adminMapper = sqlSession.getMapper(AdminMapper.class);
            return adminMapper.getAdmin(id, pw);
        }
    }
}
