package com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.ui.edit

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.activity.data.db.contact.ContactModel
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.callback.ICallBackItem
import com.remi.ringtones.audiocutter.ringtonemaker.freeringtone.databinding.ItemContactBinding

class ContactAdapter(val context: Context, val ringtone: String, val callback: ICallBackItem):RecyclerView.Adapter<ContactAdapter.ContactHolder>() {

    private var lstContact = mutableListOf<ContactModel>()

    fun setData(lstContact: MutableList<ContactModel>) {
        this.lstContact = lstContact

        notifyChange()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
        return ContactHolder(ItemContactBinding.inflate(LayoutInflater.from(context), parent, false))
    }

    override fun getItemCount(): Int {
        return if (lstContact.isNotEmpty()) lstContact.size else 0
    }

    override fun onBindViewHolder(holder: ContactHolder, position: Int) {
        holder.onBind(position)
    }

    inner class ContactHolder(binding: ItemContactBinding): RecyclerView.ViewHolder(binding.root) {

        private val binding: ItemContactBinding

        init {
            this.binding = binding
        }

        fun onBind(position: Int) {
            val contact = lstContact[position]

            binding.tvName.text = contact.name
            binding.tvContact.text = contact.contact
            binding.ivTick.visibility = if (contact.ringtone == ringtone) View.VISIBLE else View.GONE

            binding.root.setOnClickListener {
                contact.ringtone = ringtone
                callback.callBack(contact, position)
                notifyChange()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun notifyChange() {
        notifyDataSetChanged()
    }
}