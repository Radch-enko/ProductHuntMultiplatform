package com.multi.producthunt.android.screen.timeline

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import com.kuuurt.paging.multiplatform.PagingData
import com.multi.producthunt.android.ui.toTitle
import com.multi.producthunt.domain.usecase.GetStartupsUseCase
import com.multi.producthunt.ui.models.ProjectUI
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class TimelineScreenViewModel(private val useCase: GetStartupsUseCase) :
    StateScreenModel<TimelineScreenViewModel.State>(State.Empty) {

    private val currentDate = Clock.System.now().toLocalDateTime(
        TimeZone.currentSystemDefault()
    ).date

    data class State(
        val title: String = "",
        val isRefreshing: Boolean = false,
        val isPickerVisible: Boolean = false,
        val pagingList: Flow<PagingData<ProjectUI>> = emptyFlow()
    ) {
        companion object {
            val Empty = State()
        }
    }

    sealed class Event {
        class DatePicked(val date: LocalDate) : Event()
        object Refresh : Event()
        object TogglePicker : TimelineScreenViewModel.Event()
    }

    sealed class Effect {
        object ShowPicker : Effect()
    }

    private val mutableEffect = MutableSharedFlow<Effect>()
    val effect = mutableEffect.asSharedFlow()

    init {
        mutableState.update {
            it.copy(
                isRefreshing = true,
                title = currentDate.toTitle()
            )
        }
        loadData()
    }

    fun sendEvent(event: Event) {
        when (event) {
            Event.Refresh -> {
                mutableState.update { it.copy(isRefreshing = true) }
                loadData()
            }
            Event.TogglePicker -> togglePicker()
            is Event.DatePicked -> loadData(event.date)
        }
    }

    private fun togglePicker() = coroutineScope.launch {
        mutableEffect.emit(Effect.ShowPicker)
    }

    private fun loadData(
        date: LocalDate = currentDate
    ) = coroutineScope.launch {
        delay(800)
        mutableState.update {
            it.copy(
                isRefreshing = false,
                title = date.toTitle(),
                pagingList = useCase.getStartupsPagingData(date = date)
            )
        }
    }
}