package com.cczj.common.utils;

import cn.hutool.core.util.StrUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.internal.Mimetypes;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.ObjectMetadata;
import com.cczj.common.base.BaseConstant;
import com.cczj.common.base.BizException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;


@Slf4j
public class OssUtils {


    public static final String endpointInternal = "https://oss-cn-beijing-internal.aliyuncs.com";
    public static final String endpointPublic = "https://oss-cn-beijing.aliyuncs.com";
    private static final String bucketInternalUrl = BaseConstant.getBucketInternalUrl();
    public static final String bucketPublicUrl = BaseConstant.getBucketPublicUrl();
    private static final String bucketName = BaseConstant.getOssBucketName();
    private static final String accessKeyId = BaseConstant.getOssAccessKeyId();
    private static final String accessKeySecret = BaseConstant.getOssAccessKeySecret();

    /**
     * 样式
     */
    public static final String style_suofang_60x60 = "style/suofang_60x60";

    private static final OSS ossClient;

    static {
        OSSClientBuilder builder = new OSSClientBuilder();
        if (!ProfileUtils.isProd()) {
            ossClient = builder.build(endpointPublic, accessKeyId, accessKeySecret);
        } else {
            ossClient = builder.build(endpointInternal, accessKeyId, accessKeySecret);
        }
    }

    public static String upload2PrivateOSS(InputStream inputStream, String folder, String fileName) {
        return uploadFile2OSS(inputStream, folder, fileName, false);
    }

