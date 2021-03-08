package com.importexpress.utils.util;

import com.alibaba.fastjson.JSONObject;
import com.importexpress.comm.util.UrlUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author jack.luo
 * @create 2020/1/14 11:17
 * Description
 */
public class ImageServerUtils {


    public  static void main(String[] args) throws IOException {
        //输出图片路径
//        printRemoteDirs();

        //deletePids(1);
        deletePids(2);
        //deletePids(3);
    }

    private static void printRemoteDirs() throws IOException {
        List<String> lines = FileUtils.readLines(new File("c:\\tmp\\custom_benchmark_ready.txt"));
        Set<String> setDir = new HashSet<>();
        for(String line:lines){
            String[] split = StringUtils.remove(line, '"').split("/");
            setDir.add(split[split.length-1]);
        }
        System.out.println(setDir.toString());
    }

    private final static String URL = "http://192.168.1.27:8000/product/1%s.json";
    private static AtomicInteger count = new AtomicInteger(0);
    private static void deletePids(int type) throws IOException {

        String file;
        if(type==1){
            //无效PID
            file = "D:\\work\\图片服务器清理\\deletePid.txt";
        }else if(type==2){
            //硬下架
            //file = "D:\\work\\图片服务器清理\\deleteDescPid.txt";
            file = "D:\\work\\图片服务器清理\\ruanxiajia.txt";
        }else if(type==3){
            //侵权
            file = "D:\\work\\图片服务器清理\\qinquan.txt";
        }else{
            throw new IllegalArgumentException("type is invalid. type="+type);
        }

        List<String> lines = FileUtils.readLines(new File(file));
        System.out.println("total:" + lines.size());
        List<Long> lstInvalid = new ArrayList<>();

        lines.stream().parallel().forEach( line ->{

            String[] split = line.split("/");
            long pid = 0;

            if (type == 1 || type == 3) {
                //无效PID 侵权
                pid = Long.parseLong(split[split.length - 1]);
            } else {
                //硬下架
                pid = Long.parseLong(split[split.length - 2]);
            }

            Optional<JSONObject> jsonObjectOpt = null;
            jsonObjectOpt = UrlUtil.getInstance().callUrlByGet(String.format(URL, pid));
            if (jsonObjectOpt.isPresent()) {
                JSONObject goodsBean = (JSONObject) jsonObjectOpt.get().get("goodsBean");
                String valid = goodsBean.getString("valid");
                if (!"0".equals(valid)) {
                    lstInvalid.add(pid);
                }
                System.out.println("process:" + count.incrementAndGet());
            }
        });

        System.out.println("finished!");
        System.err.println("valid is not 0:pid="+lstInvalid.toString());
    }


}
