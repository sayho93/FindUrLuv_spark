package services;

import com.sun.org.apache.xpath.internal.operations.Bool;
import databases.mybatis.mapper.CommMapper;
import databases.mybatis.mapper.UserMapper;
import databases.paginator.ListBox;
import databases.paginator.PageInfo;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.session.SqlSession;
import org.eclipse.jetty.server.Authentication;
import server.comm.DataMap;
import server.response.Response;
import server.response.ResponseConst;
import server.rest.DataMapUtil;
import server.rest.RestUtil;
import server.rest.ValidationUtil;
import utils.Log;
import utils.MailSender;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserSVC extends BaseService {

    public DataMap memberLogin(DataMap map){
        final String email = map.getString("email");
        final String loginType = map.getString("loginType");

        try(SqlSession sqlSession = super.getSession()){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);

            if(loginType.equals("E")){
                Log.e("email login entered");
                final String password = map.getString("password");
                return userMapper.getMember(email, RestUtil.getMessageDigest(password));
            }
            else if(loginType.equals("F")){
                final String accessToken = map.getString("accessToken");
                return userMapper.getMemberByAccessToken(email, accessToken);
            }
        }

        return null;
    }

    public boolean checkDuplicateEmail(String email){
        try(SqlSession sqlSession = super.getSession()){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            DataMap member = userMapper.getMemberByEmail(email);

            return member == null;
        }
    }

    public boolean checkDuplicateNick(String nick){
        try(SqlSession sqlSession = super.getSession()){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            DataMap member = userMapper.getMemberByNick(nick);

            return member == null;
        }
    }

    public boolean checkDuplicatePhone(String phone){
        try(SqlSession sqlSession = super.getSession()){
            UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
            DataMap member = userMapper.getMemberByPhone(phone);

            return member == null;
        }
    }

    public int registerMember(DataMap map){
        final String nick = map.getString("nick");
        final String email = map.getString("email");
        final String regType = map.getString("regType");
        final String phone = map.getString("phone").replaceAll("-", "");
        map.put("phone", phone);

        if(regType.equals("E")){
            final String password = RestUtil.getMessageDigest(map.getString("password"));
            map.put("password", password);
            map.put("accessToken", null);
        }
        else
            map.put("password", null);

        if(ValidationUtil.validate(email, ValidationUtil.ValidationType.Email) && ValidationUtil.validate(phone, ValidationUtil.ValidationType.Phone)){
            try(SqlSession sqlSession = super.getSession()){
                UserMapper userMapper = sqlSession.getMapper(UserMapper.class);
                final DataMap preProcessEmail = userMapper.getMemberByEmail(email);
                final DataMap preProcessPhone = userMapper.getMemberByPhone(phone);
                final DataMap preProcessNick = userMapper.getMemberByNick(nick);

                if(preProcessEmail != null || preProcessPhone != null || preProcessNick != null)
                    return ResponseConst.CODE_ALREADY_EXIST;

                userMapper.registerMember(map);
                sqlSession.commit();
            }
            return ResponseConst.CODE_SUCCESS;
        }
        return ResponseConst.CODE_FAILURE;
    }
}
