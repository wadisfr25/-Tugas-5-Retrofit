package com.example.pasienapi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pasienapi.R
import com.example.pasienapi.databinding.ItemPatientBinding
import com.example.pasienapi.model.Patient

class PatientAdapter : ListAdapter<Patient, PatientAdapter.PatientViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        val binding = ItemPatientBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PatientViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PatientViewHolder(
        private val binding: ItemPatientBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(patient: Patient) {
            val context = binding.root.context
            binding.nameTextView.text = patient.nama
            binding.birthDateTextView.text =
                context.getString(R.string.patient_field_value, context.getString(R.string.label_birth_date), patient.tanggalLahir)
            binding.genderTextView.text =
                context.getString(
                    R.string.patient_field_value,
                    context.getString(R.string.label_gender),
                    if (patient.jenisKelamin == "L") {
                        context.getString(R.string.male)
                    } else {
                        context.getString(R.string.female)
                    }
                )
            binding.addressTextView.text =
                context.getString(R.string.patient_field_value, context.getString(R.string.label_address), patient.alamat)
            binding.phoneTextView.text =
                context.getString(R.string.patient_field_value, context.getString(R.string.label_phone), patient.noTelepon)
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Patient>() {
        override fun areItemsTheSame(oldItem: Patient, newItem: Patient): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Patient, newItem: Patient): Boolean {
            return oldItem == newItem
        }
    }
}
