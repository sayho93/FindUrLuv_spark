package databases.mybatis.mapper;

import org.apache.ibatis.annotations.Param;
import server.comm.DataMap;

public interface AdminMapper {
    DataMap getAdmin(@Param("id") String id, @Param("pw") String pw);
}
