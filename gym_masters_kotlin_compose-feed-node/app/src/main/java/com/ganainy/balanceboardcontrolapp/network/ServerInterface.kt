package com.ganainy.balanceboardcontrolapp.network

import com.ganainy.balanceboardcontrolapp.data.Command
import com.ganainy.balanceboardcontrolapp.data.Outfit
import java.net.URL

const val urlRoot: String = "http://192.168.0.12:8000/app/"
val urlGetOutfitList: URL = URL(urlRoot + "getOutfitList")
val urlCommand: URL = URL(urlRoot + "command")

class ServerInterface(private var onResponseListener: OnResponseListener)
{
    fun getOutfitList()
    {
        GetOutfitListRequestTask(onResponseListener).execute()
    }

    fun command(command: Command)
    {
        CommandRequestTask(onResponseListener, command).execute()
    }

    abstract class OnResponseListener
    {
        abstract fun onGetOutfitList(requestResult: RequestResult<ArrayList<Outfit>?>)
        abstract fun onCommand(requestResult: RequestResult<Void?>)
    }
}
