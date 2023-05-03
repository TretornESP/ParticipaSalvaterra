package com.tretornesp.participa.service;

import android.widget.RelativeLayout;

public interface ProposalServiceIF {
    void loadNext(RelativeLayout relativeLayout);
    void loadAt(String index, RelativeLayout relativeLayout);
    void loadFirst(RelativeLayout relativeLayout);
    boolean hasMore();
}
