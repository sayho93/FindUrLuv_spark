package services;

import databases.mybatis.mapper.CommMapper;
import databases.mybatis.mapper.UserMapper;
import databases.paginator.ListBox;
import databases.paginator.PageInfo;
import org.apache.ibatis.session.SqlSession;
import server.comm.DataMap;
import server.rest.DataMapUtil;
import utils.kopas.AKopasTransmitter;
import utils.kopas.KopasReturn;
import utils.kopas.KopasTransmitter;

import java.util.List;

public class CommonSVC extends BaseService {

    public ListBox getCompanyList(int page, int limit, String search, int sido, int gungu){
        int realPage = (page - 1) * limit;
        int total;
        List<DataMap> list;
        try(SqlSession sqlSession = super.getSession()){
            CommMapper commMapper = sqlSession.getMapper(CommMapper.class);
            list = commMapper.getCompanyList(realPage, limit, search, sido, gungu);
            total = commMapper.getCompanyCount(search, sido, gungu);
        }
        PageInfo pageInfo = new PageInfo(limit, page);
        pageInfo.commit(total);

        ListBox listBox = new ListBox(pageInfo, list);

        return listBox;
    }

    public ListBox getBoardList(int page, int limit, int company, String search, int mode){
        int realPage = (page - 1) * limit;
        int total;
        List<DataMap> list;
        try(SqlSession sqlSession = super.getSession()){
            CommMapper commMapper = sqlSession.getMapper(CommMapper.class);
            list = commMapper.getBoardList(realPage, limit, search, company, mode);
            total = commMapper.getBoardCount(search, company, mode);
        }
        PageInfo pageInfo = new PageInfo(limit, page);
        pageInfo.commit(total);

        ListBox listBox = new ListBox(pageInfo, list);

        return listBox;
    }

    public DataMap getBoard(int id){
        try(SqlSession sqlSession = super.getSession()){
            CommMapper commMapper = sqlSession.getMapper(CommMapper.class);
            final DataMap board = commMapper.getBoard(id);
            final DataMap prev = commMapper.getBesidesPrev(id);
            final DataMap next = commMapper.getBesidesNext(id);

            final DataMap besides = new DataMap();
            besides.put("prev", prev);
            besides.put("next", next);

            board.put("besides", besides);

            return board;
        }
    }

    public List<DataMap> getSidoList(){
        try(SqlSession sqlSession = super.getSession()){
            CommMapper commMapper = sqlSession.getMapper(CommMapper.class);
            return commMapper.getSidoList();
        }
    }

    public List<DataMap> getGugunList(int sidoID){
        try(SqlSession sqlSession = super.getSession()){
            CommMapper commMapper = sqlSession.getMapper(CommMapper.class);
            return commMapper.getGugunList(sidoID);
        }
    }

    public List<String> filterBeacon(String csv){
        String spArr = "'" + csv.trim().replaceAll(",", "','") + "'";
        try(SqlSession sqlSession = super.getSession()){
            CommMapper commMapper = sqlSession.getMapper(CommMapper.class);
            List<DataMap> list = commMapper.getFiltedBeacon(spArr);
            List<String> filtered = DataMapUtil.getListOf(list, "serial");
            return filtered;
        }
    }

}
