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
    private boolean isScrolled;//左边点击之后，右边会开始滑动，右边的滑动会影响左边，此标志用来屏蔽左边的连续滑动
    private LinearLayoutManager mLinearLayoutManager;

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
                    targetPosition = position;
                    isScrolled = true;
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

        if (isLeft) {
            mSortAdapter.setCheckedPosition(position);
            //此处的位置需要根据每个分类的集合来进行计算
            Log.d("pos---->", String.valueOf(position));
            mSortDetailFragment.setData(position * 10 + position);
            moveToCenter(position);

        } else {
            if (targetPosition == position) {
                isScrolled = false;
            }
            if (!isScrolled) {
                mSortAdapter.setCheckedPosition(position);
                moveToCenter(position);
            }

        }


    }

    //将当前选中的item居中
    private void moveToCenter(int position) {
        //将点击的position转换为当前屏幕上可见的item的位置以便于计算距离顶部的高度，从而进行移动居中
        View childAt = rvSort.getChildAt(position - mLinearLayoutManager.findFirstVisibleItemPosition());
        int y = (childAt.getTop() - rvSort.getHeight() / 2);
        rvSort.smoothScrollBy(0, y);
        Log.d("y---->", String.valueOf(y));
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
