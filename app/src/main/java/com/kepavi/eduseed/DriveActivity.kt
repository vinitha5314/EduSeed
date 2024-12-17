package com.kepavi.eduseed

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.webkit.DownloadListener
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DriveActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private lateinit var spinnerFolders: Spinner
    private lateinit var folderLinks: Map<String, String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drive)

        webView = findViewById(R.id.webView)
        spinnerFolders = findViewById(R.id.spinnerFolders)

        // Initialize folder links
        folderLinks = mapOf(
            "Folder 1" to "https://drive.google.com/drive/folders/1ZhtYI2ENpk00reSpLSrtjZH2QJkt8bc0?usp=drive_link",
            "Folder 2" to "https://drive.google.com/drive/folders/1WAh6Fn0pB5KbcWl-4VdtsKErfPn4VuGt?usp=drive_link",
            "Folder 3" to "https://drive.google.com/file/d/1-MmAG9dT1mZKETyRuPS-ve1wHBZKgVqL/view?usp=drive_link"
        )

        // Set up Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, folderLinks.keys.toList())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFolders.adapter = adapter

        spinnerFolders.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val folderName = parent.getItemAtPosition(position).toString()
                val folderUrl = folderLinks[folderName]
                folderUrl?.let { webView.loadUrl(it) }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

        // Initial WebView setup
        webView.webViewClient = WebViewClient()
        val webSettings: WebSettings = webView.settings
        webSettings.javaScriptEnabled = true
        webSettings.javaScriptCanOpenWindowsAutomatically = true

        webView.setDownloadListener { url, userAgent, contentDisposition, mimeType, _ ->
            val fileName = Regex("(?i)^.*filename=\"([^\"]+)\".*$")
                .find(contentDisposition ?: "")?.groupValues?.get(1) ?: "downloaded_file"

            val request = DownloadManager.Request(Uri.parse(url)).apply {
                setMimeType(mimeType)
                addRequestHeader("User-Agent", userAgent)
                setDescription("Downloading file...")
                setTitle(fileName)
                allowScanningByMediaScanner()
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            }

            val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
            if (downloadManager != null) {
                downloadManager.enqueue(request)
                Toast.makeText(this@DriveActivity, "Downloading File...", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@DriveActivity, "Download Manager not available!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
