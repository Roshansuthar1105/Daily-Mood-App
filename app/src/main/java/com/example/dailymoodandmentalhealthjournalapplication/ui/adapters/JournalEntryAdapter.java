package com.example.dailymoodandmentalhealthjournalapplication.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.dailymoodandmentalhealthjournalapplication.R;
import com.example.dailymoodandmentalhealthjournalapplication.data.entity.JournalEntry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Adapter for displaying journal entries in a RecyclerView.
 */
public class JournalEntryAdapter extends ListAdapter<JournalEntry, JournalEntryAdapter.JournalEntryViewHolder> {
    private final OnJournalEntryClickListener clickListener;
    private final OnFavoriteToggleListener favoriteListener;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());

    public interface OnJournalEntryClickListener {
        void onJournalEntryClick(JournalEntry journalEntry);
    }

    public interface OnFavoriteToggleListener {
        void onFavoriteToggle(JournalEntry journalEntry, boolean isFavorite);
    }

    public JournalEntryAdapter(OnJournalEntryClickListener clickListener, OnFavoriteToggleListener favoriteListener) {
        super(DIFF_CALLBACK);
        this.clickListener = clickListener;
        this.favoriteListener = favoriteListener;
    }

    private static final DiffUtil.ItemCallback<JournalEntry> DIFF_CALLBACK = new DiffUtil.ItemCallback<JournalEntry>() {
        @Override
        public boolean areItemsTheSame(@NonNull JournalEntry oldItem, @NonNull JournalEntry newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull JournalEntry oldItem, @NonNull JournalEntry newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                   oldItem.getContent().equals(newItem.getContent()) &&
                   oldItem.getDate() == newItem.getDate() &&
                   oldItem.isFavorite() == newItem.isFavorite();
        }
    };

    @NonNull
    @Override
    public JournalEntryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_journal_entry, parent, false);
        return new JournalEntryViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalEntryViewHolder holder, int position) {
        JournalEntry journalEntry = getItem(position);
        holder.bind(journalEntry, clickListener, favoriteListener);
    }

    static class JournalEntryViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTitle;
        private final TextView textViewDate;
        private final TextView textViewContent;
        private final TextView textViewTags;
        private final ImageButton buttonFavorite;

        public JournalEntryViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewContent = itemView.findViewById(R.id.textViewContent);
            textViewTags = itemView.findViewById(R.id.textViewTags);
            buttonFavorite = itemView.findViewById(R.id.buttonFavorite);
        }

        public void bind(JournalEntry journalEntry, OnJournalEntryClickListener clickListener, OnFavoriteToggleListener favoriteListener) {
            textViewTitle.setText(journalEntry.getTitle());
            textViewDate.setText(DATE_FORMAT.format(new Date(journalEntry.getDate())));
            
            // Truncate content if it's too long
            String content = journalEntry.getContent();
            if (content.length() > 100) {
                content = content.substring(0, 97) + "...";
            }
            textViewContent.setText(content);
            
            // Set tags if available
            if (journalEntry.getTags() != null && !journalEntry.getTags().isEmpty()) {
                textViewTags.setText(journalEntry.getTags());
                textViewTags.setVisibility(View.VISIBLE);
            } else {
                textViewTags.setVisibility(View.GONE);
            }
            
            // Set favorite button state
            buttonFavorite.setImageResource(journalEntry.isFavorite() ? 
                    R.drawable.ic_favorite : R.drawable.ic_favorite_border);
            
            // Set click listeners
            itemView.setOnClickListener(v -> clickListener.onJournalEntryClick(journalEntry));
            
            buttonFavorite.setOnClickListener(v -> {
                boolean newFavoriteState = !journalEntry.isFavorite();
                buttonFavorite.setImageResource(newFavoriteState ? 
                        R.drawable.ic_favorite : R.drawable.ic_favorite_border);
                favoriteListener.onFavoriteToggle(journalEntry, newFavoriteState);
            });
        }
    }
}
