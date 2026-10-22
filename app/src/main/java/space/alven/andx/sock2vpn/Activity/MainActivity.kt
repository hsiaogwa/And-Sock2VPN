package space.alven.andx.sock2vpn.Activity

import android.net.VpnService
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar

import space.alven.andx.sock2vpn.R
import space.alven.andx.sock2vpn.Adapter.MainAdapter
import space.alven.andx.sock2vpn.Entity.LnkConf
import space.alven.andx.sock2vpn.Entity.SshLnkConf
import space.alven.andx.sock2vpn.Service.SshVpnService
import space.alven.andx.sock2vpn.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding;

    private var vs: SshVpnService? = null;
    private var isBound: Boolean = false;

    //
    private val serviceConnection =
        object : android.content.ServiceConnection {

            override fun onServiceConnected(
                name: android.content.ComponentName,
                service: android.os.IBinder
            ) {
                val binder = service as SshVpnService.LocalBinder
                vs = binder.getService()
                isBound = true
            }

            override fun onServiceDisconnected(
                name: android.content.ComponentName
            ) {
                vs = null
                isBound = false
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(layoutInflater);
        setContentView(binding.root);
        setSupportActionBar(binding.toolbar);

        // bind view pager adapter
        val main_container: ViewPager2 = this.findViewById<ViewPager2>(R.id.main_container);
        main_container.adapter = MainAdapter();
        main_container.setCurrentItem(1, false);

        // bind floating button event
        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null)
                .setAnchorView(R.id.fab).show();
        }

    }

    override fun onStart() {
        super.onStart();

        val intent = android.content.Intent(
            this,
            SshVpnService::class.java
        )

        bindService(
            intent,
            serviceConnection,
            BIND_AUTO_CREATE
        )
    }

    override fun onStop() {
        if (this.isBound) {
            unbindService(serviceConnection)
            this.isBound = false
        }

        this.vs = null;
        super.onStop()
    }



    private val vpnPermissionLauncher =
        registerForActivityResult(
            androidx.activity.result.contract.ActivityResultContracts
                .StartActivityForResult()
        ) {
            if (it.resultCode == RESULT_OK) {
                autolnk();
            }
        }
    private var pendingLink: SshLnkConf? = null

    private fun autolnk(v: View) {
        var vpnService: SshVpnService; // TODO bind btn n load conf
        val link : LnkConf = ;/* 取得你的 SshLnkConf */
        pendingLink = link;

        val permissionIntent = VpnService.prepare(this)
        if (permissionIntent != null) {
            vpnPermissionLauncher.launch(permissionIntent)
        } else {
            vpnService?.startTunnel(link);
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true;
            else -> super.onOptionsItemSelected(item);
        }
    }
}