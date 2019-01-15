package com.restri_tech.Adapter;

import android.content.pm.ApplicationInfo;

public class app {

    ApplicationInfo ai;
    private boolean isSelected;

    public ApplicationInfo aiGet() {
        return ai;
    }

    public void aiSet(ApplicationInfo ai) {
        this.ai = ai;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}

