package br.com.digitalhouse.firepizzaapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.com.digitalhouse.firepizzaapp.R;
import br.com.digitalhouse.firepizzaapp.model.Pizza;

public class PizzaAdapter extends RecyclerView.Adapter<PizzaAdapter.ViewHolder> {
    private List<Pizza> pizzaList = new ArrayList<>();

    public void atualizarPizzas(List<Pizza> pizzaList){

        this.pizzaList = pizzaList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pizza_celula,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pizza pizza = pizzaList.get(position);
        holder.setupPizza(pizza);
    }

    @Override
    public int getItemCount() {
        return pizzaList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView descricaoTextview;
        private TextView precoTextview;
        private TextView entregueTextview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            descricaoTextview = itemView.findViewById(R.id.descricao_text_view_id);
            precoTextview = itemView.findViewById(R.id.preco_text_view_id);
            entregueTextview = itemView.findViewById(R.id.entregue_text_view_id);
        }

        public void setupPizza(Pizza pizza) {
            descricaoTextview.setText(pizza.getDescricao());
            precoTextview.setText("R$"+pizza.getPreco());
            entregueTextview.setText(pizza.isEntregue() ? "Entregue" : "Aguarde");

        }
    }
}
