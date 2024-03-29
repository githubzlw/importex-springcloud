package com.importexpress.ali1688.service;


import com.alibaba.fastjson.JSONObject;
import com.importexpress.comm.pojo.Ali1688Item;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Ali1688APITest {

    @Autowired
    private Ali1688Service ali1688Service;

    @Test
    public void getItem() {

        Assert.assertNotNull(ali1688Service.getItem(574271927688L, true).getString("item"));

        Assert.assertNull(ali1688Service.getItem(574271927689L, true).getString("item"));

    }

    @Test
    public void getLargeItem1() {

        long[] pids = {
                568209025431l, 602501511454l, 586219729613l, 605163265698l, 600978381801l, 595702740768l, 603200834514l, 586351566486l, 601453836002l, 601132817851l, 599342902216l, 602210947871l, 601141777926l, 587651958957l, 574385535923l, 601143585303l, 601534423794l, 601535083714l, 605410050160l, 602199562742l, 598280310018l, 586281032446l, 595704542961l, 599656532707l, 602679840401l, 598945245772l, 596133262803l, 600289132869l, 605689647686l, 594827876196l, 602801390688l, 599252450708l, 594824164788l, 604316543406l, 596404360182l, 603340659177l, 606300342176l, 565702953511l, 601446747108l, 602343165722l, 602923318191l, 599411318229l, 593936150364l, 604691608810l, 550142549080l, 574267843070l, 599513367644l, 594133737007l, 592418799890l, 549898734434l, 599897102582l, 588826754925l, 596931824933l, 605360551761l, 600220850294l, 596687590902l, 601233000144l, 605435614729l, 576406112491l, 595719446349l, 600594093839l, 601930988732l, 575278878330l, 601755005555l, 589994099878l, 598783240729l, 584230857752l, 602803938301l, 605695515642l, 602352025436l, 598779132974l, 597434198091l, 596705438167l, 604410782173l, 598778914150l, 601683356068l, 594390050556l, 602476153336l, 596077588422l, 602530145582l, 574096264610l, 600528733036l, 603173775770l, 605493894075l, 595725164127l, 590929496265l, 600568711910l, 601928353845l, 596885159716l, 602077847842l, 574737809611l, 591423694642l, 596298299730l, 605219440533l, 601673546054l, 605880415920l, 576494256223l, 604037440918l, 600058171464l, 605447005689l};

        List<Long> lstOfflinePid = new ArrayList<>();
        for (long pid : pids) {

            JSONObject item = ali1688Service.getItem(pid, true);
            Assert.assertNotNull(item);
            if (item.getString("item") == null) {
                lstOfflinePid.add(pid);
            }
        }
        System.err.println("not exist " + lstOfflinePid);
    }

    @Test
    public void getLargeItem2() {

        Long[] pids = {
                568218067281l, 568514761766l, 605632321042l, 605010589561l, 574397192373l, 605633780183l, 592724938672l, 605251147919l, 575821261181l, 602705465350l, 556894453516l, 565315995483l, 575262283183l, 606347754627l, 605148510542l, 569282320543l, 606367220266l, 606482542172l, 604699740873l, 600986317316l, 603055626753l, 605025568107l, 603911188435l, 527656665617l, 600469500171l, 605920196600l, 596842470902l, 599447812897l, 605595267632l, 605871814988l, 601548226768l, 600291952473l, 604847540550l, 567674545173l, 599788624585l, 601679029771l, 606313919858l, 602803402352l, 602739030547l, 605352142084l, 602429179346l, 602550634829l, 602744386627l, 605447915246l, 555935898358l, 561202408866l, 604826996838l, 605129813855l, 602533321961l, 575410136918l, 574003896222l, 594103243602l, 565730166152l, 601081129498l, 602401788374l, 574267830112l, 574551213139l, 601045901970l, 574830935840l, 573244925167l, 602634917381l, 601821860734l, 602403380007l, 604756494850l, 605288991926l, 602401660465l, 606219758520l, 602738666992l, 574910837751l, 597504354331l, 599402236638l, 604210033715l, 605379074432l, 602347940945l, 577911973130l, 605454813834l, 605159069843l, 601768384152l, 606091290575l, 588227550893l, 589110754214l, 605921914218l, 602290864048l, 592323020386l, 597434190452l, 577934379431l, 602463761634l, 568371969150l, 601180192425l, 595956645412l, 593762025593l, 574540440203l, 605383110711l, 597932652213l, 548074169194l, 548125319390l, 558116554012l, 576090592400l, 604137996016l, 596287215322l};

        List<Long> lstPids = Arrays.asList(pids);
        List<Long> lstOfflinePid = new ArrayList<>();
        lstPids.stream().parallel().forEach(pid -> {
            JSONObject item = ali1688Service.getItem(pid, true);
            Assert.assertNotNull(item);
            if (item.getString("item") == null) {
                lstOfflinePid.add(pid);
            }
        });
        System.err.println("not exist " + lstOfflinePid);
    }

    @Test
    public void getLargeItem3() throws InterruptedException {

        long[] pids = {
                574280787621l, 599866872305l, 600222245637l, 596114430366l, 601237452098l, 605610758846l, 602288060317l, 605310026783l, 599964372115l, 601797559082l, 602991097441l, 601396510299l, 600480455809l, 598041883025l, 605156824893l, 596289600326l, 605394569390l, 602318433877l, 601673178072l, 593769721534l, 600481939936l, 605464920759l, 596303055107l, 565980902750l, 602313532914l, 590945050462l, 591142385021l, 600807387886l, 602803938319l, 596951751826l, 596351148106l, 560324278489l, 576889910434l, 573850398384l, 583442062788l, 584383004949l, 540357660673l, 574555665929l, 586441849657l, 575982118259l, 597327055137l, 576053631210l, 599142340103l, 576468496459l, 586443225048l, 576474761346l, 576480253736l, 597167748184l, 577513652284l, 553333989801l, 577517436488l, 599138724600l, 583548873968l, 583443490171l, 583677366248l, 583548873380l, 577885247149l, 586574006673l, 576309600637l, 576383630526l, 574643966791l, 599569826323l, 575667656911l, 563659109550l, 556528068036l, 557083558025l, 565922104568l, 557955163259l, 584722698630l, 558132137678l, 560287933230l, 576706318176l, 583313405224l, 573608144271l, 573670053864l, 581426816481l, 576153373975l, 599755495270l, 576258625316l, 556353814426l, 576308514644l, 576591761485l, 585883901847l, 582671926454l, 573670853074l, 597697607410l, 574993009898l, 576426364986l, 553487894589l, 559995090056l, 557729582655l, 575670920247l, 575797550354l, 574243580680l, 573549796166l, 583577175928l, 573680609388l, 573588836316l, 576773889939l, 574796659198l};

        List<Long> lstOfflinePid = new CopyOnWriteArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (long pid : pids) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    JSONObject item = ali1688Service.getItem(pid, true);
                    Assert.assertNotNull(item);
                    if (item.getString("item") == null) {
                        lstOfflinePid.add(pid);
                    }
                }
            });
        }
        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.MINUTES);
        System.err.println("not exist " + lstOfflinePid);
    }

    @Test
    public void getItemsInShop() {
        List<Ali1688Item> result = ali1688Service.getItemsInShop("shop1432227742608");
        Assert.assertNotNull(result);
        Assert.assertEquals(169, result.size());
    }


    @Test
    public void uploadImgToTaobao1() throws IOException {

        Assert.assertNotNull(ali1688Service.uploadImgToTaobao("C:\\Users\\luohao\\Downloads\\1111.jpg"));
    }

    @Test
    public void searchImgFromTaobao() throws IOException {

        String URL = ali1688Service.uploadImgToTaobao("C:\\Users\\luohao\\Downloads\\1111.jpg");
        Assert.assertNotNull(URL);
        Assert.assertNotNull(ali1688Service.searchImgFromTaobao(URL));
    }

    @Test
    public void getAlibabaDetail() {

        Assert.assertNotNull(ali1688Service.getAlibabaDetail(1600165367826L, true));

    }

    @Test
    public void getAliexpressDetail() {

        Assert.assertNotNull(ali1688Service.getAliexpressDetail(1600165367826L, true));

    }
}
