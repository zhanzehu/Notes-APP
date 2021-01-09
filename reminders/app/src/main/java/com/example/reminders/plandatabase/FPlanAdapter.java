package com.example.reminders.plandatabase;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.reminders.R;

import java.util.ArrayList;
import java.util.List;

public class FPlanAdapter extends BaseAdapter {
    private Context mContext;

    final String TAG="oopoo";
    private int checkNum; // 记录选中的条目数量
    private List<Plan> backList;//用来备份原始数据
    private List<Plan> planList;//这个数据是会改变的，所以要有个变量来备份一下原始数据
    //滑动删除
    public static FListItemDelete itemDelete = null;

    public FPlanAdapter(Context mContext, List<Plan> planList) {
        this.mContext = mContext;
        this.planList = planList;
        backList = planList;
    }

    @Override
    public int getCount() {
        return planList.size();
    }

    @Override
    public Object getItem(int position) {
        return planList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mContext, R.layout.fplan_layout, null);
        TextView tv_title = (TextView)v.findViewById(R.id.ftv_title);
        TextView tv_content = (TextView)v.findViewById(R.id.ftv_content);
        TextView tv_time = (TextView)v.findViewById(R.id.ftv_time);
        ImageView btnDelete_plan=(ImageView)v.findViewById(R.id.fbtnDelete_plan);
        //ImageView btnNao_plan=(ImageView)v.findViewById(R.id.fbtnNao_plan);

        final Plan plan = planList.get(position);
        //******************************************滑动删除********************************************
        btnDelete_plan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                planList.remove(position);
                notifyDataSetChanged();
                CRUD op = new CRUD(mContext);
                op.open();
                op.removePlan_finish(plan);
                op.close();
                showInfo("点击删除了");
                itemDelete.reSet();
            }
        });
        //********************************************点击编辑***********************************************
    /**
        btnNao_plan.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    });
     **/
        //Set text for TextView
        tv_title.setText(planList.get(position).getTitle());
        tv_content.setText(planList.get(position).getContent());
        tv_time.setText(planList.get(position).getTime());
        //Save plan id to tag
        v.setTag(planList.get(position).getId());

        return v;
    }




    private Toast mToast;
//提示框
    public void showInfo(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(mContext, text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }


    public static void ItemDeleteReset() {
        if (itemDelete != null) {
            itemDelete.reSet();
        }
    }


}
