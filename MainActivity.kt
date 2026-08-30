package com.adel.wakeel

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.Gravity
import android.widget.*
import java.util.Locale

class MainActivity : Activity() {
    private lateinit var input: EditText
    private lateinit var log: TextView
    private val voiceCode = 101
    private val permissionCode = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(28, 28, 28, 28)
            layoutDirection = LinearLayout.LAYOUT_DIRECTION_RTL
        }

        val title = TextView(this).apply {
            text = "🤖 وكيل عادل"
            textSize = 28f
            gravity = Gravity.CENTER
        }
        input = EditText(this).apply {
            hint = "اكتب أمرك هنا…"
            textSize = 18f
        }
        val send = Button(this).apply { text = "تنفيذ الأمر" }
        val mic = Button(this).apply { text = "🎙️ تحدث" }
        log = TextView(this).apply { textSize = 17f; setPadding(0, 24, 0, 0) }

        root.addView(title)
        root.addView(input)
        root.addView(send)
        root.addView(mic)
        root.addView(log)
        setContentView(root)

        send.setOnClickListener { handleCommand(input.text.toString()) }
        mic.setOnClickListener { startVoice() }
    }

    private fun startVoice() {
        val i = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ar")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "تحدث إلى وكيل عادل")
        }
        try {
            startActivityForResult(i, voiceCode)
        } catch (_: Exception) {
            Toast.makeText(this, "ميزة التعرف على الصوت غير متاحة على هذا الهاتف.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == voiceCode && resultCode == RESULT_OK) {
            val text = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull() ?: return
            input.setText(text)
            handleCommand(text)
        }
    }

    private fun handleCommand(command: String) {
        val c = command.trim()
        if (c.isEmpty()) return

        if (c.startsWith("اتصل بـ") || c.startsWith("اتصل ب")) {
            log.text = "الأمر: $c\n\nميزة الاتصال بجهة اتصال ستُربط في المرحلة التالية.\nلن يتم إجراء اتصال دون موافقة المستخدم."
            requestPhonePermissionIfNeeded()
            return
        }

        log.text = "الأمر: $c\n\nحالياً: تم استقبال الأمر بنجاح.\nالمرحلة التالية: ربط محرك الذكاء الاصطناعي وأدوات التنفيذ."
    }

    private fun requestPhonePermissionIfNeeded() {
        if (checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), permissionCode)
        }
    }
}
