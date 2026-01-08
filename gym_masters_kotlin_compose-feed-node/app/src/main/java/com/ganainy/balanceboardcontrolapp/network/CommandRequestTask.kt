package com.ganainy.balanceboardcontrolapp.network

import android.os.AsyncTask
import com.ganainy.balanceboardcontrolapp.data.Command
import org.json.JSONException
import org.json.JSONObject
import java.io.DataOutputStream
import java.io.IOException
import java.net.HttpURLConnection

class CommandRequestTask(private val listener: ServerInterface.OnResponseListener, private val command: Command) :
    AsyncTask<Void, Void, RequestResult<Void?>>()
{
    override fun doInBackground(vararg params: Void?): RequestResult<Void?>
    {
        try
        {
            val urlConnection: HttpURLConnection = urlCommand.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "POST"

            val jsonObject = JSONObject()
            jsonObject.put("id", command.id)
            jsonObject.put("type", command.type)
            jsonObject.put("exercise", command.exercise)
            jsonObject.put("level", command.level)
            jsonObject.put("signalPeriod", command.signalPeriod)
            jsonObject.put("changeTime", command.changeTime)

            val dataOutputStream = DataOutputStream(urlConnection.outputStream)
            dataOutputStream.writeBytes(jsonObject.toString())
            dataOutputStream.flush()
            dataOutputStream.close()

            if(urlConnection.responseCode != 200)
                return RequestResult(ResultState.HTTP_ERROR, null)

            return RequestResult(ResultState.SUCCESS, null)

        } catch (e: IOException) {
            return RequestResult(ResultState.NETWORK_ERROR, null)
        } catch (e: JSONException) {
            return RequestResult(ResultState.JSON_ERROR, null)
        }
    }

    override fun onPostExecute(result: RequestResult<Void?>)
    {
        super.onPostExecute(result)
        listener.onCommand(result)
    }
}
