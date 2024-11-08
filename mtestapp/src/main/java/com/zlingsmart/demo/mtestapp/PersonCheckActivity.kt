package com.zlingsmart.demo.mtestapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.SystemClock
import android.text.TextUtils
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.exa.baselib.utils.L
import com.exa.baselib.utils.Tools
import com.google.gson.Gson
import com.zlingsmart.demo.mtestapp.util.SPUtils

class PersonCheckActivity : AppCompatActivity() {

    private val spKey = "person_check"

    private val members = arrayOf(
        "边玉书",
        "陈彦彤",
        "程子妤",
        "崔溪垚",
        "杜芊诺",
        "何雨桐",
        "黄千寻",
        "林恩吉",
        "邱子淼",
        "唐久惠",
        "王艺璇",
        "阳梦依",
        "曾琪棋",
        "朱铄妍",
        "白梓航",
        "丁孟宇",
        "冯彧",
        "干羽飞",
        "龚一洋",
        "洪寅轩",
        "胡文博",
        "黄家豪",
        "黄千羽",
        "刘一乐",
        "邱天",
        "唐浩云",
        "王杨帆",
        "王张兴",
        "王智逸",
        "熊嘉译",
        "熊艺泽",
        "岳茗棋",
        "张翰伦",
        "张溢航",
        "张振荣"
    )

    private var unJoinNames: String? = null
    private lateinit var etMt: EditText
    private lateinit var etRes: EditText
    private lateinit var txt: TextView
    private lateinit var tvEdit: TextView
    private var startTime = 0L
    private var isEdit = false
    private val resLst = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person_check)
        initView()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        etRes = findViewById(R.id.etRes)
        txt = findViewById(R.id.txt)
        tvEdit = findViewById(R.id.tvEdit)
        etRes.setText(getLocalPeople())
        tvEdit.setOnTouchListener { _, event ->
            doEditEvent(event)
            return@setOnTouchListener false
        }
        findViewById<Button>(R.id.btnSure).setOnClickListener {
            val result = checkNeedJoinPeople()
            setText(result)
        }
        etMt = findViewById(R.id.etMt)
        findViewById<EditText>(R.id.etMt).setOnEditorActionListener { _, _, _ ->
            Tools.hideKeyboard(etMt)
            false
        }
        val txt = findViewById<TextView>(R.id.txt)
        txt.setOnLongClickListener {
            if (unJoinNames != null) {
                Tools.copyText(this, txt.getText().toString())
                Toast.makeText(this, "复制成功", Toast.LENGTH_LONG).show()
            }
            false
        }
    }

    private fun doEditEvent(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> startTime = SystemClock.elapsedRealtime()
            MotionEvent.ACTION_UP -> {
                if (!isEdit && SystemClock.elapsedRealtime() - startTime > 3000) {
                    tvEdit.text = "确认修改"
                    etRes.isEnabled = true
                    isEdit = true
                } else if (isEdit) {
                    isEdit = false
                    saveResPeople()
                    Tools.hideKeyboard(etRes)
                    etRes.isEnabled = false
                    tvEdit.text = "长按3秒编辑"
                }
            }

            else -> Unit
        }
    }

    private fun saveResPeople() {
        val res = etRes.text.toString().trim()
        try {
            resLst.clear()
            res.split(",").forEach {
                resLst.add(it)
            }
            SPUtils.saveStringData(spKey, Gson().toJson(resLst))
            L.d("saveResPeople 保存成功 ${Gson().toJson(resLst)}")
            Toast.makeText(this, "修改成功！", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            L.de(res, e)
            etRes.setText(getLocalPeople())
            Toast.makeText(this, "修改失败！", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkNeedJoinPeople(): String? {
        val joinStr: String = etMt.getText().toString()
        val needJoins = ArrayList<String>()
        for (member in resLst) {
            if (!joinStr.contains(member)) {
                needJoins.add(member)
            }
        }
        return if (needJoins.isEmpty()) null else needJoins.toString()
    }

    private fun getLocalPeople(): String {
        val s = SPUtils.getStringData(spKey)
        L.dd("sp save: $s")
        val sb = StringBuilder()
        if (TextUtils.isEmpty(s)) {
            members.forEach {
                resLst.add(it)
                if (TextUtils.isEmpty(sb.toString())) {
                    sb.append(it)
                } else {
                    sb.append(",").append(it)
                }
            }
            val ms = Gson().toJson(resLst)
            SPUtils.saveStringData(spKey, ms)
        } else {
            Gson().fromJson(s, resLst.javaClass).forEach {
                resLst.add(it)
                if (TextUtils.isEmpty(sb.toString())) {
                    sb.append(it)
                } else {
                    sb.append(",").append(it)
                }
            }
        }
        return sb.toString()
    }

    @SuppressLint("SetTextI18n")
    private fun setText(text: String?) {
        if (text == null) {
            txt.text = "所有人员已参与"
            unJoinNames = null
        } else {
            txt.text = "以下人员未参与，请及时参与：\n$text"
            unJoinNames = text
        }
    }
}