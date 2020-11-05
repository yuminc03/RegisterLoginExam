package kr.hs.emirim.flowerbeen.byeruss.Features.Room.ShowRoomList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.recyclerview.widget.RecyclerView;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import java.util.List;

import kr.hs.emirim.flowerbeen.byeruss.Database.MySQLiteOpenHelper;
import kr.hs.emirim.flowerbeen.byeruss.Features.Room.CreateRoom.RoomItem;
import kr.hs.emirim.flowerbeen.byeruss.R;
import kr.hs.emirim.flowerbeen.byeruss.Util.Config;

public class RoomListRecyclerViewAdapter extends RecyclerView.Adapter<CustomViewHolder> {

    private Context context;
    private List<RoomItem> roomList;
    private MySQLiteOpenHelper mySQLiteOpenHelper;

    public RoomListRecyclerViewAdapter(Context context, List<RoomItem> roomList) {
        this.context = context;
        this.roomList = roomList;
        mySQLiteOpenHelper = new MySQLiteOpenHelper(context);
        Logger.addLogAdapter(new AndroidLogAdapter());
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_room, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        final int itemPosition = position;
        final RoomItem roomItem = roomList.get(position);

        holder.nameTextView.setText(roomItem.getName());
        holder.registrationNumTextView.setText(roomItem.getRoomName());
        holder.emailTextView.setText(roomItem.getRoomTime());
        holder.phoneTextView.setText(roomItem.getRoomPlace());

        holder.crossButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder.setMessage("Are you sure, You wanted to delete this student?");
                alertDialogBuilder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                deleteStudent(itemPosition);
                            }
                        });

                alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        holder.editButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StudentUpdateDialogFragment studentUpdateDialogFragment = StudentUpdateDialogFragment.newInstance(roomItem.getRegistrationNumber(), itemPosition, new StudentUpdateListener() {
                    @Override
                    public void onStudentInfoUpdated(RoomItem roomItem, int position) {
                        studentList.set(position, roomItem);
                        notifyDataSetChanged();
                    }
                });
                studentUpdateDialogFragment.show(((StudentListActivity) context).getSupportFragmentManager(), Config.UPDATE_STUDENT);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SubjectListActivity.class);
                intent.putExtra(Config.STUDENT_REGISTRATION, student.getRegistrationNumber());
                context.startActivity(intent);
            }
        });
    }

    private void deleteStudent(int position) {
        RoomItem roomItem = studentList.get(position);
        long count = databaseQueryClass.deleteStudentByRegNum(student.getRegistrationNumber());

        if(count>0){
            studentList.remove(position);
            notifyDataSetChanged();
            ((StudentListActivity) context).viewVisibility();
            Toast.makeText(context, "Student deleted successfully", Toast.LENGTH_LONG).show();
        } else
            Toast.makeText(context, "Student not deleted. Something wrong!", Toast.LENGTH_LONG).show();

    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }
}
