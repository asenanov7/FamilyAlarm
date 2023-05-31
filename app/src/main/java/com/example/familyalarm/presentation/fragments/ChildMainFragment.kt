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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.chesire.lifecyklelog.LogLifecykle
import com.example.familyalarm.R
import com.example.familyalarm.databinding.ChildMainFragmentBinding
import com.example.familyalarm.domain.entities.UserChild
import com.example.familyalarm.presentation.contract.navigator
import com.example.familyalarm.presentation.recyclerview.UsersAdapter
import com.example.familyalarm.presentation.viewmodels.MainChildVM
import com.example.familyalarm.presentation.viewmodels.MainVM
import com.example.familyalarm.utils.UiState
import com.example.familyalarm.utils.uiLifeCycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

@LogLifecykle
class ChildMainFragment : Fragment() {

    private var _binding: ChildMainFragmentBinding? = null
    private val binding: ChildMainFragmentBinding
        get() = _binding ?: throw Exception("ChildMainFragment == null")

    private val vm by lazy { ViewModelProvider(this) [MainChildVM::class.java] }

    private val adapter by lazy { UsersAdapter() }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = ChildMainFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewLifecycleOwner.lifecycleScope.launch {
            getChildsAndSubmitInAdapter()
            launchInvitationsFragment()
            bottomNavigationListener()
            binding.recyclerView.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    private suspend fun getChildsAndSubmitInAdapter() {
        uiLifeCycleScope {
            vm.getChilds().collectLatest {
                if (it is UiState.Success<List<UserChild>>) {
                    Log.d("ARSEN", "result ${it.result}")
                    adapter.submitList(it.result)
                }
            }
        }
    }


    //Норм
    private suspend fun exit() {
        uiLifeCycleScope{
            vm.logOut(requireContext()).collectLatest {
                Log.d("ChildMainFragment", "ExitState: $it ")
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


    //Норм
    private fun showAlertDialogForExit() {
        val listener = DialogInterface.OnClickListener { dialog, which ->
            when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    viewLifecycleOwner.lifecycleScope.launch {
                        exit()
                    }
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
    private fun launchInvitationsFragment() {
        uiLifeCycleScope {
            binding.buttonInvitations.setOnClickListener {
                navigator().shouldLaunchFragment(
                    InvitationsFragment.newInstance(),
                    InvitationsFragment.NAME,
                    true
                )
            }
        }
    }

    companion object {
        const val NAME: String = "ChildMainFragment"

        fun newInstance(): ChildMainFragment {
            return ChildMainFragment()
        }
    }
}

