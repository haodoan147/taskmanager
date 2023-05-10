package com.example.task_management.activity.task;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task_management.R;
import com.example.task_management.adapter.ItemAdapter;
import com.example.task_management.model.Category;
import com.example.task_management.model.PaginationTask;
import com.example.task_management.model.Task;
import com.example.task_management.service.APIService;
import com.example.task_management.utils.RetrofitClient;
import com.woxthebox.draglistview.BoardView;
import com.woxthebox.draglistview.ColumnProperties;
import com.woxthebox.draglistview.DragItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewTaskFragment extends Fragment {

    private static int sCreatedItems = 0;
    private BoardView mBoardView;
    private int mColumns;
    private boolean mGridLayout;
    private APIService apiService;
    ArrayList<Pair<Long, Task>> mItemArray = new ArrayList<>();
    List<Task> taskList= new ArrayList<>();
    List<Category> listCategory = new ArrayList<>();


    public static NewTaskFragment newInstance() {
        return new NewTaskFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        taskList = (List<Task>) getArguments().getSerializable("newTaskList");
        for (int i = 0; i < taskList.size(); i++) {
            long id = sCreatedItems++;
            mItemArray.add(new Pair<>(id, taskList.get(i)));
        }
        listCategory  = (List<Category>) getArguments().getSerializable("newCateList");
        View view = inflater.inflate(R.layout.board_layout, container, false);
        mBoardView = view.findViewById(R.id.board_view);
        mBoardView.setSnapToColumnsWhenScrolling(true);
        mBoardView.setSnapToColumnWhenDragging(true);
        mBoardView.setSnapDragItemToTouch(true);
        mBoardView.setSnapToColumnInLandscape(false);
        mBoardView.setColumnSnapPosition(BoardView.ColumnSnapPosition.CENTER);
        mBoardView.setBoardListener(new BoardView.BoardListener() {
            @Override
            public void onItemDragStarted(int column, int row) {
                //Toast.makeText(getContext(), "Start - column: " + column + " row: " + row, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemDragEnded(int fromColumn, int fromRow, int toColumn, int toRow) {
                if (fromColumn != toColumn || fromRow != toRow) {
                    //Toast.makeText(getContext(), "End - column: " + toColumn + " row: " + toRow, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onItemChangedPosition(int oldColumn, int oldRow, int newColumn, int newRow) {
                //Toast.makeText(mBoardView.getContext(), "Position changed - column: " + newColumn + " row: " + newRow, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onItemChangedColumn(int oldColumn, int newColumn) {
                TextView itemCount1 = mBoardView.getHeaderView(oldColumn).findViewById(R.id.item_count);
                itemCount1.setText(String.valueOf(mBoardView.getAdapter(oldColumn).getItemCount()));
                TextView itemCount2 = mBoardView.getHeaderView(newColumn).findViewById(R.id.item_count);
                itemCount2.setText(String.valueOf(mBoardView.getAdapter(newColumn).getItemCount()));
            }

            @Override
            public void onFocusedColumnChanged(int oldColumn, int newColumn) {
                //Toast.makeText(getContext(), "Focused column changed from " + oldColumn + " to " + newColumn, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onColumnDragStarted(int position) {
                //Toast.makeText(getContext(), "Column drag started from " + position, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onColumnDragChangedPosition(int oldPosition, int newPosition) {
                //Toast.makeText(getContext(), "Column changed from " + oldPosition + " to " + newPosition, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onColumnDragEnded(int fromPosition, int toPosition) {
                //Toast.makeText(getContext(), "Column drag ended at " + position, Toast.LENGTH_SHORT).show();
            }
        });
        resetBoard();
        return view;
    }

    private void resetBoard() {
        mBoardView.clearBoard();
        mBoardView.setCustomDragItem(new MyDragItem(getActivity(), R.layout.column_item));
        mBoardView.setCustomColumnDragItem( new MyColumnDragItem(getActivity(), R.layout.column_drag_layout));
        addColumn("TODO");
        addColumn("IN_PROGRESS");
        addColumn("DONE");
        addColumn("POSTPONED");
        addColumn("CANCELED");
    }

    private void addColumn(String status) {
        ArrayList<Pair<Long, Task>> newMItemArray = new ArrayList<>();
        for (Pair<Long, Task> task: mItemArray) {
            if(task.second.getStatus().equals(status)){
                newMItemArray.add(task);
            }
        }
        final ItemAdapter listAdapter = new ItemAdapter(newMItemArray, R.layout.column_item, R.id.item_layout, true,getActivity(),listCategory);
        final View header = View.inflate(getActivity(), R.layout.column_header, null);
        ((TextView) header.findViewById(R.id.text)).setText(status);
        ((TextView) header.findViewById(R.id.item_count)).setText("" + newMItemArray.size());
        LinearLayoutManager layoutManager = mGridLayout ? new GridLayoutManager(getContext(), 4) : new LinearLayoutManager(getContext());
        ColumnProperties columnProperties = ColumnProperties.Builder.newBuilder(listAdapter)
                .setLayoutManager(layoutManager)
                .setHasFixedItemSize(false)
                .setColumnBackgroundColor(Color.TRANSPARENT)
                .setItemsSectionBackgroundColor(Color.TRANSPARENT)
                .setHeader(header)
                .setColumnDragView(header)
                .build();

        mBoardView.addColumn(columnProperties);
        mColumns++;
    }

    private static class MyColumnDragItem extends DragItem {

        MyColumnDragItem(Context context, int layoutId) {
            super(context, layoutId);
            setSnapToTouch(false);
        }

        @Override
        public void onBindDragView(View clickedView, View dragView) {
            LinearLayout clickedLayout = (LinearLayout) clickedView;
            View clickedHeader = clickedLayout.getChildAt(0);
            RecyclerView clickedRecyclerView = (RecyclerView) clickedLayout.getChildAt(1);

            View dragHeader = dragView.findViewById(R.id.drag_header);
            ScrollView dragScrollView = dragView.findViewById(R.id.drag_scroll_view);
            LinearLayout dragLayout = dragView.findViewById(R.id.drag_list);

            Drawable clickedColumnBackground = clickedLayout.getBackground();
            if (clickedColumnBackground != null) {
                ViewCompat.setBackground(dragView, clickedColumnBackground);
            }

            Drawable clickedRecyclerBackground = clickedRecyclerView.getBackground();
            if (clickedRecyclerBackground != null) {
                ViewCompat.setBackground(dragLayout, clickedRecyclerBackground);
            }

            dragLayout.removeAllViews();

            ((TextView) dragHeader.findViewById(R.id.text)).setText(((TextView) clickedHeader.findViewById(R.id.text)).getText());
            ((TextView) dragHeader.findViewById(R.id.item_count)).setText(((TextView) clickedHeader.findViewById(R.id.item_count)).getText());

            for (int i = 0; i < clickedRecyclerView.getChildCount(); i++) {
                View view = View.inflate(dragView.getContext(), R.layout.column_item, null);
                ((TextView) view.findViewById(R.id.tv_date)).setText(((TextView) clickedRecyclerView.getChildAt(i).findViewById(R.id.tv_date)).getText());
                ((TextView) view.findViewById(R.id.tv_month)).setText(((TextView) clickedRecyclerView.getChildAt(i).findViewById(R.id.tv_month)).getText());
                ((TextView) view.findViewById(R.id.tv_title)).setText(((TextView) clickedRecyclerView.getChildAt(i).findViewById(R.id.tv_title)).getText());
                ((TextView) view.findViewById(R.id.tv_des)).setText(((TextView) clickedRecyclerView.getChildAt(i).findViewById(R.id.tv_des)).getText());
                ((TextView) view.findViewById(R.id.tv_duration)).setText(((TextView) clickedRecyclerView.getChildAt(i).findViewById(R.id.tv_duration)).getText());
                ((TextView) view.findViewById(R.id.tv_category)).setText(((TextView) clickedRecyclerView.getChildAt(i).findViewById(R.id.tv_category)).getText());
                ((TextView) view.findViewById(R.id.tv_label)).setText(((TextView) clickedRecyclerView.getChildAt(i).findViewById(R.id.tv_label)).getText());
                ((TextView) view.findViewById(R.id.tv_priority)).setText(((TextView) clickedRecyclerView.getChildAt(i).findViewById(R.id.tv_priority)).getText());
                ((TextView) view.findViewById(R.id.tv_status)).setText(((TextView) clickedRecyclerView.getChildAt(i).findViewById(R.id.tv_status)).getText());
                dragLayout.addView(view);
                if (i == 0) {
                    dragScrollView.setScrollY(-clickedRecyclerView.getChildAt(i).getTop());
                }
            }

            dragView.setPivotY(0);
            dragView.setPivotX(clickedView.getMeasuredWidth() / 2);
        }

        @Override
        public void onStartDragAnimation(View dragView) {
            super.onStartDragAnimation(dragView);
            dragView.animate().scaleX(0.9f).scaleY(0.9f).start();
        }

        @Override
        public void onEndDragAnimation(View dragView) {
            super.onEndDragAnimation(dragView);
            dragView.animate().scaleX(1).scaleY(1).start();
        }
    }

    private static class MyDragItem extends DragItem {

        MyDragItem(Context context, int layoutId) {
            super(context, layoutId);
        }

        @Override
        public void onBindDragView(View clickedView, View dragView) {
            CharSequence tv_date = ((TextView) clickedView.findViewById(R.id.tv_date)).getText();
            CharSequence tv_month = ((TextView) clickedView.findViewById(R.id.tv_month)).getText();
            CharSequence tv_title = ((TextView) clickedView.findViewById(R.id.tv_title)).getText();
            CharSequence tv_des = ((TextView) clickedView.findViewById(R.id.tv_des)).getText();
            CharSequence tv_duration = ((TextView) clickedView.findViewById(R.id.tv_duration)).getText();
            CharSequence tv_category = ((TextView) clickedView.findViewById(R.id.tv_category)).getText();
            CharSequence tv_label = ((TextView) clickedView.findViewById(R.id.tv_label)).getText();
            CharSequence tv_priority = ((TextView) clickedView.findViewById(R.id.tv_priority)).getText();
            CharSequence tv_status = ((TextView) clickedView.findViewById(R.id.tv_status)).getText();

            ((TextView) dragView.findViewById(R.id.tv_date)).setText(tv_date);
            ((TextView) dragView.findViewById(R.id.tv_month)).setText(tv_month);
            ((TextView) dragView.findViewById(R.id.tv_title)).setText(tv_title);
            ((TextView) dragView.findViewById(R.id.tv_des)).setText(tv_des);
            ((TextView) dragView.findViewById(R.id.tv_duration)).setText(tv_duration);
            ((TextView) dragView.findViewById(R.id.tv_category)).setText(tv_category);
            ((TextView) dragView.findViewById(R.id.tv_label)).setText(tv_label);
            ((TextView) dragView.findViewById(R.id.tv_priority)).setText(tv_priority);
            ((TextView) dragView.findViewById(R.id.tv_status)).setText(tv_status);
            CardView dragCard = dragView.findViewById(R.id.card);
            CardView clickedCard = clickedView.findViewById(R.id.card);
            dragCard.setMaxCardElevation(40);
            dragCard.setCardElevation(clickedCard.getCardElevation());
        }
        @Override
        public void onMeasureDragView(View clickedView, View dragView) {
            CardView dragCard = dragView.findViewById(R.id.card);
            CardView clickedCard = clickedView.findViewById(R.id.card);
            int widthDiff = dragCard.getPaddingLeft() - clickedCard.getPaddingLeft() + dragCard.getPaddingRight() -
                    clickedCard.getPaddingRight();
            int heightDiff = dragCard.getPaddingTop() - clickedCard.getPaddingTop() + dragCard.getPaddingBottom() -
                    clickedCard.getPaddingBottom();
            int width = clickedView.getMeasuredWidth() + widthDiff;
            int height = clickedView.getMeasuredHeight() + heightDiff;
            dragView.setLayoutParams(new FrameLayout.LayoutParams(width, height));

            int widthSpec = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
            dragView.measure(widthSpec, heightSpec);
        }

        @Override
        public void onStartDragAnimation(View dragView) {
            CardView dragCard = dragView.findViewById(R.id.card);
            ObjectAnimator anim = ObjectAnimator.ofFloat(dragCard, "CardElevation", dragCard.getCardElevation(), 40);
            anim.setInterpolator(new DecelerateInterpolator());
            anim.setDuration(ANIMATION_DURATION);
            anim.start();
        }

        @Override
        public void onEndDragAnimation(View dragView) {
            CardView dragCard = dragView.findViewById(R.id.card);
            ObjectAnimator anim = ObjectAnimator.ofFloat(dragCard, "CardElevation", dragCard.getCardElevation(), 6);
            anim.setInterpolator(new DecelerateInterpolator());
            anim.setDuration(ANIMATION_DURATION);
            anim.start();

        }
    }
}
