package com.kitana.pigdice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class LeaderboardAdapter(
    private val items: List<GameRecord>
) : RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRecord: TextView = itemView.findViewById(R.id.tvRecord)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_leaderboard_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val record = items[position]
        if (record.score < 0 && record.winner.isEmpty()) {
            // empty message row
            holder.tvRecord.text = record.date  // contains "No games played yet."
            holder.tvRecord.setTextColor(
                ContextCompat.getColor(holder.itemView.context, android.R.color.darker_gray)
            )
            return
        }

        holder.tvRecord.text = "${record.winner} - ${record.score} - ${record.date}"

        val context = holder.itemView.context
        val userLabel = context.getString(R.string.human)
        val colorRes = if (record.winner == userLabel) {
            R.color.leader_user
        } else {
            R.color.leader_computer
        }
        holder.tvRecord.setTextColor(ContextCompat.getColor(context, colorRes))
    }

    override fun getItemCount(): Int = items.size
}
