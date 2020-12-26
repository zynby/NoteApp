package com.shariati.notes.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.shariati.notes.R;
import com.shariati.notes.model.Note;

import java.util.List;

import saman.zamani.persiandate.PersianDate;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
    private Context mContext;
    private List<Note> mNote;

    public NoteAdapter(Context mContext, List<Note> mNote) {
        this.mContext = mContext;
        this.mNote = mNote;
    }

    private OnRecyclerItemClick onRecyclerItemClick;
    private OnRecyclerItemClickDelete OnRecyclerItemClickDelete;


    public interface OnRecyclerItemClick {
        void onClick(Note note);
    }

    public interface OnRecyclerItemClickDelete {
        void onClickDelete(Note note, int pos);}

    public void setOnRecyclerItemClick(OnRecyclerItemClick onRecyclerItemClick) {
        this.onRecyclerItemClick = onRecyclerItemClick;
    }

    public void setOnRecyclerItemClickDelete(OnRecyclerItemClickDelete OnRecyclerItemClickDelete) {
        this.OnRecyclerItemClickDelete = OnRecyclerItemClickDelete;
    }

    @NonNull
    @Override
    public NoteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.note_template, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NoteAdapter.ViewHolder holder, int position) {
        holder.title.setText(mNote.get(position).getTitle());
        holder.note.setText(mNote.get(position).getNote());
        PersianDate date = new PersianDate(mNote.get(position).getCreation_date());
        String time = date.getHour() + ":" + date.getMinute() + "  " + date.getShYear() + "/" + date.getShMonth() + "/" + date.getShDay();
        holder.date.setText(time);
    }

    @Override
    public int getItemCount() {
        return mNote.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView note;
        private TextView date;
        private TextView edit;
        private TextView delete;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            note = itemView.findViewById(R.id.note);
            date = itemView.findViewById(R.id.date);
            edit=itemView.findViewById(R.id.tvEdit);
            delete=itemView.findViewById(R.id.tvDelete);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getNotePosition();
                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getNotePositionDelete();
                }
            });

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getNotePosition();
                }
            });

        }

        private void getNotePosition() {
            if (onRecyclerItemClick != null) {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    onRecyclerItemClick.onClick(mNote.get(pos));
                }
            }
        }

        private void getNotePositionDelete() {
            if (OnRecyclerItemClickDelete != null) {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    OnRecyclerItemClickDelete.onClickDelete(mNote.get(getAdapterPosition()), pos);
                }
            }
        }


    }
}


