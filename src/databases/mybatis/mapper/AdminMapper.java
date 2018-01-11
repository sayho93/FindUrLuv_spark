package databases.mybatis.mapper;

import org.apache.ibatis.annotations.Param;
import server.comm.DataMap;

import java.util.List;

public interface AdminMapper {
    DataMap getAdmin(@Param("id") String id, @Param("pw") String pw);

    List<DataMap> getMemberList(@Param("page") int page, @Param("limit") int limit, @Param("search") String search, @Param("sido") int sido, @Param("gungu") int gungu);

    int getMemberCount(@Param("search") String search, @Param("sido") int sido, @Param("gungu") int gungu);

    void forbidMember(@Param("id") int id, @Param("day") int day);

    List<DataMap> getRestrictedMemberList(@Param("page") int page, @Param("limit") int limit, @Param("name") String name, @Param("startDate") String startDate, @Param("endDate") String endDate);

    int getRestrictedMemberCount(@Param("name") String name, @Param("startDate") String startDate, @Param("endDate") String endDate);
}
