package com.importexpress.search.common;

import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 产品标题去敏感词
 * @author Administrator
 *
 */
@Component
public class SensitiveWordUtil {
    /**
     * 标题中的敏感词
     */
    private static final String[] titleSensitiveWords = {"clearance!","plus size","outerwear","special offer","soperwillton","#a806","blusas","lt1043","ts-004","he008","m0132","j2531","kimisohand","veqking","killin it","1wbl110","e 80 85 90 95 100 b c d,","90's t0895"," zewo","turn-down","q1725","#2628","zsiibo","wanayou","3d","zanzea","lanshifei","big size","dioufond",
            "lj3866c","peter pan","qa1223","brand new","sanwony","dt729","lj4515r","glorria","soperwillton","#a506","dingtoll","wmt139","#2613","60571"," z101","vlent","gzdl","lovesky","lt739","cl1924","colrovie","8 colors","hdy haoduoyi","candy color","aug30"," [you're my secret]"," jitivr","tb846","nclagen","lienzy","nclagen","m to xl"," grs-8111",
            "dd3001","/di-241"," achiewell","after the fall","custom made","promotion","fx1052","big sale!","ql2916","hipsterme","zaful","charmma","e156","lienzy","pikb","63270","hipsterme","richlulu","yifan","girlady","artdewred","32 34 36 38 40 42 b c d","#87795","ekouaer","julexy","aq193","hot sale!","do do mian"," france brand","janpanese brand",
            "89037","zhx73","shitagi","#86364","sujetador","deruilady","32-36 b c","codysale","36-46 c/d/dd/ddd/f purple/black/beige/rose/blue/red/pink/","vestido de noiva","robe de mariage","vivian's bridal","wd3312","wowbridal","wd0340","ekouaer","yao ting","qianxiu","tz028","#p118","najowpjg","coniefox","9211w","robe de soiree","cheap price cheap",
            "op2822","rse658","robe de soiree","coniefox","finove","ph03418","hamda al fahim","yk1a626 ft2838","wc0584-1","s-xl khaki","hanhent hermanos","newest","rocksir","gosha","e-baihui","pioneer camp","raisevern","dec'ple","jamickiki","zilinglan","tarchia","pumba","giordano","langmeng","xl443","korean version","xl472","bmx00072","j.mosuya",
            "na380","n422","n47","smtpn854","na384","sedmart","#nl068","56a6","artilady","chicvie","liebe engel","american trade"," american","dianshangkaituozhe","#ln1091","xl098","effie queen","d1047","on sale","n0310","hot sale!","soperwillton","wlhb974","bolsas femininas sac","inleela","bolsos mujer","dollar price","xqxa","femme de marque",
            "zero profit","free shipping","realer brand","herald fashion","ja150","vogue star!","ya80-165"," 25s0119","miwind","ls8235s","mansur gavriel","not wrong half price!!","freya safi","guarantee 100% genuine leather","#l09585","tuladuo","linlanya","xiniu","chispaulo","jasmin noir","dizhige","femme de marque","inleela","jooz","xa568h","imchic",
            "dolove","jiaruo korean retro","vormor","linlanya","zmqn","9styles","shengxilu","famous brand","chispaulo","hisuely","xa531b","ograff","dolove","shyaa","makorste","dj0101","flying birds!","ls8235fb","miwind","iceinnight","db5723","royadong","ameliegalanti","yifan","esufeir","leftside","vicuna polo","vormor","xb114new","angel voices!",
            "yuocl fashion","bifold","monedero hombre","magic union","zdd04285","jinbaolai","tinyat","baijiawei","qq1895","sl053m","contact's","portomonee","mva","cobbler legend","xb114-b","marrant","bison denim","wiliamganu","2016 korean style","bvlriga","sl383m","bg0035","tigernu","shunvbasha","cobbler legend","baellerry","cross ox","fd bolo",
            "new muzee","chuwanglin","db3830"," flama","aokang","asyion","rm017","iceinnight","cobbler legend","new fashion","fonmor","slymaoyi","sac de taille"," gsq","bostanten","tmyoy"," danjue"," bandolera","yeso","tinyat","brand and high quality","p.p.x fashion brand","m square","zdd5133","pa878803","bo7035","chuwanglin","royadong","weiju",
            "bagsmart","baigio","sale!","ru&br","uiyi","anawishare","safebet","forudesigns","meiyasshidun","brand new"," dudini","excellent quality","factory price","xzhjt","magic fish","factory wholesale","all 6models","who cares","ankareeda","misterolina","promotion sale!!!","emma yao","hot selling!","forudesigns","nohoo ","maihoo","orthopedic",
            "delune brand","wenjie","korean fashion","clearance sale","big promotion","biggest promotion","iwish","guangzhou","muse love","shengmeiyuan","stema","aliexpress","hnm","yzwle","mileegirl","born pretty bp-w13","#bp049 # 24910","yzw-8058","biutee","belen","stz","#xf184","lke","tracy","sweet trend","new arrive","toopoot's vestidos","vander",
            "zoeva maquiagem","jimshop","love alpha","lanbena","ucanbe","fulljon","kailijumei","super deals","focallure","[rosalind beauty] mixiu","klasvsa","elera","kiki beauty world.","# ht0058","best sale","ducare","100% star","docolor","bioaqua","jashay","ducare","konjac","popfeel","her name","beauty girl","yuda","by dhl or ems","gflv","newview",
            "ipik","genuine gillette","kemei","qshave","dorco","weishi","povos","titan","caicui","eebt10_5484","ckeyin","ironsoul","a6080-207","nu-taty","hc1185","hc1104","a6080-204","bonixiya","toppik","keelorn","bear leader","[mumsbest]","bibicola","monkids","ai meng","keaiyouhuo","humor bear","lzh","eaboutique","sanlutoz","tulle tutu","novatx",
            "menoea","he hello enjoy","kavkas","10models!!","cielarko","sayoyo","tongyouyuan","european fashion","new fashion","hot uovo brand","chaussure enfant","in stock fast shipping","j ghee","cozulma","new design","lanshitina","guinea pigs","aercourm","copodenieve","sunveno","chiaus","jinobaby","imucci","brand new","lifetree","deltrue","bobei",
            "hzirip","[simfamily]","ming di","v-tree","keaiyouhuo","ropa mama e hija","free drop shipping","anlencool","chifuna","famli","zehui","emotion moms","gourd doll","muqian","low price!","zeechi","muqian","wisstt","ztov","high quality","nice-forever","best selling","new","new arrivals","new style","free shipping","drop shipping","wholesale",
            "dhl","ems"};

