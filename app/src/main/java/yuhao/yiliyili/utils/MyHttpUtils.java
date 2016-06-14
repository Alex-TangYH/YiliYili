package yuhao.yiliyili.utils;


import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;


import java.math.BigDecimal;

import yuhao.yiliyili.bean.bangummi.BangumiInfoBean;
import yuhao.yiliyili.bean.bangummi.ListOfVideoInfoBean;
import yuhao.yiliyili.bean.bangummi.RankInfoBean;
import yuhao.yiliyili.bean.bangummi.RankVedioInfoBean;
import yuhao.yiliyili.bean.bangummi.VideoInfoBean;
import yuhao.yiliyili.interfaces.SetBanguimiUI;

/**
 * 网络请求工具类
 * Created by Yuhao on 2016/6/13.
 */
public class MyHttpUtils {
    private static String TAG = "MyHttpUtils";
    int count2 = 1;
    int page2 = 1;

    private SetBanguimiUI.SetHotBanguimiUI setHotBanguimiUI;
    private SetBanguimiUI.SetVideoUI setVideoUI;
    private SetBanguimiUI.GetVideoInfo getVideoInfo;

    public MyHttpUtils(SetBanguimiUI.GetVideoInfo getVideoInfo) {
        this.getVideoInfo = getVideoInfo;
    }

    public MyHttpUtils(SetBanguimiUI.SetHotBanguimiUI hotBanguimiUI) {
        this.setHotBanguimiUI = hotBanguimiUI;
    }

    public MyHttpUtils(SetBanguimiUI.SetVideoUI setVideoUI) {
        this.setVideoUI = setVideoUI;
    }


    /**
     * 获取mp4/flv视频源地址
     * 请求方式: GET
     * 示例:GET http://bilibili-service.daoapp.io/video/3580782?quality=2
     * quailty  [int]  清晰度(1~2，根据视频有不同)
     * type  [int]  0:flv,1:hdmp4,2:mp4
     * @return Video source address
     * @param cid
     */
    public boolean getBangumi (String cid){

        HttpUtils httpUtils = new HttpUtils();
        String url = "http://bilibili-service.daoapp.io/video/";

        Log.e("url",url+cid+"?quality=2");
        httpUtils.send(HttpRequest.HttpMethod.GET,
                url+cid+"?quality=2",
                new RequestCallBack<String>(){

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {

                        String test = responseInfo.result;
                        Log.e("result",test);
                        Gson gson = new GsonBuilder().create();
                        BangumiInfoBean bangumiInfoBean = gson.fromJson(test,BangumiInfoBean.class);
                        setVideoUI.doing(bangumiInfoBean.getUrl());
                        Log.e("bangumiInfoBean.getUrl",bangumiInfoBean.getUrl().toString());
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        Log.e("msg",msg);
                        Log.e("error",error.toString());

                    }
                });
        return true;
    }


    /**
     * 获取分类下的排行信息
     * 请求方式: GET
     * 示例:GET http://bilibili-service.daoapp.io/sort/13?order=new&page=2&count=3
     * Sort 分类号  '1' => '动画','3' => '音乐','4' => '游戏','5' => '娱乐','11' => '电视剧'，
     *              '13' => '番剧','23' => '电影','36' => '科技','119' => '鬼畜','129' => '舞蹈',
     * page 页码; count 分页容量; order排序方式(new,hot);默认值为1，20，hot.使用空字符即可使用默认值
     * @return VideoInfomationList
     */
    public boolean getRankOfSort (String sortId,String order, int count, int page){

        HttpUtils httpUtils = new HttpUtils();

        count2 = count;
        page2 = page;
        String url = "http://bilibili-service.daoapp.io/sort/";
        httpUtils.send(HttpRequest.HttpMethod.GET,
                url + sortId + "?count=" + count2 +"&page=" + page2,
                new RequestCallBack<Object>() {
                    @Override
                    public void onSuccess(ResponseInfo<Object> responseInfo) {
                        String test = (String) responseInfo.result;
                        Log.e("result",test);

                        Gson gson = new GsonBuilder().create();
                        RankInfoBean rankInfoBean = new RankInfoBean();
                        rankInfoBean = gson.fromJson(test,RankInfoBean.class);
                        String rvib = rankInfoBean.getList().toString().replace("{\"0\":","").replace(",\"num\":\""+rankInfoBean.getResults()+"\"}","");
                        RankVedioInfoBean rankVedioInfoBean= gson.fromJson(rvib,RankVedioInfoBean.class);
                        setHotBanguimiUI.doing(rankVedioInfoBean);
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {

                    }
                });

        return true;
    }


    public void getCid(String aid){
        HttpUtils httpUtils = new HttpUtils();
        String url = "http://bilibili-service.daoapp.io/view/";

        httpUtils.send(HttpRequest.HttpMethod.GET,
                url+aid,
//                url,
                new RequestCallBack<String>(){

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {

                        String test = responseInfo.result;
                        Log.e("result",test);
                        Gson gson = new GsonBuilder().create();
                        VideoInfoBean videoInfoBean = new VideoInfoBean();
                        videoInfoBean = gson.fromJson(test,VideoInfoBean.class);

                        String vib = videoInfoBean.getList().toString().replace("\"0\":","").replace("{{","{").replace("}}","}");
                        ListOfVideoInfoBean listOfVideoInfoBean = gson.fromJson(vib,ListOfVideoInfoBean.class);
                        BigDecimal bd = new BigDecimal(listOfVideoInfoBean.getCid());
                        listOfVideoInfoBean.setCid(bd.toPlainString());
                        getVideoInfo.doing(listOfVideoInfoBean.getCid());
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        Log.e("msg",msg);
                        Log.e("error",error.toString());
                    }
                });
    }
}