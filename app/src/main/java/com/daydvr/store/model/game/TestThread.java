package com.daydvr.store.model.game;

import com.daydvr.store.bean.GameListBean;
import com.daydvr.store.util.LoaderHandler;
import com.daydvr.store.view.adapter.GameListAdapter;

/**
 * @author LoSyc
 * @version Created on 2018/1/8. 17:51
 */

public abstract class TestThread extends Thread{
    private GameListAdapter.ViewHolder holder;
    private GameListBean bean;
    private LoaderHandler handler;

    public TestThread(GameListAdapter.ViewHolder holder, GameListBean bean, LoaderHandler handler) {
        this.holder = holder;
        this.bean = bean;
        this.handler = handler;
    }

    public GameListAdapter.ViewHolder getHolder() {
        return holder;
    }

    public void setHolder(GameListAdapter.ViewHolder holder) {
        this.holder = holder;
    }

    public GameListBean getBean() {
        return bean;
    }

    public void setBean(GameListBean bean) {
        this.bean = bean;
    }

    public LoaderHandler getHandler() {
        return handler;
    }

    public void setHandler(LoaderHandler handler) {
        this.handler = handler;
    }
}