    public static String uploadFile2OSS(InputStream inputStream, String folder, String fileName, boolean isPublic) {
        String ret = "";
        try {
            if (folder.startsWith("/")) {
                folder = folder.replaceFirst("/", "");
            }
            if (fileName.startsWith("/")) {
                fileName = fileName.replaceFirst("/", "");
            }
            long beginTime = System.currentTimeMillis();
            //获取文件名称
            //创建上传Object的Metadata
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(inputStream.available());
            objectMetadata.setContentType(Mimetypes.getInstance().getMimetype(fileName));
            objectMetadata.setContentDisposition("inline;filename=" + URLEncoder.encode(fileName, "utf-8"));

            ossClient.putObject(bucketName, folder + "/" + fileName, inputStream, objectMetadata);
            if (isPublic) {
                ossClient.setObjectAcl(bucketName, folder + "/" + fileName, CannedAccessControlList.PublicRead);
            }
            log.info("oss上传结束,耗时:{}ms", (System.currentTimeMillis() - beginTime));
            ret = bucketPublicUrl + "/" + folder + "/" + fileName;
        } catch (IOException e) {
            log.error("上传文件失败,folder={}", folder, e);
            throw new BizException("上传文件失败");
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return ret;
    }


    public static boolean deleted(String key) {
        ossClient.deleteObject(bucketName, key);
        return true;
    }


    public static boolean checkFileExists(String objName) {
        if (StrUtil.isBlank(objName)) {
            return false;
        }
        try {
            return ossClient.doesObjectExist(bucketName, objName);
        } catch (Exception e) {
            return false;
        }
    }


    public static String getUrl(String key) {
        return bucketPublicUrl + "/" + key;
    }


    public static String getInternalUrl(String key) {
        return bucketInternalUrl + "/" + key;
    }


    /**
     * 去除"?"后的所有内容，以获取oss的原地址
     *
     * @param sourceUrl sourceUrl
     * @return oss原地址
     */
    public static String generateOriginalUrl(String sourceUrl) {
        if (StrUtil.isBlank(sourceUrl)) {
            return sourceUrl;
        }
        if (sourceUrl.contains("?")) {
            return sourceUrl.split("\\?")[0];
        }
        return sourceUrl;
    }

    /**
     * 获取一个presignedUrl，默认有效期3600s
     *
     * @param sourceUrl 源URL
     * @return
     */
    public static String generatePresignedUrl(String sourceUrl) {
        return generatePresignedUrl(sourceUrl, 3600);
    }

    /**
     * 获取有效连接地址
     *
     * @param sourceUrl    源URL
     * @param expireSecond 指定有效期
     * @return
     */
    public static String generatePresignedUrl(String sourceUrl, long expireSecond) {
        try {
            if (StrUtil.isBlank(sourceUrl)) {
                return "";
            }
            //包含x-oss-process的文件不能直接用,切割，会冲突
            if (sourceUrl.contains("x-oss-process")) {
                return getPresignedUrl(sourceUrl, expireSecond, null);
            }
            String[] split = sourceUrl.split(",");
            String finalUrl = "";
            for (String url : split) {
                String presignedUrl = getPresignedUrl(url, expireSecond, null);
                finalUrl = finalUrl + "," + presignedUrl;
            }
            if (finalUrl.startsWith(",")) {
                finalUrl = finalUrl.replaceFirst(",", "");
            }
            return finalUrl;
        } catch (Exception e) {
            log.error("获取签名链接失败", e);
            return "";
        }
    }

    /**
     * 获取带签名的url，默认有效期3600s
     *
     * @param sourceUrl 源url
     * @param newStyle  指定的新样式，若源url有样式，则会被新样式覆盖
     * @return 新url
     */
    public static String generatePresignedUrlWithNewStyle(String sourceUrl, String newStyle) {
        return generatePresignedUrlWithNewStyle(sourceUrl, 3600L, newStyle);
    }

    /**
     * 获取带签名的url
     *
     * @param sourceUrl    源url
     * @param expireSecond 过期时间，秒
     * @param newStyle     指定的新样式，若源url有样式，则会被新样式覆盖
     * @return 新url
     */
    public static String generatePresignedUrlWithNewStyle(String sourceUrl, long expireSecond, String newStyle) {
        try {
            if (StrUtil.isBlank(sourceUrl)) {
                return "";
            }
            //包含x-oss-process的文件不能直接用,切割，会冲突
            if (sourceUrl.contains("x-oss-process")) {
                return getPresignedUrl(sourceUrl, expireSecond, newStyle);
            }
            String[] split = sourceUrl.split(",");
            String finalUrl = "";
            for (String url : split) {
                String presignedUrl = getPresignedUrl(url, expireSecond, newStyle);
                finalUrl = finalUrl + "," + presignedUrl;
            }
            if (finalUrl.startsWith(",")) {
                finalUrl = finalUrl.replaceFirst(",", "");
            }
            return finalUrl;
        } catch (Exception e) {
            log.error("获取签名链接失败", e);
            return "";
        }
    }

    private static String getPresignedUrl(String sourceUrl, long expireSecond, String newStyle) {
        if (sourceUrl.startsWith("http:")) {
            sourceUrl = sourceUrl.replace("http:", "https:");
        }
        if (!sourceUrl.startsWith(bucketPublicUrl)) {
            return sourceUrl;
        }
        log.info("对源URL进行签名：{}", sourceUrl);
        sourceUrl = sourceUrl.replace(bucketPublicUrl + "/", "");
        if (sourceUrl.contains("Expires") && sourceUrl.contains("OSSAccessKeyId")) {
            //已经带有签名
            String extend = sourceUrl.split("\\?")[1];
            String[] list = extend.split("&");
            for (String anyone : list) {
                if (anyone.contains("Expires") || anyone.contains("OSSAccessKeyId") || anyone.contains("Signature")) {
                    sourceUrl = sourceUrl.replace("&" + anyone, "");
                    sourceUrl = sourceUrl.replace(anyone, "");
                }
            }
            if (sourceUrl.lastIndexOf("?") == sourceUrl.length() - 1) {
                sourceUrl = sourceUrl.substring(0, sourceUrl.length() - 1);
            }
            log.info("去除旧签名后:{}", sourceUrl);
        }
        Date expiration = new Date(new Date().getTime() + expireSecond * 1000);
        if (sourceUrl.contains("%")) {
            try {
                sourceUrl = URLDecoder.decode(sourceUrl, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                log.error("解码失败", e);
            }
        }
        if (StrUtil.isNotBlank(newStyle) && !sourceUrl.contains("x-oss-process")) {
            GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(bucketName, sourceUrl);
            req.setExpiration(expiration);
            req.setProcess(newStyle);
            URL signedUrl = ossClient.generatePresignedUrl(req);
            return signedUrl.toString().replace(bucketInternalUrl, bucketPublicUrl);
        }
        if (sourceUrl.contains("x-oss-process")) {
            String oldStyle = sourceUrl.substring(sourceUrl.indexOf("x-oss-process=")).replace("x-oss-process=", "");
            String objectKey = sourceUrl.substring(0, sourceUrl.indexOf("?"));
            log.info("包含图片处理action,解析后的style={},objectKey={}", oldStyle, objectKey);
            GeneratePresignedUrlRequest req = new GeneratePresignedUrlRequest(bucketName, objectKey);
            req.setExpiration(expiration);
            if (StrUtil.isNotBlank(newStyle)) {
                //如果指定了新样式，则使用新样式
                req.setProcess(newStyle);
            } else {
                req.setProcess(oldStyle);
            }
            URL signedUrl = ossClient.generatePresignedUrl(req);
            return signedUrl.toString().replace(bucketInternalUrl, bucketPublicUrl);
        }
        // 生成以GET方法访问的签名URL，访客可以直接通过浏览器访问相关内容。
        URL url = ossClient.generatePresignedUrl(bucketName, sourceUrl.replace(bucketPublicUrl + "/", ""), expiration);
        return url.toString().replace(bucketInternalUrl, bucketPublicUrl);
    }


    public static void main(String[] args) {


    }


}
