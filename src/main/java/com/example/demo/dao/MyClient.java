package com.example.demo.dao;

import com.dtflys.forest.annotation.*;
import com.dtflys.forest.callback.OnError;
import com.dtflys.forest.callback.OnProgress;
import com.dtflys.forest.extensions.DownloadFile;
import com.dtflys.forest.http.ForestRequest;
import com.dtflys.forest.http.ForestResponse;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface MyClient {

    @Request("http://localhost:8080/hello")
    String simpleRequest();



    @Request(
            url = "http://localhost:8080/hello",
            type = "delete"
    )
    String simpleDelete();


    /*********************************************************************************
     * HTTP URL *
    ***********************************************************************************/

    @Request("http://${myURL}/abc")
    String send2(@Var("myURL") String myURL);



    /*********************************************************************************
     * HTTP Header *
     ***********************************************************************************/

    /**
     * 使用 @Header 注解可以修饰 Map 类型的参数
     * Map 的 Key 指为请求头的名称，Value 为请求头的值
     * 通过此方式，可以将 Map 中所有的键值对批量地绑定到请求头中
     */
    @Post("http://localhost:8080/hello/user?username=foo")
    void headHelloUser(@Header Map<String, Object> headerMap);

    /**
     * 使用 @Header 注解将参数绑定到请求头上
     * @Header 注解的 value 指为请求头的名称，参数值为请求头的值
     * @Header("Accept") String accept将字符串类型参数绑定到请求头 Accept 上
     * @Header("accessToken") String accessToken将字符串类型参数绑定到请求头 accessToken 上
     */
    @Post("http://localhost:8080/hello/user?username=foo")
    void postUser(@Header("Accept") String accept, @Header("accessToken") String accessToken);



    /**
     * 使用 @Header 注解可以修饰自定义类型的对象参数
     * 依据对象类的 Getter 和 Setter 的规则取出属性
     * 其属性名为 URL 请求头的名称，属性值为请求头的值
     * 以此方式，将一个对象中的所有属性批量地绑定到请求头中
     */
//    @Post("http://localhost:8080/hello/user?username=foo")
//    void headHelloUser(@Header MyHeaderInfo headersInfo);



    /*********************************************************************************
     * HTTP Body *
     ***********************************************************************************/

    /**
     * @Body注解
     * 默认body格式为 application/x-www-form-urlencoded，即以表单形式序列化数据
     */
    @Post(
            url = "http://localhost:8080/user",
            headers = {"Accept:text/plain"}
    )
    String sendPost(@Body("username") String username,  @Body("password") String password);


    /**
     * 表单格式
     * contentType属性设置为 application/x-www-form-urlencoded 即为表单格式，
     * 当然不设置的时候默认值也为 application/x-www-form-urlencoded， 也同样是表单格式。
     * 在 @Body 注解的 value 属性中设置的名称为表单项的 key 名，
     * 而注解所修饰的参数值即为表单项的值，它可以为任何类型，不过最终都会转换为字符串进行传输。
     */
    @Post(
            url = "http://localhost:8080/user",
            contentType = "application/x-www-form-urlencoded",
            headers = {"Accept:text/plain"}
    )
    String sendPost(@Body("key1") String value1,  @Body("key2") Integer value2, @Body("key3") Long value3);


    @Request(
            url = "http://localhost:8080/hello/user",
            type = "post",
            contentType = "application/json"    // 指定contentType为application/json
    )
    String postJson(@Body MyUser user);   // 自动将user对象序列化为JSON格式


//    HTTP请求响应后返回结果的数据同样需要转换，
//    Forest则会将返回结果自动转换为您通过方法返回类型指定对象类型。
//    这个过程就是反序列化，您可以通过dataType指定返回数据的反序列化格式。
    @Request(
            url = "http://localhost:8080/data",
            dataType = "json"        // 指定dataType为json，将按JSON格式反序列化数据
    )
    Map getData();



//     异常处理
//    /**
//     * try-catch方式：捕获ForestNetworkException异常类的对象
//     */
//    try {
//            String result = myClient.send();
//        } catch (ForestNetworkException ex) {
//            int status = ex.getStatusCode();     // 获取请求响应状态码
//            ForestResponse response = ex.getResponse();  // 获取Response对象
//            String content = response.getContent();   // 获取请求的响应内容
//            String resResult = response.getResult();   // 获取方法返回类型对应的最终数据结果
//    }

    /**
     * 在请求接口中定义OnError回调函数类型参数
     */
    @Request(
            url = "http://localhost:8080/hello/user",
            headers = {"Accept:text/plain"},
            data = "username=${username}"
    )
    String send(@DataVariable("username") String username, OnError onError);


    /**
     * 用`ForestResponse`类作为请求方法的返回值类型, 其泛型参数代表实际返回数据的类型
     */
    @Request(
            url = "http://localhost:8080/hello/user",
            headers = {"Accept:text/plain"},
            data = "username=${username}"
    )
    ForestResponse<String> send(@Var("username") String username);

//    ForestResponse<String> response = myClient.send("foo");
//// 用isError方法判断请求是否失败, 比如404, 500等情况
//if (response.isError()) {
//        int status = response.getStatusCode(); // 获取请求响应状态码
//        String content = response.getContent(); // 获取请求的响应内容
//        String result = response.getResult(); // 获取方法返回类型对应的最终数据结果
//    }

//    拦截器方式
//public class ErrorInterceptor implements Interceptor<String> {
//
//    // ... ...
//    @Override
//    public void onError(ForestRuntimeException ex, ForestRequest request, ForestResponse response) {
//        int status = response.getStatusCode(); // 获取请求响应状态码
//        String content = response.getContent(); // 获取请求的响应内容
//        Object result = response.getResult(); // 获取方法返回类型对应的返回数据结果
//    }
//}


//    拦截器


    /****************************************************************
     *   上传
     * ***************************************************************/
    /**
     * 用@DataFile注解修饰要上传的参数对象
     * OnProgress参数为监听上传进度的回调函数
     */
    @Post(url = "/upload")
    Map upload(@DataFile("file") String filePath, OnProgress onProgress);

//    Map result = myClient.upload("D:\\TestUpload\\xxx.jpg", progress -> {
//        System.out.println("total bytes: " + progress.getTotalBytes());   // 文件大小
//        System.out.println("current bytes: " + progress.getCurrentBytes());   // 已上传字节数
//        System.out.println("progress: " + Math.round(progress.getRate() * 100) + "%");  // 已上传百分比
//        if (progress.isDone()) {   // 是否上传完成
//            System.out.println("--------   Upload Completed!   --------");
//        }
//    });

    /**
     * File类型对象
     */
    @Post(url = "/upload")
    Map upload(@DataFile("file") File file, OnProgress onProgress);

    /**
     * byte数组
     * 使用byte数组和Inputstream对象时一定要定义fileName属性
     */
    @Post(url = "/upload")
    Map upload(@DataFile(value = "file", fileName = "${1}") byte[] bytes, String filename);

    /**
     * Inputstream 对象
     * 使用byte数组和Inputstream对象时一定要定义fileName属性
     */
    @Post(url = "/upload")
    Map upload(@DataFile(value = "file", fileName = "${1}") InputStream in, String filename);

    /**
     * Spring Web MVC 中的 MultipartFile 对象
     */
    @PostRequest(url = "/upload")
    Map upload(@DataFile(value = "file") MultipartFile multipartFile, OnProgress onProgress);

    /**
     * Spring 的 Resource 对象
     */
    @Post(url = "/upload")
    Map upload(@DataFile(value = "file") Resource resource);

//  多文件批量上传
    /**
     * 上传Map包装的文件列表
     * 其中 ${_key} 代表Map中每一次迭代中的键值
     */
    @PostRequest(url = "/upload")
    ForestRequest<Map> uploadByteArrayMap(@DataFile(value = "file", fileName = "${_key}") Map<String, byte[]> byteArrayMap);

    /**
     * 上传List包装的文件列表
     * 其中 ${_index} 代表每次迭代List的循环计数（从零开始计）
     */
    @PostRequest(url = "/upload")
    ForestRequest<Map> uploadByteArrayList(@DataFile(value = "file", fileName = "test-img-${_index}.jpg") List<byte[]> byteArrayList);

    /**
     * 上传数组包装的文件列表
     * 其中 ${_index} 代表每次迭代List的循环计数（从零开始计）
     */
    @PostRequest(url = "/upload")
    ForestRequest<Map> uploadByteArrayArray(@DataFile(value = "file", fileName = "test-img-${_index}.jpg") byte[][] byteArrayArray);

//    下载
    /**
     * 在方法上加上@DownloadFile注解
     * dir属性表示文件下载到哪个目录
     * filename属性表示文件下载成功后以什么名字保存，如果不填，这默认从URL中取得文件名
     * OnProgress参数为监听上传进度的回调函数
     */
    @Get(url = "http://localhost:8080/images/xxx.jpg")
    @DownloadFile(dir = "${0}", filename = "${1}")
    File downloadFile(String dir, String filename, OnProgress onProgress);

//    File file = myClient.downloadFile("D:\\TestDownload", progress -> {
//        System.out.println("total bytes: " + progress.getTotalBytes());   // 文件大小
//        System.out.println("current bytes: " + progress.getCurrentBytes());   // 已下载字节数
//        System.out.println("progress: " + Math.round(progress.getRate() * 100) + "%");  // 已下载百分比
//        if (progress.isDone()) {   // 是否下载完成
//            System.out.println("--------   Download Completed!   --------");
//        }
//    });

//    如果您不想将文件下载到硬盘上，而是直接在内存中读取，可以去掉@DownloadFile注解，并且用以下几种方式定义接口:
    /**
     * 返回类型用byte[]，可将下载的文件转换成字节数组
     */
    @GetRequest(url = "http://localhost:8080/images/test-img.jpg")
    byte[] downloadImageToByteArray();

    /**
     * 返回类型用InputStream，用流的方式读取文件内容
     */
    @GetRequest(url = "http://localhost:8080/images/test-img.jpg")
    InputStream downloadImageToInputStream();






    class MyUser{}

}