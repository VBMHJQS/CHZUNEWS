package com.chzu.getnews;

import java.util.List;

import me.maxwin.view.IXListViewLoadMore;
import me.maxwin.view.XListView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.chzu.adapter.NewContentAdapter;
import com.chzu.bean.NewsDetail;
import com.chzu.util.NewsDetailUtil;

public class NewsContentActivity extends Activity implements IXListViewLoadMore {

	private XListView mListView;  
	private ImageButton shareBtn;

	/** 
	 * ��ҳ���url 
	 */  
	private String url;  
	private NewsDetailUtil newsDetaiUtil;  
	private List<NewsDetail> mDatas;  

	private ProgressBar mProgressBar;  
	private NewContentAdapter mAdapter;  

	@Override  
	protected void onCreate(Bundle savedInstanceState)  
	{  
		super.onCreate(savedInstanceState);  
		setContentView(R.layout.news_content);  

		newsDetaiUtil = new NewsDetailUtil();  

		Bundle extras = getIntent().getExtras();  
		url = extras.getString("url");  

		mAdapter = new NewContentAdapter(this);  

		mListView = (XListView) findViewById(R.id.id_listview);  
		mProgressBar = (ProgressBar) findViewById(R.id.id_newsContentPro);  

		mListView.setAdapter(mAdapter);  
		mListView.disablePullRefreash();  
		mListView.setPullLoadEnable(this);  

		shareBtn = (ImageButton) findViewById(R.id.share_new);
		
		shareBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle extras = getIntent().getExtras();  
				String title = extras.getString("title");  
				//�������ҳ��
				Intent sIntent=new Intent(Intent.ACTION_SEND);  
				sIntent.setType("text/plain");  
				sIntent.putExtra(Intent.EXTRA_SUBJECT, "����");  
				sIntent.putExtra(Intent.EXTRA_TEXT, "����һ�����ѧԺ��������:"+title+",�������"+url);  
				startActivity(Intent.createChooser(sIntent, "����")); 
			}
		});
		
		mListView.setOnItemClickListener(new OnItemClickListener()  
		{  
			@Override  
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)  
			{  

				NewsDetail news = mDatas.get(position - 1);  
				String imageLink = news.getImageLink();  
				Intent intent = new Intent(NewsContentActivity.this,ImageShowActivity.class);  
				intent.putExtra("url", imageLink);  
				startActivity(intent);  
			}  
		});  

		mProgressBar.setVisibility(View.VISIBLE);  
		
		
		new LoadDataTask().execute();  

	}  

	@Override  
	public void onLoadMore()  
	{  

	}  

	class LoadDataTask extends AsyncTask<Void, Void, Void>  
	{  

		@SuppressLint("ShowToast")
		@Override  
		protected Void doInBackground(Void... params)  
		{  
			try  
			{  
				mDatas = newsDetaiUtil.getNewsDetail(url).getNewses();  
			} catch (Exception e)  
			{  
				Looper.prepare();  
				Toast.makeText(getApplicationContext(), e.getMessage(), 1).show();  
				Looper.loop();  
			}  

			return null;  
		}  

		@Override  
		protected void onPostExecute(Void result)  
		{  
			if(mDatas == null)  
				return ;   
			mAdapter.addList(mDatas);  
			mAdapter.notifyDataSetChanged();  
			mProgressBar.setVisibility(View.GONE);  
		}  

	}  

	/** 
	 * ������ذ�ť 
	 * @param view 
	 */  
	public void back(View view)  
	{  
		finish();  
	}  
	
	

}
