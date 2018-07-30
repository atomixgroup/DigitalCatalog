package ir.codetower.samanshiri.Adapters;

import android.content.Intent;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import ir.codetower.samanshiri.Activities.ProductActivity;
import ir.codetower.samanshiri.App;
import ir.codetower.samanshiri.Models.CartItem;
import ir.codetower.samanshiri.R;


public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private ArrayList<CartItem> cartItems;
    private OnIncDecListener onIncDecListener;

    public CartAdapter(ArrayList<CartItem> cartItems, OnIncDecListener onIncDecListener) {
        this.cartItems = cartItems;
        this.onIncDecListener = onIncDecListener;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(App.context).inflate(R.layout.cart_adapter, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartViewHolder holder, int position) {
        holder.bind(cartItems.get(position));
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private AppCompatImageView item_inc, item_dec, item_image,item_delete;
        private AppCompatTextView item_title, item_price;
        private AppCompatEditText item_count;

        public CartViewHolder(View itemView) {
            super(itemView);
            item_dec = (AppCompatImageView) itemView.findViewById(R.id.item_dec);
            item_inc = (AppCompatImageView) itemView.findViewById(R.id.item_inc);
            item_image = (AppCompatImageView) itemView.findViewById(R.id.item_image);
            item_delete = (AppCompatImageView) itemView.findViewById(R.id.item_delete);
            item_title = (AppCompatTextView) itemView.findViewById(R.id.item_title);
            item_price = (AppCompatTextView) itemView.findViewById(R.id.item_price);
            item_count = (AppCompatEditText) itemView.findViewById(R.id.item_count);

            item_dec.setOnClickListener(this);
            item_inc.setOnClickListener(this);
            item_image.setOnClickListener(this);
            item_delete.setOnClickListener(this);

        }

        public void bind(CartItem cartItem) {
            item_count.setText(cartItem.getCount() + "");
            item_title.setText(cartItem.getTitle());
            item_price.setText(cartItem.getPrice() * cartItem.getCount() + " ریال ");

        }

        @Override
        public void onClick(View v) {
            int id = v.getId();
            int p_count = Integer.parseInt(item_count.getText() + "");
            CartItem cartItem = cartItems.get(getLayoutPosition());
            switch (id) {
                case R.id.item_dec:
                    if (p_count != 0) {
                        p_count--;
                        item_count.setText(p_count + "");

                        cartItem.setCount(p_count);
                        item_price.setText(p_count*cartItem.getPrice()+" ریال ");
                        App.dbHelper.insert(cartItem);
//                        onIncDecListener.onChange();
                    }
                    break;
                case R.id.item_inc:
                    p_count++;
                    item_count.setText(p_count + "");

                    cartItem.setCount(p_count);
                    item_price.setText(p_count*cartItem.getPrice()+" ریال ");

                    App.dbHelper.insert(cartItem);
//                    onIncDecListener.onChange();

                    break;
                case R.id.item_image:
                    Intent intent=new Intent(App.context, ProductActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("id",cartItems.get(getLayoutPosition()).getpId());
                    App.context.startActivity(intent);
                    break;
                case R.id.item_delete:
                    cartItems.remove(getLayoutPosition());
                    notifyItemRemoved(getLayoutPosition());
                    App.dbHelper.delete(App.dbHelper.cart_table,"id",cartItem.getId()+"");
                    App.updateCart();
                    break;
            }
            onIncDecListener.onChange();
        }
    }

    public interface OnIncDecListener {
        public void onChange();
    }
}
