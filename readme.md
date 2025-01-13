# 短链接管理系统
## 概述
提供长连接转换短链接及留存、统计相关功能，无需占用过多资源，简单配置即可快速使用，适用中小团队快速搭建短链接系统，也可根据需要自动扩展。

## 定位
+ **轻量**：基于spring boot、MySQL、redis常用的开发三件套。
+ **快速**：接入简单，简单配置后即可使用。

## 短链生成逻辑
1. 从[a-zA-Z][0-9]共计62个字符，生成62^7≈3.5万亿的可用短链接。
2. 为了避免重复，使用redis的bloom filter过滤重复短链接。
3. bloom filter默认设置1亿的容量，你可以根据需要调整布隆过滤器的容量。
4. 调用短链接生成接口时，如果生成了重复的短链接，系统会自动重试。

## 快速开始
1. **依赖**：
   - jdk8及以上
   - mysql5.7及以上
   - redis5.0及以上
   - nginx
2. **安装**：
   - 克隆仓库：`git clone https://github.com/justcode-cc/easyurl.git`
   - [部署测试环境]设置数据库 `application-test.yml` 中的redis和MySQL相关参数
   - [部署正式环境]设置数据库 `application-prod.yml` 中的redis和MySQL相关参数
   - 初始化数据库脚本：`base-sql/base.sql`
   - 设置application-[test/prod].yml中url相关属性
     + notfound-url: 当无法匹配用户访问的短链接时，跳转至404页面，默认为 [https://www.a.com/easyurl/404.html](https://www.a.com/easyurl/404.html), 其中[www.a.com替换为你的域名](http://www.a.com替换为你的域名)
     + baseUrl: 短链接的前缀，默认为 [https://www.a.com/d/](https://www.a.com/d/), 其中[www.a.com替换为你的域名](http://www.a.com替换为你的域名)

3. **配置nginx**：

   web管理后台的访问地址配置：

   ```plain
      location ^~ /easyurl {
          proxy_pass http://127.0.0.1:8902/easyurl;
          proxy_set_header Host $host;
          proxy_set_header X-Real-IP $remote_addr;
          proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
          proxy_set_header REMOTE-HOST $remote_addr;
          proxy_set_header Upgrade $http_upgrade;
          proxy_set_header Connection "upgrade";
          proxy_set_header X-Forwarded-Proto $scheme;
          proxy_http_version 1.1;
          add_header X-Cache $upstream_cache_status;
          add_header Cache-Control no-cache;
          proxy_ssl_server_name off;
          add_header Strict-Transport-Security "max-age=31536000";
          add_header 'Access-Control-Allow-Origin' * always;
          add_header 'Access-Control-Allow-Methods' 'GET, POST, OPTIONS';
          add_header 'Access-Control-Allow-Headers' '*';
          add_header 'Access-Control-Allow-Credentials' 'true';
          if ($request_method = 'OPTIONS' ) {
          return 204;
          }
      }
   ```

   短链接前缀域名配置：
   
   ```plain
       location ^~ /d {
           proxy_pass http://127.0.0.1:8902/easyurl/url/redirect; 
           proxy_set_header Host $host; 
           proxy_set_header X-Real-IP $remote_addr; 
           proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for; 
           proxy_set_header REMOTE-HOST $remote_addr; 
           proxy_set_header Upgrade $http_upgrade; 
           proxy_set_header Connection "upgrade"; 
           proxy_set_header X-Forwarded-Proto $scheme; 
           proxy_http_version 1.1; 
           add_header X-Cache $upstream_cache_status; 
           add_header Cache-Control no-cache; 
           proxy_ssl_server_name off; 
       }
   ```

4. **运行系统**：
   - 项目配置完成后，通过maven将项目打包为jar包，并启动即可
   - 启动服务器：`nohup java  -Xms512m -Xmx512m -Dproject.name=url-service -Dspring.profiles.active=prod -jar url-service-1.0.1.jar >/dev/null  2>&1 & `
   - 上述命令中-Dspring.profiles.active=prod为正式环境，如果要部署测试环境，将prod改为test即可
   - 通过 `http://www.a.com/easyurl/index.html` 访问后台管理系统，初始账号admin  密码 th749gds73phg4gg，首次登录后在系统内右上角修改密码即可
   - 登录系统，在菜单：系统管理-开放账户，添加可用的appId/appSecret，这是客户端调用生成短链接接口的凭证
5. **调用方式**：
   1. 通过http请求直接调用
      ```java
       // eg.  https://xxx.com/easyurl/openApi/url/generate
       String res = HttpUtil.createPost("你的域名或IP" + "/easyurl/openApi/url/generate")
       .header("appId", "appId")
       .header("appSecret", "appSecret")
       .body(JSONUtil.toJsonStr(request))
       .execute().body();
       ```
   
    2. 通过url-client配置
        1. 将[url-client-1.0.1.jar](https://github.com/justcode-cc/easyurl/releases/)添加到你的项目或者maven私服
        2. 在项目中引用
            ```java
            <dependency>
                <groupId>com.cczj</groupId>
                <artifactId>url-client</artifactId>
                <version>1.0.1</version>
            </dependency>
           ```
        3. 构造api

           ```java
            @Configuration
            public class EasyUrlConfiguration {
   
                @Bean
                public EasyUrlApi easyUrlApi(){
                    EasyUrlApiImpl easyUrlApi = new EasyUrlApiImpl();
                    easyUrlApi.setRequestUrl("https://xxx.com/easyurl");
                    OpenAccountConfig openAccountConfig = new OpenAccountConfig();
                    openAccountConfig.setAppId("");
                    openAccountConfig.setAppSecret("");
                    easyUrlApi.setOpenAccountConfig(openAccountConfig);
                    return new EasyUrlApiImpl();
                }
   
           }
           ```

        4. 使用示例

           ```java
   
           @Resource
           private EasyUrlApi easyUrlApi;
   
           public void test(){
               UrlGenerateRequest request = new UrlGenerateRequest();
               request.setOriginUrl("");
               request.setExpireTime(3600L);
               R<String> res = this.easyUrlApi.generateUrl(request);
               System.out.println(res.getData());
           }
           ```







