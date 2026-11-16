package space.alven.jkutil.timer;

import kotlinx.coroutines.*;
import kotlinx.coroutines.flow.MutableStateFlow;
import kotlinx.coroutines.flow.StateFlow;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

class SecTimer {

    private val tasks = ConcurrentHashMap<Long, Task>();

    private val idGenerator = AtomicLong(0);

    private var timerJob: Job? = null;

    private val scope = CoroutineScope(
        Dispatchers.Default + SupervisorJob()
    );

    fun interface TimerListener {
        fun onTick(value: Int);
    }

    private data class Task(
        val duration: Int,
        val isRaising: Boolean,
        var elapsed: Int,
        val flow: MutableStateFlow<Int>
    );

    fun add(
        seconds: Int,
        isRaising: Boolean = true
    ): StateFlow<Int> {

        val id = idGenerator.incrementAndGet();

        val initialValue =
            if (isRaising) 0
            else seconds;

        val task = Task(
            duration = seconds,
            isRaising = isRaising,
            elapsed = 0,
            flow = MutableStateFlow(initialValue)
        );

        tasks[id] = task;

        startTimer();

        return task.flow;
    }

    @Synchronized
    private fun startTimer() {

        if (timerJob?.isActive == true)
            return;

        timerJob = scope.launch {

            while (tasks.isNotEmpty()) {

                delay(1000);

                val finished = mutableListOf<Long>();
                tasks.forEach { (id, task) ->

                    task.elapsed++;

                    val value =
                        if (task.isRaising) {
                            task.elapsed
                        } else {
                            task.duration - task.elapsed
                        };

                    task.flow.value = value;
                    if (task.elapsed >= task.duration) {
                        finished += id
                    }
                }
                finished.forEach {
                    tasks.remove(it)
                }
            }
            timerJob = null;
        }
    }

    fun stop() {
        timerJob?.cancel();
        timerJob = null;
        tasks.clear();
    }

    @JvmOverloads
    fun add(
        seconds: Int,
        isRaising: Boolean = true,
        listener: TimerListener
    ) {
        val flow = add(seconds, isRaising);
        scope.launch {
            flow.collect {
                listener.onTick(it);
            }
        }
    }
}