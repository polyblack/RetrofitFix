package com.polyblack.retrofitfix.network

import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.withContext
import retrofit2.Retrofit

private typealias Invoke = suspend (method: Method, args: Array<*>) -> Any?

object RetrofitFix {

    @Suppress("UNCHECKED_CAST")
    fun <T> create(retrofit: Retrofit, context: CoroutineContext, service: Class<T>): T {
        val delegate = retrofit.create(service)

        val invoke: Invoke = { method: Method, args: Array<*> ->
            applyDispatcher(context) {
                @Suppress("SpreadOperator")
                method.invoke(delegate, *args)
            }
        }

        val invocationHandler = suspendInvocationHandler(invoke)

        return Proxy.newProxyInstance(
            service.classLoader,
            arrayOf<Class<*>>(service),
            invocationHandler
        ) as T
    }

    private suspend inline fun applyDispatcher(
        context: CoroutineContext,
        crossinline method: () -> Any?
    ) {
        withContext(context) {
            method()
        }
    }

    private fun suspendInvocationHandler(block: Invoke): InvocationHandler {
        return InvocationHandler { _, method, args ->
            val cont = args?.lastOrNull() as? Continuation<*>
            if (cont == null) {
                /* TBD
                Нерабочее решение:
                runBlocking {
                    block(proxy, method, methodArgs)
                } */
                throw RuntimeException("Не используйте RetrofitFix при вызове неsuspend функций")
            } else {
                @Suppress("UNCHECKED_CAST")
                val suspendInvoker = block as (Method, Array<*>?, Continuation<*>) -> Any?
                suspendInvoker(method, args, cont)
            }
        }
    }
}
