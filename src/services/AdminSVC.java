package services;

import databases.mybatis.mapper.AdminMapper;
import databases.mybatis.mapper.UserMapper;
import databases.paginator.ListBox;
import databases.paginator.PageInfo;
import org.apache.ibatis.session.SqlSession;
import server.comm.DataMap;
import server.rest.RestUtil;
import utils.Log;

import java.util.List;

public class AdminSVC extends BaseService {

    public DataMap checkPassword(String id, String pw){
        pw = RestUtil.getMessageDigest(pw);
        try(SqlSession sqlSession = super.getSession()){
            AdminMapper adminMapper = sqlSession.getMapper(AdminMapper.class);
            return adminMapper.getAdmin(id, pw);
        }
    }

    public ListBox getMemberList(int page, int limit, String search, int sido, int gungu){
        final int realPage = (page - 1) * limit;
        final List<DataMap> list;
        int total;

        try(SqlSession sqlSession = super.getSession()){
            AdminMapper adminMapper = sqlSession.getMapper(AdminMapper.class);
            list = adminMapper.getMemberList(realPage, limit, search, sido, gungu);
            total = adminMapper.getMemberCount(search, sido, gungu);
        }
        PageInfo pageInfo = new PageInfo(limit, page);
        pageInfo.commit(total);
        ListBox listBox = new ListBox(pageInfo, list);

        return listBox;
    }

    public void forbidMember(int id, int day){
        try(SqlSession sqlSession = super.getSession()){
            Log.i("id, day", id + "::::" + day);
            AdminMapper adminMapper = sqlSession.getMapper(AdminMapper.class);
            adminMapper.forbidMember(id, day);
            sqlSession.commit();
        }
    }

    public ListBox getRestrictedMemberList(int page, int limit, String name, String startDate, String endDate){
        final int realPage = (page - 1) * limit;
        final List<DataMap> list;
        int total;

        try(SqlSession sqlSession = super.getSession()){
            AdminMapper adminMapper = sqlSession.getMapper(AdminMapper.class);
            list = adminMapper.getRestrictedMemberList(realPage, limit, name, startDate, endDate);
            total = adminMapper.getRestrictedMemberCount(name, startDate, endDate);
        }
        PageInfo pageInfo = new PageInfo(limit, page);
        pageInfo.commit(total);
        ListBox listBox = new ListBox(pageInfo, list);

        return listBox;
    }
}
