package com.halfeaten.vaycray.util.rx

import io.reactivex.rxjava3.core.Scheduler

interface Scheduler {

    fun mainThread() : Scheduler

    fun io() : Scheduler

    fun computation() : Scheduler
}