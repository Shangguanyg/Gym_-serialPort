package com.ganainy.balanceboardcontrolapp.network

import android.os.AsyncTask
import com.ganainy.balanceboardcontrolapp.data.Outfit
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection

class GetOutfitListRequestTask(private val listener: ServerInterface.OnResponseListener) :
    AsyncTask<Void, Void, RequestResult<ArrayList<Outfit>?>>()
{
    override fun doInBackground(vararg params: Void?): RequestResult<ArrayList<Outfit>?>
    {
        try
        {
            val urlConnection: HttpURLConnection = urlGetOutfitList.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"

            if(urlConnection.responseCode != 200)
                return RequestResult(ResultState.HTTP_ERROR, null)

            val bufferedReader = BufferedReader(InputStreamReader(urlConnection.inputStream))
            var dataPacket: String?
            val responseStringBuffer = StringBuffer()

            while(true)
            {
                dataPacket = bufferedReader.readLine()
                if (dataPacket == null)
                    break
                responseStringBuffer.append(dataPacket)
            }
            bufferedReader.close()

            return RequestResult(
                ResultState.SUCCESS,
                getDataFromJson(responseStringBuffer.toString())
            )

        } catch(e: IOException) {
            return RequestResult(ResultState.NETWORK_ERROR, null)
        } catch(e: JSONException) {
            return RequestResult(ResultState.JSON_ERROR, null)
        }
    }

    override fun onPostExecute(result: RequestResult<ArrayList<Outfit>?>)
    {
        super.onPostExecute(result)
        listener.onGetOutfitList(result)
    }

    private fun getDataFromJson(json: String): ArrayList<Outfit>
    {
        val jsonObject = JSONObject(json)
        val outfitList = ArrayList<Outfit>()

        for(key in jsonObject.keys())
        {
            val outfitJsonObject = jsonObject.getJSONObject(key)

            outfitList.add(Outfit(
                outfitJsonObject.getInt("id"),
                outfitJsonObject.getString("exercise"),
                outfitJsonObject.getInt("level"),
                outfitJsonObject.getInt("motion"),
                outfitJsonObject.getInt("signalPeriod"),
                outfitJsonObject.getInt("changeTime")
            ))
        }

        return outfitList
    }
}
