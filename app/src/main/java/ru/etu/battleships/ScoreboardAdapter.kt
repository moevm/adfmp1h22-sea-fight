package ru.etu.battleships

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.etu.battleships.databinding.ScoreBoardItemBinding


class ScoreboardAdapter : RecyclerView.Adapter<ScoreboardAdapter.ScoreboardHolder> () {
    private var userScoreList: List<UserScore> = ArrayList()

    class ScoreboardHolder(item: View): RecyclerView.ViewHolder(item) {
        private var binding = ScoreBoardItemBinding.bind(item)
        fun bind(userScore: UserScore) = with(binding) {
            username.text = userScore.username
            victories.text = userScore.victories.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreboardHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.score_board_item, parent, false)
        return ScoreboardHolder(view)
    }

    override fun onBindViewHolder(holder: ScoreboardHolder, position: Int) {
        holder.bind(userScoreList[position])
    }

    override fun getItemCount(): Int {
        return userScoreList.size
    }

    fun setUserScoreList(userScoreList: List<UserScore>) {
        this.userScoreList = userScoreList
        this.notifyDataSetChanged()
    }
}
