package server.ignition;

import com.sun.org.apache.xpath.internal.operations.Bool;
import databases.paginator.ListBox;
import delayed.managers.PushManager;
import org.apache.commons.io.FileUtils;
import server.comm.DataMap;
import server.comm.RestProcessor;
import server.response.Response;
import server.response.ResponseConst;
import server.rest.DataMapUtil;
import server.rest.RestConstant;
import server.rest.RestUtil;
import services.AdminSVC;
import services.CommonSVC;
import services.UserSVC;
import spark.ModelAndView;
import spark.Service;
import spark.TemplateEngine;
import spark.utils.IOUtils;
import utils.Log;
import utils.MailSender;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.Part;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * @author 함의진
 * @version 2.0.0
 * 서버 실행을 위한 이그니션 클래스
 * @description (version 2.5.0) Response Transformer refactored with the lambda exp. and BaseIgniter applied
 * Jul-21-2017
 */
public class ServiceIgniter extends BaseIgniter{

    private Service service;

    private CommonSVC commonSVC;
    private UserSVC userSVC;
    private AdminSVC adminSVC;

    /**
     * 서버 실행에 필요한 전처리 작업을 위한 init 파트
     * utils 패키지가 포함하는 유틸리티 싱글턴의 경우, 이곳에서 상수로서 값을 전달하고, 존재하거나 초기화되었을 경우에 한해 인스턴스를 반환하도록
     * 별도로 인스턴스 취득자를 구성하였다.
     */
    {
        commonSVC = new CommonSVC();
        userSVC = new UserSVC();
        adminSVC = new AdminSVC();
        try {
            MailSender.start("euijin.ham@richware.co.kr", "gpswpf12!", 20);
            PushManager.start("AAAAWeDYee8:APA91bF8xbiIZMJdMyTuF9CciacPhwEAzn7qFN3jGPKvKoRr1y_rlXthzZTT8MzHCG3l3LFti5lo-H3Rt6n7VcpddPr69N8sCSkEvTiARHvhl4f5zVqn5Yq9CVWN8vDW2UiC-5dFx_0C");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static ServiceIgniter instance;

    public static ServiceIgniter getInstance() {
        if (instance == null) instance = new ServiceIgniter();
        return instance;
    }

    /**
     * 모든 이그니터는 그의 슈퍼클래스로서 베이스 이그니터를 상속받으며, 자동적으로 API 문서를 생성한다.
     * API에 대한 Description은 REST 명시 시 별도 인자로 전달하여 구성할 수 있으며, 구성하지 않을 경우, 공백으로 표시된다.
     */
    public void igniteServiceServer() {

        setProjectName("Kopas");
        setDeveloper("EuiJin.Ham");
        setCallSample("http://192.168.0.101:10040");
        setDebugMode(true);

        service = Service.ignite().port(RestConstant.REST_SERVICE);

        service.before((req, res) -> {
            DataMap map = RestProcessor.makeProcessData(req.raw());
            Log.e("Connection", "Service Server [" + Calendar.getInstance().getTime().toString() + "] :: [" + req.pathInfo() + "] FROM [" + RestUtil.extractIp(req.raw()) + "] :: " + map);
            res.type(RestConstant.RESPONSE_TYPE_JSON);
        });

        super.enableCORS(service, "*", "GET, PUT, DELETE, POST, OPTIONS", "Access-Control-Allow-Origin, Content-Type, Access-Control-Allow-Headers, Authorization, X-Requested-With");

        super.get(service, "/system", (req, res) -> new Response(ResponseConst.CODE_SUCCESS, ResponseConst.MSG_SUCCESS, System.getenv()), "서버 시스템 환경을 확인하기 위한 API 입니다.");

        super.get(service, "web/admin/check/password", (req, res) -> {
            DataMap map = RestProcessor.makeProcessData(req.raw());

            if(DataMapUtil.isValid(map, "account", "password")){
                final String id = map.getString("account");
                final String password = map.getString("password");

                DataMap admin = adminSVC.checkPassword(id, password);
                if(admin == null) return new Response(ResponseConst.CODE_FAILURE, ResponseConst.MSG_FAILURE);
                else{
                    DataMapUtil.mask(admin, "password");
                    return new Response(ResponseConst.CODE_SUCCESS, ResponseConst.MSG_SUCCESS, admin);
                }

            }else{
                return new Response(ResponseConst.CODE_INVALID_PARAM, ResponseConst.MSG_INVALID_PARAM);
            }
        }, "관리자 비밀번호 검증을 위한 API", "account", "password");


        super.post(service, "/web/user/check/password", (req, res) -> {
            DataMap map = RestProcessor.makeProcessData(req.raw());

            if(DataMapUtil.isValid(map, "email", "password")){
                final String id = map.getString("email");
                final String password = map.getString("password");

                DataMap user = userSVC.checkPassword(id, password);

                if(user == null) return new Response(ResponseConst.CODE_FAILURE, ResponseConst.MSG_FAILURE);
                else {
                    DataMapUtil.mask(user, "password");
                    return new Response(ResponseConst.CODE_SUCCESS, ResponseConst.MSG_SUCCESS, user);
                }
            }else{
                return new Response(ResponseConst.CODE_INVALID_PARAM, ResponseConst.MSG_INVALID_PARAM);
            }
        }, "직원용 APP 비밀번호 검증을 위한 API 입니다.", "email", "password");


        /**
         * 이미지 업로드 모듈 테스트임 - 관리자 API 개발 시 진행 예정
         */
        super.post(service, "/upload/file", (req, res) -> {
            req.attribute("org.eclipse.jetty.multipartConfig", new MultipartConfigElement("./upload"));
            Part filePart = req.raw().getPart("myfile");

            try (InputStream inputStream = filePart.getInputStream()) {
                OutputStream outputStream = new FileOutputStream(RestConstant.UPLOAD_PATH + filePart.getSubmittedFileName());
                IOUtils.copy(inputStream, outputStream);
                outputStream.close();
            }

//            FileUtils.forceMkdir();

            return "File uploaded and saved.";
        }, "<b class='emp'>테스트용 API 입니다. 호출금지!!!</b>");

    }

}
