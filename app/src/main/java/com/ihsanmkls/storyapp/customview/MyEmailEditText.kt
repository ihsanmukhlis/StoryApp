package com.ihsanmkls.storyapp.customview

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.widget.doOnTextChanged
import com.ihsanmkls.storyapp.R

class MyEmailEditText : AppCompatEditText {

    private var emailRegex: Regex = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+".toRegex()

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        doOnTextChanged { s, _, _, _ ->
            if (s.toString().isEmpty()) {
                error = context.getString(R.string.msg_empty_email)
                requestFocus()
            } else if (!s.toString().matches(emailRegex)) {
                error = context.getString(R.string.msg_invalid_email)
                requestFocus()
            }
        }
    }
}