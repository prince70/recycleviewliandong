package com.fatchao.gangedrecyclerview;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements CheckListener {
    private RecyclerView rvSort;
    private SortAdapter mSortAdapter;
    private SortDetailFragment mSortDetailFragment;
    private Context mContext;
    private int targetPosition;//点击左边某一个具体的item的位置
    private LinearLayoutManager mLinearLayoutManager;
    private boolean isMoved;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
        initData();
    }

    private void initData() {
        String[] classify = getResources().getStringArray(R.array.pill);
        List<String> list = Arrays.asList(classify);
        mSortAdapter = new SortAdapter(mContext, list, new RvListener() {
            @Override
            public void onItemClick(int id, int position) {
                if (mSortDetailFragment != null) {
                    isMoved = true;
                    targetPosition = position;
                    setChecked(position, true);
                }
            }
        });
        rvSort.setAdapter(mSortAdapter);
        createFragment();
    }

    public void createFragment() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        mSortDetailFragment = new SortDetailFragment();
        mSortDetailFragment.setListener(this);
        fragmentTransaction.add(R.id.lin_fragment, mSortDetailFragment);
        fragmentTransaction.commit();
    }

    private void setChecked(int position, boolean isLeft) {
        Log.d("p-------->", String.valueOf(position));
        if (isLeft) {
            //此处的位置需要根据每个分类的集合来进行计算
            mSortAdapter.setCheckedPosition(position);
            mSortDetailFragment.setData(position * 10 + position);
        } else {
            ItemHeaderDecoration.setCurrentTag(String.valueOf(targetPosition));
            if (isMoved) {
                isMoved = false;
            } else
                mSortAdapter.setCheckedPosition(position);

        }
        moveToCenter(position);

    }

    //将当前选中的item居中
    private void moveToCenter(int position) {
        //将点击的position转换为当前屏幕上可见的item的位置以便于计算距离顶部的高度，从而进行移动居中
        View childAt = rvSort.getChildAt(position - mLinearLayoutManager.findFirstVisibleItemPosition());
        int y = (childAt.getTop() - rvSort.getHeight() / 2);
        rvSort.smoothScrollBy(0, y);
    }


    private void initView() {
        rvSort = (RecyclerView) findViewById(R.id.rv_sort);
        mLinearLayoutManager = new LinearLayoutManager(mContext);
        rvSort.setLayoutManager(mLinearLayoutManager);
        DividerItemDecoration decoration = new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL);
        rvSort.addItemDecoration(decoration);

    }


    @Override
    public void check(int position, boolean isScroll) {
        setChecked(position, isScroll);

    }
}
