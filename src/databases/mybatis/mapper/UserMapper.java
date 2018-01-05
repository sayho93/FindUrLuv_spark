package databases.mybatis.mapper;

import org.apache.ibatis.annotations.Param;
import server.comm.DataMap;

import java.util.List;

public interface UserMapper {
    DataMap getRestrictionData(@Param("id") int id);

    DataMap getMember(@Param("email") String email, @Param("password") String password);

    DataMap getMemberByAccessToken(@Param("email") String email, @Param("accessToken") String accessToken);

    DataMap getMemberById(@Param("id") String id);

    DataMap getMemberByEmail(@Param("email") String email);

    DataMap getMemberByPhone(@Param("phone") String phone);

    DataMap getMemberByNick(@Param("nick") String nick);

    void registerMember(DataMap map);

}
