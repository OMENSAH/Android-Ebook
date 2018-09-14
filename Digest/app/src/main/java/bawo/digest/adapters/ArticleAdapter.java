package bawo.digest.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import bawo.digest.R;
import bawo.digest.activities.DetailActivity;
import bawo.digest.models.Article;


public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.MyViewHolder>{
    private Context context;
    private ArrayList<Article> articles;
    public ArticleAdapter(Context context, ArrayList<Article> articles){
        this.context = context;
        this.articles = articles;
    }
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.article_list_item, parent, false);
        return new MyViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        Article article = articles.get(position);
        holder.textView.setText(article.getTitle());
        Picasso.get()
                .load(article.getFeaturedImage())
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public MyViewHolder(View itemView, Context ctx) {
            super(itemView);
            context = ctx;
            imageView = itemView.findViewById(R.id.article_image);
            textView = itemView.findViewById(R.id.article_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("clickedArticle", articles.get(getAdapterPosition()));
                    context.startActivity(intent);
                }
            });
        }
    }

}
