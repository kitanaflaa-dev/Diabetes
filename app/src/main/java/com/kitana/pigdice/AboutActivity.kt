package com.kitana.pigdice

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val rootLayout: ConstraintLayout = findViewById(R.id.root_layout)
        val btnAbout: Button = findViewById(R.id.btnAbout)
        val btnContact: Button = findViewById(R.id.btnContact)

        rootLayout.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        btnAbout.setOnClickListener {
            openUrl("https://www.lockersoft.com/webpages/about.php")
        }

        btnContact.setOnClickListener {
            openUrl("https://formspree.io/f/xeopyzda")
        }
    }

    private fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(url)
        startActivity(intent)
    }
}
