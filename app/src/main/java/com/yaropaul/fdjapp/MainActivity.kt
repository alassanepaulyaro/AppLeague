package com.yaropaul.fdjapp

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.os.Bundle
import android.provider.BaseColumns
import android.view.Menu
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.cursoradapter.widget.CursorAdapter
import androidx.cursoradapter.widget.SimpleCursorAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import com.yaropaul.fdjapp.data.model.League
import com.yaropaul.fdjapp.data.model.Team
import com.yaropaul.fdjapp.databinding.ActivityMainBinding
import com.yaropaul.fdjapp.presentation.MainViewModel
import com.yaropaul.fdjapp.presentation.TeamsAdapter
import com.yaropaul.fdjapp.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var searchView: SearchView
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_search, menu)

        val searchItem = menu.findItem(R.id.menu_search)
        searchView = searchItem.actionView as SearchView
        searchView.queryHint = getString(R.string.search_hint)
        mainViewModel.leagueData.observe(this) { resource ->
            displayViewVisibility(resource)
            if (resource is Resource.Success) {
                resource.data?.let {
                    setLeagueAutoComplete(searchView, leagues = it.leagues, this)
                }
            }
        }
        mainViewModel.getLeaguesData()
        return true
    }

    /**
     * function displays the visibility of the view
     */
    private fun <T> displayViewVisibility(resource: Resource<T>) {
        when (resource) {
            is Resource.Loading -> {
                binding.progressBar.visibility = View.VISIBLE
                binding.errorView.visibility = View.GONE
                binding.recyclerviewTeams.visibility = View.GONE
            }
            is Resource.Error -> {
                binding.progressBar.visibility = View.GONE
                binding.errorView.visibility = View.VISIBLE
                binding.errorView.text = resource.message
                binding.recyclerviewTeams.visibility = View.GONE
            }
            is Resource.Success -> {
                binding.progressBar.visibility = View.GONE
                binding.errorView.visibility = View.GONE
                binding.recyclerviewTeams.visibility = View.VISIBLE
            }
        }
    }

    /**
     * function to show League AutoComplete
     */
    private fun setLeagueAutoComplete(
        searchView: SearchView,
        leagues: List<League>,
        context: Context
    ) {
        val from = arrayOf(SearchManager.SUGGEST_COLUMN_TEXT_1)
        val to = intArrayOf(R.id.searchItemID)
        val cursorAdapter = SimpleCursorAdapter(
            this,
            R.layout.suggestion_item_layout,
            null,
            from,
            to,
            CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        )

        val suggestionsMap = mutableMapOf<String, League>()
        searchView.suggestionsAdapter = cursorAdapter
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?): Boolean {
                val cursor =
                    MatrixCursor(arrayOf(BaseColumns._ID, SearchManager.SUGGEST_COLUMN_TEXT_1))
                newText?.let {
                    leagues.forEachIndexed { index, suggestion ->
                        if (suggestion.strLeague.contains(newText, true)) {
                            cursor.addRow(arrayOf(index, suggestion.strLeague))
                            suggestionsMap[suggestion.strLeague] = suggestion
                        }
                    }
                }
                cursorAdapter.changeCursor(cursor)
                return true
            }

            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }
        })

        var selectedLeague: League? = null
        searchView.setOnSuggestionListener(object : SearchView.OnSuggestionListener {
            @SuppressLint("Range")
            override fun onSuggestionClick(position: Int): Boolean {
                val cursor = searchView.suggestionsAdapter.getItem(position) as Cursor
                val selection =
                    cursor.getString(cursor.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1))
                searchView.setQuery(selection, false)

                selectedLeague = suggestionsMap[selection]
                mainViewModel.teamsData.observe(context as LifecycleOwner) { resource ->
                    displayViewVisibility(resource)
                    if (resource is Resource.Success) {
                        resource.data?.let {
                            setUpListOfAdapter(it.teams, context)
                        }
                    }
                }
                selectedLeague?.let { mainViewModel.getTeamsData(it.idLeague) }
                return true
            }

            override fun onSuggestionSelect(position: Int): Boolean {
                return false
            }
        })

        searchView.setOnCloseListener {
            false
        }
    }

    /**
     * function to set recyclerView Adapter
     */
    private fun setUpListOfAdapter(items: List<Team>, context: Context) {
        val teamsAdapter = TeamsAdapter(displayOneOutOfEveryTwoTeams(items), context)
        binding.recyclerviewTeams.apply {
            setHasFixedSize(true)
            adapter = teamsAdapter
            layoutManager = GridLayoutManager(context, 2)
        }
    }

    /**
     * function to display only 1 team out of 2
     */
    private fun displayOneOutOfEveryTwoTeams(teams: List<Team>): List<Team> {
        teams.sortedByDescending { it.strTeam }
        return teams.filterIndexed { index, _ -> index % 2 == 0 }
    }
}