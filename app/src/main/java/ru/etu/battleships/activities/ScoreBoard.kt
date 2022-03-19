package ru.etu.battleships.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.etu.battleships.R
import ru.etu.battleships.databinding.ActivityScoreBoardBinding
import ru.etu.battleships.databinding.ScoreBoardItemBinding
import ru.etu.battleships.db.UsersDBHelper
import ru.etu.battleships.extUI.QuestionDialog
import ru.etu.battleships.model.UserScore
import kotlin.system.exitProcess

class ScoreBoard : AppCompatActivity() {
    class ScoreboardAdapter : RecyclerView.Adapter<ScoreboardAdapter.ScoreboardHolder>() {
        private var userScoreList: List<UserScore> = ArrayList()

        class ScoreboardHolder(item: View) : RecyclerView.ViewHolder(item) {
            private var binding = ScoreBoardItemBinding.bind(item)
            fun bind(userScore: UserScore) = with(binding) {
                username.text = userScore.username
                victories.text = userScore.victories.toString()
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreboardHolder {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.score_board_item, parent, false)
            return ScoreboardHolder(view)
        }

        override fun onBindViewHolder(holder: ScoreboardHolder, position: Int) {
            holder.bind(userScoreList[position])
        }

        override fun getItemCount(): Int {
            return userScoreList.size
        }

        @SuppressLint("NotifyDataSetChanged")
        fun setUserScoreList(userScoreList: List<UserScore>) {
            this.userScoreList = userScoreList
            this.notifyDataSetChanged()
        }
    }

    private lateinit var binding: ActivityScoreBoardBinding
    private lateinit var questionDialog: QuestionDialog

    private val adapter = ScoreboardAdapter()
    lateinit var usersDBHelper: UsersDBHelper

    private var users = mutableListOf<UserScore>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScoreBoardBinding.inflate(layoutInflater)
        questionDialog = QuestionDialog(this)
            .setMessage(resources.getString(R.string.exit_dialog_message))
            .setOnAcceptListener {
                finishAffinity()
                exitProcess(0)
            }

        usersDBHelper = UsersDBHelper(this)
        users = usersDBHelper.readAllUsers()
        init()

        setContentView(binding.root)
    }

    private fun init() {
        binding.apply {
            back.setOnClickListener {
                finish()
            }
            exitButton.setOnClickListener {
                questionDialog.show()
            }

            scoreboardTable.layoutManager = LinearLayoutManager(this@ScoreBoard)
            scoreboardTable.adapter = adapter
            adapter.setUserScoreList(users)
        }
    }
}
