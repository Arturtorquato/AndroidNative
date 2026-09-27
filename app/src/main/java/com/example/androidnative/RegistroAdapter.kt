package com.example.androidnative

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class RegistroAdapter(
    private var registros: List<Registro>,
    private val onRegistroClick: (Registro) -> Unit
) : RecyclerView.Adapter<RegistroAdapter.RegistroViewHolder>() {

    class RegistroViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvTitulo: TextView =
            itemView.findViewById(R.id.tvItemTitulo)

        val tvCategoria: TextView =
            itemView.findViewById(R.id.tvItemCategoria)

        val tvValor: TextView =
            itemView.findViewById(R.id.tvItemValor)

        val tvQuantidade: TextView =
            itemView.findViewById(R.id.tvItemQuantidade)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RegistroViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(
                R.layout.item_registro,
                parent,
                false
            )

        return RegistroViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: RegistroViewHolder,
        position: Int
    ) {

        val registro = registros[position]

        holder.tvTitulo.text =
            registro.titulo

        holder.tvCategoria.text =
            "Categoria: ${registro.categoria}"

        holder.tvValor.text =
            String.format(
                Locale("pt", "BR"),
                "Valor: R$ %.2f",
                registro.valor
            )

        holder.tvQuantidade.text =
            "Quantidade: ${registro.quantidade}"

        holder.itemView.setOnClickListener {
            onRegistroClick(registro)
        }
    }

    override fun getItemCount(): Int {
        return registros.size
    }

    fun atualizarLista(
        novosRegistros: List<Registro>
    ) {
        registros = novosRegistros
        notifyDataSetChanged()
    }
}