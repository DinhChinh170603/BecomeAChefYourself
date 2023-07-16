package com.example.test.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.test.R;
import com.example.test.activities.MainActivity;
import com.example.test.components.User;
import com.example.test.database.DatabaseHelper;

public class UserFragment extends Fragment implements PopupMenu.OnMenuItemClickListener {

    private View fragmentView;

    private User profileUser;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        UserViewModel userViewModel =
//                new ViewModelProvider(this).get(UserViewModel.class);
//
//        binding = FragmentUserBinding.inflate(inflater, container, false);
//        View root = binding.getRoot();
//
//        final TextView textView = binding.textUser;
//        userViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
//        return root;


        fragmentView = inflater.inflate(R.layout.fragment_user, container, false);

//        Button logoutBtn = view.findViewById(R.id.logout_button);
//        logoutBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                builder.setTitle("Logout");
//                builder.setMessage("Are you sure you want to log out?");
//                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // Perform logout action here
//                        MainActivity.loggedInUser = null;
//                        Navigation.findNavController(view).navigate(R.id.navigation_login);
//                    }
//                });
//                builder.setNegativeButton("No", null);
//                AlertDialog dialog = builder.create();
//                dialog.show();
//            }
//        });

        return fragmentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            String username = args.getString("username");
            DatabaseHelper dbHelper = new DatabaseHelper(getActivity());
            profileUser = dbHelper.getUserWithUsername(username);
        } else {
            profileUser = MainActivity.loggedInUser;
        }

        TextView usernameTv = view.findViewById(R.id.user_username);
        EditText fullnameTv = view.findViewById(R.id.user_fullname);
        TextView bioTv = view.findViewById(R.id.user_bio);
        RelativeLayout followBtn = view.findViewById(R.id.follow_btn);
        ImageView popupMenu = view.findViewById(R.id.user_menu);

        usernameTv.setText(profileUser.getUsername());
        fullnameTv.setText(profileUser.getFullname());
        bioTv.setText(profileUser.getBio());

        if (MainActivity.loggedInUser != null && MainActivity.loggedInUser.getUsername().equals(profileUser.getUsername())) {
            popupMenu(popupMenu);
            followBtn.setVisibility(View.INVISIBLE);
        } else {
            popupMenu.setVisibility(View.INVISIBLE);
            followBtn.setVisibility(View.VISIBLE);
            DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
            if (MainActivity.loggedInUser == null || !databaseHelper.isFollowing(MainActivity.loggedInUser.getUsername(), profileUser.getUsername())) {
                View follow_frame = view.findViewById(R.id.follow_frame);
                follow_frame.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.follow_frame, null));
                TextView follow_text = view.findViewById(R.id.follow_text);
                follow_text.setText("Follow");
            } else {
                View follow_frame = view.findViewById(R.id.follow_frame);
                follow_frame.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.following_frame, null));
                TextView follow_text = view.findViewById(R.id.follow_text);
                follow_text.setText("Following");
            }

            followBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (MainActivity.loggedInUser == null) {
                        Toast.makeText(getActivity(), "Please login first!", Toast.LENGTH_SHORT).show();
                        Navigation.findNavController(view).navigate(R.id.navigation_login);
                        return;
                    }

                    if (!databaseHelper.isFollowing(MainActivity.loggedInUser.getUsername(), profileUser.getUsername())) {
                        boolean result = databaseHelper.addFollow(MainActivity.loggedInUser.getUsername(), profileUser.getUsername());

                        if (result) {
                            View follow_frame = view.findViewById(R.id.follow_frame);
                            follow_frame.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.following_frame, null));
                            TextView follow_text = view.findViewById(R.id.follow_text);
                            follow_text.setText("Following");
                        }
                    } else {
                        boolean result = databaseHelper.removeFollow(MainActivity.loggedInUser.getUsername(), profileUser.getUsername());

                        if (result) {
                            View follow_frame = view.findViewById(R.id.follow_frame);
                            follow_frame.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.follow_frame, null));
                            TextView follow_text = view.findViewById(R.id.follow_text);
                            follow_text.setText("Follow");
                        }
                    }
                }
            });
        }
    }

    public void popupMenu(ImageView popupMenu) {

        popupMenu.setVisibility(View.VISIBLE);

        PopupMenu popup = new PopupMenu(getActivity(), popupMenu);
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.user_popup_menu);

        popupMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popup.show();
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.edit_profile_menu) {
            return true;
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Logout");
            builder.setMessage("Are you sure you want to log out?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Perform logout action here
                    MainActivity.loggedInUser = null;
                    Navigation.findNavController(fragmentView).navigate(R.id.navigation_login);
                }
            });
            builder.setNegativeButton("No", null);
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        }
    }
}
