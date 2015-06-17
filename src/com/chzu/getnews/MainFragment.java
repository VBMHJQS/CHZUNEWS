package com.chzu.getnews;

import java.util.ArrayList;
import java.util.List;

import me.maxwin.view.IXListViewLoadMore;
import me.maxwin.view.IXListViewRefreshListener;
import me.maxwin.view.XListView;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.chzu.adapter.NewsListAdapter;
import com.chzu.bean.NewsTitle;
import com.chzu.dao.NewsTitleDao;
import com.chzu.util.AppUtil;
import com.chzu.util.Logger;
import com.chzu.util.NetUtil;
import com.chzu.util.NewsDetailUtil;
import com.chzu.util.URLDetail;


@SuppressLint("InflateParams")
public class MainFragment extends Fragment implements IXListViewRefreshListener, IXListViewLoadMore{

	private static final int LOAD_REFREASH = 0x111;
	
	private static final int TIP_ERROR_NO_NETWORK = 0X112;
	private static final int TIP_ERROR_SERVER = 0X113;
	
	/**
	 * �Ƿ��һ�ν���
	 */
	private boolean isFirstIn = true;
	
	/**
	 * �Ƿ���������
	 */
	private boolean isConnNet = false;
	
	/**
	 * ��ǰ�����Ƿ�������ȡ
	 */
	@SuppressWarnings("unused")
	private boolean isLoadingDataFromNetWork;
	
	
	/**
	 * Ĭ�ϵ�newType
	 */
	private int newsType = URLDetail.NEWS_LIST_WYYW;
	
	/**
	 * �������ŵĹ�����
	 */
	private NewsDetailUtil newsDetailUtil;
	
	/**
	 * �����ݿ⽻��
	 */
	private NewsTitleDao mNewsTitleDao;
	
	/**
	 * ��չ��ListView
	 */
	private XListView mXListView;
	
	/**
	 * ����������
	 */
	private NewsListAdapter mAdapter;
	
	/**
	 * ����
	 */
	private List<NewsTitle> mDatas = new ArrayList<NewsTitle>();
	
	/**
	 * ���newstype
	 * @param newsType
	 */
	public MainFragment(int newsType){
		this.newsType = newsType;
		newsDetailUtil = new NewsDetailUtil();
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.tab_item_fragment_main, null);	}


	@Override
	public void onActivityCreated( Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mNewsTitleDao = new NewsTitleDao(getActivity());
		mAdapter = new NewsListAdapter(getActivity(), mDatas);
		
		/**
		 * ��ʼ��
		 */
		mXListView = (XListView) getView().findViewById(R.id.id_xlistView);
		mXListView.setAdapter(mAdapter);
		mXListView.setPullRefreshEnable(this);
		mXListView.setPullLoadEnable(this);
		mXListView.setRefreshTime(AppUtil.getRefreashTime(getActivity(), newsType));
		
		mXListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				NewsTitle newsTitle = mDatas.get(position-1);
				Intent intent = new Intent(getActivity(), NewsContentActivity.class);
				intent.putExtra("url", newsTitle.getLink());
				startActivity(intent);
			}
		});
		if(isFirstIn){
			/**
			 * ����ʱֱ��ˢ��
			 */
			mXListView.startRefresh();
			isFirstIn = false;
		}else{
			mXListView.NotRefreshAtBegin();
		}
	}


	@Override
	public void onLoadMore() {
		
	}

	@Override
	public void onRefresh() {
		new LoadDatasTask().execute(LOAD_REFREASH);
	}
	
	public Integer refreashData(){
		if(NetUtil.checkNet(getActivity())){
			isConnNet = true;
			//��ȡ����
			try{
				List<NewsTitle> newsTitles = newsDetailUtil.getNewsList(newsType);
				mAdapter.setDatas(newsTitles);
				
				isLoadingDataFromNetWork = true;
				//����ˢ��ʱ��
				AppUtil.setRefreashTime(getActivity(), newsType);
				//������ݿ�����
				mNewsTitleDao.deleteAll(newsType);
				//�������ݿ�
				mNewsTitleDao.add(newsTitles);
			}catch(Exception e){
				e.printStackTrace();
				isLoadingDataFromNetWork = false;
				return TIP_ERROR_SERVER;
			}
		}else{
			Logger.e("����������!");
			isConnNet = false;
			isLoadingDataFromNetWork = false;
			//�����ݿ����
			List<NewsTitle> newsTitle = mNewsTitleDao.list(newsType);
			mDatas = newsTitle;
			return TIP_ERROR_NO_NETWORK;
		}
		return -1;
	}
	
	/**
	 * �������ݵ��첽����
	 * @author Administrator
	 *
	 */
	class LoadDatasTask extends AsyncTask<Integer, Void, Integer>
	{
		
		@Override
		protected void onPostExecute(Integer result)
		{
			switch (result) {
			case TIP_ERROR_NO_NETWORK:
				Toast.makeText(getActivity(), "û����������!", Toast.LENGTH_SHORT).show();
				mAdapter.setDatas(mDatas);
				mAdapter.notifyDataSetChanged();
				break;
			case TIP_ERROR_SERVER:
				Toast.makeText(getActivity(), "����������!", Toast.LENGTH_SHORT).show();
			default:
				break;
			}
			mXListView.setRefreshTime(AppUtil.getRefreashTime(getActivity(), newsType));
			mXListView.stopRefresh();
			mXListView.stopLoadMore();
		}
		@Override
		protected Integer doInBackground(Integer... params) {
			if(params[0] == LOAD_REFREASH){
				return refreashData();
			}
			return -1;
		}
	}
	
}
