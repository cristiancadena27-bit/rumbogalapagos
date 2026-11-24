package ec.familia.rumbogalapagos;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {

    private WebView web;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(Color.parseColor("#0B6E8A"));

        web = new WebView(this);
        web.setBackgroundColor(Color.parseColor("#E9F8F9"));
        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(true);
        s.setMediaPlaybackRequiresUserGesture(true);

        web.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                if ("file".equals(uri.getScheme())) {
                    return false;
                }
                if ("mailto".equals(uri.getScheme())) {
                    openMail(uri.getSchemeSpecificPart(), "", "");
                    return true;
                }
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                } catch (ActivityNotFoundException ignored) {
                }
                return true;
            }
        });

        web.addJavascriptInterface(new Bridge(), "Android");
        setContentView(web);

        if (savedInstanceState != null) {
            web.restoreState(savedInstanceState);
        } else {
            web.loadUrl("file:///android_asset/index.html");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        web.saveState(outState);
    }

    /** Funciones que la página puede llamar como window.Android.xxx */
    public class Bridge {
        @JavascriptInterface
        public void sendEmail(final String to, final String subject, final String body) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    openMail(to, subject, body);
                }
            });
        }

        @JavascriptInterface
        public void share(final String text) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, "Respaldo Rumbo a Galápagos");
                    i.putExtra(Intent.EXTRA_TEXT, text);
                    startActivity(Intent.createChooser(i, "Guardar respaldo"));
                }
            });
        }
    }

    /** Abre Gmail con el correo ya escrito; si no hay Gmail, deja elegir otra app de correo. */
    private void openMail(String to, String subject, String body) {
        Intent mail = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"));
        if (to != null && !to.isEmpty()) {
            mail.putExtra(Intent.EXTRA_EMAIL, new String[]{to});
        }
        mail.putExtra(Intent.EXTRA_SUBJECT, subject);
        mail.putExtra(Intent.EXTRA_TEXT, body);

        Intent gmail = new Intent(mail);
        gmail.setPackage("com.google.android.gm");
        try {
            startActivity(gmail);
        } catch (ActivityNotFoundException e) {
            try {
                startActivity(Intent.createChooser(mail, "Enviar felicitación"));
            } catch (ActivityNotFoundException ignored) {
            }
        }
    }

    @Override
    public void onBackPressed() {
        web.evaluateJavascript("(window.handleBack && window.handleBack()) ? 'yes' : 'no'", new android.webkit.ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                if (value == null || !value.contains("yes")) {
                    finish();
                }
            }
        });
    }
}
