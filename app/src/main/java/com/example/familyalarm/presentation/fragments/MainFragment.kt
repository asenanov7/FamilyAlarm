package com.example.familyalarm.presentation.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.familyalarm.R
import com.example.familyalarm.databinding.MainFragmentBinding
import com.example.familyalarm.domain.entities.UserChild
import com.example.familyalarm.presentation.contract.navigator
import com.example.familyalarm.presentation.recyclerview.UsersAdapter
import com.example.familyalarm.presentation.viewmodels.MainVM
import com.example.familyalarm.utils.UiState
import com.example.familyalarm.utils.collectLifecycleFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class MainFragment : Fragment() {

    private var _binding: MainFragmentBinding? = null
    private val binding: MainFragmentBinding
        get() = _binding ?: throw Exception("MainFragment == null")

    private val vm by lazy { ViewModelProvider(this)[MainVM::class.java] }

    private val adapter by lazy { UsersAdapter() }

    private var isParent by Delegates.notNull<Boolean>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        Log.d("MainFragment", "onCreateView: MainFragment $this")
        _binding = MainFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("MainFragment", "onViewCreated: MainFragment $this")

        getChildsAndSubmitInAdapter()
        updateUIWithUserCondition()
        setRecyclerViewItemSwipeListener()
        bottomNavigationListener()
        binding.recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        Log.d("MainFragment", "onDestroyView: MainFragment $this")
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainFragment", "onDestroy: MainFragment $this")
    }


    private fun getChildsAndSubmitInAdapter(){
        collectLifecycleFlow(vm.stateFlowListUserChild) {
            Log.d("ARSEN", " vm.stateFlow.collectLatest:$it ")
            if (it is UiState.Success) {
                Log.d("ARSEN", "adapter submitted $it ")
                adapter.submitList(it.result)
            }
        }
    }

    //Норм
    private fun exit() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                Log.d("MainFragment", "binding.textViewExit.setOnClickListener")
                vm.logOut(requireContext()).collectLatest {
                    Log.d("MainFragment", "ExitState: $it ")
                    when (it) {
                        UiState.Default -> {
                            binding.progressBarMain.isVisible = false
                        }
                        UiState.Loading -> {
                            binding.progressBarMain.isVisible = true
                        }
                        is UiState.Success -> {
                            navigator().shouldCloseFragment()
                            navigator().shouldLaunchFragment(
                                LoginFragment.newInstance(),
                                LoginFragment.NAME,
                                false
                            )
                        }
                        is UiState.Failure -> Toast.makeText(
                            requireContext(),
                            "Ошибка",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    //Норм
    private fun showAlertDialogForExit() {
        val listener = DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    exit()
                }
                DialogInterface.BUTTON_NEGATIVE -> {
                    dialog.cancel()
                }
            }
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setCancelable(true)
            .setTitle(R.string.exit)
            .setMessage(getString(R.string.youSureWantToExit))
            .setPositiveButton("Да", listener)
            .setNegativeButton("Нет", listener)
            .create()

        dialog.show()
    }

    //Норм
    private fun bottomNavigationListener() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.item_exit -> {
                    showAlertDialogForExit()
                    Log.d("BottomNavigation", "bottomNavigationListener: EXit")
                    true
                }
                R.id.item_alarms -> {
                    Toast.makeText(requireContext(), "Будильники", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.item_groups -> {
                    false
                }
                else -> false
            }
        }
    }

    //Переделать для каждого вида юзера свой UI и фрагмент с вьюмоделью
    private fun updateUIWithUserCondition() {
        lifecycleScope.launch {
            val user = vm.getUserInfo()
            isParent = user !is UserChild
            Log.d("Parent", "isParent: $isParent")
            if (isParent) {
                binding.buttonInvitations.visibility = View.INVISIBLE
                binding.buttonAdd.visibility = View.VISIBLE
                binding.buttonAdd.setOnClickListener {
                    navigator().shouldLaunchFragment(
                        SearchFragment.newInstance(),
                        SearchFragment.NAME,
                        true
                    )
                }

            } else {
                binding.buttonInvitations.visibility = View.VISIBLE
                binding.buttonAdd.visibility = View.INVISIBLE
                binding.buttonInvitations.setOnClickListener {
                    navigator().shouldLaunchFragment(
                        InvitationsFragment.newInstance(),
                        InvitationsFragment.NAME,
                        true
                    )
                }
            }
        }
    }

    //Норм
    private fun setRecyclerViewItemSwipeListener() {

        val callback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapter.currentList[viewHolder.adapterPosition].id?.let {
                    vm.deleteChild(it)
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    companion object {
        const val NAME: String = "MainFragment"

        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }
}

