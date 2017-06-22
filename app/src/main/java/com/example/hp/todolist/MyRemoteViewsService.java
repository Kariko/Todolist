package com.example.hp.todolist;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Created by Administrator on 2016/12/20.
 */

public class MyRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new MyRemoteViewsFactory(this.getApplicationContext(), intent);
    }

    public class MyRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
        private Context context;
        private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        private MyDB myDB;
        private Map<String, Vector<String>> unfinished;

        public MyRemoteViewsFactory(Context context, Intent intent) {
            this.context = context;
            myDB = new MyDB(context);
        }

        @Override
        public void onCreate() {
            unfinished = myDB.queryAll(MyDB.UNFINISHED);
            Vector<String> titles = unfinished.get("titles");
            Vector<String> dates = unfinished.get("dates");
            for (int i = 0; i < titles.size(); i++) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("title", titles.get(i));
                map.put("date", dates.get(i));
                list.add(map);
            }
        }

        @Override
        public void onDataSetChanged() {
            list.clear();
            unfinished = myDB.queryAll(MyDB.UNFINISHED);
            Vector<String> titles = unfinished.get("titles");
            Vector<String> dates = unfinished.get("dates");
            for (int i = 0; i < titles.size(); i++) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("title", titles.get(i));
                map.put("date", dates.get(i));
                list.add(map);
            }
        }

        @Override
        public void onDestroy() {
            list.clear();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public RemoteViews getViewAt(int position) {
            if (position < 0 || position >= list.size())
                return null;
            String title = (String) list.get(position).get("title");
            String date = (String) list.get(position).get("date");
            // 创建在当前索引位置要显示的View
            final RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.item_in_widget);

            // 设置要显示的内容
            rv.setTextViewText(R.id.titleInWidget, title);
            rv.setTextViewText(R.id.dateInWidget, date);

            // 填充Intent，填充在AppWdigetProvider中创建的PendingIntent
            Intent intent = new Intent();
            // 传入点击行的数据
            intent.putExtra("title", title);
            intent.putExtra("date", date);
            rv.setOnClickFillInIntent(R.id.widgetItem, intent);

            return rv;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }
    }
}
