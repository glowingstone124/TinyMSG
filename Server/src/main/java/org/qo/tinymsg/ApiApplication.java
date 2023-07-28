package org.qo.tinymsg;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
@SpringBootApplication
public class ApiApplication implements ErrorController {
    Server server = new Server();
        @RequestMapping("/")
        String home() {
            return """
        <title>TinyMSG api homepage</title>
        <h1>TinyMSG</h1>""";
        }
        @RequestMapping("/userlist")
        @Scheduled(fixedRate = 300000) // 60000ms = 1 minute
        String userlist(@RequestParam int raw) {
            String userContent = server.readFile(server.USER_PROFILE);
            StringBuilder usersb = new StringBuilder();
            try {
                // 解析JSON字符串
                JSONObject jsonObject = new JSONObject(userContent);

                // 遍历JSON对象并输出结果
                for (String username : jsonObject.keySet()) {
                    JSONObject userObject = jsonObject.getJSONObject(username);
                    int permission = userObject.getInt("permission");
                    String permissionlvl = "unknown";
                    switch (permission){
                        case 0: permissionlvl = "user";
                        break;
                        case 1: permissionlvl = "administrator";
                        break;
                    }
                    if (raw == 0) {
                        usersb.append(username + ": permission level: " + permissionlvl + "<br>");
                    } else {
                        usersb.append(username + ": permission level: " + permissionlvl);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return usersb.toString();
        }
        @RequestMapping("/error")
        String err() {
            ModelAndView modelAndView = new ModelAndView("error" );
            modelAndView.addObject("message", "the api you requested isn't exist or you request wrongly.");
            return "<h1>error</h1><p>the api you requested isn't exist or you request wrongly.</p>";
        }
        @RequestMapping("/status")
        @Scheduled(fixedRate = 500) // 60000ms = 1 minute
        String status() {
                SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间
                sdf.applyPattern("yyyy-MM-dd HH:mm:ss a");// a为am/pm的标记
                Date date = new Date();// 获取当前时间
                StringBuilder userList = new StringBuilder();
                for (String user : server.onlineUsers) {
                    userList.append("<br>").append(user);
                }
                server.log(server.onlineUsers.toString(),0);
                return "Online Users:" + userList + "<br>" + "Online count: " + server.OnlineCount + "<br>" + sdf.format(date);
        }
    }