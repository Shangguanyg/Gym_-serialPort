package com.ganainy.DroidJet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.ganainy.gymmasterscompose.R
import android.widget.Button
import com.ganainy.DroidJet.views.GameView
import android.widget.FrameLayout
import android.os.Handler


class TableFragment : Fragment() {
    private lateinit var gameView: GameView
    private lateinit var tableContainer: FrameLayout
    private var tableFragment: TableFragment? = null
    private val handler = Handler()

    companion object {
        private const val ARG_TABLE_DATA = "table_data"

        fun newInstance(data: List<String>): TableFragment {
            val fragment = TableFragment()
            val args = Bundle()
            args.putStringArrayList(ARG_TABLE_DATA, ArrayList(data))
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_game, container, false)
        gameView = view.findViewById(R.id.gameView)
        tableContainer = view.findViewById(R.id.tableContainer)

        // 初始化表格Fragment
        tableFragment = TableFragment.newInstance(emptyList())
        childFragmentManager.beginTransaction()
            .replace(R.id.tableContainer, tableFragment!!)
            .commit()

        // 添加退出按钮逻辑
        val exitButton: Button = view.findViewById(R.id.exitButton)
        exitButton.setOnClickListener {
            requireActivity().finish()
        }

        return view
    }

    // 添加更新表格的方法
    fun updateTableData(data: List<String>) {
        tableFragment?.let { fragment ->
            if (fragment.isAdded) {
                val newFragment = TableFragment.newInstance(data)
                childFragmentManager.beginTransaction()
                    .replace(R.id.tableContainer, newFragment)
                    .commit()
                tableFragment = newFragment
            }
        }
    }


}
