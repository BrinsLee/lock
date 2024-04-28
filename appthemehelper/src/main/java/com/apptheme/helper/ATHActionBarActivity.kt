package com.apptheme.helper

import androidx.appcompat.widget.Toolbar
import com.apptheme.helper.utils.getSupportActionBarView

class ATHActionBarActivity : ATHToolbarActivity() {

    override fun getATHToolbar(): Toolbar? {
        return getSupportActionBarView(supportActionBar)
    }
}
