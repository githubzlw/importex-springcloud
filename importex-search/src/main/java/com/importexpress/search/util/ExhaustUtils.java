package com.importexpress.search.util;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 单词重组
 */
@Component
@Slf4j
public class ExhaustUtils {
    /**穷举相邻单词组成新的词组
     * @param queryString
     * @return
     */
    public List<String> exhaust(String queryString){
        List<String> result = Lists.newArrayList();
        String[] queryStrings = queryString.split("(\\s+)");
        int length = queryStrings.length - 1;
        if(length < 1){
            return result;
        }
        for(int i=0;i<length;i++){
            List<String> lst = composeArry(queryStrings,length - i);
            if(!lst.isEmpty()){
                result.addAll(lst);
            }
        }
        return result;
    }

    /**重新排列数组，相邻的n个单词组成新的词组
     * @param arr 数组
     * @param n 相邻单词个数
     * @return
     */
    public List<String> composeArry(String[] arr,int n){
        List<String> list = Lists.newArrayList();
        StringBuilder sb;
        int loop = arr.length + 1 - n;
        for(int i=0;i<loop;i++){
            sb = new StringBuilder();
            for(int j=i;j<i+n;j++){
                sb.append(arr[j]).append(" ");
            }
            list.add(sb.toString().trim());
        }
        return list;
    }

}
