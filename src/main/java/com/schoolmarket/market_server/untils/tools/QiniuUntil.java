package com.schoolmarket.market_server.untils.tools;

import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.schoolmarket.market_server.config.GlobalComment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class QiniuUntil {

    private final GlobalComment comment;


    @Autowired
    public QiniuUntil(GlobalComment comment) {
        this.comment = comment;

        auth = Auth.create(comment.ak, comment.sk);
    }



    //创建登录凭证
    Auth auth;



    //获取token
    public String getUpToken(String fileName) {
        StringMap putPolicy = new StringMap();
        putPolicy.put("returnBody", "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"bucket\":\"$(bucket)\",\"fsize\":$(fsize),\"user\":\"$(x:user)\",\"age\",$(x:age)}");
        //return auth.uploadToken(bucketname);
        //<bucket>:<key>，表示只允许用户上传指定key的文件。在这种格式下文件默认允许“修改”，已存在同名资源则会被本次覆盖。
        //如果希望只能上传指定key的文件，并且不允许修改，那么可以将下面的 insertOnly 属性值设为 1。
        //第三个参数是token的过期时间
        return auth.uploadToken(comment.bk, fileName, 86400, putPolicy, false);

    }


}