    /**
     * 产品单页静态化文件中的名字，
     */
    private static final String[] goodsNameWords={"hotsale","aliexpress","free-shipping","new","shipping","HOT","fashion","Hot sale","Hot Worldwide"};
    /**
     * 标题中的店名和品牌
     */
    private static final String[] storeOrBrandSensitiveName = {"vestidos","vestido","vestidos de festa","shein","vfemage","femininos","feminine","gagaopt","fanala","100% good feedback","missord","kostlich","mokingtop","berydress","michley","gamiss","sisjuly","chicing","colrovie","Merderheow","colrovie","acevog","brand","female","simplee","BEFORW","ladyvostok","frisky",
            "artka","yikuyiya","qhlcn","skinnwille","veri gude","ceprask","leiji","lienzy","misun","furlove","taovk","omgala","jiayiqi","liebe engel","effie queen ","roxi","shdede","bebella","beagloer","luoteemi","Jessie pepe","neoglory","jinse","miredo","t400","icebear","iecbear","jazzevar","hdy","miegofce","x-starry","ocary","daylook","elexs",
            "astrid","tangada","kigo","bunniesfairy","xiniu","tlzc","alisister","toyouth","waibo","twotwinstyle","weoneworld","cacana"};

    public static String removeTitleSensitiveWord(String title) {
        if(title != null && !"".equals(title)){
            for(String tsw : titleSensitiveWords){
                title = title.replace(tsw.trim(), "");
            }

            for(String sbdn : storeOrBrandSensitiveName) {
                title = title.replace(sbdn.trim(), "");
            }
            //去掉年份
            int nowYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
            for(int year=1949;year<nowYear;year++){
                title = title.replace(String.valueOf(year), "");
            }
        }else{
            title = "";
        }

        return title;
    }

    /**
     * 移除产品单页名字中一些乱七八糟的词
     * @param goodsName
     * @return
     */
    public static String removeGoodsNameWords(String goodsName){

        if(goodsName!=null&&!"".equals(goodsName)){
            for (String name : goodsNameWords) {
//				goodsName=goodsName.replace(name.trim(), "");
                //替换忽略大小写
                String newName="(?i)"+name;
                goodsName=goodsName.replaceAll(newName, "");
            }

//			int nowYear = Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
			/*delete by lhao 2018.06.29
			for(int year=1949;year<=9999;year++){
				goodsName = goodsName.replace(String.valueOf(year), "");
			}*/
            return goodsName;
        }
        return goodsName;
    }


    public static void main(String[] args) {
        String se="hot freeshipping 2012 2017 HOT goods new and people NEW New aliexpreSs Aliexpress Hot sale";
        String nameWords = removeGoodsNameWords(se);
        System.out.println(nameWords);
    }

}