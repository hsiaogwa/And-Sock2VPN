package space.alven.andx.sock2vpn.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import space.alven.andx.sock2vpn.R

class MainAdapter: RecyclerView.Adapter<MainAdapter.PageViewHolder>() {
    private val pages = listOf(
        R.layout.adapter_main_selection,
        R.layout.adapter_main_home,
        R.layout.adapter_main_config
    )

    inner class PageViewHolder(view: View) : RecyclerView.ViewHolder(view);

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(this.pages[viewType], parent, false)
        return PageViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return position;
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {}
        // holder.textView.text = pages[position] bind text...

    override fun getItemCount(): Int = pages.size
}