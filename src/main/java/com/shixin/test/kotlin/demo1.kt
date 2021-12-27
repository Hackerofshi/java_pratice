package com.shixin.demo

import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlin.system.measureTimeMillis

fun main() = runBlocking<Unit> {

    val time = measureTimeMillis {
        //这里快了两倍，因为两个协程并发执行。 请注意，使用协程进行并发总是显式的。
        val one = async { doSomethingUsefulOne()  }
        val two =  async {  doSomethingUsefulTwo()}
        println("The answer is ${one.await() + two.await()}")
    }
    println("Completed in $time ms")
}

private suspend fun doSomethingUsefulOne(): Int {
    delay(1000L) // pretend we are doing something useful here
    return 13
}

private suspend fun doSomethingUsefulTwo(): Int {
    delay(1000L) // pretend we are doing something useful here, too
    return 29
}