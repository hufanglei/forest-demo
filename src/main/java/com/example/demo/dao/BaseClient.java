package com.example.demo.dao;

import com.dtflys.forest.annotation.BaseRequest;
import com.dtflys.forest.annotation.Get;
import com.dtflys.forest.annotation.Query;


//@BaseRequest注解定义在接口类上，
//在@BaseRequest上定义的属性会被分配到该接口中每一个方法上，
//但方法上定义的请求属性会覆盖@BaseRequest上重复定义的内容。
//因此可以认为@BaseRequest上定义的属性内容是所在接口中所有请求的默认属性。

/**
 * @BaseRequest 为配置接口层级请求信息的注解，
 * 其属性会成为该接口下所有请求的默认属性，
 * 但可以被方法上定义的属性所覆盖
 */
@BaseRequest(
        baseURL = "http://localhost:8080",     // 默认域名
        headers = {
                "Accept:text/plain"                // 默认请求头
        },
        sslProtocol = "TLS"                    // 默认单向SSL协议
)
public interface BaseClient {

    // 方法的URL不必再写域名部分
    @Get("/hello/user")
    String send1(@Query("username") String username);

    // 若方法的URL是完整包含http://开头的，那么会以方法的URL中域名为准，不会被接口层级中的baseURL属性覆盖
    @Get("http://www.xxx.com/hello/user")
    String send2(@Query("username") String username);


    @Get(
            url = "/hello/user",
            headers = {
                    "Accept:application/json"      // 覆盖接口层级配置的请求头信息
            }
    )
    String send3(@Query("username") String username);

}
