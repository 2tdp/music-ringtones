package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.sharepref

import android.content.Context
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.db.MusicEntity
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONException
import java.util.ArrayList

class DataLocalManager {
    private var mySharedPreferences: MySharePreferences? = null

    companion object {
        private var instance: DataLocalManager? = null
        fun init(context: Context) {
            instance = DataLocalManager()
            instance!!.mySharedPreferences = MySharePreferences(context)
        }

        private fun getInstance(): DataLocalManager? {
            if (instance == null) instance = DataLocalManager()
            return instance
        }

        fun setFirstInstall(key: String?, isFirst: Boolean) {
            getInstance()!!.mySharedPreferences!!.putBooleanValue(key, isFirst)
        }

        fun getFirstInstall(key: String?): Boolean {
            return getInstance()!!.mySharedPreferences!!.getBooleanValue(key)
        }

        fun setCheck(key: String?, volumeOn: Boolean) {
            getInstance()!!.mySharedPreferences!!.putBooleanValue(key, volumeOn)
        }

        fun getCheck(key: String?): Boolean {
            return getInstance()!!.mySharedPreferences!!.getBooleanValue(key)
        }

        fun setOption(option: String?, key: String?) {
            getInstance()!!.mySharedPreferences!!.putStringWithKey(key, option)
        }

        fun getOption(key: String?): String? {
            return getInstance()!!.mySharedPreferences!!.getStringWithKey(key, "")
        }

        fun setInt(count: Int, key: String?) {
            getInstance()!!.mySharedPreferences!!.putIntWithKey(key, count)
        }

        fun getInt(key: String?): Int {
            return getInstance()!!.mySharedPreferences!!.getIntWithKey(key, -1)
        }

        fun setLong(count: Long, key: String?) {
            getInstance()!!.mySharedPreferences!!.putLongWithKey(key, count)
        }

        fun getLong(key: String?): Long {
            return getInstance()!!.mySharedPreferences!!.getLongWithKey(key, -1L)
        }

        fun setListTimeStamp(key: String?, lstTimeStamp: ArrayList<String?>?) {
            val gson = Gson()
            val jsonArray = gson.toJsonTree(lstTimeStamp).asJsonArray
            val json = jsonArray.toString()
            getInstance()!!.mySharedPreferences!!.putStringWithKey(key, json)
        }

        fun getListTimeStamp(key: String?): ArrayList<String> {
            val lstTimeStamp = ArrayList<String>()
            val strJson = getInstance()!!.mySharedPreferences!!.getStringWithKey(key, "")
            try {
                val jsonArray = JSONArray(strJson)
                for (i in 0 until jsonArray.length()) {
                    lstTimeStamp.add(jsonArray[i].toString())
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return lstTimeStamp
        }

        fun setListFavorite(lstBorder: MutableList<MusicEntity>, key: String) {
            getInstance()!!.mySharedPreferences!!.putStringWithKey(key, Gson().toJsonTree(lstBorder).asJsonArray.toString())
        }

        fun getListFavorite(key: String): MutableList<MusicEntity> {
            val lstBorder = mutableListOf<MusicEntity>()
            try {
                val jsonArray = JSONArray(getInstance()!!.mySharedPreferences!!.getStringWithKey(key, ""))
                for (i in 0 until jsonArray.length()) {
                    lstBorder.add(Gson().fromJson(jsonArray.getJSONObject(i).toString(), MusicEntity::class.java))
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return lstBorder
        }

//        fun setListBucket(lstBucket: ArrayList<BucketPicModel>, key: String) {
//            val gson = Gson()
//            val jsonArray = gson.toJsonTree(lstBucket).asJsonArray
//            val json = jsonArray.toString()
//            getInstance()!!.mySharedPreferences!!.putStringWithKey(key, json)
//        }
//
//        fun getListBucket(key: String): ArrayList<BucketPicModel> {
//            val gson = Gson()
//            var jsonObject: JSONObject
//            val lstBucket: ArrayList<BucketPicModel> = ArrayList()
//            val strJson = getInstance()!!.mySharedPreferences!!.getStringWithKey(key, "")
//            try {
//                val jsonArray = JSONArray(strJson)
//                for (i in 0 until jsonArray.length()) {
//                    jsonObject = jsonArray.getJSONObject(i)
//                    lstBucket.add(gson.fromJson(jsonObject.toString(), BucketPicModel::class.java))
//                }
//            } catch (e: JSONException) {
//                e.printStackTrace()
//            }
//            return lstBucket
//        }
    }
}